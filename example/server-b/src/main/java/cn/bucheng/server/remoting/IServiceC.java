package cn.bucheng.server.remoting;

import cn.bucheng.server.remoting.impl.ServerCFail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author ：yinchong
 * @create ：2019/8/28 19:40
 * @description：
 * @modified By：
 * @version:
 */
@FeignClient(value = "server-c", fallback = ServerCFail.class)
public interface IServiceC {
    @RequestMapping("/test/saveTest")
    String saveTest(@RequestParam("name") String name, @RequestParam("content") String content);
}
