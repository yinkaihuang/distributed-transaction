package cn.bucheng.rm.remoting.exception;

/**
 * @author ：yinchong
 * @create ：2019/8/27 17:14
 * @description：
 * @modified By：
 * @version:
 */
public class RemotingConnectException extends RemotingException {
    public RemotingConnectException(String addr) {
        this(addr, null);
    }

    public RemotingConnectException(String addr, Throwable cause) {
        super("connect to <" + addr + "> failed", cause);
    }
}
