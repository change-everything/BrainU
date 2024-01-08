package com.peiyp.brainutumorsegmentation.controller;

import com.peiyp.brainutumorsegmentation.res.Constants;
import com.peiyp.brainutumorsegmentation.util.MinioUtil;
import io.minio.messages.DeleteError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author PeiYP
 * @since 2023年03月23日 9:46
 */
@RestController
@RequestMapping("/minio")
public class TestMinioController {

    @Resource
    private MinioUtil minIoUtil;

    // 上传文件
    @PostMapping("/uploadFile")
    public List<String> uploadFile(@RequestParam("files") List<MultipartFile> files) {
        return minIoUtil.upload(Constants.BUCKET_NAME, files);
    }

    // 删除文件
    @GetMapping("/deleteFile")
    public DeleteError deleteFile(@RequestParam String bucketFileName) {
        return minIoUtil.removeObjectsResult(Constants.BUCKET_NAME, bucketFileName);
    }

    // 下载文件
    @GetMapping("/downloadFile")
    public void uploadFile(@RequestParam String bucketFileName, @RequestParam String originalFilename, HttpServletResponse response) {
        minIoUtil.download(Constants.BUCKET_NAME, bucketFileName, originalFilename, response);
    }

    // 获取文件临时分享地址
    @GetMapping("/shareUrl")
    public String shareUrl(@RequestParam String bucketFileName) {
        return minIoUtil.getUploadedObjectUrl(Constants.BUCKET_NAME, bucketFileName, 7, TimeUnit.DAYS);
    }

    @PostMapping("fileUrl")
    public String fileUrl(@RequestParam String bucketFileName) throws Exception {
        return minIoUtil.getPreviewFileUrl(Constants.BUCKET_NAME, bucketFileName);
    }

}
