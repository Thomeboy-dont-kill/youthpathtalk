package com.neu.youthpathtalk.post.biz.rpc;

import com.neu.youthpathtalk.exception.BizException;
import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.user.api.client.UserServiceFeignClient;
import com.neu.youthpathtalk.user.api.constant.ApiConstants;
import com.neu.youthpathtalk.user.api.vo.resp.UserInfoRespVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

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

    public UserInfoRespVO getUserInfo(Long userId){
        Response<UserInfoRespVO> response = userServiceFeignClient.getUserInfo(userId);
        if (!response.getIsSuccess()) {
            log.warn("{}:getUserInfo业务异常, errorCode: {}, errorMessage: {}", ApiConstants.SERVICE_NAME,response.getErrorCode(),response.getErrorMessage());
            throw new BizException(response.getErrorCode(),response.getErrorMessage());
        }
        return response.getData();
    }

    public Map<Long, String> getMentionInfoBatch(Set<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        Response<Map<Long, String>> response = userServiceFeignClient.getMentionInfoBatch(userIds);
        if (!response.getIsSuccess()) {
            log.warn(
                    "{}:getMentionInfoBatch业务异常,errorCode:{},errorMessage:{}",
                    ApiConstants.SERVICE_NAME,
                    response.getErrorCode(),
                    response.getErrorMessage()
            );
            throw new BizException(
                    response.getErrorCode(),
                    response.getErrorMessage()
            );
        }
        return Optional.ofNullable(response.getData())
                .orElse(Collections.emptyMap());
    }

/*
    public List<Long> getIdsByUsernames(List<String> usernames){
        Response<List<Long>> response= userServiceFeignClient.getIdsByUsernames(usernames);
        if (!response.getIsSuccess()) {
            log.warn("{}:getIdsByUsernames业务异常, errorCode: {}, errorMessage: {}", ApiConstants.SERVICE_NAME,response.getErrorCode(),response.getErrorMessage());
            throw new BizException(response.getErrorCode(),response.getErrorMessage());
        }
        return response.getData();
    }
*/
}
