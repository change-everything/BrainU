package com.peiyp.brainucommon.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author PeiYP
 * @since 2023年03月22日 23:15
 */
@Data
@Accessors(chain = true)
public class PartInfo implements Serializable {

    private int partNumber;

    private String etag;

    private String lastModified;

    private Long size;

}