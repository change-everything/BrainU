package com.peiyp.brainumodel.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peiyp.brainumodel.entity.ModelInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author peiYP
 * @since 2023-05-06
 */
public interface IModelInfoService extends IService<ModelInfo> {

    /**
     * 分页查询模型
     *
     * @param modelInfo
     * @param pageNum
     * @param pageSize
     * @return java.util.List<com.peiyp.brainumodel.entity.ModelInfo>
     * @author PeiYP
     * @since 2023/5/6 22:58
     */
    Page<ModelInfo> listPage(ModelInfo modelInfo, int pageNum, int pageSize);
}
