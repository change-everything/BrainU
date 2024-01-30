package com.peiyp.brainutumorsegmentation.feign;

import com.peiyp.brainucommon.vo.R;
import com.peiyp.brainutumorsegmentation.config.FeignConfig;
import com.peiyp.brainutumorsegmentation.entity.PatientInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author PeiYP
 * @since 2023年05月06日 8:02
 */
@FeignClient(value = "brainU-user", configuration = FeignConfig.class)
public interface UserFeignService {
    @PostMapping("/patient")
    R insert(@RequestBody PatientInfo patientInfo);

    @GetMapping("/patient/one/{id}")
    PatientInfo getPatientById(@PathVariable Long id);
}
