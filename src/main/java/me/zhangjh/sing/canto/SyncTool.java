package me.zhangjh.sing.canto;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.zhangjh.share.user.service.TblAccount;
import me.zhangjh.share.user.service.mapper.WxAccountMapper;
import me.zhangjh.share.util.HttpClientUtil;
import me.zhangjh.share.util.HttpRequest;
import me.zhangjh.sing.canto.dao.entity.TblLyric;
import me.zhangjh.sing.canto.dao.entity.TblPracticed;
import me.zhangjh.sing.canto.dao.mapper.TblLyricsMapper;
import me.zhangjh.sing.canto.dao.mapper.TblPracticedMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Slf4j
public class SyncTool {

    @Autowired
    private WxAccountMapper wxAccountMapper;

    @Autowired
    private TblLyricsMapper tblLyricsMapper;

    @Autowired
    private TblPracticedMapper tblPracticedMapper;

    private static String dbUrlPre = "https://wx1.zhangjh.cn";
//    @PostConstruct
//    public void init() {
//        syncUser();
////        syncLyric();
////        syncPracticed();
//    }

    public void syncUser() {
        Wrapper<TblAccount> queryWrapper = new LambdaQueryWrapper<>();
        List<TblAccount> tblAccounts = wxAccountMapper.selectList(queryWrapper);

        String url = dbUrlPre + "/wx/saveWxUser";
        HttpRequest httpRequest = new HttpRequest(url);
        for (TblAccount tblAccount : tblAccounts) {
            httpRequest.setMethod("POST");
            JSONObject data = new JSONObject();
            data.put("user_id", tblAccount.getId());
            data.put("ext_type", tblAccount.getExtType());
            data.put("name", tblAccount.getName());
            data.put("product_type", tblAccount.getProductType());

            httpRequest.setReqData(JSONObject.toJSONString(data));
            Object result = HttpClientUtil.sendNormally(httpRequest);
            log.info("result: " + result);
        }
    }

    public void syncLyric() {
        Wrapper<TblLyric> queryWrapper = new LambdaQueryWrapper<>();
        List<TblLyric> tblLyrics = tblLyricsMapper.selectList(queryWrapper);
    }

    public void syncPracticed() {
        Wrapper<TblPracticed> queryWrapper = new LambdaQueryWrapper<>();
        List<TblPracticed> tblPracticeds = tblPracticedMapper.selectList(queryWrapper);

    }
}
