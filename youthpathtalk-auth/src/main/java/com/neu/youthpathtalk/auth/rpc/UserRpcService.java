package com.neu.youthpathtalk.auth.rpc;

import com.neu.youthpathtalk.exception.BizException;
import com.neu.youthpathtalk.user.api.client.UserServiceFeignClient;
import com.neu.youthpathtalk.user.api.constant.ApiConstants;
import com.neu.youthpathtalk.user.api.vo.resp.LoginRespVO;
import com.neu.youthpathtalk.user.api.vo.req.AddUserReqVO;
import com.neu.youthpathtalk.user.api.vo.req.CheckPhoneRegisteredReqVO;
import com.neu.youthpathtalk.user.api.vo.req.GetUserIdByPhoneReqVO;
import com.neu.youthpathtalk.user.api.vo.req.GetUserIdByPwdLoginReqVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.neu.youthpathtalk.response.Response;

/**
 * @author Julien
 * @time 2026/03/09 21:03
 * @description 远程调用用户服务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRpcService {
    private final UserServiceFeignClient userServiceFeignClient;
    public Boolean checkPhoneRegistered(CheckPhoneRegisteredReqVO checkPhoneRegisteredReqVO){
        Response<Boolean> response=userServiceFeignClient.checkPhoneRegistered(checkPhoneRegisteredReqVO);
        if (!response.getIsSuccess()) {
            log.warn("{}:checkPhoneRegistered业务异常, errorCode: {}, errorMessage: {}", ApiConstants.SERVICE_NAME,response.getErrorCode(),response.getErrorMessage());
            throw new BizException(response.getErrorCode(),response.getErrorMessage());
        }
        return response.getData();
    }
    public LoginRespVO addUser(AddUserReqVO addUserReqVO){
        Response<LoginRespVO> response = userServiceFeignClient.addUser(addUserReqVO);
        if (!response.getIsSuccess()){
            log.warn("{}:addUser业务异常, errorCode: {}, errorMessage: {}", ApiConstants.SERVICE_NAME,response.getErrorCode(),response.getErrorMessage());
            throw new BizException(response.getErrorCode(),response.getErrorMessage());
        }
        return response.getData();
    }
    public LoginRespVO getUserIdByPasswordLogin(GetUserIdByPwdLoginReqVO getUserIdByPwdLoginReqVO){
        Response<LoginRespVO> response=userServiceFeignClient.getUserIdByPasswordLogin(getUserIdByPwdLoginReqVO);
        if (!response.getIsSuccess()) {
            log.warn("{}:getUserIdByPasswordLogin业务异常, errorCode: {}, errorMessage: {}", ApiConstants.SERVICE_NAME,response.getErrorCode(),response.getErrorMessage());
            throw new BizException(response.getErrorCode(),response.getErrorMessage());
        }
        return response.getData();
    }
    public LoginRespVO getUserIdByPhone(GetUserIdByPhoneReqVO getUserIdByPhoneReqVO){
        Response<LoginRespVO> response=userServiceFeignClient.getUserIdByPhone(getUserIdByPhoneReqVO);
        if (!response.getIsSuccess()) {
            log.warn("{}:getUserIdByPhone业务异常, errorCode: {}, errorMessage: {}", ApiConstants.SERVICE_NAME,response.getErrorCode(),response.getErrorMessage());
            throw new BizException(response.getErrorCode(),response.getErrorMessage());
        }
        return response.getData();
    }
}
