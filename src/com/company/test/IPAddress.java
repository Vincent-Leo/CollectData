package com.company.test;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;

/**
 * 获取本机外网ip地址
 */
public class IPAddress {

    public static String getIpAddress() {
        String ip = null;
        String url = "http://1111.ip138.com/ic.asp";
        DefaultHttpClient client = new DefaultHttpClient(new PoolingClientConnectionManager());

        HttpGet indexGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(indexGet);
            String responseStr = EntityUtils.toString(response.getEntity());
            int start = responseStr.indexOf("[") + 1;
            int end = responseStr.indexOf("]");
            if (start < 0 || end < 0) {
                ip = null;
            }
            ip = responseStr.substring(start, end);
            return ip;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(getIpAddress());
    }
}
