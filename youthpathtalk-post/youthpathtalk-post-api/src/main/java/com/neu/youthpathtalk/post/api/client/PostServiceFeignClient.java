package com.neu.youthpathtalk.post.api.client;

import com.neu.youthpathtalk.post.api.constant.ApiConstants;
import com.neu.youthpathtalk.post.api.vo.PostListVO;
import com.neu.youthpathtalk.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author Julien
 * @time 2026/04/06 14:28
 * @description 帖子服务FeignClient
 */
@FeignClient(
        name = ApiConstants.SERVICE_NAME,
        path = "/post"
)
public interface PostServiceFeignClient {
    @PostMapping("/batch")
    Response<List<PostListVO>> batchGetPostList(@RequestBody(required = false) List<Long> ids);
}
