package cn.bucheng.rm.remoting;

import cn.bucheng.rm.remoting.exception.RemotingConnectException;
import cn.bucheng.rm.remoting.exception.RemotingSendRequestException;
import cn.bucheng.rm.remoting.exception.RemotingTimeoutException;
import cn.bucheng.rm.remoting.protocol.RemotingCommand;

import java.sql.SQLException;

public interface RemotingClient {
    /**
     * 启动，完成基本初始化工作
     */
    void start();

    /**
     * 进行远程连接
     *
     * @param ip
     * @param port
     */
    void connect(String ip, int port);

    /**
     * 关闭，完成基本资源释放
     */
    void shutdown();

    /**
     * 判断key对应的远程通道是否正常
     *
     * @return
     */
    boolean channelActive();

    /**
     * 同步阻塞发送数据
     *
     * @param command
     * @param timeoutMillis
     * @return
     * @throws InterruptedException
     * @throws RemotingSendRequestException
     * @throws RemotingTimeoutException
     * @throws RemotingConnectException
     */
    void invokeSync(final RemotingCommand command, final long timeoutMillis) throws InterruptedException, RemotingSendRequestException, RemotingTimeoutException, RemotingConnectException;

    /**
     * 处理消息
     * @param command
     */
    void handleRemotingCommand(RemotingCommand command) throws SQLException;
}
