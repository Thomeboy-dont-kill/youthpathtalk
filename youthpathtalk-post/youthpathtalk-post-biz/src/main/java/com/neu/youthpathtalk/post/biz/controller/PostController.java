package com.neu.youthpathtalk.post.biz.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.neu.youthpathtalk.post.biz.service.PostService;
import com.neu.youthpathtalk.post.biz.vo.req.PostListReqVO;
import com.neu.youthpathtalk.post.biz.vo.req.PostReqVO;
import com.neu.youthpathtalk.post.biz.vo.req.PostUpdateReqVO;
import com.neu.youthpathtalk.post.biz.vo.resp.*;
import com.neu.youthpathtalk.response.Response;
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
public class PostController {
    private final PostService postService;
    @GetMapping("/{id}/title")
    public Response<String> getPostTitle(@PathVariable Long id){
        return postService.getPostTitle(id);
    }

    @SaCheckLogin
    @SaCheckPermission("post:publish")
    @PostMapping("/publish")
    public Response<?> post(@Valid @RequestBody PostReqVO postReqVO){
        return postService.addPost(postReqVO);
    }

    @PostMapping("/list")
    public Response<CursorPageRespVO<PostListVO,Void>> getPostList(@Valid @RequestBody PostListReqVO postListReqVO){
        return postService.getPostList(postListReqVO);
    }

    @GetMapping("/{id}")
    public Response<PostDetailRespVO> getPostDetail(@PathVariable Long id){
        return postService.getPostDetail(id);
    }

    @SaCheckLogin
    @SaCheckPermission("post:update")
    @PostMapping("/update")
    public Response<?> updatePost(@Valid @RequestBody PostUpdateReqVO postUpdateReqVO){
        return postService.updatePost(postUpdateReqVO);
    }

    @SaCheckLogin
    @SaCheckPermission("post:delete")
    @DeleteMapping("/{id}")
    public Response<?> deletePost(@PathVariable Long id){
        return postService.deletePost(id);
    }

    @SaCheckLogin
    @SaCheckPermission("post:like")
    @PostMapping("/{id}/like")
    public Response<InteractRespVO> likePost(@PathVariable Long id){
        return postService.likePost(id);
    }

    @SaCheckLogin
    @SaCheckPermission("post:favorite")
    @PostMapping("/{id}/favorite")
    public Response<InteractRespVO> favoritePost(@PathVariable Long id){
        return postService.favoritePost(id);
    }

    @PostMapping("/batch")
    public Response<List<PostListVO>> batchGetPostList(@RequestBody(required = false) List<Long> ids){
        return postService.batchGetPostList(ids);
    }

    //没有用枚举
    @GetMapping("/hot/board")
    public Response<List<HotBoardItemVO>> getHotBoard(@RequestParam(defaultValue = "10") @Min(1) @Max(20) int limit){
        return postService.getHotBoard(limit);
    }
}
