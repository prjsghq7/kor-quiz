package com.kgh.korquiz.controllers;

import com.kgh.korquiz.dtos.QuizWithMeaningsDto;
import com.kgh.korquiz.results.CommonResult;
import com.kgh.korquiz.services.QuizService;
import jakarta.servlet.http.HttpSession;
import net.minidev.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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



    @RequestMapping(value = "/register", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getRegister(HttpSession session) {
        if (session.getAttribute("signedUser") == null) {
            return "home/index";
        }
        return "quiz/register";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getIndex(HttpSession session) {
        if (session.getAttribute("signedUser") == null) {
            return "home/index";
        }
        return "quiz/index";
    }
}
