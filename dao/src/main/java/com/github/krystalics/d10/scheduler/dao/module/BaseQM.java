package com.github.krystalics.d10.scheduler.dao.module;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * 基础的查询模型
 */
@Data
public class BaseQM implements Serializable {

	private Long id;                                  //id

	private List<Long> ids;                           //ids

	private Page page;                                //分页

	private OrderByOperation orderByOperation;        //order by 相关操作，支持多个字段

	private int valid;                                //查询哪种数据，1:有效数据，-1:无效数据，0:所有数据

	public static final int DEFAULT_PAGE_SIZE = 10;      //默认每页数量

	public static final String COL_ID = "id";


	/**
	 * 添加排序
	 *
	 * @param orderColumn
	 */
	public void addOrderAsc(String orderColumn) {

		addOrder(orderColumn, OrderByOperation.ORDER_OPERATOR_ASC);

	}

	public void addOrderDesc(String orderColumn) {

		addOrder(orderColumn, OrderByOperation.ORDER_OPERATOR_DESC);
	}

	public void addOrder(String column, String order) {
		if (orderByOperation == null) {
			orderByOperation = new OrderByOperation();
		}

		orderByOperation.addOrderItem(column, order);
	}

	/**
	 * 不允许设置pageSize
	 * @param pageNum 页码
	 */
	public void setPageNum(int pageNum) {
		setPage(pageNum, DEFAULT_PAGE_SIZE);
	}

	/**
	 * 根据页码和每页数量  设置分页查询参数
	 * 重载了@data注解生成的setPage(Page page)
	 * @param pageNum 页码
	 */
	public void setPage(int pageNum, int pageSize) {
		final Page page = Page.getPageByPageNo(pageNum, pageSize);
		this.setPage(page);
	}

}
