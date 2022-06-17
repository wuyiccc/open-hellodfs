package com.wuyiccc.hellodfs.admin.common.util;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author wuyiccc
 * @date 2020/10/26 19:45
 * 分页对象
 */
public class PagedGridResult {

    private int page; // 当前页数

    private long total; // 总页数

    private long records; // 总记录数

    private List<?> rows; // 每行显示的内容

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getRecords() {
        return records;
    }

    public void setRecords(long records) {
        this.records = records;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }

    public PagedGridResult() {
    }

    public PagedGridResult(List<?> list, Integer page) {
        PageInfo<?> pageInfo = new PageInfo<>(list);
        this.page = page;
        this.rows = list;
        this.total = pageInfo.getPages();
        this.records = pageInfo.getTotal();
    }



    @Override
    public String toString() {
        return "PagedGridResult{" +
                "page=" + page +
                ", total=" + total +
                ", records=" + records +
                ", rows=" + rows +
                '}';
    }
}
