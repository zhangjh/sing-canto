package me.zhangjh.sing.canto.response;

import lombok.Data;

/**
 * @author njhxzhangjihong@126.com
 * @date 10:09 2024/5/11
 * @Description
 */
@Data
public class WordEvaluateVO {

    private String word;

    private double accuracy;
}
