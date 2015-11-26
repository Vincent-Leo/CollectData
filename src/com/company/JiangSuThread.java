package com.company;

import com.company.ocr.OCR;
import com.company.pages.PageCompanyData;
import com.company.pages.PageInfoQueryServlet;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JiangSuThread implements Runnable {

    private static final String FileRootPath = "C:/codes/jiangsu";
    private static final long addNum = 100L;

    private long startCompanyId;

    public long getStartCompanyId() {
        return startCompanyId;
    }

    public void setStartCompanyId(long startCompanyId) {
        this.startCompanyId = startCompanyId;
    }

    /**
     * 将验证码图片保存到本地
     *
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

    public static void showResponse(HttpResponse response) throws IOException {
        System.out.println(EntityUtils.toString(response.getEntity()));
    }

    /**
     * 识别验证码
     *
     * @param httpclient
     * @return 验证码字符串
     */
    public static String getCaptchaCode(CloseableHttpClient httpclient) throws Exception {
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

        return capthca;
    }

    /**
     * 验证验证码
     *
     * @param httpclient
     * @param capthca
     * @return 正确返回true, 错误返回false
     */
    public static Boolean checkCaptchaCode(CloseableHttpClient httpclient, String capthca) throws Exception {
        String verifyCaptchaUrl = "http://www.jsgsj.gov.cn:58888/province/infoQueryServlet.json?checkCode=true";
        HttpPost verifyCapthcaPost = new HttpPost(verifyCaptchaUrl);
        HttpResponse verifyResponse = null;
        List<NameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("verifyCode", capthca));
        UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(list, "UTF-8");
        verifyCapthcaPost.setEntity(uefEntity);
        verifyResponse = httpclient.execute(verifyCapthcaPost);
        String reponseStr = EntityUtils.toString(verifyResponse.getEntity());

        //有mark字样的返回说明验证码错误
        if (reponseStr.contains("mark")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取httpclient
     */
    public static CloseableHttpClient getHttpClient() throws Exception {
        //第一次连接，获取cookie
        String url = "http://www.jsgsj.gov.cn:58888/province/";
        DefaultHttpClient client = new DefaultHttpClient(new PoolingClientConnectionManager());
        HttpGet indexGet = new HttpGet(url);
        client.execute(indexGet);
        CookieStore cookieStore = client.getCookieStore();

        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();

        return httpclient;
    }

    public void run() {
        try {
            //获取带cookie的httpclient
            CloseableHttpClient httpclient = getHttpClient();

            for (long i = startCompanyId; i < startCompanyId + addNum; i++) {
                String companyID = String.valueOf(i);
                String org = null;
                String id = null;
                String seq_id = null;

                //获取验证码
                String capthca = getCaptchaCode(httpclient);
                while (!checkCaptchaCode(httpclient, capthca)) {
                    capthca = getCaptchaCode(httpclient);
                }

                //获取公司的org,id,seq_id信息,并存入数据库表，后期可以直接用这些信息查询公司数据，而不需要验证
                String companyParams = PageInfoQueryServlet.getInfomation(httpclient, capthca, companyID);
                if (companyParams != null) {
                    String[] params = companyParams.split(",");
                    org = params[0];
                    id = params[1];
                    seq_id = params[2];
                }

                if (org != null && id != null && seq_id != null) {
                    PageCompanyData.getInfomation(httpclient, org, id, seq_id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}