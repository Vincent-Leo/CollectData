package com.company;

public class Main {

    public static void main(String args[]) throws Exception {

        long startCompanyId = 320594000010000L;

        for (int i = 0; i < 100; i++) {
            JiangSuThread thread = new JiangSuThread();
            thread.setStartCompanyId(startCompanyId + 10 * i);
            thread.setIp(RandomIP.getRandomIp());
            thread.run();
        }


    }
}