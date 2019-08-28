package cn.bucheng.rm.remoting.netty;

import cn.bucheng.rm.constant.RemotingConstant;
import cn.bucheng.rm.remoting.RemotingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author ：yinchong
 * @create ：2019/8/28 14:04
 * @description：
 * @modified By：
 * @version:
 */
@Component
public class NettyClientController implements CommandLineRunner {
    @Autowired
    private RemotingClient remotingClient;

    @Autowired
    private DiscoveryClient discoveryClient;
    @Value("${remoting.tm.address}")
    private String tmName;

    public NettyClientController(RemotingClient client, DiscoveryClient discoveryClient) {
        this.remotingClient = client;
        this.discoveryClient = discoveryClient;
    }

    private static ScheduledExecutorService remotingChannelThread = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void run(String... args) throws Exception {
        remotingClient.start();
        remotingChannelThread.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                if (!remotingClient.channelActive()) {
                    String[] ipAndPort = getIpAndPort();
                    remotingClient.connect(ipAndPort[0], Integer.parseInt(ipAndPort[1])+RemotingConstant.STEP);
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    //获取TM中服务的Web地址的ip和端口
    private String[] getIpAndPort() {
        List<ServiceInstance> instances = discoveryClient.getInstances(tmName);
        if (instances == null || instances.size() == 0)
            return null;
        if (instances.size() != 1) {
            throw new RuntimeException("目前版本只能支持部署一个TM实例");
        }
        ServiceInstance instance = instances.get(0);
        String[] result = new String[2];
        result[0] = instance.getHost();
        result[1] = instance.getPort() + "";
        return result;
    }
}
