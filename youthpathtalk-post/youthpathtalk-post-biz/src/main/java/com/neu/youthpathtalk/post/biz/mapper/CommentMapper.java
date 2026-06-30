package com.neu.youthpathtalk.post.biz.mapper;

import com.neu.youthpathtalk.post.biz.dto.*;
import com.neu.youthpathtalk.post.biz.entity.CommentDO;
import com.neu.youthpathtalk.post.biz.vo.cursor.CommentCursor;
import com.neu.youthpathtalk.post.biz.vo.cursor.CreateTimeIdCursor;
import com.neu.youthpathtalk.post.biz.vo.resp.CommentRespVO;
import com.neu.youthpathtalk.post.biz.vo.resp.ReplyCommentRespVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Julien
 * @time 2026/06/03 14:21
 * @description
 */
@Mapper
public interface CommentMapper {
    @Select("SELECT plain_text FROM t_comment WHERE id=#{id} AND status=1")
    String selectPlainTextById(Long id);
    @Select("SELECT user_id,post_id,root_id FROM t_comment WHERE id=#{id} AND status = 1")
    CommentMetaDTO selectMetaById(Long id);
    int deleteByPostIds(@Param("postIds") List<Long> postIds);
    int insert(CommentDO comment);
    int updateReplyCountById(@Param("id") Long id,@Param("delta") Long delta);
    //暂时不做“通用方法”
    ReplyTargetDTO selectReplyTarget(Long parentId);
    CommentHotScoreInfoDTO selectHotScoreInfoById(Long id);
    void updateHotScore(@Param("id") Long id,@Param("hotScore") BigDecimal hotScore);
    List<CommentHotScoreRecalculateDTO> selectForRecalculate(
            @Param("startTime") LocalDateTime startTime,
            @Param("cursor") CreateTimeIdCursor cursor,
            @Param("limit") Integer limit
    );
    int batchUpdateHotScore(@Param("list") List<CommentHotScoreUpdateDTO> list);
    List<CommentRespVO> selectRootComments(
            @Param("postId") Long postId,
            @Param("cursor") CommentCursor cursor,
            @Param("limit") Integer limit
    );
    List<ReplyCommentRespVO> selectReplyList(
            @Param("rootId") Long rootId,
            @Param("cursor") CreateTimeIdCursor cursor,
            @Param("limit") Integer limit
    );
    List<ReplyCommentRespVO> selectConversation(
            @Param("startId") Long startId,
            @Param("maxDepth") Integer maxDepth,
            @Param("maxNodeCount") Integer maxNodeCount
    );
    CommentEditDTO selectEditInfoById(Long id);
    int updateContent(
            @Param("id") Long id,
            @Param("content") String content,
            @Param("plainText") String plainText
    );
    CommentDeleteDTO selectDeleteInfoById(Long id);

    int logicalDelete(Long id);

    List<CommentHotDTO> selectHotInfoByIds(@Param("ids") List<Long> ids,@Param("minScore") BigDecimal minScore);

    CommentLikeTargetDTO selectLikeTarget(Long id);

    int updateLikeCountById(@Param("id") Long id,@Param("delta") Long delta);
}
