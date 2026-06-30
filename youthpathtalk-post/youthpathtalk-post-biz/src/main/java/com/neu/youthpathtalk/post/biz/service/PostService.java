package com.neu.youthpathtalk.post.biz.service;

import com.neu.youthpathtalk.post.biz.vo.req.PostListReqVO;
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
    Response<String> getPostTitle(Long id);
    Response<?> addPost(PostReqVO postReqVO);
    Response<CursorPageRespVO<PostListVO,Void>> getPostList(PostListReqVO postListReqVO);
    Response<PostDetailRespVO> getPostDetail(Long id);
    Response<?> updatePost(PostUpdateReqVO postUpdateReqVO);
    Response<?> deletePost(Long id);
    Response<InteractRespVO> likePost(Long id);
    Response<InteractRespVO> favoritePost(Long id);
    Response<List<PostListVO>> batchGetPostList(List<Long> ids);
    Response<List<HotBoardItemVO>> getHotBoard(int limit);
}
