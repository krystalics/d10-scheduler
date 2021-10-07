package com.github.krystalics.d10.scheduler.common.service;

import java.util.List;

/**
 * 基础的service
 */
public interface BaseService<M, Q> {

    /**
     * 添加数据
     *
     * @param model
     */
    void add(M model);

    /**
     * 通过id获取数据
     *
     * @param id
     * @return
     */

    M getById(long id);
    /**
     * 更新数据
     *
     * @param model
     */
    int update(M model);

    /**
     * 获取数量
     *
     * @param queryModel
     * @return
     */
    int count(Q queryModel);

    /**
     * 获取列表数据
     *
     * @param queryModel
     * @return
     */
    List<M> list(Q queryModel);

//	/**
//	 * 使数据失效
//	 *
//	 * @param m
//	 * @return
//	 */
//	int invalidate(M m);
}
