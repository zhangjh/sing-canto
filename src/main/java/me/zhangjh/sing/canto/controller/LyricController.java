package me.zhangjh.sing.canto.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.zhangjh.share.response.PageResponse;
import me.zhangjh.share.response.Response;
import me.zhangjh.share.util.HttpClientUtil;
import me.zhangjh.sing.canto.dao.entity.TblLyric;
import me.zhangjh.sing.canto.dao.entity.TblPracticed;
import me.zhangjh.sing.canto.response.EvaluateVO;
import me.zhangjh.sing.canto.response.PracticedVO;
import me.zhangjh.sing.canto.response.SearchLyricVO;
import me.zhangjh.sing.canto.response.music.*;
import me.zhangjh.sing.canto.service.ITblLyricsService;
import me.zhangjh.sing.canto.service.ITblPracticedService;
import me.zhangjh.sing.canto.service.TtsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Value("${music.api.condition}")
    private String musicApiCondition;

    @Autowired
    private ITblLyricsService tblLyricsService;

    @Autowired
    private ITblPracticedService tblPracticedService;

    @Autowired
    private TtsService ttsService;

    @GetMapping("/lyric/query")
    public PageResponse<TblLyric> queryLyric(@RequestParam(required = false) String song,
                                             @RequestParam(required = false) String singer,
                                             @RequestParam(required = false) Integer gender,
                                             @RequestParam(required = false) Integer pageIndex) {
        QueryWrapper<TblLyric> queryWrapper = new QueryWrapper<>();
        if(pageIndex == null) {
            pageIndex = 1;
        }
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

    @PostMapping("/lyric/save")
    public Response<Void> saveLyric(@RequestBody TblLyric tblLyric) {
        String song = tblLyric.getSong();
        String singer = tblLyric.getSinger();
        String cover = tblLyric.getCover();
        // 给个默认封面
        if(StringUtils.isEmpty(cover)) {
            tblLyric.setCover("https://img.zcool.cn/community/01896b597ac380a8012193a3db4f2d.png");
        }
        TblLyric existed = tblLyricsService.getOne(new QueryWrapper<TblLyric>()
                .eq("song", song).eq("singer", singer));
        if(existed != null) {
            log.info("existed, song: {}, singer: {}", song, singer);
            return Response.success(null);
        }
        tblLyricsService.save(tblLyric);
        return Response.success(null);
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

    @GetMapping("/lyric/search")
    public Response<SearchLyricVO> searchLyric(@RequestParam String song,
                                                     @RequestParam(required = false) String singer,
                                                     @RequestParam(required = false) String album) {
        // 查找歌词
        String keyword = song;
        if(StringUtils.isNotEmpty(singer)) {
            keyword += singer;
        }
        if(StringUtils.isNotEmpty(album)) {
            keyword += album;
        }
        String searchUrl = musicApiPre + keyword + musicApiCondition;
        String searchRes = HttpClientUtil.get(searchUrl).toString();
        SearchSongRes searchSongRes = JSONObject.parseObject(searchRes, SearchSongRes.class);
        Assert.isTrue(searchSongRes.getCode().contentEquals("000000"), "查询失败");
        SongResultData songResultData = searchSongRes.getSongResultData();
        List<SearchSong> searchSongs = songResultData.getResult();
        Assert.isTrue(!CollectionUtils.isEmpty(searchSongs), "查询无结果");
        SearchSong searchSong = searchSongs.get(0);
        String songName = searchSong.getName();
        List<Singer> singers = searchSong.getSingers();
        String singerName = singers.get(0).getName();
        String cover = searchSong.getImgItems().get(0).getImg();
        String lyricUrl = searchSong.getLyricUrl();
        String lyric = HttpClientUtil.get(lyricUrl).toString();

        String handleLyricContent = handleLyricContent(lyric);

        SearchLyricVO searchLyricVO = new SearchLyricVO();
        searchLyricVO.setSong(songName);
        searchLyricVO.setSinger(singerName);
        searchLyricVO.setCover(cover);
        searchLyricVO.setLyric(handleLyricContent);
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
        cleanedLyrics = cleanedLyrics.replaceAll("(?m)^[ \t]*\r?\n", "")
                .replaceAll("\n", ",");

        return cleanedLyrics;
    }
}
