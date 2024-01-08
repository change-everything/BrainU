package com.peiyp.brainuauth.controller;

import com.peiyp.brainuauth.service.LoginService;
import com.peiyp.brainucommon.entity.DoctorInfo;
import com.peiyp.brainucommon.vo.LoginVo;
import com.peiyp.brainucommon.vo.R;
import com.peiyp.brainucommon.vo.UserVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author PeiYP
 * @since 2023年05月16日 14:45
 */
@RestController
@RequestMapping
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
