package com.neu.youthpathtalk.post.biz.service;

import com.neu.youthpathtalk.post.biz.vo.req.CursorPageReqVO;
import com.neu.youthpathtalk.post.biz.vo.req.PostReqVO;
import com.neu.youthpathtalk.post.biz.vo.req.PostUpdateReqVO;
import com.neu.youthpathtalk.post.biz.vo.resp.*;
import com.neu.youthpathtalk.response.Response;

import java.util.List;

/**
 * @author Julien
 * @time 2026/03/21 11:54
 * @description Post服务层
 */
public interface PostService {
    Response<?> addPost(PostReqVO postReqVO);
    Response<CursorPageRespVO<PostListVO>> getPostList(CursorPageReqVO cursorPageReqVO);
    Response<PostDetailRespVO> getPostDetail(Long id);
    Response<?> updatePost(PostUpdateReqVO postUpdateReqVO);
    Response<?> deletePost(Long id);
    Response<PostLikeRespVO> likePost(Long id);
    Response<List<PostListVO>> batchGetPostList(List<Long> ids);
    Response<List<HotBoardItemVO>> getHotBoard(int limit);
}
