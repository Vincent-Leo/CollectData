
package com.company;

import com.company.ocr.OCR;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Test {

    /** */
    /**
     * @param args
     */
    public static void main(String[] args) {
        BufferedWriter bw = null;
        for (int i = 1; i < 10; i++) {
            try {
                File file = new File("C:/test.txt");
                if(!file.exists()){
                    file.createNewFile();
                }
                FileWriter fw = new FileWriter(file, true);
                bw = new BufferedWriter(fw);
                bw.write("companyID:" + "123,");
                bw.write("org:" + "12321,");
                bw.write("id:" + "123213,");
                bw.write("seq_id:" + "3213123");
                bw.newLine();
                bw.flush();
            } catch (Exception e) {
            }
        }
    }


//        String path = "C:/验证码/苏州/suzhou_1448327626218.bmp";
//        File file = new File(path);
//        System.out.println(file.getPath());
//        try {
//            String valCode = new OCR().recognizeText(file, "bmp");
//            System.out.println(valCode);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
