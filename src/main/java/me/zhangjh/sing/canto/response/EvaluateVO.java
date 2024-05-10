package me.zhangjh.sing.canto.response;

import lombok.Data;

/**
 * @author njhxzhangjihong@126.com
 * @date 18:06 2024/5/10
 * @Description
 */
@Data
public class EvaluateVO {

    // 音准
    private Double accuracy;

    // 流畅度
    private Double fluency;

    // 整体质量
    private Double pronScore;

    // 完整度
    private Double complete;

    public String getStars() {
        if(pronScore == null || pronScore < 0 || pronScore > 100) {
            return null;
        }
        String[] stars = {"1", "2", "3", "4", "5"};
        int index = (int) Math.ceil(pronScore / 20.0f) - 1;
        return stars[index];
    }
}
