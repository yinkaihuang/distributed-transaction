package cn.bucheng.server.service;

import cn.bucheng.server.model.po.TestDO;
import com.baomidou.mybatisplus.service.IService;

/**
 * @author ：yinchong
 * @create ：2019/8/28 17:57
 * @description：
 * @modified By：
 * @version:
 */
public interface ITestCService extends IService<TestDO> {
    int saveTest(TestDO testDO);
}
