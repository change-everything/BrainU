package com.peiyp.brainu.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author PeiYP
 * @since 2023年03月22日 23:15
 */
@Data
@Accessors(chain = true)
public class Task implements Serializable {

    // 任务id
    private String id;

    // 桶名称
    private String bucketName;

    // 文件名称
    private String fileName;

    // 存储服务器中的文件绝对路径
    private String remoteFileUrl;

    // 分片上传的uploadId
    private String uploadId;

    // 文件大小（byte）
    private Long fileSize;

    // 分片大小（byte）
    private Long chunkSize;

    // 分片数量
    private Long chunkNum;

    // 上传状态（正在上传：0；已暂停：1；上传成功：2；上传失败：3；终止上传：4）
    // 说明：暂停、开始都是前端控制，后端只是进行状态记录
    private String status;

}