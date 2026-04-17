package com.neu.youthpathtalk.post.biz.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Julien
 * @time 2026/03/24 9:42
 * @description 点赞记录持久层
 */
@Mapper
public interface LikeRecordMapper {
    int deleteByTargets(@Param("targetType") int targetType,@Param("targetIds") List<Long> targetIds);
    @Insert("INSERT IGNORE INTO t_like_record (user_id, target_type, target_id) VALUES (#{userId}, #{targetType}, #{targetId})")
    int insertIgnore(@Param("userId") Long userId, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);
    @Delete("DELETE FROM t_like_record WHERE user_id = #{userId} AND target_type = #{targetType} AND target_id = #{targetId}")
    int deleteByUserIdAndTarget(@Param("userId") Long userId, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);
    //为了评论和帖子点赞复用
    @Select("SELECT user_id FROM t_like_record WHERE target_id = #{targetId} AND target_type = #{targetType}")
    List<Long> selectUserIdsByTarget(@Param("targetId") Long targetId,@Param("targetType") Integer targetType);
}
