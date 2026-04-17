package com.neu.youthpathtalk.user.biz.service;

import com.neu.youthpathtalk.post.api.vo.PostListVO;
import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.user.biz.vo.rep.*;
import com.neu.youthpathtalk.user.biz.vo.req.*;

import java.util.List;

/**
 * @author Julien
 * @time 2026/03/05 19:29
 * @description 用户service层
 */
public interface UserService {
    Response<LoginRepVO> addUser(AddUserReqVO addUserReqVO);
    Response<Boolean> checkPhoneRegistered(CheckPhoneRegisteredReqVO checkPhoneRegisteredReqVO);
    Response<LoginRepVO> getUserIdByPasswordLogin(GetUserIdByPwdLoginReqVO getUserIdByPwdLoginReqVO);
    Response<LoginRepVO> getUserIdByPhone(GetUserIdByPhoneReqVO getUserIdByPhoneReqVO);
    Response<UserInfoRespVO> getUserInfo(Long userId);
    Response<List<BrowseHistoryVO>> getBrowseHistory();
    Response<PageRespVO<PostListVO>> getLikeHistory(PageReqVO pageReqVO);
    Response<List<CreatorWeeklyRankRespVO>> getWeeklyRank(int limit);
}
