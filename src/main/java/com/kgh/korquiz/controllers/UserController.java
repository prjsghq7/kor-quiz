package com.kgh.korquiz.controllers;

import com.kgh.korquiz.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/register/extra", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getLogin() {
        return "user/login";
    }
}
