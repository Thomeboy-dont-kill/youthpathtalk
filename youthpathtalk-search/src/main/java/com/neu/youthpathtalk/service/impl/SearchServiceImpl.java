package com.neu.youthpathtalk.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.json.JsonData;
import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.neu.youthpathtalk.constants.SearchConstants;
import com.neu.youthpathtalk.document.PostDocument;
import com.neu.youthpathtalk.enums.PageSizeEnum;
import com.neu.youthpathtalk.post.api.vo.PostListVO;
import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.rpc.PostRpcService;
import com.neu.youthpathtalk.service.SearchService;
import com.neu.youthpathtalk.vo.req.SearchPostsReqVO;
import com.neu.youthpathtalk.vo.resp.SearchPostsRespVO;
import com.neu.youthpathtalk.vo.resp.SuggestVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Julien
 * @time 2026/05/10 14:54
 * @description
 */
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final PostRpcService postRpcService;
    private final ElasticsearchOperations elasticsearchOperations;

    public Response<SearchPostsRespVO> searchPosts(SearchPostsReqVO request){
        BoolQuery.Builder boolBuilder=new BoolQuery.Builder();
        boolBuilder.must(m->m
                .multiMatch(mm->mm
                        .query(request.getKeyword())
                        .fields(
                                SearchConstants.FIELD_TITLE_WITH_BOOST,
                                SearchConstants.FIELD_TITLE_PINYIN_WITH_BOOST,
                                SearchConstants.FIELD_CONTENT
                        )
                        .operator(Operator.Or)
                        //不知道设置多少合适
                        .minimumShouldMatch(SearchConstants.MINIMUM_SHOULD_MATCH)
                        //对于中文搜索不推荐，暂时不启用
//                        .fuzziness(SearchConstants.FUZZINESS_AUTO)
                )
        );
        if (Objects.nonNull(request.getBoardType())){
            boolBuilder.filter(f->f
                    .term(t->t
                            .field(SearchConstants.FIELD_BOARDTYPE)
                            .value(request.getBoardType().getType())
                    )
            );
        }
        if (Objects.nonNull(request.getUserId())){
            boolBuilder.filter(f->f
                    .term(t->t
                            .field("userId")
                            .value(request.getUserId())
                    )
            );
        }
        long now=System.currentTimeMillis();
        NativeQuery query=NativeQuery.builder()
                .withQuery(q->q.functionScore(fs-> fs
                                            .query(bq -> bq.bool(boolBuilder.build()))
                                .functions(f->f.scriptScore(ss->ss
                                        .script(sc->sc
                                                .source("""
                                                    double relevanceScore = _score;
                                                    double likeCount = doc['likeCount'].size() == 0 ? 0 : doc['likeCount'].value;
                                                    double viewCount = doc['viewCount'].size() == 0 ? 0 : doc['viewCount'].value;
                                                    double favoriteCount = doc['favoriteCount'].size() == 0 ? 0 : doc['favoriteCount'].value;
                                                    double commentCount = doc['commentCount'].size() == 0 ? 0 : doc['commentCount'].value;
                                                    double createTime =
                                                                    doc['createTime'].size() == 0
                                                                    ? params.now
                                                                    : doc['createTime']
                                                                        .value
                                                                        .toInstant()
                                                                        .toEpochMilli();
                                                    double days = (params.now - createTime) / 86400000.0;
                                                    double timeDecay = Math.exp(-days / 7);
                                                    return relevanceScore * 0.5
                                                        + Math.log10(1 + likeCount) * 0.08
                                                        + Math.log10(1 + viewCount) * 0.02
                                                        + Math.log10(1 + favoriteCount) * 0.05
                                                        + Math.log10(1 + commentCount) * 0.15
                                                        + timeDecay * 0.2;
                                                """)
                                                .params("now", JsonData.of(System.currentTimeMillis()))
                                        )
                                ))
                                .boostMode(FunctionBoostMode.Replace)
                        )
                )
                .withMinScore(0.5f)
                .withHighlightQuery(
                        new HighlightQuery(
                                new Highlight(
                                        List.of(
                                                new HighlightField(SearchConstants.FIELD_TITLE),
                                                new HighlightField(SearchConstants.FIELD_CONTENT)
                                        )
                                ),
                                PostDocument.class
                        )
                )
                .withSort(
                        Sort.by(
                                Sort.Order.desc("_score"),
                                Sort.Order.desc("createTime"),
                                Sort.Order.desc("id")
                        )
                )
                .withPageable(PageRequest.of(0,request.getSize().getSize()))
                //有概率异常？
                .withSourceFilter(
                        new FetchSourceFilter(
                                new String[]{"id"},
                                null
                        )
                )
                .build();
        query.setTrackTotalHits(false);
        List<Object> searchAfter=request.getSearchAfter();
        if (Objects.nonNull(searchAfter)){
            Preconditions.checkArgument(searchAfter.size()==3,"searchAfter 必须包含 3 个元素");
            query.setSearchAfter(searchAfter);
        }
        SearchHits<PostDocument> hits=elasticsearchOperations.search(query, PostDocument.class);
        List<Long> postIds=new ArrayList<>();
        Map<Long, Map<String,List<String>>> highlightMap=new HashMap<>();
        List<Object> lastSortValues=null;
        for (SearchHit<PostDocument> hit:hits.getSearchHits()){
            Long postId=hit.getContent().getId();
            postIds.add(postId);
            highlightMap.put(postId,hit.getHighlightFields());
            lastSortValues=hit.getSortValues();
        }
        List<PostListVO> posts=postRpcService.batchGetPostList(postIds);
        if (CollectionUtils.isEmpty(posts)){
            return Response.ok(new SearchPostsRespVO(Collections.emptyList(),lastSortValues));
        }
        Map<Long,PostListVO> idToPost=posts.stream()
                .collect(Collectors.toMap(PostListVO::getId, Function.identity(),(a,b)->a));
        List<PostListVO> result=new ArrayList<>();
        for (Long postId:postIds){
            PostListVO vo=idToPost.get(postId);
            if (Objects.isNull(vo)){
                continue;
            }
            Map<String,List<String>> highlight=highlightMap.get(postId);
            if (Objects.nonNull(highlight)&&highlight.containsKey(SearchConstants.FIELD_TITLE)){
                vo.setTitle(highlight.get(SearchConstants.FIELD_TITLE).get(0));
            }
            if (Objects.nonNull(highlight)&&highlight.containsKey(SearchConstants.FIELD_CONTENT)){
                vo.setPreview(highlight.get(SearchConstants.FIELD_CONTENT).get(0));
            }
            result.add(vo);
        }
        return Response.ok(new SearchPostsRespVO(result,lastSortValues));
    }

    @Override
    public Response<List<SuggestVO>> suggestTitles(String keyword) {
        NativeQuery query=NativeQuery.builder()
                .withQuery(q->q.matchPhrasePrefix(m->m
                        .field("titleSuggest")
                        .query(keyword)
                        .maxExpansions(10)
                ))
                .withPageable(PageRequest.of(0, PageSizeEnum.SIZE_10.getSize()))
                .withSourceFilter(new FetchSourceFilter(
                        new String[]{"id","title"},
                        null
                ))
                .build();

        SearchHits<PostDocument> hits=elasticsearchOperations.search(query, PostDocument.class);

        List<SuggestVO> list=hits.getSearchHits()
                .stream()
                .map(hit->new SuggestVO(
                        hit.getContent().getId() ,
                        hit.getContent().getTitle()
                ))
                .distinct()
                .toList();
        return Response.ok(list);
    }
}
