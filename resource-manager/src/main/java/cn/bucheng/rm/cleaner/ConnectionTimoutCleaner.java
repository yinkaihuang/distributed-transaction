package cn.bucheng.rm.cleaner;

import cn.bucheng.rm.holder.ConnectionProxyHolder;
import cn.bucheng.rm.proxy.ConnectionProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 数据库链接持有超时清除
 *
 * @author ：yinchong
 * @create ：2019/8/28 14:38
 * @description：
 * @modified By：
 * @version:
 */
@Slf4j
public class ConnectionTimoutCleaner implements CommandLineRunner {

    private static ScheduledExecutorService remotingChannelThread = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void run(String... args) throws Exception {
        remotingChannelThread.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                timeoutConnectionClean();
            }
        }, 30, 60, TimeUnit.SECONDS);
    }

    private void timeoutConnectionClean() {
        Set<Map.Entry<String, ConnectionProxyHolder.ConnectionProxyDefinition>> entries = ConnectionProxyHolder.entrySet();
        List<String> removeKeyList = new LinkedList<>();
        for (Map.Entry<String, ConnectionProxyHolder.ConnectionProxyDefinition> entry : entries) {
            String key = entry.getKey();
            ConnectionProxyHolder.ConnectionProxyDefinition definition = entry.getValue();
            if (System.currentTimeMillis() > definition.startTime + definition.timeoutMillis + 5000) {
                removeKeyList.add(key);
            }
        }

        for (String key : removeKeyList) {
            ConnectionProxy proxy = ConnectionProxyHolder.remove(key);
            if (proxy != null) {
                try {
                    proxy.reallyRollback();
                    proxy.reallyClose();
                    log.info("xid:{} really rollback and close success", key);
                } catch (SQLException e) {
                    log.info("xid:{} really rollback and close fail", key);
                }
            }
        }
    }
}
