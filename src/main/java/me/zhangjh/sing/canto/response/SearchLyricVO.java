package me.zhangjh.sing.canto.response;

import lombok.Data;

/**
 * @author njhxzhangjihong@126.com
 * @date 10:48 2024/5/19
 * @Description
 */
@Data
public class SearchLyricVO {

    private String song;

    private String singer;

    private String cover;

    private String lyric;
}
