package com.neu.youthpathtalk.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.neu.youthpathtalk.anno.ApiOperationLog;
import com.neu.youthpathtalk.auth.service.AuthService;
import com.neu.youthpathtalk.auth.vo.PasswordLoginReqVO;
import com.neu.youthpathtalk.auth.vo.RegisterUserReqVO;
import com.neu.youthpathtalk.auth.vo.SmsLoginReqVO;
import com.neu.youthpathtalk.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author Julien
 * @time 2026/03/09 16:26
 * @description 用户认证控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ApiOperationLog(description = "注册用户并获取token")
    public Response<String> register(@RequestBody RegisterUserReqVO registerUserReqVO){
        return authService.register(registerUserReqVO);
    }
    @PostMapping("/login")
    @ApiOperationLog(description = "用户密码登录并获取token")
    public Response<String> login(@RequestBody PasswordLoginReqVO passwordLoginReqVO){
        return authService.login(passwordLoginReqVO);
    }
    @PostMapping("/login/message")
    @ApiOperationLog(description = "用户短信登陆并获取token")
    public Response<String> loginByMessage(@RequestBody SmsLoginReqVO smsLoginReqVO){
        return authService.loginByMessage(smsLoginReqVO);
    }

    @GetMapping("/logout")
    public Response<?> logout() {
        StpUtil.logout();
        return Response.ok();
    }
}
