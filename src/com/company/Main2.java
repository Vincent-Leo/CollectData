package com.company;

import com.company.db.DBTools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;

public class Main2 {

    public static void main(String args[]) {

//        String startCompanyID = args[0];
//        String endCompanyID = args[1];
        String startCompanyID;
        String endCompanyID;
        try {
            InputStream inputStream = System.in;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            System.out.println("please enter the start company id:");
            startCompanyID = bufferedReader.readLine();
            endCompanyID = bufferedReader.readLine();
            System.out.println(startCompanyID);
            System.out.println(endCompanyID);

        if (Long.parseLong(startCompanyID) > Long.parseLong(endCompanyID)) {
            System.out.println("起始注册号输入错误！");
            System.exit(0);
        }

            DBTools dbTools = new DBTools();
            String sql = "select companyid from company_index_copy where companyid>=" + startCompanyID + " and companyid<" + endCompanyID;
            System.out.println(sql);
            ResultSet rs = dbTools.executeQuery(sql);
            JiangSuThread2 jiangSuThread2 = new JiangSuThread2();
            while (rs.next()) {
                String ip = RandomIP.getRandomIp();
                String id = rs.getString(1);
                jiangSuThread2.setCompanyID(id);
                jiangSuThread2.setIp(ip);
                System.out.println("ip:" + ip);
                System.out.println("rs:" + rs.getString(1));
                jiangSuThread2.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}