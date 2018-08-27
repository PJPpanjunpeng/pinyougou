package com.pan.jd.pojo;

import java.util.List;

public class Result {

    //当前页
    private Integer curPage;
    //页数
    private Integer pageCount;
    //总记录数
    private Long recordCount;
    //记录列表
    private List<Product> productList;
    public Integer getCurPage() {
        return curPage;
    }
    public void setCurPage(Integer curPage) {
        this.curPage = curPage;
    }
    public Integer getPageCount() {
        return pageCount;
    }
    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }
    public List<Product> getProductList() {
        return productList;
    }
    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public Long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Long recordCount) {
        this.recordCount = recordCount;
    }
}
