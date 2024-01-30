package com.peiyp.brainu.service;


import com.peiyp.brainu.entity.vo.LoginVo;
import com.peiyp.brainu.entity.vo.UserVo;

/**
 * @author PeiYP
 * @since 2023年05月16日 19:16
 */
public interface LoginService {


    UserVo login(LoginVo loginVo);


}
