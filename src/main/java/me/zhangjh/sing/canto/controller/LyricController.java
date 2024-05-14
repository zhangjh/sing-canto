package me.zhangjh.sing.canto.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.zhangjh.share.response.PageResponse;
import me.zhangjh.share.response.Response;
import me.zhangjh.sing.canto.controller.request.QueryRequest;
import me.zhangjh.sing.canto.dao.entity.TblLyric;
import me.zhangjh.sing.canto.response.EvaluateVO;
import me.zhangjh.sing.canto.service.ITblLyricsService;
import me.zhangjh.sing.canto.service.TtsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author njhxzhangjihong@126.com
 * @date 14:36 2024/5/11
 * @Description
 */
@RestController
@RequestMapping("/canto/")
public class LyricController {

    @Autowired
    private ITblLyricsService tblLyricsService;

    @Autowired
    private TtsService ttsService;

    @GetMapping("/lyric/search/")
    public Response<List<String>> searchLyric(@RequestParam String song,
                                             @RequestParam String singer) {
        // todo: 根据歌名和歌手查询歌词，转换为切分后的内容返回
        return null;
    }

    @GetMapping("/lyric/query")
    public PageResponse<TblLyric> queryLyric(@RequestParam QueryRequest request) {
        QueryWrapper<TblLyric> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("song", request.getSong());
        queryWrapper.eq("singer", request.getSinger());
        queryWrapper.eq("gender", request.getGender());
        Page<TblLyric> page = new Page<>(request.getPageIndex(), request.getPageSize());
        Page<TblLyric> lyricPage = tblLyricsService.page(page, queryWrapper);
        return PageResponse.success(lyricPage.getRecords(), lyricPage.getTotal());
    }

    @GetMapping("/voice/play")
    public Response<Void> playVoice(@RequestParam String text,
                                    @RequestParam(required = false) String rate) {
        if(StringUtils.isEmpty(rate)) {
            rate = "medium";
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
}
