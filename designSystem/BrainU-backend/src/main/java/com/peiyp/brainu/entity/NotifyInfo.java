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
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author peiyp
 * @since 2023-05-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("notify_info")
public class NotifyInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 操作信息
     */
    @TableField("context")
    private String context;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 是否处理
     */
    @TableField("is_handle")
    private Integer isHandle;

    /**
     * 患者id
     */
    @TableField("patient_id")
    private Long patientId;


}
