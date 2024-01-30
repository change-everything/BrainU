package com.peiyp.brainuauth.feign;

import com.peiyp.brainuauth.config.FeignConfig;
import com.peiyp.brainucommon.entity.DoctorInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author PeiYP
 * @since 2023年05月16日 17:03
 */
@FeignClient(value = "brainU-user", configuration = FeignConfig.class)
public interface UserFeignService {

    @GetMapping("/doctor-info/{doctorId}")
    DoctorInfo getByDoctorId(@PathVariable Long doctorId);

}
