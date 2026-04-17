package com.neu.youthpathtalk.user.biz.mapper;

import com.neu.youthpathtalk.user.biz.entity.RolePermissionDO;

public interface RolePermissionDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RolePermissionDO row);

    int insertSelective(RolePermissionDO row);

    RolePermissionDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RolePermissionDO row);

    int updateByPrimaryKey(RolePermissionDO row);
}