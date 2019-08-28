package cn.bucheng.server.remoting;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ：yinchong
 * @create ：2019/8/28 19:47
 * @description：
 * @modified By：
 * @version:
 */
@FeignClient(value = "server-b")
public interface IServiceB {
    @RequestMapping("/test/saveTest")
    String saveTest(String name,String content);

    @RequestMapping("/test/saveTest2")
    String saveTest2(String name,String content);
}
