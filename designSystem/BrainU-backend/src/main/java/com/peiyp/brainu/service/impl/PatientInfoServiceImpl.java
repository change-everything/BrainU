package com.peiyp.brainu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peiyp.brainu.entity.DoctorInfo;
import com.peiyp.brainu.entity.PatientInfo;
import com.peiyp.brainu.mapper.PatientInfoMapper;
import com.peiyp.brainu.service.DoctorInfoService;
import com.peiyp.brainu.service.PatientInfoService;
import com.peiyp.brainu.service.SegmentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author PeiYP
 * @since 2023年04月07日 23:16
 */
@Service
public class PatientInfoServiceImpl extends ServiceImpl<PatientInfoMapper, PatientInfo> implements PatientInfoService {

    @Resource
    private DoctorInfoService doctorInfoService;
    @Resource
    private SegmentService segmentService;

    @Override
    public Page<PatientInfo> listPage(PatientInfo patientInfo, int pageNum, int pageSize, int isHandle) {
        LambdaQueryWrapper<PatientInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatientInfo::getIsHandle, isHandle);
        String patientName = patientInfo.getPatientName();
        if (patientName != null && !patientName.equals("")) {
            wrapper.like(PatientInfo::getPatientName, patientName);
        }
        Integer patientAge = patientInfo.getPatientAge();
        if (patientAge != null) {
            wrapper.eq(PatientInfo::getPatientAge, patientAge);
        }
        Integer patientGender = patientInfo.getPatientGender();
        if (patientGender != null) {
            wrapper.eq(PatientInfo::getPatientGender, patientGender);
        }
        String handleBy = patientInfo.getHandleBy();
        if (handleBy != null && !handleBy.equals("")) {
            wrapper.eq(PatientInfo::getHandleBy, handleBy);
        }
        Page<PatientInfo> page = this.baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<PatientInfo> records = page.getRecords();
        List<PatientInfo> collect = records.stream().peek(item -> {
            String doctorId = item.getHandleBy();
            if (doctorId != null) {
                DoctorInfo doctorInfo = doctorInfoService.getByDoctorId(Long.valueOf(doctorId));
                item.setHandleByName(doctorInfo.getDoctorName());
            }
            String rootPath = item.getImgPath();
            if (rootPath != null) {
                String url = segmentService.previewPic(rootPath);
                item.setUrl(url);
            }
        }).collect(Collectors.toList());
        page.setRecords(collect);
        return page;
    }

    @Override
    public PatientInfo getPatientInfoById(Long id) {
        PatientInfo patientInfo = this.baseMapper.selectById(id);
        DoctorInfo doctorInfo = doctorInfoService.getByDoctorId(Long.valueOf(patientInfo.getHandleBy()));
        patientInfo.setHandleBy(doctorInfo.getDoctorName());
        return patientInfo;
    }
}
