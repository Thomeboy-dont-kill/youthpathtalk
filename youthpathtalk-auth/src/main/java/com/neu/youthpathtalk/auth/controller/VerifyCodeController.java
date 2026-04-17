package com.neu.youthpathtalk.auth.controller;

import com.neu.youthpathtalk.anno.ApiOperationLog;
import com.neu.youthpathtalk.auth.service.VerifyCodeService;
import com.neu.youthpathtalk.auth.vo.SendVerifyCodeReqVO;
import com.neu.youthpathtalk.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Julien
 * @time 2026/03/09 17:19
 * @description 发送验证码控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/code")
public class VerifyCodeController {
    private final VerifyCodeService verifyCodeService;
    @PostMapping("/send")
    @ApiOperationLog(description = "发送验证码")
    public Response<?> sendVerifyCode(@Validated @RequestBody SendVerifyCodeReqVO sendVerifyCodeReqVO){
        return verifyCodeService.sendVerifyCode(sendVerifyCodeReqVO);
    }
}
