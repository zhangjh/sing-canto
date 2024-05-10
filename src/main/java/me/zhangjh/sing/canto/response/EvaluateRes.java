package me.zhangjh.sing.canto.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author njhxzhangjihong@126.com
 * @date 17:44 2024/5/10
 * @Description
 */
@Data
public class EvaluateRes {

    @JSONField(name = "NBest")
    private List<Nbest> nbest;

}
