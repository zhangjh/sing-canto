package me.zhangjh.sing.canto;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSON;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SyncTool {

    @Autowired
    private WxAccountMapper wxAccountMapper;

    @Autowired
    private TblLyricsMapper tblLyricsMapper;

    @Autowired
    private TblPracticedMapper tblPracticedMapper;

    private static String dbUrlPre = "https://db.zhangjh.cn";

    private static final Map<String, String> HEADERS = new HashMap<>();
    {
        HEADERS.put("token", "weirdSheep");
    }

//    @PostConstruct
//    public void init() {
//        syncUser();
////        syncLyric();
////        syncPracticed();
//    }

    public void syncUser() {
        Wrapper<TblAccount> queryWrapper = new LambdaQueryWrapper<>();
        List<TblAccount> tblAccounts = wxAccountMapper.selectList(queryWrapper);

        String url = dbUrlPre + "/create?table=tbl_user";
        for (TblAccount tblAccount : tblAccounts) {
            JSONObject data = new JSONObject();
            data.put("ext_id", tblAccount.getExtId());
            data.put("ext_type", tblAccount.getExtType());
            data.put("name", tblAccount.getName());
            data.put("product_type", tblAccount.getProductType());
            data.put("create_time", tblAccount.getCreateTime());
            data.put("modify_time", tblAccount.getModifyTime());
            log.info("url: {}, data: {}", url, data);
            HttpRequest request = new HttpRequest(url);
            request.setMethod("POST");
            request.setBizHeaderMap(HEADERS);
            request.setReqData(JSON.toJSONString(data));
            log.info("request: {}", request.getReqData());
            Object result = HttpClientUtil.sendNormally(request);
            log.info("result: " + result);
        }
    }

    public void syncLyric() {
        Wrapper<TblLyric> queryWrapper = new LambdaQueryWrapper<>();
        List<TblLyric> tblLyrics = tblLyricsMapper.selectList(queryWrapper);
        String url = dbUrlPre + "/create?table=tbl_lyric";
        for (TblLyric tblLyric : tblLyrics) {
            JSONObject data = new JSONObject();
            data.put("creator", tblLyric.getCreator());
            data.put("gender", tblLyric.getGender());
            data.put("song", tblLyric.getSong());
            data.put("singer", tblLyric.getSinger());
            data.put("cover", tblLyric.getCover());
            data.put("lyrics", tblLyric.getLyrics());
            data.put("create_time", tblLyric.getCreateTime());
            data.put("modify_time", tblLyric.getModifyTime());
            log.info("url: {}, data: {}", url, data);
            HttpRequest request = new HttpRequest(url);
            request.setMethod("POST");
            request.setBizHeaderMap(HEADERS);
            request.setReqData(JSON.toJSONString(data));
            Object result = HttpClientUtil.sendNormally(request);
            log.info("result: " + result);
        }
    }

    public void syncPracticed() {
        Wrapper<TblPracticed> queryWrapper = new LambdaQueryWrapper<>();
        List<TblPracticed> tblPracticeds = tblPracticedMapper.selectList(queryWrapper);
        String url = dbUrlPre + "/create?table=tbl_practiced";
        for (TblPracticed tblPracticed : tblPracticeds) {
            JSONObject data = new JSONObject();
            data.put("song_id", tblPracticed.getSongId());
            data.put("user", tblPracticed.getUser());
            data.put("create_time", tblPracticed.getCreateTime());
            data.put("modify_time", tblPracticed.getModifyTime());
            HttpRequest request = new HttpRequest(url);
            request.setMethod("POST");
            request.setBizHeaderMap(HEADERS);
            request.setReqData(JSON.toJSONString(data));

            Object result = HttpClientUtil.sendNormally(request);
            log.info("result: " + result);
        }
    }
}
