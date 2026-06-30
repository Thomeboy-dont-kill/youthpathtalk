package com.neu.youthpathtalk.post.biz.mapper;

import com.neu.youthpathtalk.post.biz.dto.PostAuthorDTO;
import com.neu.youthpathtalk.post.biz.dto.PostBasicInfoDTO;
import com.neu.youthpathtalk.post.biz.dto.PostDeleteInfoDTO;
import com.neu.youthpathtalk.post.biz.dto.PostHotScoreDTO;
import com.neu.youthpathtalk.post.biz.entity.PostDO;
import com.neu.youthpathtalk.post.biz.vo.req.PostListReqVO;
import com.neu.youthpathtalk.post.biz.vo.req.PostUpdateReqVO;
import com.neu.youthpathtalk.post.biz.vo.resp.HotBoardItemVO;
import com.neu.youthpathtalk.post.biz.vo.resp.PostDetailRespVO;
import com.neu.youthpathtalk.post.biz.vo.resp.PostListVO;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.cursor.Cursor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author Julien
 * @time 2026/03/21 11:18
 * @description 帖子持久层
 */
@Mapper
public interface PostMapper {
    int insertSelective(PostDO row);

    PostDetailRespVO selectById(Long id);
    List<PostDetailRespVO> selectByIds(
            @Param("ids") List<Long> ids
    );

    List<PostListVO> selectByCursor(@Param("cursor") PostListReqVO cursor,
                                    @Param("limit") int limit);
    int updatePostById(PostUpdateReqVO postUpdateReqVO);
    PostBasicInfoDTO selectBasicInfoById(Long id);
    //暂时没用
    List<PostAuthorDTO> selectAuthorByIds(@Param("ids") Set<Long> ids);
    List<PostListVO> selectPostListByIds(@Param("ids") List<Long> ids);
    List<HotBoardItemVO> selectHotBoardByIds(@Param("ids") List<Long> ids);
    int physicalDeleteByIds(@Param("ids")List<Long> ids);
    @Select("SELECT user_id FROM t_post WHERE id=#{id} AND status = 1")
    Long selectAuthorIdById(Long id);
    @Select("SELECT title FROM t_post WHERE id=#{id} AND status = 1")
    String selectTitleById(Long id);
    @Update("UPDATE t_post SET status = 2 WHERE id = #{id} AND status IN (0, 1)")
    int softDeletePostById(Long id);
    @Select("SELECT id,user_id,like_count FROM t_post WHERE status=2 AND update_time<#{thresholdDate} ORDER BY id")
    Cursor<PostDeleteInfoDTO> selectExpiredPostsStream(@Param("thresholdDate") LocalDateTime thresholdDate);
    @Update("UPDATE t_post SET like_count=like_count+#{delta} WHERE id=#{id} AND status=1")
    int updateLikeCountById(@Param("id") Long id,@Param("delta") Long delta);
    @Update("UPDATE t_post SET favorite_count=favorite_count+#{delta} WHERE id=#{id} AND status=1")
    int updateFavoriteCountById(@Param("id") Long id,@Param("delta") Long delta);
    @Update("UPDATE t_post SET view_count=#{viewCount} WHERE id=#{id} AND view_count<#{viewCount} AND status=1")
    int updateViewCountById(@Param("id") Long id,@Param("viewCount") Long viewCount);
    @Update("UPDATE t_post SET comment_count=comment_count+#{delta} WHERE id=#{id} AND status=1")
    int updateCommentCountById(@Param("id") Long id,@Param("delta") Long delta);
    @Select("SELECT EXISTS(SELECT 1 FROM t_post WHERE id=#{id} AND status=1)")
    boolean existsNormalById(Long id);
    @Select("SELECT id, create_time, view_count, like_count, comment_count, favorite_count " +
            "FROM t_post WHERE status = 1 AND create_time > #{thresholdDate} AND id > #{lastId} " +
            "ORDER BY id LIMIT #{batchSize}")
    List<PostHotScoreDTO> selectByCreateTimeAndIdCursor(@Param("thresholdDate") LocalDateTime thresholdDate,
                                                        @Param("lastId") Long lastId,
                                                        @Param("batchSize") int batchSize);
}
