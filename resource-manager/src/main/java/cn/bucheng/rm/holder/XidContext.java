package cn.bucheng.rm.holder;

import org.apache.logging.log4j.util.Strings;

import java.util.UUID;

/**
 * xid上下文，用于将同一线程处理的请求中的参数传递下去
 *
 * @author ：yinchong
 * @create ：2019/8/28 9:51
 * @description：xid上下文
 * @modified By：
 * @version:
 */
public class XidContext {
    private static ThreadLocal<String> xidContext = new ThreadLocal<>();

    public static String getXid() {
        return xidContext.get();
    }

    public static String createAndSaveXid() {
        String uuid = UUID.randomUUID().toString();
        xidContext.set(uuid);
        return uuid;
    }

    public static void putXid(String xid) {
        xidContext.set(xid);
    }

    public static String removeXid() {
        String xid = xidContext.get();
        if (Strings.isBlank(xid))
            return null;
        xidContext.remove();
        return xid;
    }


    public static boolean existXid() {
        String xid = xidContext.get();
        if (Strings.isBlank(xid))
            return false;
        return true;
    }
}
