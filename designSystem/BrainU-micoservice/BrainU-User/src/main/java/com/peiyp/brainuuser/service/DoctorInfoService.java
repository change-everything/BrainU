package com.peiyp.brainuuser.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.peiyp.brainucommon.entity.DoctorInfo;
import com.peiyp.brainucommon.vo.DoctorVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author peiYP
 * @since 2023-04-08
 */
public interface DoctorInfoService extends IService<DoctorInfo> {

    Page<DoctorInfo> findDoctorList(int pageNum, int pageSize, DoctorVo doctorVo);

    DoctorInfo getByDoctorId(Long doctorId);

    List<DoctorInfo> listDoctor();
}
