package com.neu.youthpathtalk.file.biz.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.neu.youthpathtalk.file.biz.service.FileService;
import com.neu.youthpathtalk.file.biz.service.impl.MinioFileServiceImpl;
import com.neu.youthpathtalk.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Julien
 * @time 2026/07/19 17:22
 * @description
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@Tag(
        name = "文件模块",
        description = "文件操作相关接口"
)
public class FileController {
    private final FileService fileService;

    @SaCheckLogin
    @PostMapping("/upload")
    @Operation(
            summary = "上传文件",
            description = """
                    上传文件到 MinIO 对象存储，返回可访问的 URL。
                    
                    支持图片、视频等常见格式。
                    
                    文件会按 userId/日期 自动分类存储。
                    """
    )
    public Response<String> upload(@Parameter(description = "上传的文件（支持 JPG、PNG、MP4 等）", required = true)
                                       @RequestParam("file") MultipartFile file) {
        return fileService.upload(file);
    }
}