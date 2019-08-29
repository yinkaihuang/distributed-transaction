package cn.bucheng.server.service.impl;

import cn.bucheng.server.mapper.TestAMapper;
import cn.bucheng.server.model.po.TestDO;
import cn.bucheng.server.remoting.IServerB;
import cn.bucheng.server.remoting.IServerC;
import cn.bucheng.server.service.ITestAService;
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
public class TestAServiceImpl extends ServiceImpl<TestAMapper, TestDO> implements ITestAService {
    @Autowired
    private IServerC serviceC;
    @Autowired
    private IServerB serviceB;

    @Transactional
    public void saveTest(String name, String content) {
        TestDO testDO = new TestDO();
        testDO.setName(name);
        testDO.setContent(content);
        testDO.setCreateTime(new Date());
        testDO.setUpdateTime(new Date());
        Integer rows = baseMapper.insert(testDO);
        if (rows <= 0) {
            throw new RuntimeException("更新数据失败");
        }
        String result = serviceB.saveTest(name, content);
        if (!"success".equals(result)) {
            throw new RuntimeException("更新数据失败");
        }
        result = serviceC.saveTest(name, content);
        if (!"success".equals(result)) {
            throw new RuntimeException("更新数据失败");
        }
    }

    @Transactional
    public void saveTest2(String name, String content) {
        TestDO testDO = new TestDO();
        testDO.setName(name);
        testDO.setContent(content);
        testDO.setCreateTime(new Date());
        testDO.setUpdateTime(new Date());
        Integer rows = baseMapper.insert(testDO);
        if (rows <= 0) {
            throw new RuntimeException("更新数据失败");
        }
        String result = serviceB.saveTest2(name, content);
        if (!"success".equals(result)) {
            throw new RuntimeException("更新数据失败");
        }
    }
}
