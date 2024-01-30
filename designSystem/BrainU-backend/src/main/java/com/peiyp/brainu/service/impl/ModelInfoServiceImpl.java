package com.peiyp.brainu.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.peiyp.brainu.entity.ModelInfo;
import com.peiyp.brainu.mapper.ModelInfoMapper;
import com.peiyp.brainu.service.IModelInfoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author peiYP
 * @since 2023-05-06
 */
@Service
public class ModelInfoServiceImpl extends ServiceImpl<ModelInfoMapper, ModelInfo> implements IModelInfoService {

    @Override
    public Page<ModelInfo> listPage(ModelInfo modelInfo, int pageNum, int pageSize) {

        return this.baseMapper.selectPage(new Page<>(pageNum, pageSize), null);
    }
}
