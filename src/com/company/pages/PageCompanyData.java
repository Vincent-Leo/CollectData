package com.company.pages;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class PageCompanyData {
    public static void getInfomation(CloseableHttpClient httpclient, String org, String id, String seq_id, String name) throws Exception {
        String resultUrl = "http://www.jsgsj.gov.cn:58888/ecipplatform/ciServlet.json?ciEnter=true";
        HttpPost resultPost = new HttpPost(resultUrl);
        List<NameValuePair> resultList = new ArrayList<>();
        resultList.add(new BasicNameValuePair("org", org));
        resultList.add(new BasicNameValuePair("id", id));
        resultList.add(new BasicNameValuePair("seq_id", seq_id));
        resultList.add(new BasicNameValuePair("specificQuery", "basicInfo"));
        UrlEncodedFormEntity resultEntity = new UrlEncodedFormEntity(resultList, "UTF-8");
        resultPost.setEntity(resultEntity);
        HttpResponse resultResponse = httpclient.execute(resultPost);
    }
}