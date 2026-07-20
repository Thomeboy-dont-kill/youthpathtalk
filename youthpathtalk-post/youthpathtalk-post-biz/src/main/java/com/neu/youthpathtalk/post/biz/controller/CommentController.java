package com.neu.youthpathtalk.post.biz.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.neu.youthpathtalk.post.biz.service.CommentService;
import com.neu.youthpathtalk.post.biz.vo.cursor.CommentCursor;
import com.neu.youthpathtalk.post.biz.vo.cursor.CreateTimeIdCursor;
import com.neu.youthpathtalk.post.biz.vo.req.CommentCreateReqVO;
import com.neu.youthpathtalk.post.biz.vo.req.CommentListReqVO;
import com.neu.youthpathtalk.post.biz.vo.req.CommentReplyReqVO;
import com.neu.youthpathtalk.post.biz.vo.req.ReplyCommentListReqVO;
import com.neu.youthpathtalk.post.biz.vo.resp.*;
import com.neu.youthpathtalk.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.neu.youthpathtalk.post.biz.vo.req.CommentUpdateReqVO;

import java.util.List;

/**
 * @author Julien
 * @time 2026/06/02 23:20
 * @description
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Tag(
        name = "帖子模块",
        description = "评论相关接口"
)
public class CommentController {
    private final CommentService commentService;
    @GetMapping("/{id}/plain-text")
    Response<String> getPlainText(@PathVariable("id") Long id) {
        return commentService.getPlainText(id);
    }

    @SaCheckLogin
    @PostMapping("/create")
    @Operation(
            summary = "发表评论",
            description = """
                用户对指定帖子发表一级评论。

                评论内容支持富文本格式。

                支持：
                - 普通文本
                - @用户
                - 图片
                """
    )
    public Response<Void> create(@RequestBody @Valid CommentCreateReqVO req) {
        return commentService.create(req);
    }

    @SaCheckLogin
    @PostMapping("/reply")
    @Operation(
            summary = "回复评论",
            description = """
                用户回复指定评论。

                回复内容支持富文本格式。

                支持：
                - 普通文本
                - @用户
                - 图片
                """
    )
    public Response<Void> reply(@RequestBody @Valid CommentReplyReqVO req) {
        return commentService.reply(req);
    }

    @PostMapping("/list")
    @Operation(
            summary = "查看帖子评论列表",
            description = """
                分页查询指定帖子的一级评论（根评论）。

                使用游标分页。

                首次请求无需传递 cursor。

                返回结果按照评论热度（hotScore）降序排序。
                """
    )
    public Response<CursorPageRespVO<CommentRespVO, CommentCursor>> list(
            @RequestBody @Valid CommentListReqVO req
    ){
        return commentService.list(req);
    }

    @PostMapping("/reply/list")
    @Operation(
            summary = "查看评论回复列表",
            description = """
                分页查询指定根评论下的所有回复。

                包括：
                - 二级回复
                - 三级回复
                - 更深层级回复

                使用游标分页。

                首次请求无需传递 cursor。

                返回结果按照回复创建时间降序排序。
                """
    )
    public Response<CursorPageRespVO<ReplyCommentRespVO, CreateTimeIdCursor>> listReply(
            @RequestBody @Valid ReplyCommentListReqVO req
    ) {
        return commentService.listReply(req);
    }

    @GetMapping("/conversation/{startId}")
    @Operation(
            summary = "查看评论对话",
            description = """
                根据起始评论ID，递归查询该评论及其所有后代回复。

                返回内容包括：
                1. 当前评论本身；
                2. 所有子回复。

                返回列表按照创建时间升序排列，
                用于展示完整评论对话链。

                适用于查看某条回复的上下文对话，
                不用于评论列表和回复列表查询（即startId不能是根评论和直接回复根评论的ID）。
                """
    )
    @Parameter(
            name = "startId",
            description = """
                对话起始评论ID。

                用于确定需要展开的评论对话链。
                
                返回结果包含该评论以及其所有后代回复。

                通常传入目标评论的父评论ID，
                以展示完整上下文。
                """,
            required = true,
            example = "2001"
    )
    public Response<List<ReplyCommentRespVO>> conversation(@PathVariable Long startId) {
        return commentService.conversation(startId);
    }

    @SaCheckLogin
    @PostMapping("/update")
    @Operation(
            summary = "编辑评论",
            description = """
                用户编辑自己发布的评论。

                仅允许编辑评论发表后 5 分钟内的内容。

                编辑时会重新处理富文本内容，包括：
                - 校验富文本节点结构
                - 处理 @ 用户引用
                - 生成纯文本内容用于通知
                """
    )
    public Response<Void> update(@RequestBody @Valid CommentUpdateReqVO req) {
        return commentService.update(req);
    }

    @SaCheckLogin
    @PostMapping("/delete")
    @Operation(
            summary = "删除评论",
            description = """
                删除用户自己发布的评论。

                删除采用软删除方式：
                - 不会物理删除评论数据
                - status 会更新为删除状态
                - 删除后的评论内容前端展示为 [评论已删除]

                仅允许删除本人发布的评论。
                """
    )
    @Parameter(
            name = "id",
            description = "评论ID",
            required = true,
            example = "2001"
    )
    public Response<Void> delete(@RequestParam("id") Long id) {
        return commentService.delete(id);
    }

    @SaCheckLogin
    @PostMapping("/{id}/like")
    @Operation(
            summary = "点赞评论",
            description = """
                用户点赞评论。
                
                用户需先登录

                如果当前用户未点赞，则执行点赞操作；
                如果当前用户已经点赞，则取消点赞。

                返回当前用户互动状态以及评论当前点赞数量。
                """
    )
    @Parameter(
            name = "id",
            description = "评论ID",
            required = true,
            example = "2001"
    )
    public Response<InteractRespVO> likeComment(@PathVariable Long id) {
        return commentService.likeComment(id);
    }
}
