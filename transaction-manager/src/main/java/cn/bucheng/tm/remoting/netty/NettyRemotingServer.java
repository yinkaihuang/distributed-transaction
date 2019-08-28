package cn.bucheng.tm.remoting.netty;

import cn.bucheng.tm.remoting.RemotingServer;
import cn.bucheng.tm.remoting.protocol.RemotingCommand;
import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

import java.util.concurrent.TimeUnit;

/**
 * @author ：yinchong
 * @create ：2019/8/28 11:43
 * @description：
 * @modified By：
 * @version:
 */
@Slf4j
public class NettyRemotingServer implements RemotingServer {

    private NioEventLoopGroup workGroup;
    private NioEventLoopGroup bossGroup;
    private ServerBootstrap bootstrap;
    private ThreadPoolExecutor poolExecutor;
    private volatile Channel serverChannel;

    public void start() {
        TaskQueue queue = new TaskQueue(1000);
        poolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, Runtime.getRuntime().availableProcessors() * 4, 10, TimeUnit.SECONDS, queue);
        queue.setParent(poolExecutor);
        workGroup = new NioEventLoopGroup();
        bossGroup = new NioEventLoopGroup(1);
        bootstrap = new ServerBootstrap();
    }

    public void shutdown() {
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

    public void invokeAsync(RemotingCommand command) {
        if (!isActive()) {
            log.error("netty server not active");
            throw new RuntimeException("netty server not active");
        }
        serverChannel.writeAndFlush(JSON.toJSONString(command)).addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    log.error("send message to send buffer fail");
                    throw new RuntimeException("send message fail,cause:"+ future.cause().toString());
                }
            }
        });
    }
}
