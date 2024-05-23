package me.zhangjh.sing.canto.response.music;

import lombok.Data;

import java.util.List;

/**
 * @author njhxzhangjihong@126.com
 * @date 10:32 2024/5/19
 * @Description
 */
@Data
public class SearchSongRes {

    private Integer pageNo;
    private Integer pageSize;
    private Integer total;
    private String key;
    private List<SearchSong> list;

}


