package com.neu.youthpathtalk.post.biz.mapper;

import com.neu.youthpathtalk.post.biz.entity.PostDO;

public interface PostDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PostDO row);

    int insertSelective(PostDO row);

    PostDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PostDO row);

    int updateByPrimaryKeyWithBLOBs(PostDO row);

    int updateByPrimaryKey(PostDO row);
}