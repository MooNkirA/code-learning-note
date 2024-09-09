package com.moonzero.shop.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 优化后的分页实体类
 * 
 * @author MoonZero
 */
public class PageBean<T> implements Serializable {
	// 每页显示的商品列表数据
	private List<T> datas;
	// 首页，都是数值为1
	private int firstPage = 1;
	// 上一页
	private int prePage;
	// 下一页
	private int nextPage;
	// 总页数
	private int totalPage;
	// 当前页
	private int curPage;
	// 总记录数据
	private int count;
	// 每页显示记录数，可以在service层进行设置，也可以在类中直接设置
	private int pageSize = 7;

	public List<T> getDatas() {
		return datas;
	}

	public void setDatas(List<T> datas) {
		this.datas = datas;
	}

	public int getFirstPage() {
		return firstPage;
	}

	public void setFirstPage(int firstPage) {
		this.firstPage = firstPage;
	}

	public int getPrePage() {
		prePage = curPage - 1;
		return prePage;
	}

	public void setPrePage(int prePage) {
		this.prePage = prePage;
	}

	public int getNextPage() {
		nextPage = curPage + 1;
		return nextPage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	public int getTotalPage() {
		totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize
				+ 1;
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return "PageBean [firstPage=" + firstPage + ", prePage=" + prePage
				+ ", nextPage=" + nextPage + ", totalPage=" + totalPage
				+ ", curPage=" + curPage + ", count=" + count + ", pageSize="
				+ pageSize + "]";
	}

	public PageBean(List<T> datas, int firstPage, int prePage, int nextPage,
			int totalPage, int curPage, int count, int pageSize) {
		super();
		this.datas = datas;
		this.firstPage = firstPage;
		this.prePage = prePage;
		this.nextPage = nextPage;
		this.totalPage = totalPage;
		this.curPage = curPage;
		this.count = count;
		this.pageSize = pageSize;
	}

	public PageBean() {
		super();
	}
}
