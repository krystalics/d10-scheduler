package com.github.krystalics.d10.scheduler.dao.entity;


import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author krysta
 * @since 2021-10-02
 */
public class InstanceRely implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer versionId;

    private Integer upVersionId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }
    public Integer getUpVersionId() {
        return upVersionId;
    }

    public void setUpVersionId(Integer upVersionId) {
        this.upVersionId = upVersionId;
    }

    @Override
    public String toString() {
        return "InstanceRely{" +
            "id=" + id +
            ", versionId=" + versionId +
            ", upVersionId=" + upVersionId +
        "}";
    }
}
