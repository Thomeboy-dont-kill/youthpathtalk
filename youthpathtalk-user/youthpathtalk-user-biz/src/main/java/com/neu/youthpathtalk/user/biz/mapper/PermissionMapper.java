package com.neu.youthpathtalk.user.biz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Julien
 * @time 2026/03/11 11:13
 * @description 权限DAO层
 */
@Mapper
public interface PermissionMapper {
    @Select("SELECT p.path FROM t_permission p INNER JOIN t_role_permission trp on p.id = trp.permission_id WHERE trp.role_id=#{roleId}")
    List<String> selectPathsByRoleId(Long roleId);
}
