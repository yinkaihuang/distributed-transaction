package cn.bucheng.rm.remoting.enu;

/**
 * @author ：yinchong
 * @create ：2019/8/27 16:53
 * @description：
 * @modified By：
 * @version:
 */
public enum CommandEnum {
    PING(0),
    ROLLBACK(-2),
    COMMIT(2),
    FIN(1),
    REGISTER(4),
    ERROR(-100),
    RESPONSE(200);
    private int code;

    CommandEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }}
