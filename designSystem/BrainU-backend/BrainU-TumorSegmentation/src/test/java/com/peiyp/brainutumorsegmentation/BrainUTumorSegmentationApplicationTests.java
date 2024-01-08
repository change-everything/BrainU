package com.peiyp.brainutumorsegmentation;

import com.peiyp.brainutumorsegmentation.entity.BrainFile;
import com.peiyp.brainutumorsegmentation.mapper.BrainFileMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

@SpringBootTest
class BrainUTumorSegmentationApplicationTests {

    @Resource
    private BrainFileMapper brainFileMapper;

    @Test
    void contextLoads() {

        BrainFile brainFile = new BrainFile();
        brainFile.setFileId("dada");
        brainFile.setCreateTime(new Date());
        brainFile.setOriginalFileName("dadawfggg");
        brainFile.setUploadPath("dawwwwwwwwwwwwwwwwwwwww");
        brainFile.setUpdateTime(new Date());

        brainFileMapper.insert(brainFile);
    }

}
