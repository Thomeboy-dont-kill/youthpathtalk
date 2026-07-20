package com.neu.youthpathtalk.user.biz.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.neu.youthpathtalk.post.api.vo.PostListVO;
import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.user.biz.constants.PageConstants;
import com.neu.youthpathtalk.user.biz.service.UserService;
import com.neu.youthpathtalk.user.biz.vo.resp.*;
import com.neu.youthpathtalk.user.biz.vo.req.*;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Julien
 * @time 2026/03/05 19:29
 * @description 用户服务控制器
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(
        name = "用户模块",
        description = "用户相关接口"
)
public class UserController {
    private final UserService userService;

    @Hidden
    @PostMapping("/phone/check")
    public Response<Boolean> checkPhoneRegistered(@Validated @RequestBody CheckPhoneRegisteredReqVO checkPhoneRegisteredReqVO){
        return userService.checkPhoneRegistered(checkPhoneRegisteredReqVO);
    }
    @Hidden
    @PostMapping("/add")
    public Response<LoginRespVO> addUser(@Validated @RequestBody AddUserReqVO addUserReqVO){
        return userService.addUser(addUserReqVO);
    }
    @Hidden
    @PostMapping("/id/pwd")
    public Response<LoginRespVO> getUserIdByPasswordLogin(@Validated @RequestBody GetUserIdByPwdLoginReqVO getUserIdByPwdLoginReqVO){
        return userService.getUserIdByPasswordLogin(getUserIdByPwdLoginReqVO);
    }
    @Hidden
    @PostMapping("/id/sms")
    public Response<LoginRespVO> getUserIdByPhone(@Validated @RequestBody GetUserIdByPhoneReqVO getUserIdByPhoneReqVO){
        return userService.getUserIdByPhone(getUserIdByPhoneReqVO);
    }
    @Hidden
    @GetMapping("/info")
    public Response<UserInfoRespVO> getUserInfo(@RequestParam("userId") Long userId){
        return userService.getUserInfo(userId);
    }
    @Hidden
    @PostMapping("/mention/info/batch")
    public Response<Map<Long, String>> getMentionInfoBatch(@RequestBody Set<Long> userIds) {
        return userService.getMentionInfoBatch(userIds);
    }
/*
    @PostMapping("/ids/by-usernames")
    public Response<List<Long>> getIdsByUsernames(@RequestBody List<String> usernames) {
        return userService.getIdsByUsernames(usernames);
    }
*/

    @SaCheckLogin
    @SaCheckPermission("user:browse:history:recent")
    @GetMapping("/browse/history/recent")
    @Operation(
            summary = "获取最近浏览记录",
            description = """
                    获取当前登录用户最近浏览过的帖子记录。

                    返回结果按照浏览时间倒序排列：

                    第一条表示最近一次浏览的帖子。

                    系统最多保留最近 50 条浏览记录，
                    超出部分会自动淘汰最早的浏览记录。

                    仅返回当前登录用户自己的浏览历史。
                    """
    )
    public Response<List<BrowseHistoryVO>> getBrowseHistory(){
        return userService.getBrowseHistory();
    }

    @SaCheckLogin
    @SaCheckPermission("user:browse:history:recent")
    @DeleteMapping("/browse/history/recent/{postId}")
    @Operation(
            summary = "删除单条浏览记录",
            description = """
                删除当前登录用户指定帖子的浏览记录。

                仅删除当前登录用户自己的浏览记录，
                不影响其他用户。

                如果该浏览记录不存在，接口仍返回成功。
                """
    )
    public Response<Void> deleteBrowseHistory(
            @Parameter(
                    description = "需要删除浏览记录的帖子ID",
                    required = true,
                    example = "5"
            )
            @NotNull
            @PathVariable("postId")
            Long postId){
        return userService.deleteBrowseHistory(postId);
    }

    @SaCheckLogin
    @SaCheckPermission("user:browse:history:recent")
    @DeleteMapping("/browse/history/recent")
    @Operation(
            summary = "清空浏览记录",
            description = """
                清空当前登录用户的全部最近浏览记录。

                操作成功后，用户最近浏览页面将为空。
                """
    )
    public Response<Void> clearBrowseHistory(){
        return userService.clearBrowseHistory();
    }

    @SaCheckLogin
    @SaCheckPermission("user:like:history")
    @GetMapping("/like/history")
    @Operation(
            summary = "查看点赞记录",
            description = """
                分页查询当前登录用户点赞过的帖子记录。

                返回结果按照点赞时间倒序排列。

                默认：
                - pageNo = 1
                - pageSize = 20

                限制：
                - pageNo >= 1
                - pageSize 范围：1~50
                """
    )
    public Response<PageRespVO<PostListVO>> getLikeHistory(

            @Parameter(
                    description = "页码，从1开始",
                    example = "1"
            )
            @RequestParam(defaultValue = PageConstants.DEFAULT_PAGE_NO)
            @Min(1)
            Integer pageNo,

            @Parameter(
                    description = "每页数量，范围1~50，默认20",
                    example = "20"
            )
            @RequestParam(defaultValue = PageConstants.DEFAULT_PAGE_SIZE)
            @Min(1)
            @Max(50)
            Integer pageSize){
        return userService.getLikeHistory(pageNo,pageSize);
    }

    @SaCheckLogin
    @SaCheckPermission("user:favorite:history")
    @GetMapping("/favorite/history")
    @Operation(
            summary = "查看收藏记录",
            description = """
                分页查询当前登录用户收藏过的帖子记录。

                返回结果按照收藏时间倒序排列。

                默认：
                - pageNo = 1
                - pageSize = 20

                限制：
                - pageNo >= 1
                - pageSize 范围：1~50
                """
    )
    public Response<PageRespVO<PostListVO>> getFavoriteHistory(

            @Parameter(
                    description = "页码，从1开始",
                    example = "1"
            )
            @RequestParam(defaultValue = PageConstants.DEFAULT_PAGE_NO)
            @Min(1)
            Integer pageNo,

            @Parameter(
                    description = "每页数量，范围1~50，默认20",
                    example = "20"
            )
            @RequestParam(defaultValue = PageConstants.DEFAULT_PAGE_SIZE)
            @Min(1)
            @Max(50)
            Integer pageSize){
        return userService.getFavoriteHistory(pageNo,pageSize);
    }
    //没有用枚举
    @GetMapping("/creator/weekly/rank")
    @Operation(
            summary = "查看创作者周榜",
            description = """
                获取创作者周排行榜。

                排行榜按照创作者最近一周的热度分数(score)降序排序。

                默认返回前10名。

                限制：
                - limit 范围：1~20
                - 默认：10
                """
    )
    public Response<List<CreatorWeeklyRankRespVO>> getWeeklyRank(

            @Parameter(
                    description = "返回排行榜条数，范围1~20，默认10",
                    example = "10"
            )
            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(20)
            int limit){
        return userService.getWeeklyRank(limit);
    }
}
