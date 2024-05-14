package me.zhangjh.sing.canto;

import me.zhangjh.sing.canto.response.EvaluateVO;
import me.zhangjh.sing.canto.service.TtsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class SingCantoApplicationTests {

    @Autowired
    private TtsService ttsService;

    @Test
    void contextLoads() {
    }

    @Test
    public void ttsTest() {
//        ttsService.playContent("在有生的瞬间能遇到你，竟花光所有运气", "medium");

        EvaluateVO evaluateVO = ttsService.evaluateFile("src/main/resources/歌词.wav", "在有生的瞬间能遇到你，竟花光所有运气");
        System.out.println(evaluateVO.getStars());
        EvaluateVO evaluateVO2 = ttsService.evaluateFile("src/main/resources/hello.wav", "你好");
        System.out.println(evaluateVO2.getStars());
    }

    @Test
    public void transferAudio() {
        ttsService.transferAudioType("test_0504",
                new File("/Users/zhangjh/Desktop/tmp/voice-clone/test_0504.aac"));
    }
}
