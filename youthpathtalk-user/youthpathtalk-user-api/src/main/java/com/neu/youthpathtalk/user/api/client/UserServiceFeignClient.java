package com.neu.youthpathtalk.user.api.client;

import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.user.api.constant.ApiConstants;
import com.neu.youthpathtalk.user.api.vo.resp.LoginRespVO;
import com.neu.youthpathtalk.user.api.vo.resp.UserInfoRespVO;
import com.neu.youthpathtalk.user.api.vo.req.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Julien
 * @time 2026/03/09 16:56
 * @description 用户服务FeignClient
 */
@FeignClient(
        name= ApiConstants.SERVICE_NAME,
        path = "/user"
)
public interface UserServiceFeignClient {
    @PostMapping("/phone/check")
    Response<Boolean> checkPhoneRegistered(@RequestBody CheckPhoneRegisteredReqVO checkPhoneRegisteredReqVO);
    @PostMapping("/add")
    Response<LoginRespVO> addUser(@RequestBody AddUserReqVO addUserReqVO);
    @PostMapping("/id/pwd")
    Response<LoginRespVO> getUserIdByPasswordLogin(@RequestBody GetUserIdByPwdLoginReqVO getUserIdByPwdLoginReqVO);
    @PostMapping("/id/sms")
    Response<LoginRespVO> getUserIdByPhone(@RequestBody GetUserIdByPhoneReqVO getUserIdByPhoneReqVO);
    @GetMapping("/info")
    Response<UserInfoRespVO> getUserInfo(@RequestParam("userId") Long userId);
    @PostMapping("/mention/info/batch")
    Response<Map<Long, String>> getMentionInfoBatch(@RequestBody Set<Long> userIds);
    /*
    @PostMapping("/ids/by-usernames")
    Response<List<Long>> getIdsByUsernames(@RequestBody List<String> usernames);
    */
}
