-- 用户表
CREATE TABLE `user`
(
    `id`            bigint        NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_name`     varchar(256)  NOT NULL DEFAULT '' COMMENT '用户昵称',
    `user_account`  varchar(256)  NOT NULL COMMENT '账号',
    `user_password` varchar(512)  NOT NULL COMMENT '密码',
    `user_avatar`   varchar(1024) NOT NULL DEFAULT '' COMMENT '用户头像',
    `gender`        tinyint       NOT NULL DEFAULT 0 COMMENT '性别 0-未知，1-男，2-女',
    `user_role`     varchar(256)  NOT NULL DEFAULT 'user' COMMENT '用户角色：user / admin',
    `is_delete`     tinyint       NOT NULL DEFAULT 0 COMMENT '是否删除',
    `create_time`   datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_user_account` (`user_account`)
) ENGINE = InnoDB COMMENT ='用户';

-- 帖子表
create table if not exists post
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(512)                       NOT NULL comment '标题',
    content     text                               null comment '内容',
    tags        varchar(1024)                      NOT NULL DEFAULT '' comment '标签列表（json 数组）',
    user_id     bigint                             not null comment '创建用户 id',
    is_delete   tinyint  default 0                 not null comment '是否删除',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_userId (user_id)
) comment '帖子' engine = InnoDB;
