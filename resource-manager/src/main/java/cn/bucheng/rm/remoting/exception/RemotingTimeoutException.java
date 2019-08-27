package cn.bucheng.rm.remoting.exception;

import cn.bucheng.rm.remoting.protocol.RemotingCommand;

/**
 * @author ：yinchong
 * @create ：2019/8/27 17:14
 * @description：
 * @modified By：
 * @version:
 */
public class RemotingTimeoutException extends RemotingException {
    public RemotingTimeoutException(String message) {
        super(message);
    }

    public RemotingTimeoutException(String addr, long timeoutMillis) {
        this(addr, timeoutMillis, null);
    }

    public RemotingTimeoutException(String addr, long timeoutMillis, Throwable cause) {
        super("wait response on the channel <" + addr + "> timeout, " + timeoutMillis + "(ms)", cause);
    }
}
