package com.peiyp.brainuauth.service;

import com.peiyp.brainucommon.vo.LoginVo;
import com.peiyp.brainucommon.vo.UserVo;

/**
 * @author PeiYP
 * @since 2023年05月16日 19:16
 */
public interface LoginService {


    UserVo login(LoginVo loginVo);


}
