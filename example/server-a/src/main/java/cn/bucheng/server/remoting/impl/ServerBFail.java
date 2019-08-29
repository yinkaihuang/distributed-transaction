package cn.bucheng.server.remoting.impl;

import cn.bucheng.server.remoting.IServerB;
import org.springframework.stereotype.Component;

/**
 * @author buchengyin
 * @create 2019/8/28 23:03
 * @describe
 */
@Component
public class ServerBFail implements IServerB {
    @Override
    public String saveTest(String name, String content) {
        throw new RuntimeException("fail");
    }

    @Override
    public String saveTest2(String name, String content) {
        throw new RuntimeException("fail");
    }
}
