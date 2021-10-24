package com.github.krystalics.d10.scheduler.dao.module;

/**
 * 分页帮助类
 */
public class Page {

    public final static Integer DEFAULT_LIMIT = 10;

    public final static Integer DEFAULT_OFFSET = 0;

    private Integer offset;

    private Integer limit;

    public Page() {
        this.offset = DEFAULT_OFFSET;
        this.limit = DEFAULT_LIMIT;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * 获取page
     *
     * @param offset
     * @param limit
     * @return
     */
    public static Page getPage(int offset, int limit) {
        Page page = new Page();
        if (offset <= 0) {
            offset = 0;
        }
        if (limit <= 0) {
            limit = DEFAULT_LIMIT;
        }
        page.setOffset(offset);
        page.setLimit(limit);
        return page;
    }

    /**
     * 获取默认的分页
     *
     * @return
     */
    public static Page getDefaultPage() {
        return getPage(DEFAULT_OFFSET, DEFAULT_LIMIT);
    }

    /**
     * 根据pageNo和pageSize获取分页
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static Page getPageByPageNo(int pageNo, int pageSize) {
        if (pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize <= 0) {
            pageSize = DEFAULT_LIMIT;
        }
        Page page = new Page();
        page.setOffset((pageNo - 1) * pageSize);
        page.setLimit(pageSize);
        return page;
    }

    public static Page getPageByPageNo(int pageNo) {
        return getPageByPageNo(pageNo, DEFAULT_LIMIT);
    }

}
