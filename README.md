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

#添加配置表示开启分布式事务
rm.global.tx.enable=true
#配置tm的服务名称
remoting.tm.address=tm

3.启动transaction-manager

4.在微服务入口的方法添加@GlobalTransacitonal注解(如果是service还需要添加Transactional配合使用)
总之大的事务还是需要依赖原理小的事务构成
```