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
public class CommentController {
    private final CommentService commentService;
    @GetMapping("/{id}/plain-text")
    Response<String> getPlainText(@PathVariable("id") Long id) {
        return commentService.getPlainText(id);
    }

    @SaCheckLogin
    @PostMapping("/create")
    public Response<Void> create(@RequestBody @Valid CommentCreateReqVO req) {
        return commentService.create(req);
    }

    @SaCheckLogin
    @PostMapping("/reply")
    public Response<Void> reply(@RequestBody @Valid CommentReplyReqVO req) {
        return commentService.reply(req);
    }

    @PostMapping("/list")
    public Response<CursorPageRespVO<CommentRespVO, CommentCursor>> list(
            @RequestBody @Valid CommentListReqVO req
    ){
        return commentService.list(req);
    }

    @PostMapping("/reply/list")
    public Response<CursorPageRespVO<ReplyCommentRespVO, CreateTimeIdCursor>> listReply(
            @RequestBody @Valid ReplyCommentListReqVO req
    ) {
        return commentService.listReply(req);
    }

    @GetMapping("/conversation/{startId}")
    public Response<List<ReplyCommentRespVO>> conversation(@PathVariable Long startId) {
        return commentService.conversation(startId);
    }

    @SaCheckLogin
    @PostMapping("/update")
    public Response<Void> update(@RequestBody @Valid CommentUpdateReqVO req) {
        return commentService.update(req);
    }

    @SaCheckLogin
    @PostMapping("/delete")
    public Response<Void> delete(@RequestParam("id") Long id) {
        return commentService.delete(id);
    }

    @SaCheckLogin
    @PostMapping("/{id}/like")
    public Response<InteractRespVO> likeComment(@PathVariable Long id) {
        return commentService.likeComment(id);
    }
}
