# Native ZooKeeper

graalvm编译zookeeper，提供二进制版本的zookeeper执行文件

## Quick Start

```bash
# 编译, 提前准备好GraalVM环境
./build-native

# 启动服务, 如果不指定配置文件则默认使用 ./conf/zoo.cfg
./zookeeper ./conf/zoo.cfg

# 进入cli命令行（此功能不完美，TAP补全失效）
./zookeeper --cli
```
