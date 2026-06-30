package com.neu.youthpathtalk.post.biz.service;

import com.neu.youthpathtalk.post.biz.vo.cursor.CommentCursor;
import com.neu.youthpathtalk.post.biz.vo.cursor.CreateTimeIdCursor;
import com.neu.youthpathtalk.post.biz.vo.req.*;
import com.neu.youthpathtalk.post.biz.vo.resp.*;
import com.neu.youthpathtalk.response.Response;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author Julien
 * @time 2026/06/02 23:26
 * @description
 */
public interface CommentService {
    Response<String> getPlainText(Long id);
    Response<Void> create(CommentCreateReqVO req);
    Response<Void> reply(CommentReplyReqVO req);
    Response<CursorPageRespVO<CommentRespVO, CommentCursor>> list(CommentListReqVO req);
    Response<CursorPageRespVO<ReplyCommentRespVO, CreateTimeIdCursor>>  listReply(ReplyCommentListReqVO req);
    Response<List<ReplyCommentRespVO>> conversation(Long startId);
    Response<Void> update(CommentUpdateReqVO req);
    Response<Void> delete(Long id);
    Response<InteractRespVO> likeComment(Long id);
}
