package cn.bucheng.server.remoting.impl;

import cn.bucheng.server.remoting.IServiceC;
import org.springframework.stereotype.Component;

/**
 * @author ：yinchong
 * @create ：2019/8/29 13:43
 * @description：
 * @modified By：
 * @version:
 */
@Component
public class ServerCFail implements IServiceC {
    public String saveTest(String name, String content) {
       throw new RuntimeException("fail");
    }
}
