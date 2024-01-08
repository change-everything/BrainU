package com.peiyp.brainucommon.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @author PeiYP
 * @since 2023年05月16日 16:56
 */
@Data
public class DoctorVo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer id;

    /**
     * 医生姓名
     */
    private String doctorName;

    /**
     * 医生工号
     */
    private Integer doctorId;

    /**
     * 医生手机号
     */
    private String doctorPhone;

    /**
     * 医生邮箱
     */
    private String doctorEmail;

    /**
     * 医生办公室
     */
    private String doctorOffice;

    /**
     * 医生状态
     */
    private Integer status;

}
