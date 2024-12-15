# MySQL JDBC 宿舍管理系统

（可能的bug（TODO）：用户名和密码对不上，暂时可删除user和path文件解决；导入功能不太对🤔；导入的提示窗口全为success）
- 可以去release 下载，解压后直接运行即可。
- 运行前请先确保计算机上有MySQL数据库或其他数据库。

## 项目描述

本项目为大数据课程作业，旨在通过 Java 与 MySQL 数据库交互实现宿舍管理功能。

## 运行环境要求

- Java Runtime Environment (JRE) 已内置在项目文件中，无需额外安装。

## 如何运行程序

1. 解压下载的压缩文件。
2. 确保 `jre` 文件夹与 `.exe` 可执行文件位于同一目录下，避免移动或分离这些文件。
3. 建议将整个解压后的文件夹放置于桌面，因为程序生成的文件默认保存位置为桌面。

## 注意事项

- 请勿将 `jre` 文件夹与 `.exe` 文件分开存放，否则可能导致程序无法正常启动。
- 程序生成的所有文件将保存在桌面上，请确保桌面有足够的权限进行读写操作。

# 数据库结构

## 表格设计

[具体构建可看此类（代码）](https://github.com/MuxiaoWF/BigDataBigHomework-SCUT23-3/blob/f79554f43d19f21d518672fd126c8a7fc4c1edb7/src/main/java/com/muxiao/system/Main.java#L94)

1. students (学生信息)

- student_id (CHAR, 主键): 学号
- name (VARCHAR): 学生姓名
- gender (ENUM('男', '女')): 性别
- major (VARCHAR): 专业
- class (VARCHAR): 班级
- contact (VARCHAR): 联系方式
- email (VARCHAR): 邮箱地址

2. dormitories (宿舍信息)

- dormitory_id (INT, 主键): 宿舍ID
- building_name (VARCHAR): 楼栋名称
- room_number (VARCHAR): 房间号
- capacity (TINYINT): 容量（床位数）
- status (ENUM('占用', '空')): 状态（已入住/空闲）

3. residences (入住记录)

- residence_id (INT, 主键, 自增): 入住记录ID
- student_id (CHAR, 外键): 学号
- dormitory_id (INT, 外键): 宿舍ID
- move_in_date (DATE): 入住日期
- move_out_date (DATE, 可为空): 退宿日期

4. repairs (维修请求)

- repair_id (INT, 主键, 自增): 维修请求ID
- student_id (CHAR, 外键): 学号
- dormitory_id (INT, 外键): 宿舍ID
- request_date (DATE): 请求日期
- description (TEXT): 描述
- status (ENUM('pending', 'in_progress', 'completed')): 状态（待处理/处理中/已完成）

5. violations (违规记录)

- violation_id (INT, 主键, 自增): 违规记录ID
- student_id (CHAR, 外键): 学号
- dormitory_id (INT, 外键): 宿舍ID
- date (DATE): 记录日期
- type (VARCHAR): 违规类型
- details (TEXT): 详细描述

6. fees (费用记录)

- fee_id (INT, 主键, 自增): 费用记录ID
- student_id (CHAR, 外键): 学号
- amount (DECIMAL): 金额
- payment_date (DATE): 缴费日期
- description (VARCHAR): 费用描述

## 关系说明

- students 和 dormitories 通过 residences 表连接，表示学生和宿舍之间的入住关系。
- repairs, violations, fees 表都关联到 students 和 dormitories 表，记录与具体学生和宿舍相关的维修请求、违规记录及费用信息。

## 程序运行截图

### 开始使用：

- 设置MySQL连接信息:（首次使用需要设置）

![1.png](pic/1.png)

- 选择界面：可以选择不登录直接查询

![2.png](pic/2.png)

### 查询界面：

- 可输入查询条件查询或直接展示整个表的信息：

![3.png](pic/3.png)

- 同时可以导出为excel文件或直接为sql文件、以支持的html和xml文件： ——运用apache poi提供excel支持

![4.png](pic/4.png)

- 导出的文件会放在桌面的一个叫宿舍管理系统的文件夹内，因此建议直接将解压后的文件夹放在桌面。

![5.png](pic/5.png)

![13.png](pic/13.png)

### 登录界面：

- 输入用户名和密码即可登录，同时放上注册按钮（懒得加用户个数限制了）：
- 注册用户名时，需要输入管理员的账号和密码并登录，否则无法注册；管理员登录成功后可在添加新用户选项里注册新用户

![6.png](pic/6.png)

- 由于文件读取的原因，不能设置相同的名字！

![7.png](pic/7.png)

- 登录成功后，可以进行增删改查操作：

![8.png](pic/8.png)

### 导入记录：

- 导入记录时，仅支持本软件备份的文件，同时需要delete和insert权限

### 增加记录：

- 增加记录时，需要输入所有标记为【NOT NULL】的信息
  --输入错误信息会弹出错误提示框，并提示错误信息

![9.png](pic/9.png)

### 修改记录值：

- 修改记录调用了查找记录的方法，因此可以先查找记录，选中条目直接点击修改即可

![10.png](pic/10.png)

### 修改列属性：

- 修改列属性需要输入MySQL对应的列属性

![11.png](pic/11.png)

### 删除记录：

- 删除记录也调用了查找记录的方法，因此可以先查找记录再删除

![12.png](pic/12.png)

### 查找记录：

- 与前面的一样，不再写出。

## 文件说明：

程序运行时会产生以下几种文件（均在桌面的宿舍管理系统目录里）：

- path：保存了加密后的数据库地址，用户名，密码
- dormitory.sql：你选择导出的sql文件
- dormitory.xml: 你选择导出的xml文件
- dormitory.html：你选择导出的html文件
- （导出的）数据库原始文件.xlsx：你选择导出的excel文件
- user：保存了此程序注册的用户名以及密码（加密后）