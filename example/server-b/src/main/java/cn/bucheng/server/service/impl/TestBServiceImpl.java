package cn.bucheng.server.service.impl;

import cn.bucheng.server.mapper.TestBMapper;
import cn.bucheng.server.model.po.TestDO;
import cn.bucheng.server.remoting.IServiceC;
import cn.bucheng.server.service.ITestBService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author ：yinchong
 * @create ：2019/8/28 17:57
 * @description：
 * @modified By：
 * @version:
 */
@Service
@SuppressWarnings("all")
public class TestBServiceImpl extends ServiceImpl<TestBMapper, TestDO> implements ITestBService {
    @Autowired
    private IServiceC serviceC;

    @Transactional
    public int saveTest(TestDO testDO) {
        Integer row = baseMapper.insert(testDO);
        return row;
    }

    @Transactional
    public int saveTest2(String name, String content) {
        TestDO testDO = new TestDO();
        testDO.setName(name);
        testDO.setContent(content);
        testDO.setCreateTime(new Date());
        testDO.setUpdateTime(new Date());
        Integer row = baseMapper.insert(testDO);
        if (row <= 0)
            return row;
        String result = serviceC.saveTest(name, content);
        if (!"success".equals(result))
            return 0;
        return 1;
    }
}
