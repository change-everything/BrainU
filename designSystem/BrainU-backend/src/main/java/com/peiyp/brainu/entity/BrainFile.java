package com.peiyp.brainu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author PeiYP
 * @since 2023年03月23日 20:58
 */
@Data
public class BrainFile {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    private String fileId;
    private String originalFileName;
    private String uploadPath;
    private Date createTime;
    private Date updateTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long patientId;
}
