package com.neu.youthpathtalk.service;

import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.vo.req.SearchPostsReqVO;
import com.neu.youthpathtalk.vo.resp.SearchPostsRespVO;
import com.neu.youthpathtalk.vo.resp.SuggestVO;

import java.util.List;

/**
 * @author Julien
 * @time 2026/05/07 21:10
 * @description
 */
public interface SearchService {
    Response<SearchPostsRespVO> searchPosts(SearchPostsReqVO request);
    Response<List<SuggestVO>> suggestTitles(String keyword);
    Response<List<String>> getSearchHistory();
    Response<Void> clearSearchHistory();
    Response<Void> deleteSearchHistory(String keyword);
}
