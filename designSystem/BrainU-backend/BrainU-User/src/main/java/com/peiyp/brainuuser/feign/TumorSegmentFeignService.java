package com.peiyp.brainuuser.feign;

import com.peiyp.brainucommon.vo.R;
import com.peiyp.brainuuser.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author PeiYP
 * @since 2023年04月12日 15:44
 */
@FeignClient(value = "brainU-tumorSegment", configuration = FeignConfig.class)
public interface TumorSegmentFeignService {

    @GetMapping("/segment/previewPic")
    String previewPicUrl(@RequestParam("rootPath") String rootPath);

}
