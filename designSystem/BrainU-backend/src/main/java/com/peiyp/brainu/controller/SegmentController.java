package com.peiyp.brainu.controller;

import com.alibaba.fastjson.JSON;
import com.peiyp.brainu.entity.PatientInfo;
import com.peiyp.brainu.entity.vo.R;
import com.peiyp.brainu.res.Constants;
import com.peiyp.brainu.service.SegmentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author PeiYP
 * @since 2023年03月16日 17:33
 */
@RestController
@RequestMapping("/segment")
public class SegmentController {

    @Resource
    private SegmentService segmentService;


    @PostMapping("/upload")
    public R uploadFile(@ModelAttribute List<MultipartFile> files, @RequestParam("patientInfo") String patientJson) {
        PatientInfo patientInfo = JSON.parseObject(patientJson, PatientInfo.class);
        String savePath = segmentService.folderUpload(files, patientInfo);
        return R.success().setData(savePath);
    }

    @GetMapping("/{modelId}/{patientId}")
    public R segment(@PathVariable String modelId, @PathVariable String patientId, HttpServletRequest request) {
        String msg = segmentService.segment(modelId, patientId, request);
        return R.success().setData(msg);
    }

    @GetMapping("/previewPicUrl")
    public R previewPicUrl(@RequestParam String rootPath) {
        Map<String, List<String>> data = segmentService.previewPicUrl(rootPath);
        return R.success().setData(data);
    }

    @GetMapping("/changePicUrl/{key}")
    public R changePicUrl(@RequestParam String rootPath, @PathVariable String key) {
        List<String> list = segmentService.changePicUrl(rootPath, key);
        return R.success().setData(list);
    }


    @GetMapping("/previewPic")
    public String previewPic(@RequestParam String rootPath) {
        return segmentService.previewPic(rootPath);
    }


    @GetMapping("/downloadFile")
    public void uploadFile(@RequestParam String bucketFileName, @RequestParam String originalFilename, HttpServletResponse response) {
        segmentService.download(Constants.BUCKET_NAME, bucketFileName, originalFilename, response);
    }


    @GetMapping("/downloadTumor")
    public void downloadTumor(@RequestParam String patientId, HttpServletResponse response) {
        segmentService.downloadTumor(patientId, response);
    }

    @GetMapping("/getDiff")
    public R getDiffPic() {
        Map<String, Object> map = segmentService.getDiffPic();
        return R.success().setData(map);
    }


}
