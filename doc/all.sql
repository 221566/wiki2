# drop table if exists `test`;
# create table `test`(
#     `id` bigint not null comment 'id',
#     `name` varchar(50) comment '名称',
#     `password` varchar(50) comment '密码',
#     primary key (`id`)
# )engine = innodb default charset = utf8mb4 comment '测试';

# insert into `test` (id, name, password) value (1,'测试','123')

# drop table if exists `demo`;
# create table `demo`(
#                        `id` bigint not null comment 'id',
#                        `name` varchar(50) comment '名称',
#                        primary key (`id`)
# )engine = innodb default charset = utf8mb4 comment '测试';
#
# select * from test

drop table  if exists `ebook`;
create table `ebook`(
                        `id` bigint not null comment 'id',
                        `name` varchar(50) comment '名称',
                        `category1_Id` bigint comment '分类1',
                        `category2_Id` bigint comment '分类2',
                        `description` varchar(200) comment '描述',
                        `cover` varchar(200) comment '封面',
                        `doc_Count` int comment '文档数',
                        `view_Count` int comment '阅读数',
                        `vote_Count` int comment '点赞数',
                        primary key (`id`)
)engine = innodb default charset = utf8mb4 comment = '电子书';
insert into `ebook`(id, name,description) VALUES (1,'Spring Boot 入门教程','零基础入门 Java开发,企业级应用开发最佳首选框架');
insert into `ebook`(id, name,description) VALUES (2,'Vue 入门教程','零基础入门 Vue开发,企业级应用开发最佳首选框架');
insert into `ebook`(id, name,description) VALUES (3,'Mysql 入门教程','零基础入门 Mysql开发,企业级应用开发最佳首选框架');