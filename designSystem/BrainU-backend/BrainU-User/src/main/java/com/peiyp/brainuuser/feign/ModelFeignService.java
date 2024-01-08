package com.peiyp.brainuuser.feign;

import com.peiyp.brainucommon.entity.ModelInfo;
import com.peiyp.brainuuser.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author PeiYP
 * @since 2023年05月07日 0:36
 */
@FeignClient(value = "brainU-model", configuration = FeignConfig.class)
public interface ModelFeignService {

    @GetMapping("/modelInfo/list")
    List<ModelInfo> list();

}
