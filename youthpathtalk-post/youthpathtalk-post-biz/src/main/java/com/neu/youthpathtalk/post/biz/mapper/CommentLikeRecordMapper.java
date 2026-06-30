package com.neu.youthpathtalk.post.biz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Julien
 * @time 2026/06/10 12:34
 * @description 目前从redis获取点赞/取消点赞后的点赞状态和点赞数没有DB兜底
 */
@Mapper
public interface CommentLikeRecordMapper {
    int deleteByPostIds(@Param("postIds") List<Long> postIds);

    /**
     * 查询某个评论下所有点赞用户
     */
    @Select("""
        SELECT user_id 
        FROM t_comment_like_record 
        WHERE comment_id = #{commentId}
    """)
    List<Long> selectUserIdsByCommentId(@Param("commentId") Long commentId);

    /**
     * 点赞（幂等：已点赞不会重复插入）
     */
    @Insert("""
        INSERT IGNORE INTO t_comment_like_record (user_id, comment_id)
        VALUES (#{userId}, #{commentId})
    """)
    int insertIgnore(@Param("userId") Long userId,
                     @Param("commentId") Long commentId);

    /**
     * 取消点赞
     */
    @Delete("""
        DELETE FROM t_comment_like_record 
        WHERE user_id = #{userId} 
          AND comment_id = #{commentId}
    """)
    int deleteByUserIdAndCommentId(@Param("userId") Long userId,
                                   @Param("commentId") Long commentId);
    //DB兜底

    /**
     * 查询某条评论被多少人点赞（用于回填/校准）
     */
//    @Select("""
//        SELECT COUNT(1)
//        FROM t_comment_like_record
//        WHERE comment_id = #{commentId}
//    """)
//    Long countByCommentId(@Param("commentId") Long commentId);
//
//    @Select("""
//        SELECT EXISTS(
//        SELECT comment_id
//        FROM t_comment_like_record
//        WHERE user_id = #{userId}
//          AND comment_id = #{commentId}
//        )
//    """)
//    boolean exists(@Param("userId") Long userId,@Param("commentId") Long commentId);
}