package cn.bucheng.tm.remoting.netty;

import cn.bucheng.tm.constant.RemotingConstant;
import cn.bucheng.tm.remoting.RemotingServer;
import cn.bucheng.tm.remoting.protocol.RemotingCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author ：yinchong
 * @create ：2019/8/28 13:57
 * @description：
 * @modified By：
 * @version:
 */
@Component
public class NettyServerController implements CommandLineRunner {

    @Value("${server.port}")
    private int port;
    @Autowired
    private RemotingServer remotingServer;

    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public void run(String... args) throws Exception {
        remotingServer.start();
        loopCheckServerHeap();
    }

    private void loopCheckServerHeap() {
        executor.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                if(!remotingServer.isActive()){
                    remotingServer.bind(port+ RemotingConstant.STEP);
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }
}
