package me.zhangjh.sing.canto.response.music;

import lombok.Data;

import java.util.List;

/**
 * @author njhxzhangjihong@126.com
 * @date 15:33 2024/5/26
 * @Description
 */
@Data
public class SearchSong {

    private String id;

    private String resourceType;

    private String name;

    private List<Singer> singers;

    private List<Album> albums;

    private String lyricUrl;

    private List<ImgItem> imgItems;

}
