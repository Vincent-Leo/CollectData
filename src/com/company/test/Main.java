package com.company.test;

import com.company.ocr.OCR;
import com.company.pages.PageCompanyBasic;
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
import java.util.*;

public class Main {

    private static final String FileRootPath = "C:/codes/jiangsu";

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

    public static void main(String args[]) throws Exception {


//        获取带cookie的httpclient
//        CloseableHttpClient httpclient = getHttpClient();

        //infoQueryServlet页面,用于获取org, id, seq_id信息
//        for (long i = 320594000125222L; i < 320594000125922L; i++) {
//            String fileName = "C:/codes/JiangSu.txt";
//            String companyID = String.valueOf(i);
//            //获取验证码
//            String capthca = getCaptchaCode(httpclient);
//            while (!checkCaptchaCode(httpclient, capthca)){
//                capthca = getCaptchaCode(httpclient);
//            }
//            PageInfoQueryServlet.getInfomation(httpclient, capthca, companyID, fileName);
//        }

        //result页面
        //PageCompanyBasic.getInfomation(httpclient, "1402", "28801380", "49", "320594000125222");

        //Data页面
        //PageCompanyData.getInfomation(httpclient, "1402", "29013092", "12");
        //String dataResponse = "[{\"ORG\":1402,\"ID\":28801380,\"SEQ_ID\":49,\"C2\":\"苏州赛富科技有限公司\",\"C1\":\"320594000125222\",\"C3\":\"有限责任公司\\n\",\"ADMIT_MAIN\":\"08\",\"C7\":\"苏州工业园区星湖街218号生物纳米园C1组团B栋\",\"FARE_PLACE\":\"\",\"C6\":\"6196.1323万元人民币\",\"CAPI_TYPE_NAME\":\"人民币\",\"REG_CAPI_DOLLAR\":0,\"INVEST_CAPI\":\"0万元人民币\",\"INVEST_CAPI_DOLLAR\":0,\"C5\":\"高胜涛\",\"PARENT_CORP_NAME\":\"\",\"OPER_MAN_ADDR\":\"\",\"C9\":\"2008-09-28\",\"C10\":\"2058-09-26\",\"ABUITEM\":\"\",\"CBUITEM\":\"\",\"C8\":\"批发：预包装食品；汽车配件、计算机领域内的软硬件开发、技术咨询；从事货物和技术的进口业务；销售：计算机及配件、机电产品、仪器仪表、通信设备及相关产品、电子元器件、纺织原料及产品、化工原料、橡塑制品、五金工具、家用电器、钢材、木材、建材、燃料油、百货、珠宝首饰、家具及木制品、一类医疗器械、饲料、鲜活食用农产品；商务信息咨询、物流信息咨询、外包服务咨询、国际货运咨询；企业供应链管理及相关配套服务。（依法须经批准的项目，经相关部门批准后方可开展经营活动）\",\"C11\":\"江苏省苏州工业园区工商行政管理局\",\"HEAD_NAME\":\"\",\"FOREIGN_NAME\":\"\",\"SEND_CORP_REG_SITE\":\"\",\"C13\":\"在业\",\"CORP_OPERATE\":\"22\",\"C4\":\"2008-09-28\",\"WRITEOFF_DATE\":\"\",\"C12\":\"2015-01-21\",\"REVOKE_DATE\":\"\"}]";

    }
}