package com.company.pages;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class PageCompanyBasic {
    public static void getInfomation(CloseableHttpClient httpclient, String org, String id, String seq_id, String name, String ip) throws Exception {
        String resultUrl = "http://www.jsgsj.gov.cn:58888/ecipplatform/inner_ci/ci_queryCorpInfor_gsRelease.jsp";
        HttpPost resultPost = new HttpPost(resultUrl);
        resultPost.addHeader("x-forwarded-for",ip);
        List<NameValuePair> resultList = new ArrayList<>();
        resultList.add(new BasicNameValuePair("org", org));
        resultList.add(new BasicNameValuePair("id", id));
        resultList.add(new BasicNameValuePair("seq_id", seq_id));
        resultList.add(new BasicNameValuePair("reg_no", name));
        resultList.add(new BasicNameValuePair("uni_scid", ""));
        resultList.add(new BasicNameValuePair("sname", ""));
        resultList.add(new BasicNameValuePair("containContextPath", "ecipplatform"));
        resultList.add(new BasicNameValuePair("verifyCode", ""));
        resultList.add(new BasicNameValuePair("name", name));
        UrlEncodedFormEntity resultEntity = new UrlEncodedFormEntity(resultList, "UTF-8");
        resultPost.setEntity(resultEntity);
        HttpResponse resultResponse = httpclient.execute(resultPost);
        System.out.println(EntityUtils.toString(resultResponse.getEntity()));
    }
}