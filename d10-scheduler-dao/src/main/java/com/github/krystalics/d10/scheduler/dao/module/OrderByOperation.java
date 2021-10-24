package com.github.krystalics.d10.scheduler.dao.module;

import java.util.ArrayList;
import java.util.List;

/**
 * order by 操作类 ，支持多个字段
 * @author yueyunyue
 *
 */
public class OrderByOperation {

	/**
	 * 操作符集合
	 */
	private List<OrderByItem> itemOperators = new ArrayList<>();

	/**
	 * 操作符号 asc desc 枚举
	 */
	public static final String ORDER_OPERATOR_ASC = "asc";

	public static final String ORDER_OPERATOR_DESC = "desc";

	public List<OrderByItem> getItemOperators() {
		return itemOperators;
	}

	public void setItemOperators(List<OrderByItem> itemOperators) {
		this.itemOperators = itemOperators;
	}
	/*
	 * 添加操作单项
	 */
	public void addOrderItem(OrderByItem item){
		itemOperators.add(item);
	}

	/*
	 * 添加操作单项
	 */
	public void addOrderItem(String column, String operator){
		itemOperators.add(new OrderByItem(column, operator));
	}


	/**
	 * 每个操作的item
	 * @author yueyunyue
	 *
	 */
	public static class OrderByItem{
		private String column; //字段名称
		private String operator;//操作 asc或desc
		public OrderByItem() {

		}
		public OrderByItem(String column, String operator) {
			this.column = column;
			this.operator = operator;
		}
		public String getColumn() {
			return column;
		}

		public void setColumn(String column) {
			this.column = column;
		}

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}

	}

}
