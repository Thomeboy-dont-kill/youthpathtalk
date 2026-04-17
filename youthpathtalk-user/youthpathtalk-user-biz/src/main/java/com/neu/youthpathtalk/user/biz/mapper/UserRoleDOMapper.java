package com.neu.youthpathtalk.user.biz.mapper;

import com.neu.youthpathtalk.user.biz.entity.UserRoleDO;

public interface UserRoleDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserRoleDO row);

    int insertSelective(UserRoleDO row);

    UserRoleDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserRoleDO row);

    int updateByPrimaryKey(UserRoleDO row);
}