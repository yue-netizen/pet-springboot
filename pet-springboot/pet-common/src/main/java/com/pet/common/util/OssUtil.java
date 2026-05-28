package com.pet.common.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.pet.common.config.OssConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * OSS文件上传工具类
 * 
 * 功能说明：
 * - 上传文件到阿里云OSS
 * - 自动生成UUID文件名防止冲突
 * - 文件类型和大小安全校验
 * 
 * 安全特性：
 * ✅ 文件类型白名单校验（仅允许图片、视频等）
 * ✅ 文件大小限制（默认最大10MB）
 * ✅ 文件名随机化（使用UUID，防路径遍历）
 * ✅ Content-Type自动设置
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "aliyun.oss", name = "enabled", havingValue = "true")
public class OssUtil {

    @Autowired
    private OSS ossClient;

    @Autowired
    private OssConfig ossConfig;

    /** 允许上传的图片格式白名单 */
    private static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"
    ));

    /** 允许上传的视频格式白名单 */
    private static final Set<String> ALLOWED_VIDEO_TYPES = new HashSet<>(Arrays.asList(
            ".mp4", ".avi", ".mov", ".wmv", ".flv", ".mkv"
    ));

    /** 允许上传的文档格式白名单 */
    private static final Set<String> ALLOWED_DOC_TYPES = new HashSet<>(Arrays.asList(
            ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx"
    ));

    /** 最大文件大小：10MB */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    /**
     * 上传文件到指定文件夹
     * 
     * @param file 上传的文件
     * @param folder 目标文件夹名称
     * @return 文件的完整URL地址
     * @throws RuntimeException 文件类型不允许或过大时抛出异常
     */
    public String uploadFile(MultipartFile file, String folder) {
        validateFile(file);
        
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename).toLowerCase();
        validateFileType(extension, folder);
        
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        String objectName = folder + "/" + fileName;

        try {
            InputStream inputStream = file.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            
            ossClient.putObject(ossConfig.getBucketName(), objectName, inputStream, metadata);
            
            String url = "https://" + ossConfig.getBucketName() + "." + 
                         ossConfig.getEndpoint().replace("https://", "").replace("http://", "") + 
                         "/" + objectName;
            
            log.info("文件上传成功: {}", fileName);
            return url;
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            throw new RuntimeException("文件上传失败", e);
        }
    }

    /**
     * 上传文件到默认文件夹（images）
     * @param file 上传的文件
     * @return 文件的URL地址
     */
    public String uploadFile(MultipartFile file) {
        return uploadFile(file, "images");
    }

    /**
     * 删除指定URL的文件
     * 
     * ⚠️ 安全提示：
     * - 会验证文件URL是否属于当前bucket
     * - 防止路径遍历攻击（../）
     * 
     * @param fileUrl 要删除的文件的完整URL
     * @throws RuntimeException URL无效或不属于当前bucket时抛出异常
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new IllegalArgumentException("文件URL不能为空");
        }
        
        validateFileUrl(fileUrl);
        
        try {
            String objectName = extractObjectName(fileUrl);
            ossClient.deleteObject(ossConfig.getBucketName(), objectName);
            log.info("文件删除成功: {}", objectName);
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage());
            throw new RuntimeException("文件删除失败", e);
        }
    }

    /**
     * 校验文件基本信息
     * @param file 要校验的文件
     * @throws IllegalArgumentException 文件为空或过大时抛出异常
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过10MB");
        }
        
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
    }

    /**
     * 校验文件类型是否允许
     * @param extension 文件扩展名（如 .jpg, .mp4）
     * @param folder 目标文件夹（用于判断允许的类型范围）
     * @throws IllegalArgumentException 不允许的文件类型时抛出异常
     */
    private void validateFileType(String extension, String folder) {
        Set<String> allowedTypes;
        
        if (folder.contains("image") || folder.contains("avatar") || folder.contains("pet")) {
            allowedTypes = ALLOWED_IMAGE_TYPES;
        } else if (folder.contains("video")) {
            allowedTypes = ALLOWED_VIDEO_TYPES;
        } else if (folder.contains("doc")) {
            allowedTypes = ALLOWED_DOC_TYPES;
        } else {
            allowedTypes = new HashSet<>();
            allowedTypes.addAll(ALLOWED_IMAGE_TYPES);
            allowedTypes.addAll(ALLOWED_VIDEO_TYPES);
        }
        
        if (!allowedTypes.contains(extension)) {
            throw new IllegalArgumentException(
                String.format("不支持的文件类型: %s，允许的类型: %s", 
                    extension, allowedTypes.toString())
            );
        }
    }

    /**
     * 获取文件扩展名
     * @param filename 原始文件名
     * @return 扩展名（包含点号，如 .jpg）
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new IllegalArgumentException("文件缺少扩展名");
        }
        return filename.substring(lastDotIndex);
    }

    /**
     * 校验文件URL是否合法且属于当前bucket
     * @param fileUrl 文件URL
     * @throws IllegalArgumentException URL不合法时抛出异常
     */
    private void validateFileUrl(String fileUrl) {
        if (!fileUrl.contains(ossConfig.getBucketName())) {
            throw new IllegalArgumentException("只能删除本系统上传的文件");
        }
        
        if (fileUrl.contains("..")) {
            throw new IllegalArgumentException("检测到非法字符");
        }
    }

    /**
     * 从完整URL中提取对象名称（objectName）
     * @param fileUrl 完整的文件URL
     * @return OSS中的对象名称（如 images/xxx.jpg）
     */
    private String extractObjectName(String fileUrl) {
        int lastSlashIndex = fileUrl.lastIndexOf("/");
        if (lastSlashIndex == -1) {
            throw new IllegalArgumentException("无效的文件URL格式");
        }
        return fileUrl.substring(lastSlashIndex + 1);
    }
}
