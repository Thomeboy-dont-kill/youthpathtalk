package com.neu.youthpathtalk.notification.infrastructure.rpc;

import com.neu.youthpathtalk.enums.CommonResponseErrorCode;
import com.neu.youthpathtalk.exception.BizException;
import com.neu.youthpathtalk.post.api.client.PostServiceFeignClient;
import com.neu.youthpathtalk.post.api.constant.ApiConstants;
import com.neu.youthpathtalk.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Julien
 * @time 2026/06/14 10:02
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostRpcService {
    private final PostServiceFeignClient feignClient;

    public String getPostTitle(Long postId){
        Response<String> response = feignClient.getPostTitle(postId);
        if (response == null) {
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }
        if (!response.getIsSuccess()) {
            log.warn(
                    "{}:getPostTitle业务异常,errorCode:{},errorMessage:{}",
                    ApiConstants.SERVICE_NAME,
                    response.getErrorCode(),
                    response.getErrorMessage()
            );
            throw new BizException(
                    response.getErrorCode(),
                    response.getErrorMessage()
            );
        }
        return response.getData();
    }
}
