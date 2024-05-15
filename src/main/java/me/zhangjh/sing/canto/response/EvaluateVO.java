package me.zhangjh.sing.canto.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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

    private List<WordEvaluateVO> wordEvaluates = new ArrayList<>();

    public String getStars() {
        double score = 0d;
        for (WordEvaluateVO wordEvaluate : wordEvaluates) {
            score += wordEvaluate.getAccuracy();
        }
        String[] stars = {"1", "2", "3", "4", "5"};
        double average = (score / (wordEvaluates.size() * 100));
        int index = (int) Math.floor(average / 0.2d) - 1;
        if(index < 0) {
            return "0";
        }
        return stars[index];
    }
}
