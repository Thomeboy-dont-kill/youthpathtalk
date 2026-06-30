package com.neu.youthpathtalk.notification.infrastructure.rpc;

import com.neu.youthpathtalk.enums.CommonResponseErrorCode;
import com.neu.youthpathtalk.exception.BizException;
import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.user.api.client.UserServiceFeignClient;
import com.neu.youthpathtalk.user.api.constant.ApiConstants;
import com.neu.youthpathtalk.user.api.vo.resp.UserInfoRespVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Julien
 * @time 2026/03/09 21:03
 * @description 远程调用用户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRpcService {
    private final UserServiceFeignClient feignClient;
    public UserInfoRespVO getUserInfo(Long userId){
        Response<UserInfoRespVO> response= feignClient.getUserInfo(userId);

        if (response == null) {
            throw new BizException(
                    CommonResponseErrorCode.SYSTEM_ERROR
            );
        }

        if (!response.getIsSuccess()) {
            log.warn("{}:getUserInfo业务异常, errorCode: {}, errorMessage: {}", ApiConstants.SERVICE_NAME,response.getErrorCode(),response.getErrorMessage());
            throw new BizException(response.getErrorCode(),response.getErrorMessage());
        }
        return response.getData();
    }
}
