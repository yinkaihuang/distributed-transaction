package cn.bucheng.tm.remoting.netty;

import cn.bucheng.tm.remoting.RemotingServer;
import cn.bucheng.tm.remoting.enu.CommandEnum;
import cn.bucheng.tm.remoting.protocol.RemotingCommand;
import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * @author ：yinchong
 * @create ：2019/8/28 11:43
 * @description：
 * @modified By：
 * @version:
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class NettyRemotingServer implements RemotingServer {

    public static final int PING_CODE = 0;
    public static final int REGISTER_CODE = 4;
    public static final int ERROR_CODE = -100;
    public static final int FIN_CODE = 1;
    public static final int WAIT_EXECUTE_TIMEOUT = 1000 * 60 * 5;
    private NioEventLoopGroup workGroup;
    private NioEventLoopGroup bossGroup;
    private ServerBootstrap bootstrap;
    private ThreadPoolExecutor poolExecutor;
    private volatile Channel serverChannel;
    private Timer timer;
    //记录xid和远程通道的对应关系
    private ConcurrentHashMap<String, List<Channel>> remotingChannelTable = new ConcurrentHashMap<String, List<Channel>>();
    //如果xid存在异常会记录再这里
    private CopyOnWriteArraySet<String> errorSet = new CopyOnWriteArraySet<String>();
    //记录xid的超时时间
    private ConcurrentHashMap<String, RemotingDefinition> channelTimeoutTable = new ConcurrentHashMap<String, RemotingDefinition>();

    public void start() {
        timer = new Timer("server-response-clear");
        TaskQueue queue = new TaskQueue(1000);
        poolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, Runtime.getRuntime().availableProcessors() * 4, 10, TimeUnit.SECONDS, queue);
        queue.setParent(poolExecutor);
        workGroup = new NioEventLoopGroup();
        bossGroup = new NioEventLoopGroup(1);
        bootstrap = new ServerBootstrap();
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.group(bossGroup, workGroup);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childOption(ChannelOption.SO_RCVBUF, 1024 * 1024);
        bootstrap.childOption(ChannelOption.SO_SNDBUF, 1024 * 1024);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 8, 0, 8));
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new RemotingServerHandler());
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

    @PreDestroy
    public void shutdown() {
        if (timer != null) {
            timer.cancel();
        }
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }

        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (poolExecutor != null) {
            poolExecutor.shutdown();
        }

    }

    public void bind(final int port) {
        bootstrap.bind(port).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    serverChannel = future.channel();
                    log.info("netty server start success ,bin port in:{}", port);
                    return;
                }
                log.warn("netty server start fail,bin port in:{},cause:{}", port, future.cause().toString());
            }
        });
    }

    public boolean isActive() {
        if (serverChannel == null || !serverChannel.isActive())
            return false;
        return true;
    }


    public void invokeAsync(Channel channel, RemotingCommand command) {
        if (channel == null || !channel.isActive()) {
            log.error("netty server not active");
            throw new RuntimeException("netty server not active");
        }
        channel.writeAndFlush(JSON.toJSONString(command)).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    log.error("send message to send buffer fail");
                    throw new RuntimeException("send message fail,cause:" + future.cause().toString());
                }
            }
        });
    }

    /**
     * 处理远程消息
     *
     * @param remotingCommand
     */
    public void handleRemotingMessage(Channel channel, RemotingCommand remotingCommand) {
        String xid = remotingCommand.getXid();
        int type = remotingCommand.getType();
        switch (type) {
            case PING_CODE:
                log.info("receive ping from remoting client");
                break;
            case REGISTER_CODE:
                List<Channel> channels = remotingChannelTable.get(xid);
                if (channels == null) {
                    channels = initChannelsAndDefinition(xid);
                }
                channels.add(channel);
                sendResponse(channel, xid);
                break;
            case ERROR_CODE:
                errorSet.add(xid);
                sendResponse(channel, xid);
                break;
            case FIN_CODE:
                boolean commit = isCommit(xid);
                asyncSendRollbackOrCommit(xid, commit);
                clear(xid);
                sendResponse(channel, xid);
                break;
        }
    }

    /**
     * 扫描异常信息
     */
    private void scanResponseTable() {
        List<String> removeKeyList = new LinkedList<String>();
        for (Map.Entry<String, RemotingDefinition> entry : channelTimeoutTable.entrySet()) {
            String xid = entry.getKey();
            RemotingDefinition definition = entry.getValue();
            if (System.currentTimeMillis() > definition.beginTime + definition.timeoutMillis + 5000) {
                removeKeyList.add(xid);
            }
        }

        for (String xid : removeKeyList) {
            clear(xid);
        }
    }

    /**
     * 发送远程响应信息
     *
     * @param channel
     * @param xid
     */
    private void sendResponse(Channel channel, String xid) {
        RemotingCommand command;
        command = new RemotingCommand(xid, CommandEnum.RESPONSE.getCode());
        invokeAsync(channel, command);
    }

    /**
     * 异步发送回滚或者提交指令
     *
     * @param xid
     * @param commit
     */
    private void asyncSendRollbackOrCommit(String xid, boolean commit) {
        RemotingCommand command;
        List<Channel> channels;
        if (commit) {
            command = new RemotingCommand(xid, CommandEnum.COMMIT.getCode());
        } else {
            command = new RemotingCommand(xid, CommandEnum.ROLLBACK.getCode());
        }
        channels = remotingChannelTable.get(xid);
        for (Channel temp : channels) {
            invokeAsync(temp, command);
        }
    }

    /**
     * 进行远程xid相关数据清除
     *
     * @param xid
     */
    private void clear(String xid) {
        errorSet.remove(xid);
        remotingChannelTable.remove(xid);
        channelTimeoutTable.remove(xid);
    }

    /**
     * 是否需要进行提交
     *
     * @param xid
     * @return
     */
    private boolean isCommit(String xid) {
        if (errorSet.contains(xid))
            return false;
        List<Channel> channels = remotingChannelTable.get(xid);
        for (Channel channel : channels) {
            if (!channel.isActive())
                return false;
        }
        return true;
    }


    private List<Channel> initChannelsAndDefinition(String xid) {
        List<Channel> channels;
        synchronized (xid) {
            channels = remotingChannelTable.get(xid);
            if (channels == null) {
                channels = new CopyOnWriteArrayList<Channel>();
                remotingChannelTable.put(xid, channels);
                channelTimeoutTable.put(xid, new RemotingDefinition(WAIT_EXECUTE_TIMEOUT));
            }
        }
        return channels;
    }


    private class RemotingServerHandler extends SimpleChannelInboundHandler<String> {

        protected void channelRead0(final ChannelHandlerContext ctx, final String msg) throws Exception {
            poolExecutor.execute(new Runnable() {
                public void run() {
                    log.info(msg);
                    handleRemotingMessage(ctx.channel(), JSON.parseObject(msg, RemotingCommand.class));
                }
            });
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("{} happen error,cause:{}", ctx.channel().remoteAddress(), cause.toString());
            markXidErrorFlag(ctx.channel());
        }
    }


    private class RemotingDefinition {
        long beginTime = System.currentTimeMillis();
        long timeoutMillis;

        public RemotingDefinition(long timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
        }
    }

    /**
     * 标记xid对应提交标记为错误
     *
     * @param channel
     */
    private void markXidErrorFlag(Channel channel) {
        for (Map.Entry<String, List<Channel>> entry : remotingChannelTable.entrySet()) {
            String key = entry.getKey();
            List<Channel> values = entry.getValue();
            if (values != null)
                continue;
            for (Channel temp : values) {
                if (temp == channel) {
                    errorSet.add(key);
                    break;
                }
            }
        }
    }


}
