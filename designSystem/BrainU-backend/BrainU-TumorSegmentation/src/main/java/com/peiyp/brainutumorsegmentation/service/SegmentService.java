package com.peiyp.brainutumorsegmentation.service;

import com.peiyp.brainutumorsegmentation.entity.PatientInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author PeiYP
 * @since 2023年03月16日 18:01
 */
public interface SegmentService {
    /**
     * 文件夹上传
     *
     * @param files       多文件
     * @param patientInfo
     * @return java.lang.String
     * @author PeiYP
     * @since 2023/3/16 18:04
     */
    String folderUpload(List<MultipartFile> files, PatientInfo patientInfo);

    /**
     * 调用模型进行分割
     *
     * @param modelId   模型文件id
     * @param patientId
     * @author PeiYP
     * @since 2023/3/16 19:06
     */
    String segment(String modelId, String patientId, HttpServletRequest request);

    /**
     * 将图片路径发送到前端
     * @author PeiYP
     * @since 2023/3/26 17:26
     * @param rootPath 图片路径根目录
     * @return Map<String,List<String>>
     */
    Map<String, List<String>> previewPicUrl(String rootPath);

    /**
     * 下载文件
     * @author PeiYP
     * @since 2023/3/27 15:10
     * @param bucketName 桶名称
     * @param bucketFileName 文件路径
     * @param originalFilename 文件名
     */
    void download(String bucketName, String bucketFileName, String originalFilename, HttpServletResponse response);

    /**
     * 提供feign接口
     * @author PeiYP
     * @since 2023/4/12 15:54
     * @param rootPath
     * @return java.lang.String
     */
    String previewPic(String rootPath);

    void downloadTumor(String patientId, HttpServletResponse response);

    List<String> changePicUrl(String rootPath, String key);

    /**
     * 获取两张图片的对比图
     */
    Map<String, Object> getDiffPic();
}
