-- 用户表
CREATE TABLE `user`
(
    `id`            bigint        NOT NULL AUTO_INCREMENT COMMENT 'id',
    `user_name`     varchar(256)  NOT NULL DEFAULT "" COMMENT '用户昵称',
    `user_account`  varchar(256)  NOT NULL COMMENT '账号',
    `user_password` varchar(512)  NOT NULL COMMENT '密码',
    `user_avatar`   varchar(1024) NOT NULL DEFAULT "" COMMENT '用户头像',
    `gender`        tinyint       NOT NULL DEFAULT 0 COMMENT '性别 0-未知，1-男，2-女',
    `user_role`     varchar(256)  NOT NULL DEFAULT 'user' COMMENT '用户角色：user / admin',
    `is_delete`     tinyint       NOT NULL DEFAULT 0 COMMENT '是否删除',
    `create_time`   datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uni_user_account` (`user_account`)
) ENGINE = InnoDB COMMENT ='用户';
