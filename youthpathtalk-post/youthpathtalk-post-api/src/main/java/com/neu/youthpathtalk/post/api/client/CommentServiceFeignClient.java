package com.neu.youthpathtalk.post.api.client;

import com.neu.youthpathtalk.post.api.constant.ApiConstants;
import com.neu.youthpathtalk.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Julien
 * @time 2026/06/13 16:36
 * @description
 */
@FeignClient(
        name = ApiConstants.SERVICE_NAME,
        contextId = "commentFeignClient",
        path = "/comment"
)
public interface CommentServiceFeignClient {
    @GetMapping("/{id}/plain-text")
    Response<String> getPlainText(@PathVariable("id") Long id);
}
