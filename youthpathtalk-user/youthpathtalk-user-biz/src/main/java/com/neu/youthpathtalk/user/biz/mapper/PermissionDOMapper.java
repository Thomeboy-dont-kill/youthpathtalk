package com.neu.youthpathtalk.user.biz.mapper;

import com.neu.youthpathtalk.user.biz.entity.PermissionDO;

public interface PermissionDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PermissionDO row);

    int insertSelective(PermissionDO row);

    PermissionDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PermissionDO row);

    int updateByPrimaryKey(PermissionDO row);
}