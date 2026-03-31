package com.pet.common.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.pet.common.config.OssConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "aliyun.oss", name = "enabled", havingValue = "true")
public class OssUtil {

    @Autowired
    private OSS ossClient;

    @Autowired
    private OssConfig ossConfig;

    public String uploadFile(MultipartFile file, String folder) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        String objectName = folder + "/" + fileName;

        try {
            InputStream inputStream = file.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            
            ossClient.putObject(ossConfig.getBucketName(), objectName, inputStream, metadata);
            
            return "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint().replace("https://", "").replace("http://", "") + "/" + objectName;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    public String uploadFile(MultipartFile file) {
        return uploadFile(file, "images");
    }

    public void deleteFile(String fileUrl) {
        try {
            String objectName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            String folder = fileUrl.substring(0, fileUrl.lastIndexOf("/"));
            folder = folder.substring(folder.lastIndexOf("/") + 1);
            ossClient.deleteObject(ossConfig.getBucketName(), folder + "/" + objectName);
        } catch (Exception e) {
            throw new RuntimeException("文件删除失败", e);
        }
    }
}
