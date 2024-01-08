package com.peiyp.brainutumorsegmentation.vo;

import com.peiyp.brainutumorsegmentation.entity.PatientInfo;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author PeiYP
 * @since 2023年05月05日 23:30
 */
@Data
public class FileVo {

    private List<MultipartFile> files;
    private PatientInfo patientInfo;
}
