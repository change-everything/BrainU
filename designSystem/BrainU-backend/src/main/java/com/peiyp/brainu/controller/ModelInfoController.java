package com.peiyp.brainu.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.peiyp.brainu.entity.ModelInfo;
import com.peiyp.brainu.entity.vo.R;
import com.peiyp.brainu.service.IModelInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author peiYP
 * @since 2023-05-06
 */
@RestController
@RequestMapping("/modelInfo")
public class ModelInfoController {

    @Resource
    private IModelInfoService modelInfoService;

    @GetMapping("/list/{pageNum}/{pageSize}")
    public R modelList(@RequestBody(required = false) ModelInfo modelInfo, @PathVariable int pageNum, @PathVariable int pageSize) {
        Page<ModelInfo> list = modelInfoService.listPage(modelInfo, pageNum, pageSize);
        return R.success().setData(list);
    }

    @GetMapping("/list")
    public R list() {
        List<ModelInfo> list = modelInfoService.list();
        return R.success().setData(list);
    }

    @GetMapping("/{id}")
    public R getById(@PathVariable String id) {
        ModelInfo modelInfo = modelInfoService.getById(id);
        return R.success().setData(modelInfo);
    }

    @PostMapping
    public R save(@RequestBody ModelInfo modelInfo) {
        modelInfoService.saveOrUpdate(modelInfo);
        return R.success();
    }


    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        modelInfoService.removeById(id);
        return R.success();
    }


}
