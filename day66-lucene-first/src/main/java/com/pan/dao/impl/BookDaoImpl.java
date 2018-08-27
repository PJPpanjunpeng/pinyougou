package com.pan.dao.impl;

import com.pan.dao.BookDao;
import com.pan.pojo.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {
    public List<Book> queryBookList() {
        List<Book> bookList = new ArrayList<Book>();

        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            //加载驱动
            Class.forName("com.mysql.jdbc.Driver");
            //获取数据库连接
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/day66_lucene", "root", "root");
            //创建执行对象
            pstm = conn.prepareStatement("select * from book");
            //执行数据库操作
            rs = pstm.executeQuery();
            //结果集处理
            Book book = null;
            while (rs.next()) {
                book = new Book();
                book.setId(rs.getInt("id"));
                book.setBookname(rs.getString("bookname"));
                book.setPrice(rs.getFloat("price"));
                book.setPic(rs.getString("pic"));
                book.setBookdesc(rs.getString("bookdesc"));

                bookList.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放资源
            try {
                if (rs != null) {
                    rs.close();
                }

                if (pstm != null) {
                    pstm.close();
                }

                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return bookList;
    }


    public static void main(String agrs[]) {
        BookDao bookDao = new BookDaoImpl();
        List<Book> bookList = bookDao.queryBookList();

        for (Book book : bookList) {
            System.out.println(book);
        }
    }
}
