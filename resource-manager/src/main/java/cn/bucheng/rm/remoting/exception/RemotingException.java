package cn.bucheng.rm.remoting.exception;

/**
 * @author ：yinchong
 * @create ：2019/8/27 17:14
 * @description：
 * @modified By：
 * @version:
 */
public class RemotingException extends Exception {

    public RemotingException(String message){
        super(message);
    }

    public RemotingException(String message,Throwable cause){
        super(message,cause);
    }
}
