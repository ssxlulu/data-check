# 项目介绍

项目名称：data-check

项目目的：对数据库源端及目标端的数据进行比对，记录不同点

过程描述：

1. 任务初始化，每张表初始化一个数据比对任务
2. 每个任务将创建两个线程，一个线程负责从源端读取表数据，另一个线程负责从目标端读取表数据，并进行数据对比

# 设计方案

通过JDBC游标批量提取源端表数据，然后通过提取出的数据主键去查询目标库中的表数据，查询后进行数据比对。

# 快速开始

1.下载代码编译生成二进制包：
```
git clone git@github.com:ssxlulu/data-check.git
cd data-check
mvn clean package
```
编译完成后，会在根目录下产生target/data-check-${version}.tar.gz

2.解压缩
```
tar -zxvf data-check-${version}.tar.gz
```
项目目录如下：
```
bin __ start.sh
   |__ start.bat
   |__ stop.sh
conf__ config.json
   |__ logback.xml
lib
   |__ data-check-${version}.jar
    ...
LICENSE
logs__ stdout.log
   |__ table1__ read.log
   |        |__ check.log         
   |__ table2__ read.log
   |        |__ check.log
   |__ ...
README.md
```
注意：针对每张表的数据读取及对比都会生成相应的日志，存放在logs/${table}目录下，方便以表为粒度查看。

3.修改配置文件

主要修改conf/config.json文件：
```
{
  "sourceDatasource": {
    "jdbcUrl": "jdbc:mysql://127.0.0.1:3306/db1?serverTimezone=UTC&useSSL=false",
    "username": "root",
    "password": "123456"
  },
  "destinationDataSource": {
    "jdbcUrl": "jdbc:mysql://127.0.0.1:3306/db2?serverTimezone=UTC&useSSL=false",
    "username": "root",
    "password": "123456"
  },
  "jobConfiguration": {
    "concurrency": 3
  }
}
```
说明：将源端及目标端数据库配置修改为需进行数据对比的数据库配置， `concurrency`参数为并发度控制，控制同时进行的表比对任务数，避免线程数太多造成效率低下。

4.启动

./start.sh

5.查看比对结果

每张表的比对日志都记录在相应的logs/${table}目录下。

check.log示例如下所示：
```
Different records in table item , source record: [1,pyVJLfgxCHQcyA,70.85,VlvBwVYzQAsYylBWmQqswUNvTFTui,3401]
 destination record: [1,pyVJLfgxCHQcyA,70.85,VlvBwVYzQAsYylBWmQqswUNvTFTui,3402]
Different records in table item , source record: [99013,toUdHdTdrnctC,21.55,wdMYNFbfggZpnpquqmoJYrGvb,4269]
 destination record: [99013,toUdHdTdrnctC,21.55,wdMYNFbfggZpnpquqmoJYrGvb,1111]
Different records in table item , source record: [99016,PldJdjYocMIexnBfwyhw,49.92,TRsHrvDqJIuGUWtliBVqQJEvvreUXkLkrOK,8473]
 destination record: [99016,PldJdjYocMIexnBfwyhw,49.92,TRsHrvDqJIuGUWtliBVqQJEvvreUXkLkrOK,6666]
Table item check finished.
```
上述日志表示item表已经比对完成，总共有三处记录不同。

read.log示例如下所示：
```
Table item read finished.
```
表示item表所有记录都有读取完成。

# 约束
1. 待比对的表结构需一致且都必须有主键
2. 目前实际测试过MySQL间的数据比对，从设计来讲，通过JDBC应该同样能够支持其他数据库间的数据比对
