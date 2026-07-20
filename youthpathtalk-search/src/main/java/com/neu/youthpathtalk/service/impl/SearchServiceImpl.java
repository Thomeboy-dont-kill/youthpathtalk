package com.neu.youthpathtalk.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.PhraseSuggestOption;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import co.elastic.clients.json.JsonData;
import com.alibaba.nacos.shaded.com.google.common.base.Preconditions;
import com.neu.youthpathtalk.cache.RedisService;
import com.neu.youthpathtalk.constant.redis.SearchRedisKey;
import com.neu.youthpathtalk.constants.SearchConstants;
import com.neu.youthpathtalk.document.PostDocument;
import com.neu.youthpathtalk.enums.*;
import com.neu.youthpathtalk.exception.BizException;
import com.neu.youthpathtalk.holder.LoginUserContextHolder;
import com.neu.youthpathtalk.post.api.vo.PostListVO;
import com.neu.youthpathtalk.response.Response;
import com.neu.youthpathtalk.rpc.PostRpcService;
import com.neu.youthpathtalk.service.SearchService;
import com.neu.youthpathtalk.vo.req.SearchPostsReqVO;
import com.neu.youthpathtalk.vo.resp.SearchFacetItemVO;
import com.neu.youthpathtalk.vo.resp.SearchFacetVO;
import com.neu.youthpathtalk.vo.resp.SearchPostsRespVO;
import com.neu.youthpathtalk.vo.resp.SuggestVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
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
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final RedisService redisService;
    private final PostRpcService postRpcService;
    private final ElasticsearchClient elasticsearchClient;
    private final ElasticsearchOperations elasticsearchOperations;

    public Response<SearchPostsRespVO> searchPosts(SearchPostsReqVO request){
        BoolQuery.Builder boolBuilder=new BoolQuery.Builder();
        boolBuilder.must(m->m
                .multiMatch(mm->mm
                        .query(request.getKeyword())
                        .fields(
                                SearchConstants.FIELD_TITLE_WITH_BOOST,
                                SearchConstants.FIELD_TITLE_PINYIN_WITH_BOOST,
                                SearchConstants.FIELD_PLAINTEXT
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
        buildPublishTimeFilter(boolBuilder,request);
        BoolQuery boolQuery = boolBuilder.build();
        NativeQueryBuilder queryBuilder =NativeQuery.builder()
                .withQuery(buildSearchQuery(boolQuery, request))
                .withMinScore(0.5f)
                .withAggregation(
                        SearchConstants.BOARD_TYPE_AGG,
                        Aggregation.of(a -> a
                                .terms(t -> t
                                        .field(SearchConstants.FIELD_BOARDTYPE)
                                        .size(10)
                                )
                        )
                )
                .withHighlightQuery(
                        new HighlightQuery(
                                new Highlight(
                                        List.of(
                                                new HighlightField(SearchConstants.FIELD_TITLE),
                                                new HighlightField(
                                                        SearchConstants.FIELD_PLAINTEXT,
                                                        HighlightFieldParameters.builder()
                                                                .withFragmentSize(SearchConstants.HIGHLIGHT_FRAGMENT_SIZE)
                                                                .withNumberOfFragments(SearchConstants.HIGHLIGHT_NUMBER_OF_FRAGMENTS)
                                                                .build()
                                                )
                                        )
                                ),
                                PostDocument.class
                        )
                )
                .withSort(buildSort(request))
                .withPageable(PageRequest.of(0,request.getSize().getSize()))
                //有概率异常？
                .withSourceFilter(
                        new FetchSourceFilter(
                                new String[]{SearchConstants.FIELD_ID},
                                null
                        )
                );
        if (Objects.isNull(request.getStartTime())&&Objects.isNull(request.getEndTime())) {
            queryBuilder
                    .withAggregation(
                            SearchConstants.CREATE_TIME_AGG,
                            Aggregation.of(a -> a
                                    .dateRange(dr -> dr
                                            .field(SearchConstants.FIELD_CREATETIME)
                                            .ranges(
                                                    DateRangeExpression.of(r -> r
                                                            .key(PublishTimeRange.ONE_DAY.getLabel())
                                                            .from(FieldDateMath.of(f -> f.expr(PublishTimeRange.ONE_DAY.getFrom())))
                                                            .to(FieldDateMath.of(f -> f.expr(PublishTimeRange.ONE_DAY.getTo())))
                                                    ),

                                                    DateRangeExpression.of(r -> r
                                                            .key(PublishTimeRange.ONE_WEEK.getLabel())
                                                            .from(FieldDateMath.of(f -> f.expr(PublishTimeRange.ONE_WEEK.getFrom())))
                                                            .to(FieldDateMath.of(f -> f.expr(PublishTimeRange.ONE_WEEK.getTo())))
                                                    ),

                                                    DateRangeExpression.of(r -> r
                                                            .key(PublishTimeRange.ONE_MONTH.getLabel())
                                                            .from(FieldDateMath.of(f -> f.expr(PublishTimeRange.ONE_MONTH.getFrom())))
                                                            .to(FieldDateMath.of(f -> f.expr(PublishTimeRange.ONE_MONTH.getTo())))
                                                    ),

                                                    DateRangeExpression.of(r -> r
                                                            .key(PublishTimeRange.OLDER.getLabel())
                                                            .to(FieldDateMath.of(f -> f.expr(PublishTimeRange.OLDER.getTo())))
                                                    )
                                            )
                                    )
                            )
                    );
        }
        NativeQuery query=queryBuilder.build();
        query.setTrackTotalHits(false);
        List<Object> searchAfter=request.getSearchAfter();
        if (Objects.nonNull(searchAfter)){
            Preconditions.checkArgument(
                    searchAfter.size() == getSearchAfterSize(request.getSortType()),
                    "searchAfter 参数错误"
            );;
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
            String suggestKeyword = suggestKeyword(request.getKeyword());

            return Response.ok(
                    new SearchPostsRespVO(
                            Collections.emptyList(),
                            lastSortValues,
                            Collections.emptyList(),
                            suggestKeyword
                    )
            );
        }
        redisService.saveHistory(
                LoginUserContextHolder.getUserId(),
                request.getKeyword()
        );

        Map<Long,PostListVO> idToPost=posts.stream()
                .collect(Collectors.toMap(PostListVO::getId, Function.identity(),(a,b)->a));
        List<PostListVO> result=new ArrayList<>();
        for (Long postId:postIds){
            PostListVO vo=idToPost.get(postId);
            if (Objects.isNull(vo)){
                continue;
            }
            Map<String,List<String>> highlight=highlightMap.get(postId);
            if (Objects.nonNull(highlight)){
                List<String> fragments = highlight.get(SearchConstants.FIELD_TITLE);

                if (!CollectionUtils.isEmpty(fragments)) {
                    vo.setTitle(fragments.get(0));
                }

                List<String> fragments1 = highlight.get(SearchConstants.FIELD_PLAINTEXT);

                if (!CollectionUtils.isEmpty(fragments1)) {
                    vo.setPreview(fragments1.get(0));
                }
            }
            result.add(vo);
        }
        ElasticsearchAggregations aggs = (ElasticsearchAggregations) hits.getAggregations();

        List<SearchFacetVO> facets = new ArrayList<>();
        ElasticsearchAggregation boardAgg = aggs.get(SearchConstants.BOARD_TYPE_AGG);

        if (Objects.nonNull(boardAgg)) {

            List<SearchFacetItemVO> items = new ArrayList<>();

            boardAgg.aggregation().getAggregate().lterms().buckets().array().forEach(bucket -> {

                String key = String.valueOf(bucket.key());

                String label = BoardType.getBoardTypeName(Integer.parseInt(key));

                items.add(
                        new SearchFacetItemVO(
                                key,
                                label,
                                bucket.docCount()
                        )
                );
            });

            facets.add(
                    new SearchFacetVO(
                            "板块类型",
                            items
                    )
            );
        }
        ElasticsearchAggregation publishAgg = aggs.get(SearchConstants.CREATE_TIME_AGG);

        if (Objects.nonNull(publishAgg)) {

            List<SearchFacetItemVO> items = new ArrayList<>();

            publishAgg.aggregation().getAggregate().dateRange().buckets().array().forEach(bucket -> {
                if (bucket.docCount() <= 0) {
                    return;
                }

                items.add(
                        new SearchFacetItemVO(
                                bucket.key(),
                                bucket.key(),
                                bucket.docCount()
                        )
                );
            });

            facets.add(
                    new SearchFacetVO(
                            "发布时间",
                            items
                    )
            );
        }
        return Response.ok(new SearchPostsRespVO(result,lastSortValues,facets,null));
    }
    //只能英文纠错，会有很多多余的中文token产生
    private String suggestKeyword(String keyword) {
        if (!keyword.matches("^[a-zA-Z0-9\\s]+$")) {
            return null;
        }
        try {

            SearchResponse<Void> response = elasticsearchClient.search(s -> s
                            .index(SearchConstants.INDEX_POST)
                            .suggest(su -> su
                                    .suggesters(
                                            SearchConstants.SUGGEST_CORRECTION_NAME,
                                            sg -> sg
                                                    .text(keyword)
                                                    .phrase(ph -> ph
                                                            .field(SearchConstants.FIELD_TITLE_TRIGRAM)
                                                            .size(SearchConstants.SUGGEST_SIZE)
                                                            //这里过于激进，后续需要调试
                                                            .maxErrors(SearchConstants.SUGGEST_MAX_ERRORS)
                                                            .confidence(SearchConstants.SUGGEST_CONFIDENCE)
                                                    )
                                    )
                            ),
                    Void.class
            );

            Map<String, List<Suggestion<Void>>> suggestMap =
                    response.suggest();

            if (CollectionUtils.isEmpty(suggestMap)) {
                return null;
            }

            List<Suggestion<Void>> suggestions =
                    suggestMap.get(SearchConstants.SUGGEST_CORRECTION_NAME);

            if (CollectionUtils.isEmpty(suggestions)) {
                return null;
            }

            Suggestion<Void> suggestion = suggestions.get(0);

            if (Objects.isNull(suggestion.phrase())) {
                return null;
            }

            List<PhraseSuggestOption> options =
                    suggestion.phrase().options();

            if (CollectionUtils.isEmpty(options)) {
                return null;
            }

            return options.get(0).text();

        } catch (Exception e) {

            log.error("搜索纠错失败", e);

            return null;
        }
    }
    private Query buildSearchQuery(
            BoolQuery boolQuery,
            SearchPostsReqVO request
    ) {

        SearchSortType sortType = request.getSortType();

        if (Objects.isNull(sortType)) {
            sortType = SearchSortType.RELEVANCE;
        }

        // 非综合排序：不走 script_score
        if (!sortType.equals(SearchSortType.RELEVANCE)) {

            return Query.of(q -> q.bool(boolQuery));
        }

        // 综合排序：走 function_score
        return Query.of(q -> q.functionScore(fs -> fs
                .query(b -> b.bool(boolQuery))
                .functions(f -> f.scriptScore(ss -> ss
                        .script(sc -> sc
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
        ));
    }
    private int getSearchAfterSize(SearchSortType sortType) {

        return switch (sortType) {

            case RELEVANCE -> 3;

            case CREATE_TIME, VIEW_COUNT -> 2;
        };
    }
    private Sort buildSort(SearchPostsReqVO request) {

        SearchSortType sortType = request.getSortType();

        if (Objects.isNull(sortType)) {
            sortType = SearchSortType.RELEVANCE;
        }

        return switch (sortType) {

            case CREATE_TIME -> Sort.by(
                    Sort.Order.desc(SearchConstants.FIELD_CREATETIME),
                    Sort.Order.desc(SearchConstants.FIELD_ID)
            );

            case VIEW_COUNT -> Sort.by(
                    Sort.Order.desc(SearchConstants.FIELD_VIEWCOUNT),
                    Sort.Order.desc(SearchConstants.FIELD_ID)
            );

            case RELEVANCE -> Sort.by(
                    Sort.Order.desc("_score"),
                    Sort.Order.desc(SearchConstants.FIELD_CREATETIME),
                    Sort.Order.desc(SearchConstants.FIELD_ID)
            );
        };
    }
    private void buildPublishTimeFilter(
            BoolQuery.Builder boolBuilder,
            SearchPostsReqVO request
    ) {
        if (Objects.nonNull(request.getStartTime()) || Objects.nonNull(request.getEndTime())) {

            boolBuilder.filter(f -> f.range(r -> r.date(d -> {

                d.field(SearchConstants.FIELD_CREATETIME);

                if (Objects.nonNull(request.getStartTime())) {
                    d.gte(String.valueOf(request.getStartTime()));
                }

                if (Objects.nonNull(request.getEndTime())) {
                    d.lte(String.valueOf(request.getEndTime()));
                }

                return d;
            })));

            return;
        }
        PublishTimeRange range=request.getPublishTimeRange();
        if (Objects.isNull(range)) {
            return;
        }

        boolBuilder.filter(f -> f.range(r -> r.date(d -> {
            d.field(SearchConstants.FIELD_CREATETIME);
            if(Objects.nonNull(range.getFrom())) {
                d.gte(range.getFrom());
            }
            if (Objects.nonNull(range.getTo())) {
                d.lte(range.getTo());
            }
            return d;
        })));
    }
    @Override
    public Response<List<SuggestVO>> suggestTitles(String keyword) {

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(mm -> mm
                        .query(keyword)
                        .fields(
                                SearchConstants.FIELD_TITLESUGGEST,
                                SearchConstants.FIELD_TITLESUGGEST_PINYIN
                        )
                ))
                .withPageable(PageRequest.of(0, 10))
                .withSourceFilter(
                        new FetchSourceFilter(
                                new String[]{
                                        SearchConstants.FIELD_ID,
                                        SearchConstants.FIELD_TITLE
                                },
                                null
                        )
                )
                .build();

        SearchHits<PostDocument> hits =
                elasticsearchOperations.search(query, PostDocument.class);

        List<SuggestVO> list = hits.getSearchHits()
                .stream()
                .map(hit -> new SuggestVO(
                        hit.getContent().getId(),
                        hit.getContent().getTitle()
                ))
                .distinct()
                .toList();

        return Response.ok(list);
    }

    @Override
    public Response<List<String>> getSearchHistory() {
        Long userId=LoginUserContextHolder.getUserId();

        if (Objects.isNull(userId)) {
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }

        List<String> list;
        String historyKey= SearchRedisKey.history(userId);
        try {
            list = redisService.listHistory(historyKey);
        } catch (Exception e) {
            log.error("获取搜索历史失败，userId={}", userId, e);
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }
        return Response.ok(list);
    }

    @Override
    public Response<Void> clearSearchHistory() {
        Long userId=LoginUserContextHolder.getUserId();

        if (Objects.isNull(userId)) {
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }

        String historyKey= SearchRedisKey.history(userId);
        try {
            redisService.clearHistory(historyKey);
        } catch (Exception e) {
            log.error("清空搜索历史失败，userId={}", userId, e);
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }
        return Response.ok();
    }

    @Override
    public Response<Void> deleteSearchHistory(String keyword) {
        Long userId=LoginUserContextHolder.getUserId();

        if (Objects.isNull(userId)) {
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }

        String historyKey= SearchRedisKey.history(userId);
        try {
            redisService.deleteHistory(historyKey,keyword);
        } catch (Exception e) {
            log.error("删除搜索历史失败，userId: {},keyword: {}", userId,keyword, e);
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }
        return Response.ok();
    }
}
