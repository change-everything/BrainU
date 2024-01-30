package com.peiyp.brainucommon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author PeiYP
 * @since 2023年04月07日 23:09
 */
@Data
public class PatientInfo {
    @TableId(type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    private String patientName;
    private Integer patientAge;
    private Integer patientGender;
    private String patientPhone;
    private String patientIdcard;
    private String handleBy;
    private Date createTime;
    private Date updateTime;
    private String imgPath;
    @TableField(exist = false)
    private String url;
    private Integer isHandle;
    @TableField(exist = false)
    private String handleByName;
}
