package cn.bucheng.rm.remoting.netty;

import cn.bucheng.rm.remoting.RemotingClient;
import cn.bucheng.rm.remoting.exception.RemotingConnectException;
import cn.bucheng.rm.remoting.exception.RemotingSendRequestException;
import cn.bucheng.rm.remoting.exception.RemotingTimeoutException;
import cn.bucheng.rm.remoting.protocol.RemotingCommand;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

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
public class NettyRemotingClient implements RemotingClient {
    /**
     * 保存请求结果集
     */
    private ConcurrentHashMap<String, ResponseFuture> responseTable = new ConcurrentHashMap<String, ResponseFuture>(200);
    /**
     * 保存连接成功的远程通道
     */
    private volatile Channel remotingChannl;

    private NioEventLoopGroup workGroup;
    private Bootstrap bootstrap;
    private ThreadPoolExecutor poolExecutor;

    public void start() {
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
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                    }
                });
    }

    public void connect(final String ip, final int port) {
        bootstrap.connect(ip, port).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("connect remote channel {}:{} success", ip, port);
                    remotingChannl = future.channel();
                    return;
                }
                log.warn("connect remote channel {}:{} fail,cause:", ip, port, future.cause().toString());
            }
        });
    }

    public void shutdown() {
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }
        if (poolExecutor != null) {
            poolExecutor.shutdown();
        }
    }

    public boolean channelActive(String key) {
        if (remotingChannl == null || !remotingChannl.isActive())
            return false;
        return true;
    }

    public void invokeSync(RemotingCommand command, long timeoutMillis) throws InterruptedException, RemotingSendRequestException, RemotingTimeoutException, RemotingConnectException {

    }


}
