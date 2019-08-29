package cn.bucheng.rm.config;

import cn.bucheng.rm.aspect.DataSourceAspect;
import cn.bucheng.rm.aspect.GlobalTransactionalAspect;
import cn.bucheng.rm.aspect.TransactionalAspect;
import cn.bucheng.rm.cleaner.ConnectionTimoutCleaner;
import cn.bucheng.rm.feign.FeignRequestInterceptor;
import cn.bucheng.rm.remoting.RemotingClient;
import cn.bucheng.rm.remoting.netty.NettyClientController;
import cn.bucheng.rm.remoting.netty.NettyRemotingClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：yinchong
 * @create ：2019/8/28 16:54
 * @description：
 * @modified By：
 * @version:
 */
@Configuration
@ConditionalOnProperty(value = "rm.global.tx.enable", matchIfMissing = true)
public class RMConfig {

    @Bean
    public RemotingClient remotingClient() {
        return new NettyRemotingClient();
    }

    @Bean
    public DataSourceAspect dataSourceAspect() {
        return new DataSourceAspect();
    }

    @Bean
    public GlobalTransactionalAspect globalTransactionalAspect(RemotingClient client) {
        return new GlobalTransactionalAspect(client);
    }

    @Bean
    public TransactionalAspect transactionalAspect(RemotingClient client) {
        return new TransactionalAspect(client);
    }

    @Bean
    public ConnectionTimoutCleaner connectionTimoutCleaner(){
        return new ConnectionTimoutCleaner();
    }

    @Bean
    public FeignRequestInterceptor feignRequestInterceptor(){
        return new FeignRequestInterceptor();
    }

    @Bean
    public NettyClientController clientController(RemotingClient client, DiscoveryClient discoveryClient){
        return new NettyClientController(client,discoveryClient);
    }

}
