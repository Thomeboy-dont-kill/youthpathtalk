package com.neu.youthpathtalk.controller;

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
}
