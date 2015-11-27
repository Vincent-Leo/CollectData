package com.company.pages;

import com.company.db.DBTools;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageCompanyData {
    public static void getInfomation(CloseableHttpClient httpclient, String org, String id, String seq_id, String ip) throws Exception {
        String resultUrl = "http://www.jsgsj.gov.cn:58888/ecipplatform/ciServlet.json?ciEnter=true";
        HttpPost resultPost = new HttpPost(resultUrl);
        resultPost.addHeader("x-forwarded-for", ip);
        List<NameValuePair> resultList = new ArrayList<>();
        resultList.add(new BasicNameValuePair("org", org));
        resultList.add(new BasicNameValuePair("id", id));
        resultList.add(new BasicNameValuePair("seq_id", seq_id));
        resultList.add(new BasicNameValuePair("specificQuery", "basicInfo"));
        UrlEncodedFormEntity resultEntity = new UrlEncodedFormEntity(resultList, "UTF-8");
        resultPost.setEntity(resultEntity);
        HttpResponse resultResponse = httpclient.execute(resultPost);
        String responseStr = EntityUtils.toString(resultResponse.getEntity());
        System.out.println(responseStr);
        saveToDataBase(responseStr);
    }


    //解析response数据，拆出字段数据存入结构数据库
    public static boolean saveToDataBase(String responseStr){
        String[] dataArray = responseStr.replace("[","").replace("{","").replace("]","").replace("}","").trim().split(",");
        Map<String, String> dataMap = new HashMap<>();
        for(String data : dataArray){
            String key = data.split(":")[0].replace("\"", "");
            String value = "'" + data.split(":")[1].replace("\"", "").replace("\\n","") + "'";
            dataMap.put(key, value);
        }
        String sql = "insert into company set "
                + "companyID=" + dataMap.get("C1")
                + ",org=" + dataMap.get("ORG")
                + ",id=" + dataMap.get("ID")
                + ",seq_id=" + dataMap.get("SEQ_ID")
                + ",name=" + dataMap.get("C2")
                + ",type=" + dataMap.get("C3")
                + ",address=" + dataMap.get("C7")
                + ",capital=" + dataMap.get("C6")
                + ",capitalType=" + dataMap.get("CAPI_TYPE_NAME")
                + ",LegalRepresentative=" + dataMap.get("C5")
                + ",EstablishmentDate=" + dataMap.get("C9")
                + ",BusinessDateTo=" + dataMap.get("C10")
                + ",BusinessRange=" + dataMap.get("C8")
                + ",GovenmentName=" + dataMap.get("C11")
                + ",status=" + dataMap.get("C13")
                + ",checkDate=" + dataMap.get("C12");
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