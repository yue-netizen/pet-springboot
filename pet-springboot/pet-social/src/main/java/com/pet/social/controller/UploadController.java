package com.pet.social.controller;

import com.pet.common.result.Result;
import com.pet.common.util.OssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件上传管理", description = "文件上传相关接口")
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private OssUtil ossUtil;

    @Operation(summary = "上传图片")
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = ossUtil.uploadFile(file, "social/images");
        return Result.success(url);
    }

    @Operation(summary = "上传视频")
    @PostMapping("/video")
    public Result<String> uploadVideo(@RequestParam("file") MultipartFile file) {
        String url = ossUtil.uploadFile(file, "social/videos");
        return Result.success(url);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping
    public Result<Void> deleteFile(@RequestParam("url") String url) {
        ossUtil.deleteFile(url);
        return Result.success();
    }
}
