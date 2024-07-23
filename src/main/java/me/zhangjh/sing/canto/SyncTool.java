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

        StringBuilder url = new StringBuilder(dbUrlPre + "/create?table=tbl_user");
        for (TblAccount tblAccount : tblAccounts) {
            url.append("&user_id=").append(tblAccount.getId())
                    .append("&ext_type=").append(tblAccount.getExtType())
                    .append("&name=").append(tblAccount.getName())
                    .append("&product_type=").append(tblAccount.getProductType())
                    .append("&create_time=").append(tblAccount.getCreateTime())
                    .append("&modify_time=").append(tblAccount.getModifyTime());
            Object result = HttpClientUtil.get(url.toString(), HEADERS);
            log.info("result: " + result);
            url = new StringBuilder(dbUrlPre + "/create?table=tbl_user");
        }
    }

    public void syncLyric() {
        Wrapper<TblLyric> queryWrapper = new LambdaQueryWrapper<>();
        List<TblLyric> tblLyrics = tblLyricsMapper.selectList(queryWrapper);
        StringBuilder url = new StringBuilder(dbUrlPre + "/create?table=tbl_lyric");
        for (TblLyric tblLyric : tblLyrics) {
            url.append("&creator=").append(tblLyric.getCreator())
                    .append("&gender=").append(tblLyric.getGender())
                    .append("&song=").append(tblLyric.getSong())
                    .append("&singer=").append(tblLyric.getSinger())
                    .append("&cover=").append(tblLyric.getCover())
                    .append("&lyrics=").append(tblLyric.getLyrics())
                    .append("&create_time=").append(tblLyric.getCreateTime())
                    .append("&modify_time=").append(tblLyric.getModifyTime());
            Object result = HttpClientUtil.get(url.toString(), HEADERS);
            log.info("result: " + result);
            url = new StringBuilder(dbUrlPre + "/create?table=tbl_lyric");
        }
    }

    public void syncPracticed() {
        Wrapper<TblPracticed> queryWrapper = new LambdaQueryWrapper<>();
        List<TblPracticed> tblPracticeds = tblPracticedMapper.selectList(queryWrapper);
        StringBuilder url = new StringBuilder(dbUrlPre + "/create?table=tbl_practiced");
        for (TblPracticed tblPracticed : tblPracticeds) {
            url.append("&song_id=").append(tblPracticed.getSongId())
                    .append("&user=").append(tblPracticed.getUser())
                    .append("&create_time=").append(tblPracticed.getCreateTime())
                    .append("&modify_time=").append(tblPracticed.getModifyTime());

            Object result = HttpClientUtil.get(url.toString(), HEADERS);
            log.info("result: " + result);
            url = new StringBuilder(dbUrlPre + "/create?table=tbl_practiced");
        }
    }
}
