package me.zhangjh.sing.canto.response;

import lombok.Data;

/**
 * @author njhxzhangjihong@126.com
 * @date 22:47 2024/5/16
 * @Description
 */
@Data
public class PracticedVO {

    private Long songId;

    private String songName;

    private String singer;

    private String cover;

    private String user;
}
