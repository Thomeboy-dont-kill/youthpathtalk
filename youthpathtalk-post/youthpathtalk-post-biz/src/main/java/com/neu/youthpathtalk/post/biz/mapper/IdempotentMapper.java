package com.neu.youthpathtalk.post.biz.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * @author Julien
 * @time 2026/04/01 10:52
 * @description 消费消息幂等表
 */
@Mapper
public interface IdempotentMapper {
    @Insert("INSERT IGNORE INTO post_like_count_msg_idempotent (message_id) VALUES (#{messageId})")
    int insertIfNotExist(@Param("messageId") String messageId);
    @Delete("DELETE FROM post_like_count_msg_idempotent WHERE create_time < #{threshold} LIMIT #{batchSize}")
    int deleteExpiredRecords(@Param("threshold")LocalDateTime threshold,@Param("batchSize") int batchSize);
}
