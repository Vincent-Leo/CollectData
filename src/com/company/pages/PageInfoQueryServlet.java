package com.company.pages;

import com.company.db.DBTools;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageInfoQueryServlet {
    public static String getInfomation(CloseableHttpClient httpclient, String capthca, String companyID, String ip) throws Exception {
        String queryResultUrl = "http://www.jsgsj.gov.cn:58888/province/infoQueryServlet.json?queryCinfo=true";
        HttpPost queryResultPost = new HttpPost(queryResultUrl);
        queryResultPost.addHeader("x-forwarded-for",ip);
        List<NameValuePair> queryResultList = new ArrayList<>();
        String org = null;
        String id = null;
        String seq_id = null;
        queryResultList.add(new BasicNameValuePair("name", companyID));
        queryResultList.add(new BasicNameValuePair("verifyCode", capthca));
        UrlEncodedFormEntity queryResultEntity = new UrlEncodedFormEntity(queryResultList, "UTF-8");
        queryResultPost.setEntity(queryResultEntity);
        HttpResponse queryResultResponse = httpclient.execute(queryResultPost);
        String responseStr = EntityUtils.toString(queryResultResponse.getEntity());
        System.out.println("Response:" + responseStr);
        System.out.println("companyID:" + companyID);
        System.out.println("capthca:" + capthca);

        if (responseStr.contains("ci_queryCorpInfor_gsRelease")) {
            String[] params = responseStr.split(",");
            org = params[1].replace("'", "");
            id = params[2].replace("'", "");
            seq_id = params[3].replace("'", "");
            System.out.println("org:" + org);
            System.out.println("id:" + id);
            System.out.println("seq_id:" + seq_id);
            //存入数据库
            saveToDataBase(companyID, org, id, seq_id);

            //将org等信息写入文件
                    BufferedWriter bw = null;
                    try {
                        File file = new File("C:/codes/JiangSu_log.txt");
                        if (!file.exists()) {
                            file.createNewFile();
                }
                FileWriter fw = new FileWriter(file, true);
                bw = new BufferedWriter(fw);
                bw.write("companyID:" + companyID);
                bw.write(",org:" + org);
                bw.write(",id:" + id);
                bw.write(",seq_id:" + seq_id);
                bw.newLine();
                bw.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try{
                    bw.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return org + "," + id + "," + seq_id;
        } else {
            //将org等信息写入文件
            BufferedWriter bw = null;
            try {
                File file = new File("C:/codes/JiangSu_log.txt");
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fw = new FileWriter(file, true);
                bw = new BufferedWriter(fw);
                bw.write("companyID:" + companyID + " is invalid");
                bw.newLine();
                bw.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try{
                    bw.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public static boolean saveToDataBase(String companyID, String org, String id, String seq_id) {
        String sql = "insert into company_index set "
                + "companyID=" + companyID
                + ",org=" + org
                + ",id=" + id
                + ",seq_id=" + seq_id;
        System.out.println(sql);
        DBTools tool = new DBTools();
        int code = tool.executeInsert(sql);
        if (code > -1) {
            return true;
        } else {
            return false;
        }
    }
}
