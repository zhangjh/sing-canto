package me.zhangjh.sing.canto.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.zhangjh.share.response.PageResponse;
import me.zhangjh.share.response.Response;
import me.zhangjh.share.util.HttpClientUtil;
import me.zhangjh.share.util.HttpRequest;
import me.zhangjh.sing.canto.dao.entity.TblLyric;
import me.zhangjh.sing.canto.dao.entity.TblPracticed;
import me.zhangjh.sing.canto.response.EvaluateVO;
import me.zhangjh.sing.canto.response.PracticedVO;
import me.zhangjh.sing.canto.response.SearchLyricVO;
import me.zhangjh.sing.canto.response.music.*;
import me.zhangjh.sing.canto.service.ITblLyricsService;
import me.zhangjh.sing.canto.service.ITblPracticedService;
import me.zhangjh.sing.canto.service.TtsService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.swing.UIManager.get;

/**
 * @author njhxzhangjihong@126.com
 * @date 14:36 2024/5/11
 * @Description
 */
@RestController
@RequestMapping("/canto/")
@Slf4j
public class LyricController {

    @Value("${music.api.pre}")
    private String musicApiPre;

    @Value("${cover.api.pre}")
    private String coverApiPre;

    @Autowired
    private ITblLyricsService tblLyricsService;

    @Autowired
    private ITblPracticedService tblPracticedService;

    @Autowired
    private TtsService ttsService;

    @GetMapping("/lyric/query")
    public PageResponse<TblLyric> queryLyric(@RequestParam(required = false) String song,
                                             @RequestParam(required = false) String singer,
                                             @RequestParam(required = false) Integer gender) {
        QueryWrapper<TblLyric> queryWrapper = new QueryWrapper<>();
        int pageIndex = 1;
        int pageSize = 10;

        if(StringUtils.isNotEmpty(song)) {
            queryWrapper.eq("song", song);
        }
        if(StringUtils.isNotEmpty(singer)) {
            queryWrapper.eq("singer", singer);
        }
        if(gender != null) {
            queryWrapper.eq("gender", gender);
        }

        Page<TblLyric> page = new Page<>(pageIndex, pageSize);
        Page<TblLyric> lyricPage = tblLyricsService.page(page, queryWrapper);
        return PageResponse.success(lyricPage.getRecords(), lyricPage.getTotal());
    }

    @PostMapping("/lyric/savePracticed")
    public Response<Void> savePracticed(@RequestBody TblPracticed tblPracticed) {
        // 幂等，(user,songId)不重复插入
        QueryWrapper<TblPracticed> query = new QueryWrapper<>();
        query.eq("user", tblPracticed.getUser()).eq("song_id", tblPracticed.getSongId());
        TblPracticed practiced = tblPracticedService.getOne(query);
        if(practiced != null) {
            log.info("already saved, user: {}, song: {}", tblPracticed.getUser(), tblPracticed.getSongId());
            return Response.success(null);
        }
        tblPracticedService.save(tblPracticed);
        return Response.success(null);
    }

    @GetMapping("/lyric/getPracticed")
    public PageResponse<PracticedVO> getPracticed(@RequestParam String userId,
                                                  @RequestParam(required = false) Integer pageIndex) {
        Assert.isTrue(StringUtils.isNotEmpty(userId), "userId为空");
        if(pageIndex == null) {
            pageIndex = 1;
        }
        int pageSize = 5;
        Page<TblPracticed> page = new Page<>(pageIndex, pageSize);
        Page<TblPracticed> practicedPage = tblPracticedService.page(page, new QueryWrapper<TblPracticed>().eq("user", userId));
        List<PracticedVO> practicedVOS = new ArrayList<>();
        List<Long> songIds = practicedPage.getRecords().stream().map(TblPracticed::getSongId).toList();
        if(CollectionUtils.isEmpty(songIds)) {
            return PageResponse.success(practicedVOS, 0L);
        }
        List<TblLyric> tblLyrics = tblLyricsService.listByIds(songIds);
        for (TblLyric tblLyric : tblLyrics) {
            PracticedVO practicedVO = new PracticedVO();
            practicedVO.setSongId(tblLyric.getId());
            practicedVO.setSongName(tblLyric.getSong());
            practicedVO.setSinger(tblLyric.getSinger());
            practicedVO.setCover(tblLyric.getCover());
            practicedVO.setUser(userId);
            practicedVOS.add(practicedVO);
        }
        return PageResponse.success(practicedVOS, practicedPage.getTotal());
    }

    @GetMapping("/voice/play")
    public Response<Void> playVoice(@RequestParam String text,
                                    @RequestParam(required = false) String rate) {
        if(StringUtils.isEmpty(rate)) {
            rate = "slow";
        }
        ttsService.playContent(text, rate);
        return Response.success(null);
    }

    @GetMapping("/voice/evaluate")
    public Response<EvaluateVO> evaluateVoice(@RequestParam String text) {
        EvaluateVO evaluate = ttsService.evaluate(text);
        return Response.success(evaluate);
    }

    @PostMapping("/voice/evaluateFile")
    public Response<EvaluateVO> evaluateFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("text") String text) {
        Assert.isTrue(StringUtils.isNotEmpty(text), "参考文本为空");
        EvaluateVO evaluateVO = ttsService.evaluateFile(file, text);
        return Response.success(evaluateVO);
    }

    @SneakyThrows
    @GetMapping("/lyric/search")
    public Response<SearchLyricVO> searchLyric(@RequestParam String song,
                                                     @RequestParam(required = false) String singer,
                                                     @RequestParam(required = false) String album) {
        // 查找歌词
        String lyricUrl = musicApiPre + "/lyrics/single?title=" + song;
        String coverUrl = coverApiPre + "/cover/album?title=" + song;
        if(StringUtils.isNotEmpty(singer)) {
            lyricUrl += "&artist=" + singer;
            coverUrl += "&artist=" + singer;
        }
        if(StringUtils.isNotEmpty(album)) {
            lyricUrl += "&album=" + album;
            coverUrl += "&album=" + album;
        }
        Object lyricObj = HttpClientUtil.get(lyricUrl);
        String lyric = lyricObj.toString();
        String handleLyricContent = handleLyricContent(lyric);
        int length = handleLyricContent.length();
        int cutLength = Math.min(length, 100);

        // 查找封面
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .followRedirects(false)
                .build();
        Request request = new Request.Builder()
                .url(coverUrl)
                .build();
        String cover = "";
        try (okhttp3.Response response = okHttpClient.newCall(request).execute()) {
            if (response.isRedirect()) {
                // 获取重定向后的 URL
                cover = response.header("Location");
                System.out.println("Redirected URL: " + cover);
            } else {
                System.out.println("No redirection, response code: " + response.code());
            }
        }
        SearchLyricVO searchLyricVO = new SearchLyricVO();
        searchLyricVO.setSong(song);
        searchLyricVO.setSinger(singer);
        searchLyricVO.setLyric(handleLyricContent.substring(0, cutLength) + "...");
        searchLyricVO.setCover(cover);
        return Response.success(searchLyricVO);
    }

    private String handleLyricContent(String lyric) {
        // 定义正则表达式模式来匹配时间戳
        String regex = "\\[.*?\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(lyric);

        // 用空字符串替换所有匹配的时间戳
        String cleanedLyrics = matcher.replaceAll("\n");

        // 移除多余的空行
        cleanedLyrics = cleanedLyrics.replaceAll("(?m)^[ \t]*\r?\n", "");

        return cleanedLyrics;
    }
}
