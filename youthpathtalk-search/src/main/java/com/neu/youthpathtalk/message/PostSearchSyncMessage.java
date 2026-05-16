package com.neu.youthpathtalk.message;

import com.neu.youthpathtalk.document.PostDocument;
import com.neu.youthpathtalk.enums.SearchSyncOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author Julien
 * @time 2026/05/13 12:32
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSearchSyncMessage {

    /**
     * 操作类型
     */
    private SearchSyncOperation operation;

    /**
     * 文档ID
     */
    private Long postId;

    /**
     * 字段
     */
    private Map<String,Object> data;
}
