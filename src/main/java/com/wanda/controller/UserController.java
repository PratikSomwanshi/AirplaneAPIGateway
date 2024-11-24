package com.wanda.controller;

import com.wanda.entity.Users;
import com.wanda.service.UserService;
import com.wanda.utils.response.SuccessResponse;
import com.wanda.utils.response.TokenResponse;
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
    public SuccessResponse<Users> register(@RequestBody Users user) {
        var newUser = this.userService.saveUser(user);
        return new SuccessResponse<>(true, "user register successfully", newUser);
    }

    @PostMapping("/login")
    public SuccessResponse<TokenResponse> login(@RequestBody Users user) {
        var token =  this.userService.verify(user);

        TokenResponse tokenResponse = new TokenResponse(token);

        return new SuccessResponse<>(true, "login successful", tokenResponse);
    }
}
