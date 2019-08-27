package cn.bucheng.rm.remoting.netty;

import cn.bucheng.rm.remoting.protocol.RemotingCommand;
import lombok.Data;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author ：yinchong
 * @create ：2019/8/27 16:56
 * @description：
 * @modified By：
 * @version:
 */
@Data
public class ResponseFuture {
    private String xid;
    private long timeoutMillis;
    private long beginTime = System.currentTimeMillis();
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private volatile RemotingCommand command;
    private volatile boolean sendRequestOK;
    private volatile Throwable cause;



    public ResponseFuture(String xid,long timeoutMillis){
        this.xid = xid;
        this.timeoutMillis = timeoutMillis;
    }

    public RemotingCommand waitResponse(final  long timeoutMillis) throws InterruptedException {
        countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        return command;
    }

    public RemotingCommand waitResponse() throws InterruptedException {
        countDownLatch.await(timeoutMillis,TimeUnit.MILLISECONDS);
        return command;
    }

    public void release(){
        countDownLatch.countDown();
    }


    public void putResponse(final RemotingCommand remotingCommand) {
        this.command = remotingCommand;
        this.countDownLatch.countDown();
    }
}
