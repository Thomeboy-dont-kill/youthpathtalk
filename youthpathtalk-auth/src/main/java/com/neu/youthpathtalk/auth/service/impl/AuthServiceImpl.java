package com.neu.youthpathtalk.auth.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.neu.youthpathtalk.auth.cache.RedisService;
import com.neu.youthpathtalk.auth.enums.BizResponseErrorCode;
import com.neu.youthpathtalk.auth.rpc.UserRpcService;
import com.neu.youthpathtalk.auth.service.AuthService;
import com.neu.youthpathtalk.auth.vo.PasswordLoginReqVO;
import com.neu.youthpathtalk.auth.vo.RegisterUserReqVO;
import com.neu.youthpathtalk.auth.vo.SmsLoginReqVO;
import com.neu.youthpathtalk.constant.redis.UserRedisKey;
import com.neu.youthpathtalk.exception.BizException;
import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.user.api.vo.rep.LoginRepVO;
import com.neu.youthpathtalk.user.api.vo.req.AddUserReqVO;
import com.neu.youthpathtalk.user.api.vo.req.GetUserIdByPwdLoginReqVO;
import com.neu.youthpathtalk.user.api.vo.req.GetUserIdByPhoneReqVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Julien
 * @time 2026/03/10 9:25
 * @description 用户认证服务实现类
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final RedisService redisService;
    private final UserRpcService userRpcService;
    @Override
    public Response<String> register(RegisterUserReqVO registerUserReqVO) {
        String verifyCodeKey=UserRedisKey.verifyCode(registerUserReqVO.getPhone());
        if (!redisService.validateVerifyCode(verifyCodeKey, registerUserReqVO.getVerifyCode())){
            throw new BizException(BizResponseErrorCode.VERIFY_CODE_INVALID);
        }
        AddUserReqVO addUserReqVO = AddUserReqVO.builder()
                .username(registerUserReqVO.getUsername())
                .password(registerUserReqVO.getPassword())
                .phone(registerUserReqVO.getPhone())
                .userType(registerUserReqVO.getUserType())
                .build();
        LoginRepVO loginRepVO=userRpcService.addUser(addUserReqVO);
        StpUtil.login(loginRepVO.getUserId());
        String token=StpUtil.getTokenValue();
        StpUtil.getSession().set("permission_list",loginRepVO.getPaths());
        return Response.ok(token);
    }

    @Override
    public Response<String> login(PasswordLoginReqVO passwordLoginReqVO) {
        GetUserIdByPwdLoginReqVO getUserIdByPwdLoginReqVO=new GetUserIdByPwdLoginReqVO();
        getUserIdByPwdLoginReqVO.setUsername(passwordLoginReqVO.getUsername());
        getUserIdByPwdLoginReqVO.setPassword(passwordLoginReqVO.getPassword());
        LoginRepVO loginRepVO=userRpcService.getUserIdByPasswordLogin(getUserIdByPwdLoginReqVO);
        StpUtil.login(loginRepVO.getUserId());
        String token=StpUtil.getTokenValue();
        StpUtil.getSession().set("permission_list",loginRepVO.getPaths());
        return Response.ok(token);
    }

    @Override
    public Response<String> loginByMessage(SmsLoginReqVO smsLoginReqVO) {
        String verifyCodeKey= UserRedisKey.verifyCode(smsLoginReqVO.getPhone());
        if (!redisService.validateVerifyCode(verifyCodeKey,smsLoginReqVO.getVerifyCode())) {
            throw new BizException(BizResponseErrorCode.VERIFY_CODE_INVALID);
        }
        GetUserIdByPhoneReqVO getUserIdByPhoneReqVO =new GetUserIdByPhoneReqVO();
        getUserIdByPhoneReqVO.setPhone(smsLoginReqVO.getPhone());
        LoginRepVO loginRepVO=userRpcService.getUserIdByPhone(getUserIdByPhoneReqVO);
        StpUtil.login(loginRepVO.getUserId());
        String token=StpUtil.getTokenValue();
        StpUtil.getSession().set("permission_list",loginRepVO.getPaths());//Sa-Token框架key是写死的
        return Response.ok(token);
    }
}
