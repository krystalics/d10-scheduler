package com.github.krystalics.d10.scheduler.dao.entity;


import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author krysta
 * @since 2021-10-02
 */
@Data
public class InstanceRely implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer versionId;

    private Integer upVersionId;

}
