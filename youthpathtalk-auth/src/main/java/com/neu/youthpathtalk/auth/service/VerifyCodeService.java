package com.neu.youthpathtalk.auth.service;

import com.neu.youthpathtalk.auth.vo.SendVerifyCodeReqVO;
import com.neu.youthpathtalk.response.Response;

/**
 * @author Julien
 * @time 2026/03/09 19:03
 * @description 验证码服务接口类
 */
public interface VerifyCodeService {
    Response<?> sendVerifyCode(SendVerifyCodeReqVO sendVerifyCodeReqVO);
}
