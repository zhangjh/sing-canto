package me.zhangjh.sing.canto.dao.entity;

import lombok.Data;

import java.util.List;

/**
 * @author njhxzhangjihong@126.com
 * @date 13:36 2024/5/11
 * @Description
 */
@Data
public class TblLyric {

    // 歌名
    private String song;

    // 歌手
    private String singer;

    // 歌曲配图
    private String pic;

    // 歌词，原始未断句切分的
    private String oriLyric;

    // 歌词，已断句切分，每句不超过20字
    private List<String> lyrics;
}
