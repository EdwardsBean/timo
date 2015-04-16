package com.edwardsbean.timo.common.pagination;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 分页List，具体分页信息保存在Paginator中。
 *
 * @author dingxuefeng
 */
public class PageList<T> {
    private List<T> list;
    private Paginator paginator;

    public PageList() {
        this.list = new ArrayList<T>();
        paginator = new Paginator();
    }

    public PageList(Collection<? extends T> c) {
        this(c, null);
    }

    public PageList(Collection<? extends T> c, Paginator paginator) {
        this.list = new ArrayList<T>(c);
        this.paginator = paginator == null ? new Paginator() : paginator;
    }

    public Paginator getPaginator() {
        return paginator;
    }

    public void setPaginator(Paginator paginator) {
        if (paginator != null) {
            this.paginator = paginator;
        }
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
