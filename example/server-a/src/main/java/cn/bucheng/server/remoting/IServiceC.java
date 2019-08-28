package cn.bucheng.server.remoting;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author ：yinchong
 * @create ：2019/8/28 19:48
 * @description：
 * @modified By：
 * @version:
 */
@FeignClient(value = "server-c")
public interface IServiceC {
    @RequestMapping("/test/saveTest")
    String saveTest(String name, String content);
}
