package me.zhangjh.sing.canto.controller.request;

import lombok.Data;

/**
 * @author njhxzhangjihong@126.com
 * @date 14:41 2024/5/11
 * @Description
 */
@Data
public class QueryRequest {

    private String singer;

    private Integer gender;

    private String song;

    private int pageIndex = 1;

    private int pageSize = 10;
}
