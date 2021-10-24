package com.github.krystalics.d10.scheduler.dao;

import java.util.List;

/**
 * 基础的dao
 */
public interface BaseMapper<M, Q> {

    /**
     * 添加数据
     * @param model 对应数据封装domain
	 * @return 插入行数
     */
    long insert(M model);

    /**
     * 通过id获取数据
     * @param id 查找id
     * @return 数据封装domain
     */
    M getById(long id);

    /**
     * 更新数据
     * @param model 数据封装domain
	 * @return 更新行数
     */
    int update(M model);

    /**
     * 获取数量
     * @param queryModel 数据封装的domain
     * @return 查询得到多少行数据
     */
    int count(Q queryModel);

    /**
     * 获取列表数据
     * @param queryModel 数据封装的domain
     * @return 查询结果列表
     */
    List<M> list(Q queryModel);

}
