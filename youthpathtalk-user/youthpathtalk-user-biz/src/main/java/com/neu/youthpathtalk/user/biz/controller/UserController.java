package com.neu.youthpathtalk.user.biz.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.neu.youthpathtalk.post.api.vo.PostListVO;
import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.user.biz.constants.PageConstants;
import com.neu.youthpathtalk.user.biz.service.UserService;
import com.neu.youthpathtalk.user.biz.vo.rep.*;
import com.neu.youthpathtalk.user.biz.vo.req.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Julien
 * @time 2026/03/05 19:29
 * @description 用户服务控制器
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/phone/check")
    public Response<Boolean> checkPhoneRegistered(@Validated @RequestBody CheckPhoneRegisteredReqVO checkPhoneRegisteredReqVO){
        return userService.checkPhoneRegistered(checkPhoneRegisteredReqVO);
    }
    @PostMapping("/add")
    public Response<LoginRepVO> addUser(@Validated @RequestBody AddUserReqVO addUserReqVO){
        return userService.addUser(addUserReqVO);
    }
    @PostMapping("/id/pwd")
    public Response<LoginRepVO> getUserIdByPasswordLogin(@Validated @RequestBody GetUserIdByPwdLoginReqVO getUserIdByPwdLoginReqVO){
        return userService.getUserIdByPasswordLogin(getUserIdByPwdLoginReqVO);
    }
    @PostMapping("/id/sms")
    public Response<LoginRepVO> getUserIdByPhone(@Validated @RequestBody GetUserIdByPhoneReqVO getUserIdByPhoneReqVO){
        return userService.getUserIdByPhone(getUserIdByPhoneReqVO);
    }
    @GetMapping("/info")
    public Response<UserInfoRespVO> getUserInfo(@RequestParam("userId") Long userId){
        return userService.getUserInfo(userId);
    }

    @SaCheckLogin
    @SaCheckPermission("user:browse:history:recent")
    @GetMapping("/browse/history/recent")
    public Response<List<BrowseHistoryVO>> getBrowseHistory(){
        return userService.getBrowseHistory();
    }

    @SaCheckLogin
    @SaCheckPermission("user:like:history")
    @GetMapping("/like/history")
    public Response<PageRespVO<PostListVO>> getLikeHistory(@RequestParam(defaultValue = PageConstants.DEFAULT_PAGE_NO) @Min(1)Integer pageNo,
                                                           @RequestParam(defaultValue = PageConstants.DEFAULT_PAGE_SIZE) @Min(1)@Max(50)Integer pageSize){
        return userService.getLikeHistory(pageNo,pageSize);
    }
    @GetMapping("/creator/weekly/rank")
    public Response<List<CreatorWeeklyRankRespVO>> getWeeklyRank(@RequestParam(defaultValue = "10") int limit){
        return userService.getWeeklyRank(limit);
    }
}
