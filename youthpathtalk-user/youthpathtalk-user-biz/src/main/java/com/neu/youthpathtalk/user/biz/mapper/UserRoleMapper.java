package com.neu.youthpathtalk.user.biz.mapper;

import com.neu.youthpathtalk.user.biz.entity.UserRoleDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author Julien
 * @time 2026/03/09 14:24
 * @description 用户角色dao层
 */
@Mapper
public interface UserRoleMapper {
    int insertSelective(UserRoleDO row);
    @Select("SELECT role_id FROM t_user_role WHERE user_id=#{userId}")
    Long selectRoleIdByUserId(Long userId);
}
