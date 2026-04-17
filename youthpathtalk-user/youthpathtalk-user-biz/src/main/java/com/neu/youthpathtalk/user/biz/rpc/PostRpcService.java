package com.neu.youthpathtalk.user.biz.rpc;

import com.neu.youthpathtalk.exception.BizException;
import com.neu.youthpathtalk.post.api.client.PostServiceFeignClient;
import com.neu.youthpathtalk.post.api.constant.ApiConstants;
import com.neu.youthpathtalk.post.api.vo.PostListVO;
import com.neu.youthpathtalk.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Julien
 * @time 2026/04/06 14:58
 * @description 远程调用帖子服务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostRpcService {
    private final PostServiceFeignClient feignClient;

    public List<PostListVO> batchGetPostList(List<Long> postIds){
        Response<List<PostListVO>> response=feignClient.batchGetPostList(postIds);
        if (Boolean.FALSE.equals(response.getIsSuccess())){
            log.warn("{}:checkPhoneRegistered业务异常, errorCode: {}, errorMessage: {}", ApiConstants.SERVICE_NAME,response.getErrorCode(),response.getErrorMessage());
            throw new BizException(response.getErrorCode(),response.getErrorMessage());
        }
        return response.getData();
    }
}
