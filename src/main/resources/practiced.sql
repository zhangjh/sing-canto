/*!40101 SET NAMES utf8 */;

use sing_canto;

drop table if exists tbl_practiced;

create table tbl_practiced
(
    id            bigint(64) not null auto_increment comment '主键id',
    `create_time` DATETIME     NOT NULL default current_timestamp COMMENT '创建时间',
    `modify_time` DATETIME     NOT NULL default current_timestamp on update current_timestamp COMMENT '修改时间',
    `is_deleted`  tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `song_id`    bigint(64) not null comment '歌曲id',
    `user`      varchar(100) not null comment '用户',
    primary key (id)
) character set utf8mb4 collate utf8mb4_unicode_ci comment = '歌词';

create index idx_user_song on tbl_practiced (user, song_id);

insert into tbl_practiced(song_id, user)
    values (1,'zhangjh');
insert into tbl_practiced(song_id, user)
    values (2, 'zhangjh');
insert into tbl_practiced(song_id, user)
    values (3, 'zhangjh');