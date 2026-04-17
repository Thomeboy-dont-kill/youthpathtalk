package com.neu.youthpathtalk.user.biz.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author Julien
 * @time 2026/03/21 13:27
 * @description 大学持久层
 */
@Mapper
public interface UniversityMapper {
    @Select("SELECT name FROM t_university WHERE id=#{id}")
    String selectUniversityNameById(Long id);
}
