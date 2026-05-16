package com.neu.youthpathtalk.consumer;

import com.neu.youthpathtalk.constants.MQConstants;
import com.neu.youthpathtalk.message.PostSearchSyncMessage;
import com.neu.youthpathtalk.repository.PostRepository;
import com.neu.youthpathtalk.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Component;
import com.neu.youthpathtalk.enums.SearchSyncOperation;

import java.util.Map;

/**
 * @author Julien
 * @time 2026/05/13 12:15
 * @description 暂时不单独部署到另一个微服务也不做bulk
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MQConstants.TOPIC_POST_SEARCH_SYNC,
        consumerGroup = MQConstants.CONSUMER_GROUP_POST_SEARCH_SYNC,
        consumeMode = ConsumeMode.ORDERLY
)
public class PostSearchSyncConsumer
        implements RocketMQListener<PostSearchSyncMessage> {

    private final ElasticsearchOperations elasticsearchOperations;

    private final PostRepository postRepository;

    private final JsonUtils jsonUtils;

    @Override
    public void onMessage(PostSearchSyncMessage message) {

        SearchSyncOperation operation = message.getOperation();

        switch (operation) {

            case INSERT ->
                    handleInsert(message);

            case UPDATE ->
                    handleUpdate(message);

            case DELETE ->
                    handleDelete(message);

            default ->
                    log.warn("未知搜索同步操作类型: {}", operation);
        }
    }

    private void handleInsert(PostSearchSyncMessage message) {
        try {
            Map<String, Object> source = message.getData();

            elasticsearchOperations.save(
                    source,
                    IndexCoordinates.of("post_index")
            );

            log.info("ES INSERT成功 postId={}", message.getPostId());

        } catch (Exception e) {
            log.error("ES INSERT失败 postId={}", message.getPostId(), e);
        }
    }

    private void handleUpdate(PostSearchSyncMessage message) {
        UpdateQuery updateQuery =
                UpdateQuery.builder(String.valueOf(message.getPostId()))
                        .withDocument(
                                Document.from(message.getData())
                        )
                        .withDocAsUpsert(true)
                        .build();
        elasticsearchOperations.update(
                updateQuery,
                IndexCoordinates.of("post_index")
        );
        log.info("UPDATE 部分更新 ES 成功, postId={}, 变更字段={}", message.getPostId(), message.getData().keySet());
    }

    private void handleDelete(PostSearchSyncMessage message) {
        postRepository.deleteById(message.getPostId());
        log.info("ES DELETE成功 postId={}", message.getPostId());
    }
}