package cn.bucheng.server.controller;

import cn.bucheng.server.model.po.TestDO;
import cn.bucheng.server.service.ITestCService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author ：yinchong
 * @create ：2019/8/28 17:56
 * @description：
 * @modified By：
 * @version:
 */
@RestController
@SuppressWarnings("all")
@RequestMapping("test")
public class TestCController {
    @Autowired
    private ITestCService service;

    @RequestMapping("/saveTest")
    public String saveTest(String name, String content) {
        TestDO testDO = new TestDO();
        testDO.setName(name);
        testDO.setContent(content);
        testDO.setCreateTime(new Date());
        testDO.setUpdateTime(new Date());
        int row = service.saveTest(testDO);
        if (row > 0) {
            return "success";
        }
        return "fail";
    }
}
