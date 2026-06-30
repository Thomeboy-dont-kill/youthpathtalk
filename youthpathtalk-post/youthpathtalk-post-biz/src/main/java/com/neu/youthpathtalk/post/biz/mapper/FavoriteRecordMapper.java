package com.neu.youthpathtalk.post.biz.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Julien
 * @time 2026/05/28 22:31
 * @description
 */
@Mapper
public interface FavoriteRecordMapper {
    int deleteByPostIds(@Param("postIds") List<Long> postIds);
    @Select("SELECT user_id FROM t_favorite_record WHERE post_id=#{postId}")
    List<Long> selectUserIdsByPostId(@Param("postId") Long postId);
    @Insert("INSERT IGNORE INTO t_favorite_record (user_id, post_id) VALUES (#{userId}, #{postId})")
    int insertIgnore(@Param("userId") Long userId, @Param("postId") Long postId);
    @Delete("DELETE FROM t_favorite_record WHERE user_id = #{userId} AND post_id = #{postId}")
    int deleteByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);
}
