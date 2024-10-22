package com.peiyp.brainu.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.peiyp.brainu.entity.NotifyInfo;
import com.peiyp.brainu.entity.vo.R;
import com.peiyp.brainu.service.INotifyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author peiyp
 * @since 2023-05-22
 */
@RestController
@RequestMapping("/tumor/notify-info")
public class NotifyInfoController {

    @Autowired
    private INotifyInfoService notifyInfoService;


    @GetMapping
    public R list() {
        List<NotifyInfo> list = notifyInfoService.list(new LambdaQueryWrapper<NotifyInfo>().eq(NotifyInfo::getIsHandle, 0));
        return R.success().setData(list);
    }

}
