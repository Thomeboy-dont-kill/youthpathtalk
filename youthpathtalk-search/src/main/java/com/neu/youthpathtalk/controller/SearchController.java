package com.neu.youthpathtalk.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.service.SearchService;
import com.neu.youthpathtalk.vo.req.SearchPostsReqVO;
import com.neu.youthpathtalk.vo.resp.SearchPostsRespVO;
import com.neu.youthpathtalk.vo.resp.SuggestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "搜索模块",
        description = "帖子搜索相关接口"
)
public class SearchController {
    private final SearchService searchService;

    @PostMapping("/post")
    @Operation(
            summary = "搜索帖子",
            description = """
                    根据关键词搜索帖子。

                    支持：

                    1. 关键词全文搜索
                    2. 板块筛选
                    3. 发布时间筛选
                    4. 自定义时间区间筛选
                    5. 多种排序方式
                    6. 游标分页
                    7. 搜索纠错建议

                    特殊规则：

                    - 当 startTime 或 endTime 不为空时，
                      publishTimeRange 将被忽略。

                    - 首次搜索时 searchAfter 不传。

                    - 后续分页请求必须原样携带上一页返回的
                      searchAfter，不允许修改。

                    - 仅当纯英文关键词搜索结果为空时，
                      系统可能返回 suggestKeyword 作为纠错建议。
                    """
    )
    public Response<SearchPostsRespVO> searchPost(@Validated @RequestBody SearchPostsReqVO searchPostsReqVO){
        return searchService.searchPosts(searchPostsReqVO);
    }

    @GetMapping("/post/suggest")
    @Operation(
            summary = "搜索自动补全",
            description = """
                    根据用户输入的关键词返回帖子标题补全建议。

                    典型使用场景：

                    - 搜索框输入时实时调用
                    - 下拉展示候选标题
                    - 用户点击候选项后可直接搜索或跳转帖子详情

                    返回结果按相关性排序。
                    """
    )
    public Response<List<SuggestVO>> suggest(
            @Parameter(
                    description = "用户输入的搜索关键词",
                    example = "大三实",
                    required = true
            )
            @RequestParam
            @NotBlank(message = "关键词不能为空")
            String keyword) {
        return searchService.suggestTitles(keyword);
    }

    @SaCheckLogin
    @GetMapping("/history")
    @Operation(
            summary = "获取搜索历史",
            description = """
                获取当前登录用户最近的搜索历史记录。

                返回结果按照搜索时间倒序排列：

                第一条为最近一次搜索。

                仅返回当前登录用户自己的搜索历史。
                """
    )
    public Response<List<String>> getSearchHistory(){
        return searchService.getSearchHistory();
    }

    @SaCheckLogin
    @DeleteMapping("/history")
    @Operation(
            summary = "清空搜索历史",
            description = """
                清空当前登录用户的全部搜索历史记录。

                操作成功后，该用户的搜索历史将全部删除。
                """
    )
    public Response<Void> clearSearchHistory(){
        return searchService.clearSearchHistory();
    }

    @SaCheckLogin
    @DeleteMapping("/history/{keyword}")
    @Operation(
            summary = "删除单条搜索历史",
            description = """
                删除当前登录用户指定关键词对应的搜索历史记录。

                如果该关键词不存在，则接口仍返回成功。
                """
    )
    public Response<Void> deleteSearchHistory(@PathVariable String keyword){
        return searchService.deleteSearchHistory(keyword);
    }
}
