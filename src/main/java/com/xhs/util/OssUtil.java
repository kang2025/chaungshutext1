package com.xhs.util;

import com.aliyun.oss.OSS;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;


@Component
public class OssUtil {
    private final OSS ossClient;
    private final String bucketName;
    private final String endpoint;
    public OssUtil(OSS ossClient,
                   @Value("${aliyun.oss.bucket-name}") String bucketName,
                   @Value("${aliyun.oss.endpoint}") String endpoint) {
        this.ossClient = ossClient;
        this.bucketName = bucketName;
        this.endpoint = endpoint;
    }

    /**
     * 上传文件到OSS
     * @param file 文件对象
     * @param folder 存储目录（如 "avatars"、"posts"）
     * @return 文件访问URL
     */
    public String upload(MultipartFile file, String folder) throws IOException {
        // 生成文件名：日期+随机字符串
        String fileName = folder + "/"
                + LocalDate.now().toString() + "/"
                + RandomStringUtils.randomAlphanumeric(10)
                + getFileExtension(file.getOriginalFilename());

        // 上传文件流
        try (InputStream inputStream = file.getInputStream()) {
            ossClient.putObject(bucketName, fileName, inputStream);
        }

        // 返回完整访问路径
        return "https://" + bucketName + "." + endpoint + "/" + fileName;
    }

    private String getFileExtension(String originalFilename) {
        return originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
    }
}
