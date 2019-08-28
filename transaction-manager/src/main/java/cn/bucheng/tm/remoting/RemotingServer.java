package cn.bucheng.tm.remoting;

import cn.bucheng.tm.remoting.protocol.RemotingCommand;
import io.netty.channel.Channel;


public interface RemotingServer {
    /**
     * 启动进行基本资源初始工作
     */
    void start();

    /**
     * 关闭完成基本资源释放
     */
    void shutdown();

    /**
     * 绑定端口进行启动
     *
     * @param port
     */
    void bind(int port);

    /**
     * 判断当前服务是否活跃
     *
     * @return
     */
    boolean isActive();

    /**
     * 异步调用发送数据
     *
     * @param command
     */
    void invokeAsync(Channel channel, RemotingCommand command);
}
