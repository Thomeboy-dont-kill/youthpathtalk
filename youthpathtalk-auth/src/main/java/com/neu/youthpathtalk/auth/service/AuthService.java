package com.neu.youthpathtalk.auth.service;

import com.neu.youthpathtalk.auth.vo.PasswordLoginReqVO;
import com.neu.youthpathtalk.auth.vo.RegisterUserReqVO;
import com.neu.youthpathtalk.auth.vo.SmsLoginReqVO;
import com.neu.youthpathtalk.response.Response;

/**
 * @author Julien
 * @time 2026/03/10 9:24
 * @description 用户认证服务接口
 */
public interface AuthService {
    Response<String> register(RegisterUserReqVO registerUserReqVO);
    Response<String> login(PasswordLoginReqVO passwordLoginReqVO);
    Response<String> loginByMessage(SmsLoginReqVO smsLoginReqVO);
}
