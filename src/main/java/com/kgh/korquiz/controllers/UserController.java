package com.kgh.korquiz.controllers;

import com.kgh.korquiz.results.Result;
import com.kgh.korquiz.services.UserService;
import jakarta.servlet.http.HttpSession;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/register/extra", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getRegisterExtra(HttpSession session, Model model) {
        OAuth2User oauthUser = (OAuth2User) session.getAttribute("oauthUser");
        if (oauthUser == null) {
            return "redirect:/login?error=session_expired";
        }
        oauthUser.getAttributes().forEach((key, value) -> {
            System.out.println(key + " = " + value);
        });
        model.addAttribute("email", oauthUser.getAttribute("email"));
        model.addAttribute("provider", oauthUser.getAttribute("provider"));
        model.addAttribute("name", oauthUser.getAttribute("name"));
        return "user/register-extra"; // 템플릿 파일
    }

    @RequestMapping(value = "/check-nickname", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postCheckNickname(@RequestParam(value = "nickname", required = false) String nickname) {
        System.out.println("nickname = " + nickname);
        Result result = this.userService.isNicknameAvailable(nickname);
        JSONObject response = new JSONObject();
        response.put("result", result.nameToLower());
        return response.toString();
    }
}
