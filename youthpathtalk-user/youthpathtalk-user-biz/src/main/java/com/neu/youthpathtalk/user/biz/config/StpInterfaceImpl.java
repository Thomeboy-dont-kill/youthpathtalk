package com.neu.youthpathtalk.user.biz.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Julien
 * @time 2026/05/07 14:15
 * @description
 */
@Component
public class StpInterfaceImpl implements StpInterface {
    @Override
    public List<String> getPermissionList(Object loginId,String s) {
        Object permissionList=StpUtil.getSessionByLoginId(loginId).get("permission_list");
        return Objects.isNull(permissionList)? Collections.emptyList():(List<String>)permissionList;
    }

    @Override
    public List<String> getRoleList(Object o, String s) {
        return null;
    }
}
