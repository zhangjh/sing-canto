use sing_canto;

DROP TABLE IF EXISTS tbl_account;
CREATE TABLE tbl_account
(
    `id`          BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`  TINYINT(1) DEFAULT 0  NOT NULL COMMENT '逻辑删除',
    `name`   VARCHAR(64) NULL COMMENT '昵称',
    `avatar`      TEXT NULL COMMENT '头像,url或base64',
    `ext_id`       VARCHAR(64) NULL COMMENT '外部系统id',
    `ext_type`     TINYINT(1) NOT NULL COMMENT '外部系统类型',
    `mobile`      VARCHAR(11) NULL COMMENT '手机',
    `product_type` VARCHAR(10) NOT NULL COMMENT '用户所属的产品类型',
    PRIMARY KEY (id)
) COMMENT = '账号信息';

CREATE UNIQUE INDEX uq_extType_extId ON tbl_account(ext_type, ext_id, product_type, is_deleted);
