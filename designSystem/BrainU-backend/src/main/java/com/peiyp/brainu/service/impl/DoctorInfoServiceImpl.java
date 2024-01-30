package com.peiyp.brainu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peiyp.brainu.entity.DoctorInfo;
import com.peiyp.brainu.entity.vo.DoctorVo;
import com.peiyp.brainu.mapper.DoctorInfoMapper;
import com.peiyp.brainu.service.DoctorInfoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author peiYP
 * @since 2023-04-08
 */
@Service
public class DoctorInfoServiceImpl extends ServiceImpl<DoctorInfoMapper, DoctorInfo> implements DoctorInfoService {

    @Override
    public Page<DoctorInfo> findDoctorList(int pageNum, int pageSize, DoctorVo doctorVo) {

        LambdaQueryWrapper<DoctorInfo> wrapper = new LambdaQueryWrapper<>();

        String doctorName = doctorVo.getDoctorName();
        if (doctorName != null && !doctorName.equals("")) {
            wrapper.like(DoctorInfo::getDoctorName, doctorName);
        }
        String doctorOffice = doctorVo.getDoctorOffice();
        if (doctorOffice != null && !doctorOffice.equals("")) {
            wrapper.eq(DoctorInfo::getDoctorOffice, doctorOffice);
        }
        Integer doctorId = doctorVo.getDoctorId();
        if (doctorId != null) {
            wrapper.eq(DoctorInfo::getDoctorId, doctorId);
        }


        Page<DoctorInfo> page = this.baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        List<DoctorInfo> collect = page.getRecords().stream().peek(item -> item.setPassword(null))
                .collect(Collectors.toList());
        page.setRecords(collect);

        return page;


    }

    @Override
    public DoctorInfo getByDoctorId(Long doctorId) {
        LambdaQueryWrapper<DoctorInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DoctorInfo::getDoctorId, doctorId);
        return this.getOne(wrapper);
    }

    @Override
    public List<DoctorInfo> listDoctor() {
        List<DoctorInfo> list = this.list();
        return list.stream().peek(item -> item.setPassword(null)).collect(Collectors.toList());
    }
}
