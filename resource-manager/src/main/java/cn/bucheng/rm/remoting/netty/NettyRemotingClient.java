package cn.bucheng.rm.remoting.netty;

import cn.bucheng.rm.holder.ConnectionProxyHolder;
import cn.bucheng.rm.proxy.ConnectionProxy;
import cn.bucheng.rm.remoting.RemotingClient;
import cn.bucheng.rm.remoting.enu.CommandEnum;
import cn.bucheng.rm.remoting.exception.RemotingConnectException;
import cn.bucheng.rm.remoting.exception.RemotingSendRequestException;
import cn.bucheng.rm.remoting.exception.RemotingTimeoutException;
import cn.bucheng.rm.remoting.protocol.RemotingCommand;
import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author ：yinchong
 * @create ：2019/8/27 17:12
 * @description：
 * @modified By：
 * @version:
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class NettyRemotingClient implements RemotingClient {
    public static final int RESPONSE_CODE = 200;
    public static final int ROLLBACK_CODE = -2;
    public static final int COMMIT_CODE = 2;
    /**
     * 保存请求结果集
     */
    private ConcurrentHashMap<String, ResponseFuture> responseTable = new ConcurrentHashMap<String, ResponseFuture>(200);
    /**
     * 保存连接成功的远程通道
     */
    private volatile Channel remotingChannel;

    private NioEventLoopGroup workGroup;
    private Bootstrap bootstrap;
    private ThreadPoolExecutor poolExecutor;

    private Timer timer;

    public void start() {
        timer = new Timer("response_table_cleaner");
        workGroup = new NioEventLoopGroup();
        TaskQueue taskQueue = new TaskQueue(10000);
        poolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 3, 10, TimeUnit.SECONDS, taskQueue);
        taskQueue.setParent(poolExecutor);
        bootstrap = new Bootstrap();
        bootstrap.group(workGroup)//
                .channel(NioSocketChannel.class)//
                .option(ChannelOption.SO_KEEPALIVE, true)//
                .option(ChannelOption.SO_SNDBUF, 1024 * 1024)//
                .option(ChannelOption.SO_RCVBUF, 1024 * 1024)//
                .option(ChannelOption.TCP_NODELAY, true)//
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000 * 5)//
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(0, 0, 30, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 8, 0, 8));
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new RemotingClientHandle());
                        ch.pipeline().addFirst(new StringEncoder());
                        ch.pipeline().addFirst(new LengthFieldPrepender(8));
                    }
                });

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                scanResponseTable();
            }
        }, 30 * 1000, 60 * 1000);
    }

    public void connect(final String ip, final int port) {
        bootstrap.connect(ip, port).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("connect remote channel {}:{} success", ip, port);
                    remotingChannel = future.channel();
                    return;
                }
                log.warn("connect remote channel {}:{} fail,cause:", ip, port, future.cause().toString());
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }
        if (poolExecutor != null) {
            poolExecutor.shutdown();
        }
        if (timer != null) {
            timer.cancel();
        }
    }

    public boolean channelActive() {
        if (remotingChannel == null || !remotingChannel.isActive())
            return false;
        return true;
    }

    public void invokeSync(RemotingCommand command, long timeoutMillis) throws InterruptedException, RemotingSendRequestException, RemotingTimeoutException, RemotingConnectException {
        if (!channelActive()) {
            throw new RemotingConnectException("远程连接 transaction manager 服务失败");
        }
        final ResponseFuture responseFuture = new ResponseFuture(command.getXid(), 1000 * 60 * 5);
        try {
            responseTable.put(command.getXid(), responseFuture);
            remotingChannel.pipeline().writeAndFlush(JSON.toJSONString(command)).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        responseFuture.setSendRequestOK(true);
                        return;
                    }
                    responseFuture.setSendRequestOK(false);
                    responseFuture.setCause(future.cause());
                    responseFuture.putResponse(null);
                }
            });
            RemotingCommand remotingCommand = responseFuture.waitResponse();
            if (!responseFuture.isSendRequestOK()) {
                throw new RemotingSendRequestException("远程发送数据失败");
            }
            if (remotingCommand == null) {
                throw new RemotingTimeoutException("获取数据超时");
            }
            if (remotingCommand.getType() != CommandEnum.RESPONSE.getCode()) {
                throw new RuntimeException("未知响应错误");
            }
        } finally {
            responseTable.remove(command.getXid());
        }
    }

    @Override
    public void handleRemotingCommand(RemotingCommand command) throws SQLException {
        int type = command.getType();
        String xid = command.getXid();
        ConnectionProxy proxy = null;
        switch (type) {
            case RESPONSE_CODE:
                ResponseFuture responseFuture = responseTable.get(xid);
                responseFuture.putResponse(command);
                break;
            case ROLLBACK_CODE:
                proxy = ConnectionProxyHolder.remove(xid);
                if (proxy == null) {
                    log.error("not find proxy to rollback with xid:{}", xid);
                    return;
                }
                log.info("proxy to rollback and close with xid:{}",xid);
                proxy.reallyRollback();
                proxy.reallyClose();
                break;
            case COMMIT_CODE:
                proxy = ConnectionProxyHolder.remove(xid);
                if (proxy == null) {
                    log.error("not find proxy to commit with xid:{}", xid);
                    return;
                }
                log.info("proxy to commit and close with xid:{}",xid);
                proxy.reallyCommit();
                proxy.reallyClose();
                break;
        }
    }

    /**
     * 定时扫描结果集和，将异常的数据移除掉防止出现内存泄漏问题
     */
    public void scanResponseTable() {
        List<String> removeKeyList = new LinkedList<>();
        for (Map.Entry<String, ResponseFuture> entry : responseTable.entrySet()) {
            String key = entry.getKey();
            ResponseFuture future = entry.getValue();
            if (System.currentTimeMillis() > future.getBeginTime() + future.getTimeoutMillis() + 5000) {
                future.release();
                removeKeyList.add(key);
            }
        }
        for (String removeKey : removeKeyList) {
            responseTable.remove(removeKey);
        }

    }


    private class RemotingClientHandle extends SimpleChannelInboundHandler<String> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, final String msg) throws Exception {
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        handleRemotingCommand(JSON.parseObject(msg, RemotingCommand.class));
                    } catch (SQLException e) {
                        log.error(e.toString());
                    }
                }
            });
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            remotingChannel = null;
            log.warn("{} happen error,cause:{}", ctx.channel().remoteAddress(), cause.toString());
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (!(evt instanceof IdleStateEvent))
                return;
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                case ALL_IDLE:
                    RemotingCommand pingCommand = new RemotingCommand("", CommandEnum.PING.getCode());
                    ctx.pipeline().writeAndFlush(JSON.toJSONString(pingCommand));
                    break;
            }
        }
    }

}
