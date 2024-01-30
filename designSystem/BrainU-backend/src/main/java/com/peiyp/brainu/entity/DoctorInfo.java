package com.peiyp.brainu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author peiYP
 * @since 2023-04-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("doctor_info")
public class DoctorInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer id;

    /**
     * 医生姓名
     */
    @TableField("doctor_name")
    private String doctorName;

    /**
     * 医生工号
     */
    @TableField("doctor_id")
    private Integer doctorId;

    /**
     * 医生手机号
     */
    @TableField("doctor_phone")
    private String doctorPhone;

    /**
     * 医生邮箱
     */
    @TableField("doctor_email")
    private String doctorEmail;

    /**
     * 医生办公室
     */
    @TableField("doctor_office")
    private String doctorOffice;

    /**
     * 登录密码
     */
    @TableField("password")
    private String password;

    /**
     * 医生状态
     */
    @TableField("status")
    private Integer status;


}
