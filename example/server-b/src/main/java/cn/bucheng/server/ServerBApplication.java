package cn.bucheng.server;

import io.netty.bootstrap.ServerBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author ：yinchong
 * @create ：2019/8/27 16:45
 * @description：
 * @modified By：
 * @version:
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class ServerBApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerBApplication.class, args);
    }
}
