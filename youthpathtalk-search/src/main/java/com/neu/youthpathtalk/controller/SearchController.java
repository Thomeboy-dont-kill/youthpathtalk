package com.neu.youthpathtalk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.service.SearchService;
import com.neu.youthpathtalk.vo.req.SearchPostsReqVO;
import com.neu.youthpathtalk.vo.resp.SearchPostsRespVO;
import com.neu.youthpathtalk.vo.resp.SuggestVO;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Julien
 * @time 2026/05/10 14:48
 * @description
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;

    @PostMapping("/post")
    public Response<SearchPostsRespVO> searchPost(@Validated @RequestBody SearchPostsReqVO searchPostsReqVO){
        return searchService.searchPosts(searchPostsReqVO);
    }

    @GetMapping("/post/suggest")
    public Response<List<SuggestVO>> suggest(@RequestParam @NotBlank(message = "关键词不能为空") String keyword) {
        return searchService.suggestTitles(keyword);
    }

    @SaCheckLogin
    @GetMapping("/history")
    public Response<List<String>> getSearchHistory(){
        return searchService.getSearchHistory();
    }

    @SaCheckLogin
    @DeleteMapping("/history")
    public Response<Void> clearSearchHistory(){
        return searchService.clearSearchHistory();
    }

    @SaCheckLogin
    @DeleteMapping("/history/{keyword}")
    public Response<Void> deleteSearchHistory(@PathVariable String keyword){
        return searchService.deleteSearchHistory(keyword);
    }
}
