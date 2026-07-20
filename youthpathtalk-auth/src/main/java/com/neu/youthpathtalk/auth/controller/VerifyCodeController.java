package com.neu.youthpathtalk.auth.controller;

import com.neu.youthpathtalk.anno.ApiOperationLog;
import com.neu.youthpathtalk.auth.service.VerifyCodeService;
import com.neu.youthpathtalk.auth.vo.SendVerifyCodeReqVO;
import com.neu.youthpathtalk.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "验证码模块",
        description = "短信验证码相关接口"
)
public class VerifyCodeController {
    private final VerifyCodeService verifyCodeService;
    @PostMapping("/send")
    @ApiOperationLog(description = "发送验证码")
    @Operation(
            summary = "发送短信验证码",
            description = """
                向指定手机号发送短信验证码。

                注意：

                限制同一手机号发送验证码间隔为1分钟，
                如果发送过于频繁，接口会返回限流错误。

                验证码有效期为5分钟。
                """
    )
    public Response<?> sendVerifyCode(@Validated @RequestBody SendVerifyCodeReqVO sendVerifyCodeReqVO){
        return verifyCodeService.sendVerifyCode(sendVerifyCodeReqVO);
    }
}
