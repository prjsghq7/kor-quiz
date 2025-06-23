package com.kgh.korquiz.services;

import com.kgh.korquiz.dtos.QuizFilterDto;
import com.kgh.korquiz.dtos.QuizResultDto;
import com.kgh.korquiz.dtos.QuizWithMeaningsDto;
import com.kgh.korquiz.entities.MeaningEntity;
import com.kgh.korquiz.entities.QuizEntity;
import com.kgh.korquiz.entities.QuizHistoryEntity;
import com.kgh.korquiz.entities.UserEntity;
import com.kgh.korquiz.mappers.MeaningMapper;
import com.kgh.korquiz.mappers.QuizHistoryMapper;
import com.kgh.korquiz.mappers.QuizMapper;
import com.kgh.korquiz.results.CommonResult;
import com.kgh.korquiz.results.Result;
import com.kgh.korquiz.results.ResultTuple;
import com.kgh.korquiz.results.quiz.RegisterResult;
import com.kgh.korquiz.utils.NodeListConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class QuizService {

    @Value("${api-krdict-korean-key}")
    private String API_KEY;

    private static final String API_BASE_URL = "https://krdict.korean.go.kr/api/search";
    private static final String PART = "word";
    private static final String TRANSLATED = "Y";
    private static final String TRANS_LANG = "0";

    private final QuizMapper quizMapper;
    private final MeaningMapper meaningMapper;
    private final QuizHistoryMapper quizHistoryMapper;

    public QuizService(QuizMapper quizMapper, MeaningMapper meaningMapper, QuizHistoryMapper quizHistoryMapper) {
        this.quizMapper = quizMapper;
        this.meaningMapper = meaningMapper;
        this.quizHistoryMapper = quizHistoryMapper;
    }

    private String getText(Element parent, String tag) {
        NodeList node = parent.getElementsByTagName(tag);
        if (node.getLength() == 0) return null;

        String value = node.item(0).getTextContent();
        return (value == null || value.isBlank()) ? null : value;
    }

    private int convertLangNameToCode(String lang) {
        switch (lang.trim()) {
            case "전체":
                return 0;
            case "영어":
                return 1;
            case "일본어":
                return 2;
            case "프랑스어":
                return 3;
            case "스페인어":
                return 4;
            case "아랍어":
                return 5;
            case "몽골어":
                return 6;
            case "베트남어":
                return 7;
            case "타이어":
                return 8;
            case "인도네시아어":
                return 9;
            case "러시아어":
                return 10;
            case "중국어":
                return 11;
            default:
                return 0;
        }
    }

    public ResultTuple<QuizResultDto> getQuizResult(UserEntity signedUser,
                                                    String code,
                                                    String userAnswer) {
        if (signedUser == null
                || signedUser.isDeleted()
                || signedUser.isSuspended()) {
            return ResultTuple.<QuizResultDto>builder()
                    .result(CommonResult.FAILURE_SESSION_EXPIRED)
                    .build();
        }

        QuizEntity quiz = this.quizMapper.selectByCode(code);
        if (quiz == null) {
            return ResultTuple.<QuizResultDto>builder()
                    .result(CommonResult.FAILURE)
                    .build();
        }

        QuizHistoryEntity quizHistory = new QuizHistoryEntity();
        quizHistory.setUserId(signedUser.getId());
        quizHistory.setQuizCode(code);
        quizHistory.setUserAnswer(userAnswer);
        quizHistory.setCorrect(quiz.getAnswer().equals(userAnswer));
        quizHistory.setSolvedAt(LocalDateTime.now());
        if (this.quizHistoryMapper.insertQuizHistory(quizHistory) < 1) {
            return ResultTuple.<QuizResultDto>builder()
                    .result(CommonResult.FAILURE)
                    .build();
        }

        QuizResultDto quizResultDto = new QuizResultDto();
        quizResultDto.setCorrect(quizHistory.isCorrect());
        quizResultDto.setUserAnswer(quizHistory.getUserAnswer());
        quizResultDto.setCorrectAnswer(quiz.getAnswer());
        quizResultDto.setLink(quiz.getLink());

        return ResultTuple.<QuizResultDto>builder()
                .result(CommonResult.SUCCESS)
                .payload(quizResultDto)
                .build();
    }

    public MeaningEntity[] getMeanings(String targetCode, int languageCode) {
        return this.meaningMapper.selectByTargetCodeAndLanguageCode(targetCode, languageCode);
    }

    public QuizEntity getRandomQuiz(QuizFilterDto quizFilterDto) {
        if (quizFilterDto == null) {
            return null;
        }
        if (quizFilterDto.getWordGrade() == null || quizFilterDto.getWordGrade().isEmpty()) {
            quizFilterDto.setWordGrade("all");
        }
        if (quizFilterDto.getPartOfSpeach() == null || quizFilterDto.getPartOfSpeach().isEmpty()) {
            quizFilterDto.setPartOfSpeach("all");
        }
        return this.quizMapper.selectRandomByFilter(quizFilterDto);
    }

    public QuizWithMeaningsDto[] searchExternalByKeyword(String keyword) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

        String url = String.format(
                "%s?key=%s&type_search=search&part=%s&translated=%s&trans_lang=%s&q=%s",
                API_BASE_URL, API_KEY, PART, TRANSLATED, TRANS_LANG, encodedKeyword
        );

        // HttpClient 요청
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0 (compatible; korquiz-bot/1.0)")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String xml = response.body();

        // XML 파싱
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        Element[] items = NodeListConverter.toElementArray(doc.getElementsByTagName("item"));
        if (items.length == 0) {
            return new QuizWithMeaningsDto[0];
        }

        List<QuizWithMeaningsDto> result = new ArrayList<>();
        for (Element item : items) {
            String targetCode = getText(item, "target_code");
            String word = getText(item, "word");
            String pos = getText(item, "pos");
            String grade = getText(item, "word_grade");
            String link = getText(item, "link");


            if (targetCode == null || word == null || pos == null || link == null) {
                return null;
            }
            if (grade == null) {
                grade = "미정";
            }

            QuizEntity quiz = new QuizEntity(targetCode, word, pos, grade, link);

            List<MeaningEntity> meaningList = new ArrayList<>();

            Element[] senses = NodeListConverter.toElementArray(item.getElementsByTagName("sense"));
            for (Element sense : senses) {
                String definition = getText(sense, "definition");
                String strOrderNo = getText(sense, "sense_order");
                if (definition == null || strOrderNo == null) {
                    return null;
                }
                int orderNo = Integer.parseInt(strOrderNo);

                meaningList.add(MeaningEntity
                        .builder()
                        .targetCode(targetCode)
                        .languageCode(0)
                        .languageName("한국어")
                        .definition(definition)
                        .orderNo(orderNo)
                        .build());

                Element[] translations = NodeListConverter.toElementArray(sense.getElementsByTagName("translation"));
                for (Element trans : translations) {
                    String langName = getText(trans, "trans_lang");
                    String transDfn = getText(trans, "trans_dfn");

                    if (langName == null || transDfn == null) {
                        return null;
                    }
                    langName = langName.trim();
                    int langCode = convertLangNameToCode(langName);

                    MeaningEntity meaning = new MeaningEntity();
                    meaning.setTargetCode(targetCode);
                    meaning.setLanguageCode(langCode);
                    meaning.setLanguageName(langName);
                    meaning.setDefinition(transDfn);
                    meaning.setOrderNo(orderNo);
                    meaningList.add(meaning);
                }
            }
            if (meaningList.isEmpty()) {
                return null;
            }
            MeaningEntity[] meanings = meaningList.toArray(new MeaningEntity[meaningList.size()]);
            result.add(new QuizWithMeaningsDto(quiz, meanings));
        }
        if (result.isEmpty()) {
            return null;
        }
        return result.toArray(new QuizWithMeaningsDto[result.size()]);
    }


    @Transactional
    public Result register(QuizWithMeaningsDto quizWithMeaningsDto) {
        QuizEntity quiz = quizWithMeaningsDto.getQuiz();

        if(this.quizMapper.selectCountByCode(quiz.getCode()) > 0) {
            return RegisterResult.FAILURE_DUPLICATE_QUIZ;
        }

        if (this.quizMapper.insertQuiz(quiz) < 1) {
            return CommonResult.FAILURE;
        }

        MeaningEntity[] meanings = quizWithMeaningsDto.getMeanings();
        for (MeaningEntity meaning : meanings) {
            if (meaningMapper.insertMeaning(meaning) < 1) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return CommonResult.FAILURE;
            }
        }

        return CommonResult.SUCCESS;
    }
}
