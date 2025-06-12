package com.kgh.korquiz.controllers;

import com.kgh.korquiz.services.QuizService;
import jakarta.servlet.http.HttpSession;
import net.minidev.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/quiz")
public class QuizController {
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @RequestMapping(value = "/search-external", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getSearchExternal() {
        JSONObject response = new JSONObject();
        response.put("result", "success");
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
