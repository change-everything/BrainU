package com.peiyp.brainutumorsegmentation.feign;

import com.peiyp.brainucommon.vo.R;
import com.peiyp.brainutumorsegmentation.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author PeiYP
 * @since 2023年05月07日 18:01
 */
@FeignClient(value = "brainU-model", configuration = FeignConfig.class)
public interface ModelFeignService {

    @GetMapping("/modelInfo/{id}")
    R getModelById(@PathVariable("id") String id);

}
