package com.company.db;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class DBTools {

    private java.sql.Connection connection;
    private java.sql.PreparedStatement preparedStatement;

    public DBTools() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/collectdata", "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ResultSet executeQuery(String sql, LinkedList<Object> params) {
        try {
            preparedStatement = connection.prepareStatement(sql);
            if (params != null) {
                int i = 1;
                for (Object p : params) {
                    preparedStatement.setObject(i++, p);
                }
            }
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResultSet executeQuery(String sql) {
        try {
            preparedStatement = connection.prepareStatement(sql);
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int executeInsert(String sql) {
        try {
            preparedStatement = connection.prepareStatement(sql);
            return preparedStatement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -2;
    }

//    public static void main(String[] args) {
//        LinkedList<Object> params = new LinkedList<Object>();
//        params.add("HOME");
//        DBTools tool = new DBTools();
//        ResultSet rs = tool.executeQuery("SELECT * FROM users where Type= ? order by UpdateTime limit 100", params);
//        try {
//            System.err.println(rs.next());
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        tool.close();
//    }
}

