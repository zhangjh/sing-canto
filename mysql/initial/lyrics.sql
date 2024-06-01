/*!40101 SET NAMES utf8 */;

drop database if exists sing_canto;
create database sing_canto character set utf8mb4 collate utf8mb4_unicode_ci;

use sing_canto;

drop table if exists tbl_lyrics;
create table tbl_lyrics
(
    id            bigint(64) not null auto_increment comment '主键id',
    `create_time` DATETIME     NOT NULL default current_timestamp COMMENT '创建时间',
    `modify_time` DATETIME     NOT NULL default current_timestamp on update current_timestamp COMMENT '修改时间',
    `is_deleted`  tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `creator`     varchar(100) not null comment '创建人',
    `song`        varchar(100) not null comment '歌曲名',
    `singer`      varchar(100) not null comment '歌手名',
    `gender`    tinyint(1) not null comment '性别',
    `cover`     varchar(255) null default 'https://img.zcool.cn/community/01896b597ac380a8012193a3db4f2d.png' comment '封面',
    `lyrics`      text         not null comment '歌词',
    primary key (id)
) character set utf8mb4 collate utf8mb4_unicode_ci comment = '歌词';

create index idx_song_singer on tbl_lyrics (song, singer);
