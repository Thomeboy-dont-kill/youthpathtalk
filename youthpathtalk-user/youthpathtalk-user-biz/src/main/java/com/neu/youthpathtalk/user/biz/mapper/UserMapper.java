package com.neu.youthpathtalk.user.biz.mapper;

import com.neu.youthpathtalk.user.biz.dto.UserInfoDTO;
import com.neu.youthpathtalk.user.biz.dto.UserMentionDTO;
import com.neu.youthpathtalk.user.biz.dto.UserPostInfoDTO;
import com.neu.youthpathtalk.user.biz.dto.UserWeeklyRankInfoDTO;
import com.neu.youthpathtalk.user.biz.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Julien
 * @time 2026/03/05 19:31
 * @description 用户dao层
 */
@Mapper
public interface UserMapper {
    int insertSelective(UserDO row);
    int batchDecrTotalLikeCount(@Param("userDeltas")Map<Long,Long> userDeltas);
    List<UserWeeklyRankInfoDTO> selectUserWeeklyRankInfoByIds(@Param("ids") List<Long> ids);
//    List<Long> selectIdsByUsernames(@Param("usernames") List<String> usernames);
    List<UserMentionDTO> selectMentionInfoByIds(@Param("ids") Collection<Long> ids);
    @Select("SELECT EXISTS(SELECT 1 FROM t_user WHERE phone=#{phone})")
    Boolean existsByPhone(String phone);
    @Select("SELECT id,password,status FROM t_user WHERE username=#{username}")
    UserInfoDTO selectUserInfoByUsername(String username);
    @Select("SELECT id FROM t_user WHERE phone=#{phone} and status=1")
    Long selectUserIdByPhone(String phone);
    @Select("SELECT username,avatar,university_id FROM t_user WHERE id=#{id}")
    UserPostInfoDTO selectUserInfoById(Long id);
    @Update("UPDATE t_user SET total_like_count=total_like_count+#{delta} WHERE id = #{id} AND status=1")
    int updateTotalLikeCountById(@Param("id") Long id, @Param("delta") Long delta);
}
