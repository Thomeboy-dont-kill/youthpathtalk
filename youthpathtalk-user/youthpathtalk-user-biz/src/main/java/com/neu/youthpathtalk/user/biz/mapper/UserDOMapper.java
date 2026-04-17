package com.neu.youthpathtalk.user.biz.mapper;

import com.neu.youthpathtalk.user.biz.entity.UserDO;

public interface UserDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserDO row);

    int insertSelective(UserDO row);

    UserDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserDO row);

    int updateByPrimaryKey(UserDO row);
}