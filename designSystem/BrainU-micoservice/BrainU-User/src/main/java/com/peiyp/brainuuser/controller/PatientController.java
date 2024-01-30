package com.peiyp.brainuuser.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peiyp.brainucommon.vo.R;
import com.peiyp.brainucommon.entity.PatientInfo;
import com.peiyp.brainuuser.service.PatientInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author PeiYP
 * @since 2023年04月07日 23:09
 */
@RestController
@RequestMapping("/patient")
public class PatientController {

    @Resource
    private PatientInfoService patientInfoService;

    @PostMapping("/list/{pageNum}/{pageSize}/{isHandle}")
    public R list(@RequestBody PatientInfo patientInfo, @PathVariable int pageNum, @PathVariable int pageSize, @PathVariable int isHandle) {
        Page<PatientInfo> list = patientInfoService.listPage(patientInfo, pageNum, pageSize, isHandle);
        return R.success().setData(list);
    }

    @PostMapping
    public R saveOrUpdate(@RequestBody PatientInfo patientInfo) {
        patientInfoService.saveOrUpdate(patientInfo);
        return R.success();
    }

    @GetMapping("/{id}")
    public R getById(@PathVariable Long id) {
        PatientInfo patientInfo = patientInfoService.getPatientInfoById(id);
        return R.success().setData(patientInfo);
    }


    @GetMapping("/one/{id}")
    public PatientInfo getPatientById(@PathVariable Long id) {
        return patientInfoService.getById(id);
    }

    @DeleteMapping("/{id}")
    public R deletePatient(@PathVariable Long id) {
        patientInfoService.removeById(id);
        return R.success();
    }



}
