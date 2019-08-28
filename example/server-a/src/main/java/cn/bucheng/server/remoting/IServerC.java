package cn.bucheng.server.remoting;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author ：yinchong
 * @create ：2019/8/28 19:48
 * @description：
 * @modified By：
 * @version:
 */
@FeignClient(value = "server-c")
public interface IServerC {
    @RequestMapping("/test/saveTest")
    String saveTest(@RequestParam("name") String name,@RequestParam("content") String content);
}
