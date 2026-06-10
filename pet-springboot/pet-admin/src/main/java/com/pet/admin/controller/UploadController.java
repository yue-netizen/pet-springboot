package com.pet.admin.controller;

import com.pet.common.result.Result;
import com.pet.common.util.OssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件上传管理", description = "文件上传相关接口")
@RestController
@RequestMapping("/admin/upload")
public class UploadController {

    @Autowired
    private OssUtil ossUtil;

    @Operation(summary = "上传图片到默认文件夹")
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = ossUtil.uploadFile(file, "images");
        return Result.success(url);
    }

    @Operation(summary = "上传宠物照片")
    @PostMapping("/pet")
    public Result<String> uploadPetImage(@RequestParam("file") MultipartFile file) {
        String url = ossUtil.uploadFile(file, "pets");
        return Result.success(url);
    }

    @Operation(summary = "上传用户头像")
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String url = ossUtil.uploadFile(file, "avatars");
        return Result.success(url);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping
    public Result<Void> deleteFile(@RequestParam("url") String url) {
        ossUtil.deleteFile(url);
        return Result.success();
    }
}
