package com.peiyp.brainu.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.peiyp.brainu.entity.BrainFile;
import com.peiyp.brainu.entity.ModelInfo;
import com.peiyp.brainu.entity.NotifyInfo;
import com.peiyp.brainu.entity.PatientInfo;
import com.peiyp.brainu.mapper.BrainFileMapper;
import com.peiyp.brainu.res.Constants;
import com.peiyp.brainu.service.IModelInfoService;
import com.peiyp.brainu.service.INotifyInfoService;
import com.peiyp.brainu.service.PatientInfoService;
import com.peiyp.brainu.service.SegmentService;
import com.peiyp.brainu.util.MinioUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author PeiYP
 * @since 2023年03月16日 18:01
 */
@Service
public class SegmentServiceImpl implements SegmentService {

    @Resource
    private MinioUtil minioUtil;
    @Resource
    private BrainFileMapper brainFileMapper;
    @Resource
    private PatientInfoService patientInfoService;
    @Resource
    private IModelInfoService modelInfoService;
    @Resource
    private INotifyInfoService notifyInfoService;

    public static String path = "";


    public static final String ROOT_PATH = System.getProperty("user.dir");

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String folderUpload(List<MultipartFile> files, PatientInfo patientInfo) {
        long patientId = IdWorker.getId();
        patientInfo.setId(patientId);
        patientInfo.setIsHandle(0);
        patientInfo.setCreateTime(new Date());
        boolean b = patientInfoService.save(patientInfo);

        NotifyInfo notifyInfo = new NotifyInfo();
        notifyInfo.setPatientId(patientId);
        notifyInfo.setContext("您有新患者等待处理:【" + patientInfo.getPatientName() + "】");
        notifyInfo.setIsHandle(0);
        notifyInfo.setCreateTime(LocalDateTime.now());
        notifyInfoService.save(notifyInfo);


        String fileId = UUID.randomUUID().toString().replace("-", "");
        for (MultipartFile file : files) {
            try {
                String originalFilename = file.getOriginalFilename();
                assert originalFilename != null;
                originalFilename = originalFilename.substring(originalFilename.lastIndexOf("/") + 1);
                String path = ROOT_PATH + fileId + "/" + originalFilename;
                File tempFile = new File(path);
                if(!tempFile.exists()) {
                    tempFile.mkdirs();
                }
                file.transferTo(tempFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        String uploadPath = ROOT_PATH + fileId + "/";


        BrainFile brainFile = new BrainFile();
        brainFile.setUploadPath(uploadPath);
        brainFile.setFileId(fileId);
        brainFile.setPatientId(patientId);
        brainFile.setCreateTime(new Date());
        brainFileMapper.insert(brainFile);
        return ROOT_PATH + fileId;
    }


    @Override
    public String segment(String modelId, String patientId, HttpServletRequest request) {
        BrainFile brainFile = brainFileMapper.selectOne(new LambdaQueryWrapper<BrainFile>()
                .eq(BrainFile::getPatientId, patientId));
        String uploadPath = brainFile.getUploadPath();
        ModelInfo data = modelInfoService.getById(modelId);
        String modelPath = data.getModelPath();
        String modelName = data.getModelName();
        try {
            String message = "1" + modelPath + "|" + uploadPath + "|" + modelName + "end send";
            String serverMsg = this.getSocketServerMsg("127.0.0.1", 50007, message);
            String[] split = serverMsg.split("\\|");
            System.out.println(serverMsg);
            path = split[0];
            if (!"".equals(serverMsg)) {
                PatientInfo patientInfo = patientInfoService.getById(Long.parseLong(patientId));
                patientInfo.setIsHandle(1);
                String doctorId = (String) request.getAttribute("doctorId");
                patientInfo.setHandleBy(doctorId);
                patientInfo.setImgPath(split[0]);
                patientInfo.setUpdateTime(new Date());
                patientInfoService.save(patientInfo);
                NotifyInfo notifyInfo = notifyInfoService.getOne(new LambdaQueryWrapper<NotifyInfo>()
                        .eq(NotifyInfo::getPatientId, patientId));
                notifyInfo.setIsHandle(1);
                notifyInfoService.updateById(notifyInfo);
                return serverMsg;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Map<String, List<String>> previewPicUrl(String rootPath) {
        Map<String, List<String>> res = new HashMap<>();
        List<String> seList = new ArrayList<>();
        List<String> seSideList = new ArrayList<>();
        List<String> seFontList = new ArrayList<>();
        for (int i = 0; i < 155; i++) {
            String sePath = "/se/image_" + i + ".png";
            String seUrl = minioUtil.getUploadedObjectUrl(Constants.BUCKET_NAME, rootPath + sePath, 1, TimeUnit.DAYS);
            seList.add(seUrl);
        }
        for (int i = 0; i < 240; i++) {
            String seFontPath = "/seFont/image_" + i + ".png";
            String seFontUrl = minioUtil.getUploadedObjectUrl(Constants.BUCKET_NAME, rootPath + seFontPath, 1, TimeUnit.DAYS);
            seFontList.add(seFontUrl);
        }
        for (int i = 0; i < 240; i++) {
            String seSidePath = "/seSide/image_" + i + ".png";
            String seSideUrl = minioUtil.getUploadedObjectUrl(Constants.BUCKET_NAME, rootPath + seSidePath, 1, TimeUnit.DAYS);
            seSideList.add(seSideUrl);
        }
        res.put("seList", seList);
        res.put("seFontList", seFontList);
        res.put("seSideList", seSideList);
        return res;
    }


    @Override
    public List<String> changePicUrl(String rootPath, String key) {

        List<String> list = new ArrayList<>();
        int index = 155;
        if (!key.equals("se")) {
            index = 240;
        }

        for (int i = 0; i < index; i++) {
            String path = "/" + key + "/image_" + i + ".png";
            String url = minioUtil.getUploadedObjectUrl(Constants.BUCKET_NAME, rootPath + path, 1, TimeUnit.DAYS);
            list.add(url);
        }

        return list;
    }

    @Override
    public Map<String, Object> getDiffPic() {
        Map<String, Object> map = new HashMap<>();
        String before = path + "/original/se/image_80.png";
        String after = path + "/se/image_80.png";
        String beforeUrl = minioUtil.getUploadedObjectUrl(Constants.BUCKET_NAME, before, 1, TimeUnit.DAYS);
        String afterUrl = minioUtil.getUploadedObjectUrl(Constants.BUCKET_NAME, after, 1, TimeUnit.DAYS);
        map.put("beforeUrl", beforeUrl);
        map.put("afterUrl", afterUrl);
        path = "";
        return map;
    }

    @Override
    public void download(String bucketName, String bucketFileName, String originalFilename, HttpServletResponse response) {
        minioUtil.download(bucketName, bucketFileName, originalFilename, response);
    }

    @Override
    public String previewPic(String rootPath) {
        String path = rootPath + "/se/image_80.png";
        return minioUtil.getUploadedObjectUrl(Constants.BUCKET_NAME, path, 1, TimeUnit.DAYS);
    }

    @Override
    public void downloadTumor(String patientId, HttpServletResponse response) {
        BrainFile brainFile = brainFileMapper.selectOne(new LambdaQueryWrapper<BrainFile>()
                .eq(BrainFile::getPatientId, patientId));
        try {
            String path = brainFile.getUploadPath() + "result.mha";
            // path是指欲下载的文件的路径。
            File file = new File(path);
            // 取得文件名。
            String filename = file.getName();
            // 取得文件的后缀名。
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            //设置强制下载不打开
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    private String getSocketServerMsg(String host, int port, String message) throws Exception {
        // 与服务端建立连接
        Socket socket = new Socket(host, port);
        // 获得输出流
        OutputStream outputStream = socket.getOutputStream();
        PrintWriter pWriter = new PrintWriter(outputStream);
        pWriter.write(message);
        pWriter.flush();
        // 通过shutdownOutput高速服务器已经发送完数据，后续只能接受数据
        socket.shutdownOutput();
        // 获得输入流
        InputStream inputStream = socket.getInputStream();

        byte[] bytes = new byte[1024];
        int len;
        StringBuilder sb = new StringBuilder();
        while ((len = inputStream.read(bytes)) != -1) {
            // 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
            sb.append(new String(bytes, 0, len, StandardCharsets.UTF_8)).append("|");
        }
        inputStream.close();
        outputStream.close();
        socket.close();
        return sb.toString();
    }
}
