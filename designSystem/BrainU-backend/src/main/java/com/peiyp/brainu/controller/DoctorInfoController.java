package com.peiyp.brainu.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peiyp.brainu.entity.DoctorInfo;
import com.peiyp.brainu.entity.vo.DoctorVo;
import com.peiyp.brainu.entity.vo.R;
import com.peiyp.brainu.service.DoctorInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author peiYP
 * @since 2023-04-08
 */
@RestController
@RequestMapping("/doctor-info")
public class DoctorInfoController {

    @Resource
    private DoctorInfoService doctorInfoService;

    @PostMapping("/list/{pageNum}/{pageSize}")
    public R list(@PathVariable int pageNum, @PathVariable int pageSize, @RequestBody DoctorVo doctorVo) {
        Page<DoctorInfo> list = doctorInfoService.findDoctorList(pageNum, pageSize, doctorVo);
        return R.success().setData(list);
    }

    @PostMapping("/list")
    public R listDoctor() {
        List<DoctorInfo> list = doctorInfoService.listDoctor();
        return R.success().setData(list);
    }

    @GetMapping("/{doctorId}")
    public DoctorInfo getByDoctorId(@PathVariable Long doctorId) {
        System.out.println("in");
        return doctorInfoService.getByDoctorId(doctorId);
    }

    @GetMapping("/info/{doctorId}")
    public R getOneDoctorIdForWeb(@PathVariable Long doctorId) {
        DoctorInfo doctorInfo = doctorInfoService.getByDoctorId(doctorId);
        return R.success().setData(doctorInfo);
    }

}


