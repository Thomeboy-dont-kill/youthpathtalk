package com.neu.youthpathtalk.post.biz.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Julien
 * @time 2026/03/24 9:42
 * @description 点赞记录持久层
 */
@Mapper
public interface PostLikeRecordMapper {
    int deleteByPostIds(@Param("postIds") List<Long> postIds);
    @Insert("INSERT IGNORE INTO t_post_like_record (user_id, post_id) VALUES (#{userId}, #{postId})")
    int insertIgnore(@Param("userId") Long userId, @Param("postId") Long postId);
    @Delete("DELETE FROM t_post_like_record WHERE user_id = #{userId} AND post_id = #{postId}")
    int deleteByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);
    @Select("SELECT user_id FROM t_post_like_record WHERE post_id = #{postId}")
    List<Long> selectUserIdsByPostId(@Param("postId") Long postId);
}
