package com.neu.youthpathtalk.post.biz.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.neu.youthpathtalk.post.biz.service.PostService;
import com.neu.youthpathtalk.post.biz.vo.req.PostListReqVO;
import com.neu.youthpathtalk.post.biz.vo.req.PostReqVO;
import com.neu.youthpathtalk.post.biz.vo.req.PostUpdateReqVO;
import com.neu.youthpathtalk.post.biz.vo.resp.*;
import com.neu.youthpathtalk.response.Response;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Julien
 * @time 2026/03/21 11:22
 * @description 帖子控制器
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(
        name = "帖子模块",
        description = "帖子相关接口"
)
public class PostController {
    private final PostService postService;
    @Hidden
    @PostMapping("/batch")
    public Response<List<PostListVO>> batchGetPostList(@RequestBody(required = false) List<Long> ids){
        return postService.batchGetPostList(ids);
    }
    @Hidden
    @GetMapping("/{id}/title")
    public Response<String> getPostTitle(@PathVariable Long id){
        return postService.getPostTitle(id);
    }

    //后面再看要不要处理富文本节点
    @SaCheckLogin
    @SaCheckPermission("post:publish")
    @PostMapping("/publish")
    @Operation(
            summary = "发布帖子",
            description = """
                    用户发布帖子。

                    需要登录。

                    帖子内容采用富文本 JSON 字符串格式（由 TipTap 生成）。

                    前端提交的 content 必须是 TipTap 的 JSON 序列化结果，例如：

                    {
                      "type":"doc",
                      "content":[
                        {
                          "type":"text",
                          "text":"大三找不到实习怎么办"
                        },
                        {
                          "type":"mention",
                          "attrs":{
                            "userId":2001,
                            "username":"Thome"
                          }
                        }
                      ]
                    }
                    """
    )
    public Response<?> post(@Valid @RequestBody PostReqVO postReqVO){
        return postService.addPost(postReqVO);
    }

    @PostMapping("/list")
    @Operation(
            summary = "分页查询帖子列表",
            description = """
                无限滚动分页查询帖子列表。

                首次加载：
                - lastIsTop、lastIsEssence、lastCreateTime、lastId 均不传。

                加载下一页：
                - 必须携带上一页最后一条帖子的：
                  lastIsTop、lastIsEssence、lastCreateTime、lastId。

                排序规则：
                1. 置顶帖优先
                2. 精华帖优先
                3. 创建时间倒序
                4. ID 倒序

                返回结果中的 hasNext=true 表示还有下一页。
                """
    )
    public Response<CursorPageRespVO<PostListVO,Void>> getPostList(@Valid @RequestBody PostListReqVO postListReqVO){
        return postService.getPostList(postListReqVO);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "查询帖子详情",
            description = """
                根据帖子ID查询帖子详情。

                接口会返回：
                - 发帖用户信息
                - 帖子基础信息
                - 帖子正文内容
                - 当前登录用户是否已点赞
                - 当前登录用户是否已收藏

                未登录用户也可以访问，
                此时 liked、favorited 固定返回 false。
                """
    )
    public Response<PostDetailRespVO> getPostDetail(
            @Parameter(
                    description = "帖子ID",
                    required = true,
                    example = "5"
            )
            @PathVariable Long id){
        return postService.getPostDetail(id);
    }

    @SaCheckLogin
    @SaCheckPermission("post:update")
    @PostMapping("/update")
    @Operation(
            summary = "更新帖子",
            description = """
                更新当前用户自己发布的帖子。

                仅帖子作者可以修改帖子内容。

                支持修改板块、标题以及富文本内容。
                """
    )
    public Response<?> updatePost(@Valid @RequestBody PostUpdateReqVO postUpdateReqVO){
        return postService.updatePost(postUpdateReqVO);
    }

    @SaCheckLogin
    @SaCheckPermission("post:delete")
    @DeleteMapping("/{id}")
    @Operation(
            summary = "删除帖子",
            description = """
                删除当前用户自己发布的帖子。

                删除后帖子将无法继续浏览。
                """
    )
    public Response<?> deletePost(
            @Parameter(
                    description = "需要删除的帖子ID",
                    required = true,
                    example = "8"
            )
            @PathVariable Long id){
        return postService.deletePost(id);
    }

    @SaCheckLogin
    @SaCheckPermission("post:like")
    @PostMapping("/{id}/like")
    @Operation(
            summary = "点赞帖子",
            description = """
                对帖子执行点赞操作。

                如果用户已经点赞，则取消点赞。

                返回当前点赞状态以及帖子当前点赞数量。
                """
    )
    public Response<InteractRespVO> likePost(
            @Parameter(
                    description = "点赞的帖子ID",
                    required = true,
                    example = "8"
            )
            @PathVariable Long id){
        return postService.likePost(id);
    }

    @SaCheckLogin
    @SaCheckPermission("post:favorite")
    @PostMapping("/{id}/favorite")
    @Operation(
            summary = "收藏帖子",
            description = """
                对帖子执行收藏操作。

                如果用户已经收藏，则取消收藏。

                返回当前收藏状态以及帖子当前收藏数量。
                """
    )
    public Response<InteractRespVO> favoritePost(
            @Parameter(
                    description = "收藏的帖子ID",
                    required = true,
                    example = "8"
            )
            @PathVariable Long id){
        return postService.favoritePost(id);
    }

    //没有用枚举
    @GetMapping("/hot/board")
    @Operation(
            summary = "查看帖子热榜",
            description = """
                查看帖子热榜。

                根据帖子热度排序，返回热门帖子标题。

                默认返回10条，最多返回20条。
                """
    )
    public Response<List<HotBoardItemVO>> getHotBoard(
            @Parameter(
                    description = """
                        热榜显示数量。

                        默认10条。

                        最大20条。
                        """,
                    example = "10"
            )
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "热榜数量不能小于1")
            @Max(value = 20, message = "热榜数量不能超过20")
            int limit
    ){
        return postService.getHotBoard(limit);
    }
}
