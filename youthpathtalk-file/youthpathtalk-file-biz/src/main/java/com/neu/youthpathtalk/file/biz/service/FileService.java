package com.neu.youthpathtalk.file.biz.service;

import com.neu.youthpathtalk.response.Response;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Julien
 * @time 2026/07/19 17:59
 * @description
 */
public interface FileService {
    Response<String> upload(MultipartFile file);
}
