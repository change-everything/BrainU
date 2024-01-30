package com.peiyp.brainu.util;

import com.google.common.collect.Multimap;
import com.peiyp.brainu.entity.ObjectItem;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import io.minio.messages.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MinioUtil {

    @Resource
    private MinioClient minioClient;

    @Resource
    private ParallelMinioClient parallelMinioClient;

    /**
     * 判断存储桶是否存在，不存在则创建
     *
     * @param bucketName 存储桶名称
     */
    public void existBucket(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return 桶是否存在
     */
    public boolean bucketExists(String bucketName) {
        boolean found;
        try {
            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            found = false;
            e.printStackTrace();
        }
        return found;
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     * @return 是否创建成功
     */
    public Boolean makeBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除存储桶
     *
     * @param bucketName 存储桶名称
     * @return 是否删除成功
     */
    public Boolean removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断对象是否存在
     *
     * @param bucketName 存储桶名称
     * @param objectName MinIO中存储对象全路径
     * @return 对象是否存在
     */
    public boolean existObject(String bucketName, String objectName) {
        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 文件上传
     *
     * @param bucketName 存储桶名称
     * @param file       文件
     * @return 桶中位置
     */
    public String upload(String bucketName, MultipartFile file) {
        MultipartFile[] fileArr = {file};
        List<String> fileNames = upload(bucketName, fileArr);
        return fileNames.size() == 0 ? null : fileNames.get(0);
    }

    /**
     * 上传文件
     *
     * @param bucketName 存储桶名称
     * @param fileList   文件列表
     * @return 桶中位置列表
     */
    public List<String> upload(String bucketName, List<MultipartFile> fileList) {
        MultipartFile[] fileArr = fileList.toArray(new MultipartFile[0]);
        return upload(bucketName, fileArr);
    }

    /**
     * description: 上传文件
     *
     * @param bucketName 存储桶名称
     * @param fileArr    文件列表
     * @return 桶中位置列表
     */
    public List<String> upload(String bucketName, MultipartFile[] fileArr) {
        // 统一文件夹
        String rootPath = UUID.randomUUID().toString().replace("-", "");
        // 保证桶一定存在
        existBucket(bucketName);
        // 执行正常操作
        List<String> bucketFileNames = new ArrayList<>(fileArr.length);
        for (MultipartFile file : fileArr) {
            // 获取桶中文件名称
            // 获取原始文件名称
            String originalFileName = file.getOriginalFilename();
            // 获取当前日期，格式例如：2020/11/04
            String datePath = new SimpleDateFormat("yyyy/MM-dd/HH").format(new Date());
            String bucketFileName = datePath + "/" + rootPath + "/" + originalFileName;

            // 推送文件到MinIO
            try (InputStream in = file.getInputStream()) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(bucketFileName)
                        .stream(in, in.available(), -1)
                        .contentType(file.getContentType())
                        .build()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            bucketFileNames.add(bucketFileName);
        }
        return bucketFileNames;
    }

    /**
     * 文件下载
     *
     * @param bucketName       存储桶名称
     * @param bucketFileName   桶中文件名称
     * @param originalFileName 原始文件名称
     * @param response         response对象
     */
    public void download(String bucketName, String bucketFileName, String originalFileName, HttpServletResponse response) {
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName).object(bucketFileName).build();
        try (GetObjectResponse objResponse = minioClient.getObject(objectArgs)) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            byte[] buf = new byte[1024];
            int len;
            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()) {
                while ((len = objResponse.read(buf)) != -1) {
                    os.write(buf, 0, len);
                }
                os.flush();
                byte[] bytes = os.toByteArray();
                response.setCharacterEncoding("utf-8");
                //设置强制下载不打开
                response.setContentType("application/force-download");
                // 设置附件名称编码
                originalFileName = new String(originalFileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
                // 设置附件名称
                response.addHeader("Content-Disposition", "attachment;fileName=" + originalFileName);
                // 写入文件
                try (ServletOutputStream stream = response.getOutputStream()) {
                    stream.write(bytes);
                    stream.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取已上传对象的文件流
     *
     * @param bucketName     存储桶名称
     * @param bucketFileName 桶中文件名称
     * @return 文件流
     */
    public InputStream getFileStream(String bucketName, String bucketFileName) throws Exception {
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName).object(bucketFileName).build();
        return minioClient.getObject(objectArgs);
    }

    /**
     * 批量删除文件对象结果
     *
     * @param bucketName      存储桶名称
     * @param bucketFileName 桶中文件名称
     * @return 删除结果
     */
    public DeleteError removeObjectsResult(String bucketName, String bucketFileName) {
        List<DeleteError> results = removeObjectsResult(bucketName, Collections.singletonList(bucketFileName));
        return results.size() > 0 ? results.get(0) : null;
    }

    /**
     * 批量删除文件对象结果
     *
     * @param bucketName      存储桶名称
     * @param bucketFileNames 桶中文件名称集合
     * @return 删除结果
     */
    public List<DeleteError> removeObjectsResult(String bucketName, List<String> bucketFileNames) {
        Iterable<Result<DeleteError>> results = removeObjects(bucketName, bucketFileNames);
        List<DeleteError> res = new ArrayList<>();
        for (Result<DeleteError> result : results) {
            try {
                res.add(result.get());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("遍历删除结果出现错误：" + e.getMessage());
            }
        }
        return res;
    }

    /**
     * 批量删除文件对象
     *
     * @param bucketName      存储桶名称
     * @param bucketFileNames 桶中文件名称集合
     */
    private Iterable<Result<DeleteError>> removeObjects(String bucketName, List<String> bucketFileNames) {
        List<DeleteObject> dos = bucketFileNames.stream().map(DeleteObject::new).collect(Collectors.toList());
        return minioClient.removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(dos).build());
    }

    /**
     * 查看文件对象
     *
     * @param bucketName 存储桶名称
     * @return 文件对象集合
     */
    public List<ObjectItem> listObjects(String bucketName) {
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).build());
        List<ObjectItem> objectItems = new ArrayList<>();
        try {
            for (Result<Item> result : results) {
                Item item = result.get();
                ObjectItem objectItem = new ObjectItem();
                objectItem.setObjectName(item.objectName());
                objectItem.setSize(item.size());
                objectItems.add(objectItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return objectItems;
    }

    /**
     * 获取桶（桶类型：public）中文件访问url
     *
     * @param bucketName     存储桶名称
     * @param bucketFileName 桶中文件名称
     * @return 访问url
     */
    public String getUploadedObjectUrlForPublicBucket(String bucketName, String bucketFileName) {
        return bucketName + "/" + bucketFileName;
    }

    /**
     * 获取桶（不限制桶类型）中文件访问url
     *
     * @param bucketName     存储桶名称
     * @param bucketFileName 桶中文件名称
     * @param expiry         过期时间数量
     * @param timeUnit       过期时间单位
     * @return 访问url
     */
    public String getUploadedObjectUrl(String bucketName, String bucketFileName, Integer expiry, TimeUnit timeUnit) {
        GetPresignedObjectUrlArgs urlArgs = GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(bucketFileName)
                .expiry(expiry, timeUnit)
                .build();
        try {
            return minioClient.getPresignedObjectUrl(urlArgs);
        } catch (Exception e) {
            log.error("获取已上传文件的 Url 失败：" + e.getMessage());
            return "";
        }
    }

    /**
     * 根据文件路径得到预览文件绝对地址
     *
     * @param bucketName
     * @param fileName
     * @return
     */
    public String getPreviewFileUrl(String bucketName, String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 创建分片上传请求
     * @date   2022/12/15 12:47
     * @param  bucketName 桶名称
     * @param  region 一般填null就行
     * @param  objectName MinIO中文件全路径
     * @param  headers 一般只需要设置“Content-Type”
     * @return CreateMultipartUploadResponse对象
     **/
    public CreateMultipartUploadResponse createMultipartUpload(String bucketName, String region, String objectName, Multimap<String, String> headers, Multimap<String, String> extraQueryParams) {
        // 保证桶一定存在
        existBucket(bucketName);
        // 创建分片上传任务
        try {
            return parallelMinioClient.createMultipartUploadAsync(bucketName, region, objectName, headers, extraQueryParams).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 前端通过后端上传分片到MinIO
     * @date   2022/12/15 12:51
     * @param  bucketName MinIO桶名称
     * @param  region 一般填null就行
     * @param  objectName MinIO中文件全路径
     * @param  data 分片文件，只能接收RandomAccessFile、InputStream类型的，一般使用InputStream类型
     * @param  length 文件大小
     * @param  uploadId 文件上传uploadId
     * @param  partNumber 分片编号
     * @param  extraHeaders 一般填null就行
     * @param  extraQueryParams 一般填null就行
     * @return UploadPartResponse对象
     **/
    public UploadPartResponse uploadPart(String bucketName, String region, String objectName, Object data, long length, String uploadId, int partNumber, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) {
        try {
            return parallelMinioClient.uploadPartAsync(bucketName, region, objectName, data, length, uploadId, partNumber, extraHeaders, extraQueryParams).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取分片上传地址，前端直接上传分片到MinIO
     * @date   2022/12/15 12:54
     * @param  bucketName MinIO桶名称
     * @param  ossFilePath MinIO中文件全路径
     * @param  queryParams 查询参数，一般只需要设置“uploadId”和“partNumber”
     * @return 分片上传地址
     **/
    public String getPreSignUploadUrl(String bucketName, String ossFilePath, Map<String, String> queryParams) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(ossFilePath)
                            .expiry(60 * 60 * 24)
                            .extraQueryParams(queryParams)
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取已上传的所有分片列表，可以为前端和completeMultipartUpload方法服务
     * @date   2022/12/15 12:57
     * @param  bucketName MinIO桶名称
     * @param  region 一般填null就行
     * @param  ossFilePath MinIO中文件全路径
     * @param  maxParts 最大分片数，一般填写10000即可
     * @param  partNumberMarker 直接填0即可
     * @param  uploadId 文件上传uploadId
     * @param  extraHeaders 一般填null就行
     * @param  extraQueryParams 一般填null就行
     * @return ListPartsResponse对象
     **/
    public ListPartsResponse listParts(String bucketName, String region, String ossFilePath, Integer maxParts, Integer partNumberMarker, String uploadId, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) {
        try {
            return parallelMinioClient.listPartsAsync(bucketName, region, ossFilePath, maxParts, partNumberMarker, uploadId, extraHeaders, extraQueryParams).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 合并分片
     * @date   2022/12/15 12:50
     * @param  bucketName MinIO桶名称
     * @param  region 一般填null就行
     * @param  ossFilePath MinIO中文件全路径
     * @param  uploadId 文件上传uploadId
     * @param  parts 分片信息
     * @param  extraHeaders 一般填null就行
     * @param  extraQueryParams 一般填null就行
     * @return ObjectWriteResponse对象
     **/
    public ObjectWriteResponse completeMultipartUpload(String bucketName, String region, String ossFilePath, String uploadId, Part[] parts, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) {
        try {
            return parallelMinioClient.completeMultipartUploadAsync(bucketName, region, ossFilePath, uploadId, parts, extraHeaders, extraQueryParams).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除MinIO中已有分片
     * @date   2022/12/15 13:04
     * @param  bucketName MinIO桶名称
     * @param  region 一般填null就行
     * @param  ossFilePath MinIO中文件全路径
     * @param  uploadId 文件上传uploadId
     * @param  extraHeaders 一般填null就行
     * @param  extraQueryParams 一般填null就行
     * @return AbortMultipartUploadResponse对象
     **/
    public AbortMultipartUploadResponse abortMultipartUpload(String bucketName, String region, String ossFilePath, String uploadId, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) {
        try {
            return parallelMinioClient.abortMultipartUploadAsync(bucketName, region, ossFilePath, uploadId, extraHeaders, extraQueryParams).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
