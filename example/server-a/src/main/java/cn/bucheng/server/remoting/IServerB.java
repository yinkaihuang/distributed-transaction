package cn.bucheng.server.remoting;

import cn.bucheng.server.remoting.impl.ServerBFail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author ：yinchong
 * @create ：2019/8/28 19:47
 * @description：
 * @modified By：
 * @version:
 */
@FeignClient(value = "server-b",fallback = ServerBFail.class)
public interface IServerB {
    @RequestMapping("/test/saveTest")
    String saveTest(@RequestParam("name") String name,@RequestParam("content") String content);

    @RequestMapping("/test/saveTest2")
    String saveTest2(@RequestParam("name") String name,@RequestParam("content") String content);
}
