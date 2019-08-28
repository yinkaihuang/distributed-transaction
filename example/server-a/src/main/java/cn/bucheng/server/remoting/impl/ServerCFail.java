package cn.bucheng.server.remoting.impl;

import cn.bucheng.server.remoting.IServerC;

/**
 * @author buchengyin
 * @create 2019/8/28 23:04
 * @describe
 */
public class ServerCFail implements IServerC {
    @Override
    public String saveTest(String name, String content) {
        throw new RuntimeException("fail");
    }
}
