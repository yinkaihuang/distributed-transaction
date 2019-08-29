# demo启动搭建

```
1.将demo模块下面的resources目录下面的table-init.sql在mysql执行一下

2.修改demo模块下面的resources目录下面application.properties中的mysql账号密码配置

3.进行启动
>>启动register
>>启动transaction-manager
>>启动server-a
>>启动server-b
>>启动server-c

4.调用测试接口
接口2：
http://127.0.0.1:8383/test/saveTest2?name=yinchong&content=test

接口1：
http://127.0.0.1:8383/test/saveTest?name=yinchong&content=test

5.查看server_a.t_test/server_b.t_test/server_c.t_test 查看数据是否一致

```

## 接口2流程图
![](https://github.com/yinbucheng/mypic/blob/master/%E6%8E%A5%E5%8F%A31%E6%B5%81%E7%A8%8B.png?raw=true)

## 接口1流程图
![](https://github.com/yinbucheng/mypic/blob/master/%E6%8E%A5%E5%8F%A32.png?raw=true)