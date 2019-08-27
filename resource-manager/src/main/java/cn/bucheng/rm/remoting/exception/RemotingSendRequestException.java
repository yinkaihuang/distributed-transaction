package cn.bucheng.rm.remoting.exception;

/**
 * @author ：yinchong
 * @create ：2019/8/27 17:13
 * @description：
 * @modified By：
 * @version:
 */
public class RemotingSendRequestException extends RemotingException {

    public RemotingSendRequestException(String addr) {
        this(addr, null);
    }

    public RemotingSendRequestException(String addr, Throwable cause) {
        super("send request to <" + addr + "> failed", cause);
    }
}
