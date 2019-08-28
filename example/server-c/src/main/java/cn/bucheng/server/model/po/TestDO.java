package cn.bucheng.server.model.po;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ：yinchong
 * @create ：2019/8/28 19:21
 * @description：
 * @modified By：
 * @version:
 */
@TableName(value = "t_test")
@Alias("test")
@Data
public class TestDO implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String content;
    private Date createTime;
    private Date updateTime;
}
