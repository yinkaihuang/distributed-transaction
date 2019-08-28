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

    /**
     * 等待阻塞结果响应
     * @param timeoutMillis
     * @return
     * @throws InterruptedException
     */
    public RemotingCommand waitResponse(final  long timeoutMillis) throws InterruptedException {
        countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        return command;
    }

    /**
     * 等待阻塞结果响应
     * @return
     * @throws InterruptedException
     */
    public RemotingCommand waitResponse() throws InterruptedException {
        countDownLatch.await(timeoutMillis,TimeUnit.MILLISECONDS);
        return command;
    }

    /**
     * 是否阻塞
     */
    public void release(){
        countDownLatch.countDown();
    }


    /**
     * 存放数据到结果集中并唤醒阻塞
     * @param remotingCommand
     */
    public void putResponse(final RemotingCommand remotingCommand) {
        this.command = remotingCommand;
        this.countDownLatch.countDown();
    }
}
