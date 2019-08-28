package cn.bucheng.rm.holder;

import cn.bucheng.rm.proxy.ConnectionProxy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代理数据库连接持有者
 *
 * @author ：yinchong
 * @create ：2019/8/28 9:39
 * @description：
 * @modified By：
 * @version:
 */
public class ConnectionProxyHolder {
    private static ConcurrentHashMap<String, ConnectionProxyDefinition> proxyTable = new ConcurrentHashMap<>();

    public static void putProxy(String xid, ConnectionProxy proxy, long timeout) {
        ConnectionProxyDefinition definition = new ConnectionProxyDefinition();
        definition.proxy = proxy;
        definition.timeoutMillis = timeout;
        proxyTable.put(xid, definition);
    }

    public static ConnectionProxy remove(String xid) {
        ConnectionProxyDefinition proxyDefinition = proxyTable.remove(xid);
        if (proxyDefinition == null)
            return null;
        return proxyDefinition.proxy;
    }

    public static ConnectionProxyDefinition get(String xid) {
        return proxyTable.get(xid);
    }


    public static Set<Map.Entry<String, ConnectionProxyDefinition>> entrySet() {
        return proxyTable.entrySet();
    }

    public static class ConnectionProxyDefinition {
        public ConnectionProxy proxy;
        public long startTime = System.currentTimeMillis();
        public long timeoutMillis;
    }
}
