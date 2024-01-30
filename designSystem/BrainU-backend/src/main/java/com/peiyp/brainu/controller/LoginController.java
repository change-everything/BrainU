package com.peiyp.brainu.controller;

import com.peiyp.brainu.entity.vo.LoginVo;
import com.peiyp.brainu.entity.vo.R;
import com.peiyp.brainu.entity.vo.UserVo;
import com.peiyp.brainu.service.LoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author peiYP
 * @create 2024-01-30 21:49
 **/
@RestController
@RequestMapping("/auth")
public class LoginController {


    @Resource
    private LoginService loginService;

    @PostMapping("/login")
    public R login(@RequestBody LoginVo loginVo) {
        UserVo userVo = loginService.login(loginVo);
        if (userVo != null) {
            return R.success("登录成功").setData(userVo);
        } else {
            return R.error(10001, "登录失败");
        }

    }

}
