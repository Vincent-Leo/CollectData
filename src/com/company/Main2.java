package com.company;

import com.company.ocr.OCR;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Main2 {

    private static final String FileRootPath = "C:/codes/jiangsu";

    /**
     * 将验证码图片保存到本地
     * @param httpEntity
     * @param filename
     */
    public static void saveToLocal(HttpEntity httpEntity, String filename) {

        try {
            File dir = new File(FileRootPath);
            if (!dir.isDirectory()) {
                dir.mkdir();
            }

            File file = new File(FileRootPath + "/" + filename);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            InputStream inputStream = httpEntity.getContent();

            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] bytes = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(bytes)) > 0) {
                fileOutputStream.write(bytes, 0, length);
            }
            inputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showResponse (HttpResponse response) throws IOException {
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    public static void main(String args[]) throws Exception {

        //第一次连接，获取cookie
        String url = "http://www.jsgsj.gov.cn:58888/province/";
        DefaultHttpClient client = new DefaultHttpClient(new PoolingClientConnectionManager());
        HttpGet indexGet = new HttpGet(url);
        client.execute(indexGet);
        CookieStore cookieStore = client.getCookieStore();

        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();

        //生成验证码的链接
        String createCaptchaUrl = "http://www.jsgsj.gov.cn:58888/province/rand_img.jsp?type=7";

        //获取验证码
        HttpGet captchaHttpGet = new HttpGet(createCaptchaUrl);
        HttpResponse capthcaResponse = httpclient.execute(captchaHttpGet);

        //将验证码写入本地
        String captchaImageFilename = null;
        if (capthcaResponse.getStatusLine().getStatusCode() == 200) {
            captchaImageFilename = "suzhou_" + System.currentTimeMillis() + ".bmp";
            saveToLocal(capthcaResponse.getEntity(), captchaImageFilename);
        }

        //TestVersion 1 : 手工输入验证码
//        String capthca = null;
//        InputStream inputStream = System.in;
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//        System.out.println("请输入验证码:");
//        capthca = bufferedReader.readLine();

        // Version 2 : 调用验证码识别API
        //String capthca = CaptchaRecognize.getCaptchaCode(FileRootPath + "/" + captchaImageFilename);

        // Version 3 : 本地OCR识别
        String capthca = new OCR().recognizeText(new File(FileRootPath + "/" + captchaImageFilename), "bmp");

        //验证码验证
        String verifyCaptchaUrl = "http://www.jsgsj.gov.cn:58888/province/infoQueryServlet.json?checkCode=true";
        HttpPost verifyCapthcaPost = new HttpPost(verifyCaptchaUrl);
        HttpResponse verifyResponse = null;
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("verifyCode", capthca));
        UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(list, "UTF-8");
        verifyCapthcaPost.setEntity(uefEntity);
        verifyResponse = client.execute(verifyCapthcaPost);
        String reponseStr = EntityUtils.toString(verifyResponse.getEntity());
        System.out.println("check:" + reponseStr);

        //For test

//        //infoQueryServlet页面
//        String queryResultUrl = "http://www.jsgsj.gov.cn:58888/province/infoQueryServlet.json?queryCinfo=true";
//        HttpPost queryResultPost = new HttpPost(queryResultUrl);
//        List<NameValuePair> queryResultList = new ArrayList<>();
//        String name = "320594000125222";
//        String org = null;
//        String id = null;
//        String seq_id = null;
//        queryResultList.add(new BasicNameValuePair("name", name));
//        queryResultList.add(new BasicNameValuePair("verifyCode", capthca));
//        UrlEncodedFormEntity queryResultEntity = new UrlEncodedFormEntity(queryResultList, "UTF-8");
//        queryResultPost.setEntity(queryResultEntity);
//        HttpResponse queryResultResponse = httpclient.execute(queryResultPost);
//        String responseStr = EntityUtils.toString(queryResultResponse.getEntity());
//        System.out.println("Response:" + responseStr);
//        System.out.println("capthca:" + capthca);
//        if (responseStr.contains("ci_queryCorpInfor_gsRelease")){
//            String[] params = responseStr.split(",");
//            org = params[1].replace("'", "");
//            id = params[2].replace("'", "");
//            seq_id = params[3].replace("'", "");
//            System.out.println("org:" + org);
//            System.out.println("id:" + id);
//            System.out.println("seq_id:" + seq_id);
//        }

//        //result页面
//        String resultUrl = "http://www.jsgsj.gov.cn:58888/ecipplatform/inner_ci/ci_queryCorpInfor_gsRelease.jsp";
//        HttpPost resultPost = new HttpPost(resultUrl);
//        List<NameValuePair> resultList = new ArrayList<>();
//        resultList.add(new BasicNameValuePair("org", org));
//        resultList.add(new BasicNameValuePair("id", id));
//        resultList.add(new BasicNameValuePair("seq_id", seq_id));
//        resultList.add(new BasicNameValuePair("reg_no", name));
//        resultList.add(new BasicNameValuePair("uni_scid", ""));
//        resultList.add(new BasicNameValuePair("sname", ""));
//        resultList.add(new BasicNameValuePair("containContextPath", "ecipplatform"));
//        resultList.add(new BasicNameValuePair("verifyCode", ""));
//        resultList.add(new BasicNameValuePair("name", name));
//        UrlEncodedFormEntity resultEntity = new UrlEncodedFormEntity(resultList, "UTF-8");
//        resultPost.setEntity(resultEntity);
//        HttpResponse resultResponse = httpclient.execute(resultPost);
//        showResponse(resultResponse);
//
//        //result2页面
//        String resultUrl2 = "http://www.jsgsj.gov.cn:58888/ecipplatform/ciServlet.json?ciEnter=true";
//        HttpPost resultPost2 = new HttpPost(resultUrl2);
//        List<NameValuePair> resultList2 = new ArrayList<>();
//        resultList2.add(new BasicNameValuePair("org", org));
//        resultList2.add(new BasicNameValuePair("id", id));
//        resultList2.add(new BasicNameValuePair("seq_id", seq_id));
//        resultList2.add(new BasicNameValuePair("specificQuery", "basicInfo"));
//        UrlEncodedFormEntity resultEntity2 = new UrlEncodedFormEntity(resultList2, "UTF-8");
//        resultPost2.setEntity(resultEntity2);
//        HttpResponse resultResponse2 = httpclient.execute(resultPost2);
//        showResponse(resultResponse2);
    }
}