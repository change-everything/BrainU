package com.peiyp.brainu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.peiyp.brainu.entity.PatientInfo;

/**
 * @author PeiYP
 * @since 2023年04月07日 23:15
 */
public interface PatientInfoService extends IService<PatientInfo> {
    /**
     * 条件检索+分页
     * @author PeiYP
     * @since 2023/4/8 15:54
     */
    Page<PatientInfo> listPage(PatientInfo patientInfo, int pageNum, int pageSize, int isHandle);

    /**
     * 详细信息
     * @author PeiYP
     * @since 2023/4/12 11:39
     * @param id
     */
    PatientInfo getPatientInfoById(Long id);
}
