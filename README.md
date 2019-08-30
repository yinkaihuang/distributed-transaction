# 分布式事务使用

## 模块讲解
```
1.RM（ResourceManager）   资源管理器
2.TM(TransactionManager)   事务协调器
```


## 架构图
![](https://github.com/yinbucheng/mypic/blob/master/%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1.png?raw=true)


## 分布式事务异常流程
![](https://github.com/yinbucheng/mypic/blob/master/%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1%E5%BC%82%E5%B8%B8%E6%B5%81%E7%A8%8B.png?raw=true)



## 分布式事务正常流程
![](https://github.com/yinbucheng/mypic/blob/master/%E5%88%86%E5%B8%83%E5%BC%8F%E4%BA%8B%E5%8A%A1%E6%AD%A3%E5%B8%B8%E6%B5%81%E7%A8%8B.png?raw=true)


### 使用
```
1.在需要使用分布式事务添加下面依赖

<dependency>
     <groupId>cn.bucheng</groupId>
     <artifactId>resource-manager</artifactId>
     <version>1.0-SNAPSHOT</version>
</dependency>

2.在服务中添加下面配置

#添加配置表示开启分布式事务（可选配置默认开启）
rm.global.tx.enable=true
#添加配置分布式事务最大并发数量(默认为15)
rm.global.tx.number=20(建议最大配置不要超过总连接数一般)

#配置tm的服务名称(必须配置）
remoting.tm.address=tm
#取消重复调用(必须配置)
ribbon.okToRetryOnAllOperations = false
ribbon.MaxAutoRetriesNextServer = 0

3.mysql拦截配置
确认使用的spring.datasource.driver-class-name=com.mysql.jdbc.Driver
在mysql的url配置最后面加上：&statementInterceptors=cn.bucheng.rm.intercept.mysql.MySQLIntercept

4.启动transaction-manager

5.在微服务入口的方法添加@GlobalTransacitonal注解(如果是service还需要添加Transactional配合使用)
总之大的事务还是需要依赖原理小的事务构成
```

## demo
```
请查看demo模块的readme使用
```