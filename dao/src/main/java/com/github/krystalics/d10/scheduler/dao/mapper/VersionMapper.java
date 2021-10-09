package com.github.krystalics.d10.scheduler.dao.mapper;

import com.github.krystalics.d10.scheduler.dao.BaseMapper;
import com.github.krystalics.d10.scheduler.dao.entity.Version;
import com.github.krystalics.d10.scheduler.dao.qm.VersionQM;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author linjiabao001
 * @date 2021/10/5
 * @description
 */
@Mapper
public interface VersionMapper extends BaseMapper<Version, VersionQM> {
    /**
     * 通过任务id和version number寻找version
     * @param taskId 任务id
     * @param versionNo 版本号
     * @return 版本
     */
    Version findByTaskIdAndVersionNo(@Param("taskId") long taskId, @Param("versionNo") String versionNo);
}
