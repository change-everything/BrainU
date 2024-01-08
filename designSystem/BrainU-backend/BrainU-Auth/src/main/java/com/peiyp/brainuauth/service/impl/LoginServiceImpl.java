package com.peiyp.brainuauth.service.impl;

import com.peiyp.brainuauth.feign.UserFeignService;
import com.peiyp.brainuauth.service.LoginService;
import com.peiyp.brainucommon.util.JWTUtil;
import com.peiyp.brainucommon.entity.DoctorInfo;
import com.peiyp.brainucommon.vo.LoginVo;
import com.peiyp.brainucommon.vo.UserVo;
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
    private UserFeignService userFeignService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public UserVo login(LoginVo loginVo) {
        Long username = Long.valueOf(loginVo.getUsername());
        DoctorInfo info = userFeignService.getByDoctorId(username);
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
