package com.moon.crm.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 封装分页对象实体类
 * 
 * @author MoonZero
 */
public class PageBean<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	// 每页显示的对象列表数据
	private List<T> Datas;
	// 当前页
	private int curPage;
	// 每页的数量
	private int pageSize = 3;
	// 总记录数量
	private int count;
	// 总页数
	private int totalPage;
	// 上一页
	private int prePage;
	// 下一页
	private int nextPage;
	// 开始索引，用于查询limit使用
	private int startIndex;

	/**
	 * 提供有参构造方法，方法内对可以定义的值进行封装
	 * 
	 * @param curPage
	 *            当前页数
	 * @param count
	 *            总记录数
	 */
	public PageBean(int curPage, int count) {
		this.curPage = curPage;
		this.count = count;
		// 总页数
		totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
		// 开始索引
		startIndex = (curPage - 1) * pageSize;
	}

	// 上一页
	public int getPrePage() {
		prePage = curPage - 1;
		if (prePage < 1) {
			prePage = 1;
		}
		return prePage;
	}

	// 下一页
	public int getNextPage() {
		nextPage = curPage + 1;
		if (nextPage > totalPage) {
			nextPage = totalPage;
		}
		return nextPage;
	}

	public List<T> getDatas() {
		return Datas;
	}

	public void setDatas(List<T> datas) {
		Datas = datas;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public void setPrePage(int prePage) {
		this.prePage = prePage;
	}

	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}

	@Override
	public String toString() {
		return "PageBean [Datas=" + Datas + ", curPage=" + curPage + ", pageSize=" + pageSize + ", count=" + count
				+ ", totalPage=" + totalPage + ", prePage=" + prePage + ", nextPage=" + nextPage + ", startIndex="
				+ startIndex + "]";
	}
}
