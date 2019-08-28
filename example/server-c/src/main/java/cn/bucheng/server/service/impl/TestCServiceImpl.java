package cn.bucheng.server.service.impl;

import cn.bucheng.server.mapper.TestCMapper;
import cn.bucheng.server.model.po.TestDO;
import cn.bucheng.server.service.ITestCService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ：yinchong
 * @create ：2019/8/28 17:57
 * @description：
 * @modified By：
 * @version:
 */
@Service
public class TestCServiceImpl extends ServiceImpl<TestCMapper, TestDO> implements ITestCService {

    @Transactional
    public int saveTest(TestDO testDO) {
        Integer row = baseMapper.insert(testDO);
        return row;
    }
}
