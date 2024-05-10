package me.zhangjh.sing.canto.service;

import com.alibaba.fastjson2.JSONObject;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import me.zhangjh.sing.canto.response.EvaluateRes;
import me.zhangjh.sing.canto.response.EvaluateVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author njhxzhangjihong@126.com
 * @date 15:07 2024/5/10
 * @Description
 */
@Slf4j
@Component
public class TtsService {

    @Value("${SPEECH_KEY}")
    private String speechKey;

    @Value("${SPEECH_REGION}")
    private String speechRegion;

    @Value("${FEMALE_SPEECH_VOICE_NAME}")
    private String femaleSpeechVoiceName;

    @Value("${MALE_SPEECH_VOICE_NAME}")
    private String maleSpeechVoiceName;

    private static SpeechConfig speechConfig;

    private static AudioConfig audioConfig;

    /**
     * 用来生成语音
     * */
    private static SpeechSynthesizer ttsSynthesizer;

    /**
     * 用来识别语音
     * */
    private static SpeechRecognizer speechRecognizer;

    private void initSpeech() {
        speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);
        speechConfig.setSpeechRecognitionLanguage("zh-HK");
        speechConfig.setSpeechSynthesisVoiceName(maleSpeechVoiceName);
        ttsSynthesizer = new SpeechSynthesizer(speechConfig);
        Connection connection = Connection.fromSpeechSynthesizer(ttsSynthesizer);
        connection.openConnection(true);

        audioConfig = AudioConfig.fromDefaultSpeakerOutput();
        speechRecognizer = new SpeechRecognizer(
                speechConfig,
                audioConfig);
    }

    @PostConstruct
    public void init() {
        initSpeech();
    }

    public void playContent(String text, String rate) {
        String ssml = "<speak version='1.0' " +
                "xmlns='http://www.w3.org/2001/10/synthesis' xml:lang='zh-CN'>" +
                "<voice name='yue-CN-YunSongNeural'>" +
                "<prosody rate='" + rate + "'>" +
                text +
                "</prosody>" +
                "</voice>" +
                "</speak>";
        SpeechSynthesisResult result = ttsSynthesizer.SpeakSsml(ssml);
        if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
            log.info("Speech synthesized for text: {}", text);
        } else if (result.getReason() == ResultReason.Canceled) {
            SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(result);
            log.info("CANCELED: SpeechSynthesis was canceled: Reason=" + cancellation.getReason());
            if (cancellation.getReason() == CancellationReason.Error) {
                log.info("ErrorCode: {}, Error Details: {}", cancellation.getErrorCode(), cancellation.getErrorDetails());
            }
        }
    }

    public EvaluateVO evaluate() {
        return doEvaluate();
    }
    public EvaluateVO evaluateTest(String filePath) {
        AudioConfig audioConfig = AudioConfig.fromWavFileInput(filePath);
        speechRecognizer = new SpeechRecognizer(
                speechConfig,
                audioConfig);
        return doEvaluate();
    }

    private EvaluateVO doEvaluate() {
        try {
            try (PronunciationAssessmentConfig pronunciationConfig = new PronunciationAssessmentConfig("",
                    PronunciationAssessmentGradingSystem.HundredMark, PronunciationAssessmentGranularity.Phoneme, false)
            ) {
                pronunciationConfig.enableProsodyAssessment();
                pronunciationConfig.applyTo(speechRecognizer);
                Future<SpeechRecognitionResult> future = speechRecognizer.recognizeOnceAsync();
                SpeechRecognitionResult speechRecognitionResult = future.get(30, TimeUnit.SECONDS);

                PronunciationAssessmentResult pronResult =
                        PronunciationAssessmentResult.fromResult(speechRecognitionResult);

                String pronunciationAssessmentResultJson = speechRecognitionResult.getProperties()
                        .getProperty(PropertyId.SpeechServiceResponse_JsonResult);

                EvaluateRes evaluateRes = JSONObject.parseObject(pronunciationAssessmentResultJson, EvaluateRes.class);
                System.out.println("result: " + JSONObject.toJSONString(evaluateRes));
                log.info("result: {}", pronunciationAssessmentResultJson);
                // 准确度
                System.out.println("accuracy:" + pronResult.getAccuracyScore());
                // 完整度
                System.out.println("complete:" + pronResult.getCompletenessScore());
                // 流畅度
                System.out.println("fluency:" + pronResult.getFluencyScore());
                // 总分，发音质量
                System.out.println("pronu:" + pronResult.getPronunciationScore());
                // todo: 返回一个等第结果给端，如五星等第判定，20分一个星
                EvaluateVO evaluateVO = new EvaluateVO();
                evaluateVO.setAccuracy(pronResult.getAccuracyScore());
                evaluateVO.setComplete(pronResult.getCompletenessScore());
                evaluateVO.setFluency(pronResult.getFluencyScore());
                evaluateVO.setPronScore(pronResult.getPronunciationScore());
                return evaluateVO;
            }
        } catch (Exception e) {
            log.error("doEvaluate exception: ", e);
            throw new RuntimeException(e);
        } finally {
            speechRecognizer.close();
            ttsSynthesizer.close();
        }
    }
}
