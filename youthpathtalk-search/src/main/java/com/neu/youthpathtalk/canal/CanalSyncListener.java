package com.neu.youthpathtalk.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.neu.youthpathtalk.config.CanalProperties;
import com.neu.youthpathtalk.constants.CanalConstants;
import com.neu.youthpathtalk.constants.SearchConstants;
import com.neu.youthpathtalk.document.PostDocument;
import com.neu.youthpathtalk.repository.PostRepository;
import com.neu.youthpathtalk.util.DateUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Component;
import com.neu.youthpathtalk.domain.FieldMeta;

import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author Julien
 * @time 2026/05/10 20:52
 * @description
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CanalSyncListener {
    private final ElasticsearchOperations elasticsearchOperations;
    private final PostRepository postRepository;
    private final CanalProperties canalProperties;
    private ExecutorService executor;

    /**
     * 字段映射器
     */
    private static final Map<String, FieldMeta> FIELD_META_MAP =
            Map.ofEntries(

                    Map.entry(
                            "id",
                            new FieldMeta(
                                    "id",
                                    null,
                                    Long::parseLong,
                                    (doc, v) -> doc.setId((Long) v)
                            )
                    ),

                    Map.entry("title",
                            new FieldMeta("title",
                                    "titleSuggest",
                                    v -> v,
                                    (doc, v) -> {
                                        String title = (String) v;
                                        doc.setTitle(title);
                                        doc.setTitleSuggest(title);
                                    })
                    ),

                    Map.entry(
                            "content",
                            new FieldMeta(
                                    "content",
                                    null,
                                    CanalSyncListener::cleanContent,
                                    (doc, v) -> doc.setContent((String) v)
                            )
                    ),

                    Map.entry(
                            "board_type",
                            new FieldMeta(
                                    "boardType",
                                    null,
                                    Integer::parseInt,
                                    (doc, v) -> doc.setBoardType((Integer) v)
                            )
                    ),

                    Map.entry(
                            "user_id",
                            new FieldMeta(
                                    "userId",
                                    null,
                                    Long::parseLong,
                                    (doc, v) -> doc.setUserId((Long) v)
                            )
                    ),

                    Map.entry(
                            "like_count",
                            new FieldMeta(
                                    "likeCount",
                                    null,
                                    Long::parseLong,
                                    (doc, v) -> doc.setLikeCount((Long) v)
                            )
                    ),

                    Map.entry(
                            "comment_count",
                            new FieldMeta(
                                    "commentCount",
                                    null,
                                    Long::parseLong,
                                    (doc, v) -> doc.setCommentCount((Long) v)
                            )
                    ),

                    Map.entry(
                            "favorite_count",
                            new FieldMeta(
                                    "favoriteCount",
                                    null,
                                    Long::parseLong,
                                    (doc, v) -> doc.setFavoriteCount((Long) v)
                            )
                    ),

                    Map.entry(
                            "view_count",
                            new FieldMeta(
                                    "viewCount",
                                    null,
                                    Long::parseLong,
                                    (doc, v) -> doc.setViewCount((Long) v)
                            )
                    ),

                    Map.entry(
                            "create_time",
                            new FieldMeta(
                                    "createTime",
                                    null,
                                    DateUtils::parseToMillis,
                                    (doc, v) -> doc.setCreateTime((Long) v)
                            )
                    )
            );


    @PostConstruct
    public void start(){
        log.info("CanalSyncListener 开始初始化，连接目标: {}:{} 实例: {}",
                canalProperties.getHost(), canalProperties.getPort(), canalProperties.getDestination());
        executor= new ThreadPoolExecutor(
                1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1),
                r->{
                    Thread t=new Thread(r,"canal-sync-thread");
                    t.setDaemon(false);
                    return t;
                },
                new ThreadPoolExecutor.AbortPolicy()
        );

        executor.submit(this::runCanalLoop);
    }

    private void runCanalLoop(){
        while (!Thread.currentThread().isInterrupted()){
            CanalConnector connector=null;
            try {
                connector= CanalConnectors.newSingleConnector(
                        new InetSocketAddress(
                                canalProperties.getHost(),
                                canalProperties.getPort()
                        ),
                        canalProperties.getDestination(),
                        canalProperties.getUsername(),
                        canalProperties.getPassword()
                );
                connector.connect();
                connector.subscribe(canalProperties.getSubscribe());
                log.info("Canal 连接并订阅成功，订阅表达式: {}", canalProperties.getSubscribe());
                consumeLoop(connector);
            } catch (Exception e) {
                log.error("Canal连接或订阅异常",e);
            }finally {
                if (connector != null) {
                    try {
                        connector.disconnect();
                    } catch (Exception ignored) {
                    }
                }
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void consumeLoop(CanalConnector connector){
        while (!Thread.currentThread().isInterrupted()){
            long batchId=-1;
            try {
                Message message=connector.getWithoutAck(
                        CanalConstants.DEFAULT_BATCH_SIZE,
                        CanalConstants.DEFAULT_GET_TIMEOUT,
                        CanalConstants.DEFAULT_GET_TIMEOUT_UNIT);
                batchId= message.getId();
                if (batchId==-1||message.getEntries().isEmpty()){
                    log.debug("无新消息，batchId={}", batchId);
                    Thread.sleep(500);
                    continue;
                }
                log.info("收到 Canal 消息 batchId={}, entry数量={}", batchId, message.getEntries().size());
                //处理消息
                processMessage(message);
                connector.ack(batchId);
            }catch (Exception e){
                log.error("消息处理失败",e);
                if (isConnectionError(e)){
                    log.warn("检测到连接性错误，退出 consumeLoop，等待外层重连");
                    break;
                }
                if (batchId!=-1&&Objects.nonNull(connector)){
                    connector.rollback(batchId);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private boolean isConnectionError(Exception e) {
        return e instanceof java.net.ConnectException
                || e instanceof java.net.SocketTimeoutException
                || (e instanceof CanalClientException
                && e.getMessage() != null
                && (e.getMessage().contains("connect") || e.getMessage().contains("socket")));
    }

    private void processMessage(Message message)throws Exception{
        for (CanalEntry.Entry entry: message.getEntries()){
            log.info("entry type: {}", entry.getEntryType());
            if (entry.getEntryType()==CanalEntry.EntryType.ROWDATA){
                CanalEntry.RowChange rowChange=CanalEntry.RowChange.parseFrom(entry.getStoreValue());
                String schema = entry.getHeader().getSchemaName();
                String table = entry.getHeader().getTableName();
                CanalEntry.EventType eventType = rowChange.getEventType();
                log.info("解析到事件: schema={}, table={}, eventType={}", schema, table, eventType);
                for (CanalEntry.RowData rowData:rowChange.getRowDatasList()){
                    Long postId=null;
                    if (rowChange.getEventType()==CanalEntry.EventType.INSERT){
                        PostDocument doc=buildPostDocument(rowData.getAfterColumnsList());
                        postRepository.save(doc);
                        log.info("INSERT 同步 ES 成功, postId={}", doc.getId());
                    }else if (rowChange.getEventType()==CanalEntry.EventType.UPDATE){
                        updatePostDocument(rowData.getAfterColumnsList());
                    }else if (rowChange.getEventType()== CanalEntry.EventType.DELETE){
                        for (CanalEntry.Column column:rowData.getBeforeColumnsList()){
                            if ("id".equals(column.getName())){
                                postId=Long.parseLong(column.getValue());
                                break;
                            }
                        }
                        if (Objects.nonNull(postId)){
                            log.info("DELETE 同步 ES, postId={}", postId);
                            postRepository.deleteById(postId);
                        }
                    }
                }
            }
        }
    }

    private PostDocument buildPostDocument(List<CanalEntry.Column> columns){
        PostDocument doc = new PostDocument();
        for (CanalEntry.Column column : columns) {
            String name = column.getName();
            String value = column.getValue();
            if (value == null) {
                continue;
            }
            FieldMeta meta=FIELD_META_MAP.get(name);
            if (Objects.nonNull(meta)){
                Object converted=meta.getConverter().apply(value);
                meta.getSetter().accept(doc,converted);
            }
        }

        return doc;
    }

    private void updatePostDocument(List<CanalEntry.Column> columns){
        Long postId=null;
        Map<String,Object> updateFields =new LinkedHashMap<>();
        for (CanalEntry.Column column:columns){
            String name=column.getName();
            String value= column.getValue();
            if ("id".equals(name)){
                postId=Long.parseLong(value);
                continue;
            }
            if (!column.getUpdated()){
                continue;
            }
            FieldMeta meta=FIELD_META_MAP.get(name);
            if (Objects.nonNull(meta)){
                //允许更新为null值是为了扩展，但是要注意一些字段“不应该”为null值
                Object converted=Objects.isNull(value)?null:meta.getConverter().apply(value);
                updateFields.put(meta.getEsField(),converted);
                if (meta.getExtraEsField() != null) {
                    updateFields.put(meta.getExtraEsField(), converted);
                }
            }
        }
        if (Objects.isNull(postId)||updateFields.isEmpty()){
            log.warn("UPDATE 跳过: postId={}, updateFields为空", postId);
            return;
        }
        UpdateQuery updateQuery=
                UpdateQuery.builder(String.valueOf(postId))
                        .withDocument(
                                Document.from(updateFields)
                        ).build();
        elasticsearchOperations.update(
                updateQuery,
                IndexCoordinates.of("post_index")
        );
        log.info("UPDATE 部分更新 ES 成功, postId={}, 变更字段={}", postId, updateFields.keySet());
    }

    private static String cleanContent(String content){
        if (Objects.isNull(content)||content.isEmpty()){
            return content;
        }
        String text=Jsoup.parse(content).text();
        if (text.length()> SearchConstants.MAX_CONTENT_LENGTH){
            text=text.substring(0,SearchConstants.MAX_CONTENT_LENGTH);
        }
        return text;
    }

    @PreDestroy
    public void stop(){
        if (Objects.nonNull(executor)){
            executor.shutdownNow();
            try {
                executor.awaitTermination(
                        CanalConstants.AWAIT_TERMINATION_TIMEOUT,
                        CanalConstants.AWAIT_TERMINATION_TIMEOUT_UNIT
                );
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }
}
