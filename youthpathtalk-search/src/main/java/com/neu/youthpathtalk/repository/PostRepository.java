package com.neu.youthpathtalk.repository;

import com.neu.youthpathtalk.document.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author Julien
 * @time 2026/05/07 21:08
 * @description
 */
public interface PostRepository extends ElasticsearchRepository<PostDocument,Long> {
}
