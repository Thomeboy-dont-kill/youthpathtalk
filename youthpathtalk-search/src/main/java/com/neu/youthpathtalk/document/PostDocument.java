package com.neu.youthpathtalk.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/05/07 20:59
 * @description
 */
@Data
@Document(indexName = "post_index")
public class PostDocument {
    @Id
    private Long id;

    @Field(type = FieldType.Text,
            analyzer = "ik_max_word",
            searchAnalyzer = "ik_smart"
    )
    private String title;

    @Field(type = FieldType.Text,
            analyzer = "edge_ngram_analyzer"
    )
    private String titleSuggest;

    @Field(type = FieldType.Text,
            analyzer = "ik_max_word",
            searchAnalyzer = "ik_smart"
    )
    private String content;

    //标签？
    @Field(type = FieldType.Integer)
    private Integer boardType;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Long)
    private Long likeCount;

    @Field(type = FieldType.Long)
    private Long commentCount;

    @Field(type = FieldType.Long)
    private Long favoriteCount;

    @Field(type = FieldType.Long)
    private Long viewCount;

    @Field(type = FieldType.Date)
    private Long createTime;
    //后面再扩展
}
