package me.zhangjh.sing.canto.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author njhxzhangjihong@126.com
 * @date 17:11 2024/5/10
 * @Description
 */
@Data
public class Word {

    @JSONField(name = "PronunciationAssessment")
    private PronunciationAssessment pronunciationAssessment;

    @JSONField(name = "Word")
    private String word;
}
