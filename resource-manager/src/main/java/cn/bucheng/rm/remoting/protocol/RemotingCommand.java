package cn.bucheng.rm.remoting.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author ：yinchong
 * @create ：2019/8/27 16:51
 * @description：
 * @modified By：
 * @version:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemotingCommand implements Serializable {
    private String xid;
    private Integer type;
}
