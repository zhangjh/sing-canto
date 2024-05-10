package me.zhangjh.sing.canto;

import me.zhangjh.sing.canto.response.EvaluateVO;
import me.zhangjh.sing.canto.service.TtsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

        EvaluateVO evaluateVO = ttsService.evaluateTest("src/main/resources/歌词.wav");
        System.out.println(evaluateVO.getStars());
    }
}
