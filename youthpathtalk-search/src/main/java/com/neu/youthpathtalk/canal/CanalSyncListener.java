package com.neu.youthpathtalk.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.neu.youthpathtalk.config.CanalProperties;
import com.neu.youthpathtalk.constants.CanalConstants;
import com.neu.youthpathtalk.enums.SearchSyncOperation;
import com.neu.youthpathtalk.message.PostSearchSyncMessage;
import com.neu.youthpathtalk.producer.SearchSyncProducer;
import com.neu.youthpathtalk.util.DateUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.jsoup.Jsoup;
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
    private final CanalProperties canalProperties;
    private final SearchSyncProducer searchSyncProducer;
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
                                    Long::parseLong
                            )
                    ),

                    Map.entry("title",
                            new FieldMeta("title",
                                    "titleSuggest",
                                    v -> v)
                    ),

                    Map.entry(
                            "plain_text",
                            new FieldMeta(
                                    "plainText",
                                    null,
                                    v -> v
                            )
                    ),

                    Map.entry(
                            "board_type",
                            new FieldMeta(
                                    "boardType",
                                    null,
                                    Integer::parseInt
                            )
                    ),

                    Map.entry(
                            "user_id",
                            new FieldMeta(
                                    "userId",
                                    null,
                                    Long::parseLong
                            )
                    ),

                    Map.entry(
                            "like_count",
                            new FieldMeta(
                                    "likeCount",
                                    null,
                                    Long::parseLong
                            )
                    ),

                    Map.entry(
                            "comment_count",
                            new FieldMeta(
                                    "commentCount",
                                    null,
                                    Long::parseLong
                            )
                    ),

                    Map.entry(
                            "favorite_count",
                            new FieldMeta(
                                    "favoriteCount",
                                    null,
                                    Long::parseLong
                            )
                    ),

                    Map.entry(
                            "view_count",
                            new FieldMeta(
                                    "viewCount",
                                    null,
                                    Long::parseLong
                            )
                    ),

                    Map.entry(
                            "create_time",
                            new FieldMeta(
                                    "createTime",
                                    null,
                                    DateUtils::parseToMillis
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
                        Map<String,Object> doc = buildEsDocument(rowData.getAfterColumnsList());
                        PostSearchSyncMessage msg = PostSearchSyncMessage.builder()
                                .operation(SearchSyncOperation.INSERT)
                                .postId((Long) doc.get("id"))
                                .data(doc)
                                .build();
                        searchSyncProducer.send(msg);
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
                            PostSearchSyncMessage msg = PostSearchSyncMessage.builder()
                                    .operation(SearchSyncOperation.DELETE)
                                    .postId(postId)
                                    .build();

                            searchSyncProducer.send(msg);
                        }
                    }
                }
            }
        }
    }

    private Map<String,Object> buildEsDocument(List<CanalEntry.Column> columns){
        Map<String,Object> doc = new LinkedHashMap<>();

        for (CanalEntry.Column column : columns) {

            String name = column.getName();
            String value = column.getValue();

            if (value == null) {
                continue;
            }

            FieldMeta meta = FIELD_META_MAP.get(name);

            if (Objects.nonNull(meta)) {

                Object converted = meta.getConverter().apply(value);

                doc.put(meta.getEsField(), converted);

                // title -> titleSuggest
                if (meta.getExtraEsField() != null) {
                    doc.put(meta.getExtraEsField(), converted);
                }
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
            //软删除不删文档
            FieldMeta meta=FIELD_META_MAP.get(name);
            if (Objects.nonNull(meta)){
                //允许更新为null值是为了扩展，但是要注意一些字段“不应该”为null值
                Object converted=convertValue(meta,value);
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
        PostSearchSyncMessage message = PostSearchSyncMessage.builder()
                .operation(SearchSyncOperation.UPDATE)
                .postId(postId)
                .data(updateFields)
                .build();

        searchSyncProducer.send(message);
    }

    private Object convertValue(FieldMeta meta,String value){

        if (value == null || value.isBlank()) {
            return null;
        }

        return meta.getConverter().apply(value);
    }

/*
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
*/

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
