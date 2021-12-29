# d10-scheduler
借鉴[elastic-job](https://shardingsphere.apache.org/elasticjob/) 的弹性调度与[Quartz](http://www.quartz-scheduler.org/) 的调度策略构建了基于DAG的分布式任务调度系统。对标[Apache DolphinScheduler](https://dolphinscheduler.apache.org/zh-cn/index.html) </br>
简单易用是本项目的目标、初步立一个小flag 2022年100个star！！


整体架构如下：
![](./doc/imgs/framework.png)

特性如下：
- 弹性调度
```markdown
调度器高可用，任务会归属于多个调度器中的一个。当发生故障，会自动的转移到其他可用的调度器中
```

- 独特的依赖体系

```markdown

```

- 独特的依赖体系

```markdown

```

- 提供时间参数、让用户可填入写一个sql即可用于所有的版本
```markdown

```

- admin console
```markdown
todo
```
