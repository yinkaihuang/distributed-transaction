package cn.bucheng.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author ：yinchong
 * @create ：2019/8/27 16:45
 * @description：
 * @modified By：
 * @version:
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ServerCApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerCApplication.class, args);
    }
}
