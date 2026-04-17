package com.neu.youthpathtalk.youthpathtalkdistributedidgenerator.leaf.segment.dao;

import com.neu.youthpathtalk.youthpathtalkdistributedidgenerator.leaf.segment.model.LeafAlloc;

import java.util.List;

public interface IDAllocDao {
     List<LeafAlloc> getAllLeafAllocs();
     LeafAlloc updateMaxIdAndGetLeafAlloc(String tag);
     LeafAlloc updateMaxIdByCustomStepAndGetLeafAlloc(LeafAlloc leafAlloc);
     List<String> getAllTags();
}
