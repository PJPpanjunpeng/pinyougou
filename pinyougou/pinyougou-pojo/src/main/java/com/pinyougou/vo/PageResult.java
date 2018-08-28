package com.pinyougou.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 该对象需要进行网络传递，需要实现序列化
 */
public class PageResult implements Serializable {

    //总记录数
    private Long total;

    //记录列表：？表示占位符类似泛型
    private List<?>  rows;

    public PageResult(Long total, List<?> rows) {
        this.total = total;
        this.rows = rows;
    }

    public PageResult() {
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
}
