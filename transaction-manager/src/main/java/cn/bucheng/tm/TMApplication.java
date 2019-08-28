package cn.bucheng.tm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author ：yinchong
 * @create ：2019/8/28 11:34
 * @description：
 * @modified By：
 * @version:
 */
@EnableDiscoveryClient
@SpringBootApplication
public class TMApplication {
    public static void main(String[] args) {
        SpringApplication.run(TMApplication.class, args);
    }
}
