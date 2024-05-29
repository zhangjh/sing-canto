package me.zhangjh.sing.canto.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author njhxzhangjihong@126.com
 * @date 13:36 2024/5/11
 * @Description
 */
@Data
@TableName("tbl_lyrics")
public class TblLyric extends BaseEntity {

    // 创建人
    private String creator;

    // 歌名
    private String song;

    // 歌手
    private String singer;

    // 性别: 0-男，1-女
    private int gender = 0;

    // 封面图
    private String cover;

    // 歌词，原始未断句切分的
    private String lyrics;
}
