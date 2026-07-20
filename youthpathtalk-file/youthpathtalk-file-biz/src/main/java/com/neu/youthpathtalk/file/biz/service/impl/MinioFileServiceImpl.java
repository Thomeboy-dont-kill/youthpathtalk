package com.neu.youthpathtalk.file.biz.service.impl;

import com.neu.youthpathtalk.enums.CommonResponseErrorCode;
import com.neu.youthpathtalk.exception.BizException;
import com.neu.youthpathtalk.file.biz.enums.BizResponseErrorCode;
import com.neu.youthpathtalk.file.biz.service.FileService;
import com.neu.youthpathtalk.holder.LoginUserContextHolder;
import com.neu.youthpathtalk.response.Response;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Julien
 * @time 2026/07/19 16:04
 * @description
 */
@Service
@RequiredArgsConstructor
public class MinioFileServiceImpl implements FileService {
    private final MinioClient minioClient;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Override
    public Response<String> upload(MultipartFile file) {
        Long userId = LoginUserContextHolder.getUserId();
        if (userId == null) {
            throw new BizException(BizResponseErrorCode.AUTH_NOT_LOGIN);
        }

        // 1. 生成按用户分组的文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String objectName = userId + "/" + datePath + "/" + UUID.randomUUID().toString().replace("-", "") + suffix;

        // 2. 上传
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }catch (Exception e){
            e.printStackTrace();
            throw new BizException(CommonResponseErrorCode.SYSTEM_ERROR);
        }

        // 3. 构建访问 URL（使用配置的 endpoint）
        // 假设 endpoint 配置为 "http://localhost:9000"，不包含 bucket 和 object
        String url = endpoint + "/" + bucketName + "/" + objectName;
        // 如果 bucket 为私有，需生成预签名 URL，这里简单处理为公开读
        return Response.ok(url);
    }
}
