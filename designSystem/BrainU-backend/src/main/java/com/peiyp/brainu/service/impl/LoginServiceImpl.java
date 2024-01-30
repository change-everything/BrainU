package com.peiyp.brainu.service.impl;


import com.peiyp.brainu.entity.DoctorInfo;
import com.peiyp.brainu.entity.vo.LoginVo;
import com.peiyp.brainu.entity.vo.UserVo;
import com.peiyp.brainu.service.DoctorInfoService;
import com.peiyp.brainu.service.LoginService;
import com.peiyp.brainu.util.JWTUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author PeiYP
 * @since 2023年05月16日 19:17
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private DoctorInfoService doctorInfoService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public UserVo login(LoginVo loginVo) {
        Long username = Long.valueOf(loginVo.getUsername());
        DoctorInfo info = doctorInfoService.getByDoctorId(username);
        if (info != null) {
            if (loginVo.getPassword().equals(info.getPassword())) {
                UserVo userVo = new UserVo();userVo.setDoctorId(Math.toIntExact(username));
                String token = JWTUtil.createToken(username, "USER", 60);
                stringRedisTemplate.opsForValue().set("login:token:" + username, token, 60, TimeUnit.MINUTES);
                userVo.setToken(token);
                return userVo;
            } else {
                return null;
            }
        }
        return null;
    }
}
