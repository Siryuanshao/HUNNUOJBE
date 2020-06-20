# hunnuoj

### 项目打包构建

```
mvn clean package
```

### 项目配置文件

```
后台服务器资源路径配置: (backend.properties)
avatar_absolute_path: 头像资源上传写入的磁盘绝对路径
avatar_relative_path: 头像资源用于回显写入数据库的相对路径
image_absolute_path: 图片资源上传写入的磁盘绝对路径
image_relative_path: 图片资源用于回显写入数据库的相对路径
secure_token: 用于和JudgeServer交互所用的鉴权token, 需要与JudgeServer配置一致

mysql数据库配置: (db.properties)

JudgeServer配置: (judged.properties)
judge_host: 判题服务ip
judge_port: 判题服务端口
problemChannel: 普通题目push到redis列表的名称
contestChannel: 比赛题目push到redis列表的名称
(需要与JudgerServer配置中列表名一致)

log4j日志配置: (log4j.properties)

mail配置: (mail.properties)

redis配置: (redis.properties)

```



