package me.zhangjh.sing.canto.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author njhxzhangjihong@126.com
 * @date 17:05 2024/5/10
 * @Description
 */
@Data
public class Nbest {

    @JSONField(name = "Confidence")
    private Double confidence;

    @JSONField(name = "PronunciationAssessment")
    private PronunciationAssessment pronunciationAssessment;

    @JSONField(name = "Words")
    private List<Word> words;
}
