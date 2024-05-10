package me.zhangjh.sing.canto.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author njhxzhangjihong@126.com
 * @date 17:08 2024/5/10
 * @Description
 */
@Data
public class PronunciationAssessment {

    @JSONField(name = "AccuracyScore")
    private Float accuracyScore;

    @JSONField(name = "FluencyScore")
    private Float fluencyScore;

    @JSONField(name = "PronScore")
    private Float pronScore;

    @JSONField(name = "ProsodyScore")
    private Float prosodyScore;

    @JSONField(name = "CompletenessScore")
    private Float completenessScore;

    @JSONField(name = "ErrorType")
    private String errorType;
}
