package com.neu.youthpathtalk.vo.resp;

import com.neu.youthpathtalk.post.api.vo.PostListVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Julien
 * @time 2026/05/10 17:09
 * @description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchPostsRespVO {
    private List<PostListVO> list;
    private List<Object> searchAfter;
    private List<SearchFacetVO> facets;
    private String suggestKeyword;
}
