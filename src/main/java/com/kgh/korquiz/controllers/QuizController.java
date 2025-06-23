package com.kgh.korquiz.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgh.korquiz.dtos.QuizFilterDto;
import com.kgh.korquiz.dtos.QuizResultDto;
import com.kgh.korquiz.dtos.QuizWithMeaningsDto;
import com.kgh.korquiz.entities.MeaningEntity;
import com.kgh.korquiz.entities.QuizEntity;
import com.kgh.korquiz.entities.UserEntity;
import com.kgh.korquiz.results.CommonResult;
import com.kgh.korquiz.results.Result;
import com.kgh.korquiz.results.ResultTuple;
import com.kgh.korquiz.services.QuizService;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Controller
@RequestMapping(value = "/quiz")
public class QuizController {
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }


    @RequestMapping(value = "/translate-meanings", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MeaningEntity[] getTranslateMeanings(@RequestParam(value = "targetCode") String targetCode,
                                                @RequestParam(value = "languageCode") int languageCode) {
        return this.quizService.getMeanings(targetCode, languageCode);
    }

    @RequestMapping(value = "/solve", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postSolve(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
                        @RequestParam(value = "code") String code,
                        @RequestParam(value = "answer") String answer) throws JsonProcessingException {
        ResultTuple<QuizResultDto> result = this.quizService.getQuizResult(signedUser, code, answer);
        JSONObject response = new JSONObject();
        response.put("result", result.getResult().nameToLower());
        System.out.println("isCorrect : " + result.getPayload().isCorrect());
        ObjectMapper mapper = new ObjectMapper();
        response.put("quizResult", new JSONObject(mapper.writeValueAsString(result.getPayload())));
        return response.toString();
    }


    @RequestMapping(value = "/solve", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getSolve(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser,
                           Model model        ,
                           QuizFilterDto quizFilterDto) {
        if (signedUser == null) {
            return "home/index";
        }
        QuizEntity quiz = this.quizService.getRandomQuiz(quizFilterDto);
        model.addAttribute("quiz", quiz);
        if (quiz != null && quiz.getCode() != null) {
            MeaningEntity[] meanings = this.quizService.getMeanings(quiz.getCode(), 0);
            model.addAttribute("meanings", meanings);
        }
        return "quiz/solve";
    }

    @RequestMapping(value = "/setting", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getSetting(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser) {
        if (signedUser == null) {
            return "home/index";
        }
        return "quiz/setting";
    }

    @RequestMapping(value = "/search-external", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getSearchExternal(@RequestParam(value = "keyword") String keyword) throws IOException, ParserConfigurationException, InterruptedException, SAXException {
        QuizWithMeaningsDto[] quizDtos = this.quizService.searchExternalByKeyword(keyword);
        JSONObject response = new JSONObject();
        if (quizDtos != null && quizDtos.length > 0) {
            response.put("result", CommonResult.SUCCESS.nameToLower());
            response.put("quizDtos", quizDtos);
        } else {
            response.put("result", CommonResult.FAILURE.nameToLower());
        }

        return response.toString();
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRegister(QuizWithMeaningsDto quizWithMeaningsDto) {
        Result result = this.quizService.register(quizWithMeaningsDto);
        JSONObject response = new JSONObject();
        response.put("result", result.nameToLower());
        return response.toString();
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getRegister(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser) {
        if (signedUser == null) {
            return "home/index";
        }
        return "quiz/register";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getIndex(@SessionAttribute(value = "signedUser", required = false) UserEntity signedUser) {
        if (signedUser == null) {
            return "home/index";
        }
        return "quiz/index";
    }
}
