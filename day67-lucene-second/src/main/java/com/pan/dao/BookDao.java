package com.pan.dao;

import com.pan.pojo.Book;

import java.util.List;

public interface BookDao {

    /**
     * 查询图书列表
     */
    List<Book> queryBookList();
}
