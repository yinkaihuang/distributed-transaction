package cn.bucheng.server.controller;

import cn.bucheng.rm.annotation.GlobalTransactional;
import cn.bucheng.server.service.ITestAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：yinchong
 * @create ：2019/8/28 17:56
 * @description：
 * @modified By：
 * @version:
 */
@RestController
@RequestMapping("test")
public class TestAController {
    @Autowired
    private ITestAService service;


    @RequestMapping("saveTest")
    @GlobalTransactional
    public String saveTest(String name, String content) {
        service.saveTest(name, content);
        return "succcess";
    }

    @RequestMapping("saveTest2")
    @GlobalTransactional
    public String saveTest2(String name, String content) {
        service.saveTest2(name, content);
        return "success";
    }
}
