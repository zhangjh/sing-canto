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
    `song`        varchar(100) not null comment '歌曲名',
    `singer`      varchar(100) not null comment '歌手名',
    `gender`    tinyint(1) not null comment '性别',
    `cover`     varchar(255) null default 'https://img.zcool.cn/community/01896b597ac380a8012193a3db4f2d.png' comment '封面',
    `lyrics`      text         not null comment '歌词',
    primary key (id)
) character set utf8mb4 collate utf8mb4_unicode_ci comment = '歌词';

create index idx_song_singer on tbl_lyrics (song, singer);

insert into tbl_lyrics (song, singer, gender, cover, lyrics)
values ('粤语残片', '陈奕迅', '0', 'https://p1.music.126.net/jzNxBp5DCER2_aKGsXeRww==/109951167435823724.jpg', '乔迁那日打扫废物,家居仿似开战,无意发现当天,穿返学夏季衬衣,奇怪却是茄汁污垢,渗在这衬衣布章外边,极其大意,为何如此,想那日初次约会,心惊手震胆颤,忙里泄露各种的丑态像丧尸,而尴尬是快餐厅里,我误把浆汁四周乱溅,骇人场面相当讽刺,你及时递上餐纸,去为我清洗衬衣,刹那间身体的触碰大件事,今天看这段历史,像褪色午夜残片,笑话情节此刻变窝心故事,现时大了那种心跳难重演,极灿烂时光,一去难再遇上一次,怎努力都想不起,初恋怎会改变,情侣数字我屈指一算大概知,奇怪却是每恋一次,震撼总逐渐变得越浅,令人动心只得那次,有没捱坏了身子,会为哪位披嫁衣,你有否挂念当天这丑小子,今天看那段历史,像褪色午夜残片,笑话情节此刻变窝心故事,现时大了那种心跳难重演,极灿烂时光,一去难再遇上一次,在混乱杂物当中找到,失去的往事,但现在杂物与我,举家将会搬迁,让纪念成历史,想想那旧时日子,像褪色午夜残片,任何情节今天多一种意义,现时大了那种心跳难重演,极爆裂场面,想再遇确实靠天意');

insert into tbl_lyrics (song, singer, gender, cover,  lyrics)
values ('十七岁', '刘德华', '0', 'https://img3.kuwo.cn/star/starheads/500/65/42/2631374422.jpg', '17岁 - 刘德华,词：刘德华 徐继宗,曲：徐继宗,十七岁那日不要脸 参加了挑战,明星也有训练班短短一年太新鲜,记得四哥发哥都已见过面,后来荣升主角太突然,廿九岁颁奖的晚宴 fans太疯癫,来听我唱段情歌一曲歌词太经典,我的震 音 假 音 早已太熟练,然而情歌总唱不厌,喜欢我 别遮脸 任由途人发现,尽管唱用心把这情绪歌 中染,唱 情 歌 齐齐来一遍,无时无刻都记住掌声 响遍天,来唱 情 歌 由从头再一遍,如情浓有点泪流难避免,音阶起跌拍子改变,每首歌 是每张脸,喜欢我 别遮脸 任由途人发现,尽管唱用心把这情绪歌声中喧染,唱 情 歌 齐齐来一遍,无时无刻都记住掌声 响遍天,来唱 情 歌 由从头再一遍,如情浓有点泪流难避免,音阶起跌拍子改变,年月变但,我未变,唱情歌齐齐来一遍无时无刻都记住,掌声响遍天,来唱 情 歌 由从头再一遍,如情浓有点泪流,难避免,音阶起跌拍子改变,每首歌 是每张脸,如今我四十看从前 沙哑了声线,回忆我冀望那掌声都依然到今天,那首潮 水,忘情水,不再 经典,仍长埋你的心中从未变');

insert into tbl_lyrics (song, singer, gender, cover, lyrics)
values ('光辉岁月', 'Beyond', '0', 'https://p1.music.126.net/JOJvZc_7SqQjKf8TktQ_bw==/29686813951246.jpg', '钟声响起归家的讯号,在他生命里,仿佛带点唏嘘,黑色肌肤给他的意义,是一生奉献 肤色斗争中,年月把拥有变做失去,疲倦的双眼带着期望,今天只有残留的躯壳,迎接光辉岁月,风雨中抱紧自由,一生经过彷徨的挣扎,自信可改变未来,问谁又能做到,可否不分肤色的界限,愿这土地里,不分你我高低,缤纷色彩闪出的美丽,是因它没有,分开每种色彩,年月把拥有变做失去,疲倦的双眼带着期望,今天只有残留的躯壳,迎接光辉岁月,风雨中抱紧自由,一生经过彷徨的挣扎,自信可改变未来,问谁又能做到,今天只有残留的躯壳,迎接光辉岁月,风雨中抱紧自由,一生经过彷徨的挣扎,自信可改变未来,问谁又能做到,Woo,Ah,今天只有残留的躯壳,迎接光辉岁月,风雨中抱紧自由,一生经过彷徨的挣扎,自信可改变未来,问谁又能做到,Woo,Ah,今天只有残留的躯壳,迎接光辉岁月,风雨中抱紧自由,一生经过彷徨的挣扎,自信可改变未来,');
