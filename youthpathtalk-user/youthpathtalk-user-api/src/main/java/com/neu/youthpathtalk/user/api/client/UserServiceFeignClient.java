package com.neu.youthpathtalk.user.api.client;

import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.user.api.constant.ApiConstants;
import com.neu.youthpathtalk.user.api.vo.rep.LoginRepVO;
import com.neu.youthpathtalk.user.api.vo.rep.UserInfoRespVO;
import com.neu.youthpathtalk.user.api.vo.req.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
    Response<LoginRepVO> addUser(@RequestBody AddUserReqVO addUserReqVO);
    @PostMapping("/id/pwd")
    Response<LoginRepVO> getUserIdByPasswordLogin(@RequestBody GetUserIdByPwdLoginReqVO getUserIdByPwdLoginReqVO);
    @PostMapping("/id/sms")
    Response<LoginRepVO> getUserIdByPhone(@RequestBody GetUserIdByPhoneReqVO getUserIdByPhoneReqVO);
    @GetMapping("/info")
    Response<UserInfoRespVO> getUserInfo(@RequestParam("userId") Long userId);
}
