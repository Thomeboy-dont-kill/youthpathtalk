package com.neu.youthpathtalk.auth.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import com.neu.youthpathtalk.anno.ApiOperationLog;
import com.neu.youthpathtalk.auth.service.AuthService;
import com.neu.youthpathtalk.auth.vo.PasswordLoginReqVO;
import com.neu.youthpathtalk.auth.vo.RegisterUserReqVO;
import com.neu.youthpathtalk.auth.vo.SmsLoginReqVO;
import com.neu.youthpathtalk.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author Julien
 * @time 2026/03/09 16:26
 * @description 用户认证控制器
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(
        name = "认证模块",
        description = "用户注册、登录、退出登录相关接口"
)
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ApiOperationLog(description = "注册用户并获取token")
    @Operation(
            summary = "用户注册",
            description = """
                注册成功后自动完成登录。

                登录成功后：

                1. 后端会自动在 Cookie 中写入认证信息。
                2. 前端无需手动保存 Token。
                3. 浏览器后续请求会自动携带 Cookie 完成认证。
                """
    )
    public Response<String> register(@Validated @RequestBody RegisterUserReqVO registerUserReqVO){
        return authService.register(registerUserReqVO);
    }
    @PostMapping("/login")
    @ApiOperationLog(description = "用户密码登录并获取token")
    @Operation(
            summary = "账号密码登录",
            description = """
                使用用户名和密码登录。

                登录成功后：

                1. 后端会自动在 Cookie 中写入认证信息。
                2. 前端无需手动保存 Token。
                3. 浏览器后续请求会自动携带 Cookie 完成认证。

                返回值仅表示登录是否成功。
                """
    )
    public Response<String> login(@Validated @RequestBody PasswordLoginReqVO passwordLoginReqVO){
        return authService.login(passwordLoginReqVO);
    }
    @PostMapping("/login/message")
    @ApiOperationLog(description = "用户短信登陆并获取token")
    @Operation(
            summary = "短信验证码登录",
            description = """
                    用户输入手机号和验证码完成登录。
                    
                    登录成功后：

                    1. 后端会自动在 Cookie 中写入认证信息。
                    2. 前端无需手动保存 Token。
                    3. 浏览器后续请求会自动携带 Cookie 完成认证。
    
                    返回值仅表示登录是否成功。
                    """
    )
    public Response<String> loginByMessage(@Validated @RequestBody SmsLoginReqVO smsLoginReqVO){
        return authService.loginByMessage(smsLoginReqVO);
    }

    @GetMapping("/logout")
    @Operation(
            summary = "退出登录",
            description = """
                    当前登录用户退出登录。
                    """
    )
    public Response<?> logout(HttpServletRequest request) {
        StpUtil.logout();
        return Response.ok();
    }
}
