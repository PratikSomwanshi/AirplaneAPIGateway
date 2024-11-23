package com.wanda.controller;

import com.wanda.entity.Users;
import com.wanda.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Users register(@RequestBody Users user) {
        return this.userService.saveUser(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody Users user) {
        return this.userService.verify(user);
    }
}
