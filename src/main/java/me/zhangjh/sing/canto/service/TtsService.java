package me.zhangjh.sing.canto.service;

import com.alibaba.fastjson2.JSONObject;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import me.zhangjh.sing.canto.response.*;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    @Value("${temp.dir}")
    private String tempDir;

    private static SpeechConfig speechConfig;

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

    }

    @PostConstruct
    public void init() {
        initSpeech();
    }

    public void playContent(String text, String rate) {
        String ssml = "<speak version='1.0' " +
                "xmlns='http://www.w3.org/2001/10/synthesis' xml:lang='zh-CN'>" +
                "<voice name='" + maleSpeechVoiceName + "'>" +
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

    public EvaluateVO evaluate(String referText) {
        return doEvaluate(referText);
    }
    public EvaluateVO evaluateFile(String filePath, String referText) {
        AudioConfig audioConfig = AudioConfig.fromWavFileInput(filePath);
        speechRecognizer = new SpeechRecognizer(
                speechConfig,
                audioConfig);
        return doEvaluate(referText);
    }

    public EvaluateVO evaluateFile(MultipartFile file, String referText) {
        File aacFile = null;
        File wavFile = null;
        try {
            String fileName = String.valueOf(System.currentTimeMillis());
            aacFile = File.createTempFile(tempDir + fileName, ".aac");
            file.transferTo(aacFile);
            if (!aacFile.canRead()) {
                throw new RuntimeException("File cannot be read");
            }
            wavFile = transferAudioType(fileName, aacFile);
            return evaluateFile(wavFile.getAbsolutePath(), referText);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (wavFile != null && wavFile.exists()) {
                try {
                    FileUtils.forceDelete(wavFile);
                    FileUtils.forceDelete(aacFile);
                } catch (IOException e) {
                    // Logging the error to ensure the file deletion issue is noticed but not throwing an exception
                    log.error("Failed to delete temp file: {}", wavFile.getAbsolutePath());
                }
            }
        }
    }

    public File transferAudioType(String fileName, File aacFile) {
        try {
            File wavFile = new File(tempDir + fileName + ".wav");
            if(!wavFile.exists()) {
                boolean newFile = wavFile.createNewFile();
                Assert.isTrue(newFile, "创建临时文件失败");
            }
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", aacFile.getAbsolutePath(), "-y",
                    "-f", "wav", wavFile.getAbsolutePath());
            pb.inheritIO().start().waitFor();

            return wavFile;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private EvaluateVO doEvaluate(String referText) {
        try {
            try (PronunciationAssessmentConfig pronunciationConfig = new PronunciationAssessmentConfig(referText,
                    PronunciationAssessmentGradingSystem.HundredMark, PronunciationAssessmentGranularity.Phoneme, false)
            ) {
                pronunciationConfig.enableProsodyAssessment();
                pronunciationConfig.enableContentAssessmentWithTopic("singing song");
                pronunciationConfig.applyTo(speechRecognizer);
                Future<SpeechRecognitionResult> future = speechRecognizer.recognizeOnceAsync();
                SpeechRecognitionResult speechRecognitionResult = future.get(30, TimeUnit.SECONDS);

                PronunciationAssessmentResult pronResult =
                        PronunciationAssessmentResult.fromResult(speechRecognitionResult);

                if(pronResult == null) {
                    return new EvaluateVO();
                }

                String pronunciationAssessmentResultJson = speechRecognitionResult.getProperties()
                        .getProperty(PropertyId.SpeechServiceResponse_JsonResult);

                EvaluateRes evaluateRes = JSONObject.parseObject(pronunciationAssessmentResultJson, EvaluateRes.class);
                log.info("result: {}", pronunciationAssessmentResultJson);

                // 返回一个等第结果给端，如五星等第判定
                EvaluateVO evaluateVO = new EvaluateVO();
                evaluateVO.setAccuracy(pronResult.getAccuracyScore());
                evaluateVO.setComplete(pronResult.getCompletenessScore());
                evaluateVO.setFluency(pronResult.getFluencyScore());
                evaluateVO.setPronScore(pronResult.getPronunciationScore());

                List<WordEvaluateVO> wordEvaluateVOS = new ArrayList<>();
                for (Nbest nbest : evaluateRes.getNbest()) {
                    PronunciationAssessment assessment = nbest.getPronunciationAssessment();
                    log.info("accuracy: {}", assessment.getAccuracyScore());
                    log.info("fluency: {}", assessment.getFluencyScore());
                    log.info("pronScore: {}", assessment.getPronScore());
                    log.info("complete: {}", assessment.getCompletenessScore());
                    for (Word word : nbest.getWords()) {
                        log.info("word: {}", word.getWord());
                        if (word.getPronunciationAssessment() != null) {
                            // 输出转为日志
                            log.info("word accuracy: {}", word.getPronunciationAssessment().getAccuracyScore());
                            WordEvaluateVO wordEvaluateVO = new WordEvaluateVO();
                            wordEvaluateVO.setAccuracy(word.getPronunciationAssessment().getAccuracyScore());
                            wordEvaluateVO.setWord(word.getWord());
                            wordEvaluateVOS.add(wordEvaluateVO);
                        }
                    }
                }
                evaluateVO.setWordEvaluates(wordEvaluateVOS);
                evaluateVO.getStars();
                return evaluateVO;
            }
        } catch (Exception e) {
            log.error("doEvaluate exception: ", e);
            throw new RuntimeException(e);
        }
    }
}
