package jp.co.worksap.roster.rest.modelview;

import java.util.List;

public class PaginatedData<E> {
	public PaginatedData(List<E> data, long page, long size) {
		super();
		this.data = data;
		this.page = page;
		this.size = size;
	}
	private List<E> data;
	private long page;
	private long size;

	public List<E> getData() {
		return data;
	}
	public void setData(List<E> data) {
		this.data = data;
	}
	public long getPage() {
		return page;
	}
	public void setPage(long page) {
		this.page = page;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
}
