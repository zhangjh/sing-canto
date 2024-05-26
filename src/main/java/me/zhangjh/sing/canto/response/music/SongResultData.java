package me.zhangjh.sing.canto.response.music;

import lombok.Data;

import java.util.List;

/**
 * @author njhxzhangjihong@126.com
 * @date 10:26 2024/5/19
 * @Description
 */
@Data
public class SongResultData {

    private String totalCount;

    private String resultType;

    private List<SearchSong> result;
}
