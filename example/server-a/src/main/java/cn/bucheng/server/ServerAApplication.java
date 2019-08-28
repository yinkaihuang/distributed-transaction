package cn.bucheng.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author ：yinchong
 * @create ：2019/8/27 16:44
 * @description：
 * @modified By：
 * @version:
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ServerAApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerAApplication.class, args);
    }
}
