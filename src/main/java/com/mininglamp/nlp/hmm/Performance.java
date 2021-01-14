package com.mininglamp.nlp.hmm;


import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class Performance {

    public static void testPinyin2Hanzi(String targetFile, String resultFile){
        System.out.println("start test ......");
        File readerFile=new File(targetFile);
        BufferedReader reader=null;

        FileWriter writer=null;

        List<List<String>> hanzisList=null;
        String result="";
        int count=0;
        try {
            writer=new FileWriter(resultFile);
            reader = new BufferedReader(new FileReader(readerFile));  //以行为单位读取文件内容，一次读一整行
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                if(count%100==0){
                    System.out.println(count);
                }
                count++;
                try {
                    String[] items = tempString.split("\t");
                    hanzisList=Pinyin2Hanzi.transform(items[1], 2, 10);
                    for(int i=0; i<hanzisList.size(); i++){
                        List<String> inner=new ArrayList<>();
                        for(int j=0; j<hanzisList.get(i).size(); j++){
                            inner.add(hanzisList.get(i).get(j));
                            inner.add(String.valueOf(j));
                        }
                        result= result +"\t"+ String.join("\t", inner);
                    }
                    result = tempString + result +"\n";
                    writer.write(result);
                    result="";
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    continue;
                }
            }
        }catch (Exception e){
            System.out.println(e.getCause());
        } finally {
            try {
                reader.close();
                writer.close();
            } catch (Exception e){
                System.out.println(e.getMessage());
            }

        }
    }

    public static void testPinyin2HanziStability(String targetFile) {
        File file = new File(targetFile);
        BufferedReader reader = null;
        List<String> pinyin_list = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(file));  //以行为单位读取文件内容，一次读一整行
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                try {
                    String[] items = tempString.split("\t");
                    String pinyin = items[1];
                    pinyin_list.add(pinyin);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    continue;
                }
            }

            int batch = 100;
            int epoch = pinyin_list.size() / batch;

            System.out.println("开始性能测试！");
            long startTime = new Date().getTime();
            for (int i = 0; i < epoch; i++) {
                long startTimeEpoch = new Date().getTime();
                int start = i * batch;
                int end = (i + 1) * batch;
                for (int j = start; j < end; j++) {
                    try {
                        Pinyin2Hanzi.transform(pinyin_list.get(j), 2, 10); //定义返回结果
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        continue;
                    }

                }
                long endTimeEpoch = new Date().getTime();
                System.out.println("第" + String.valueOf(i) + "轮的时间：" + String.valueOf((double) (endTimeEpoch - startTimeEpoch) / 1000));
            }
            long endTime = new Date().getTime();
            System.out.println("所有轮时间共计：" + String.valueOf((double) (endTime - startTime) / 1000));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void testSinglePinyin() {
        System.out.println("input pinyin:");
        Scanner sc = new Scanner(System.in);
        String pinyin;
        List<String> hanzis = null;
        List<List<String>> hanzisList = null;
        while (sc.hasNext()) {
            pinyin = sc.next();
            try {
                hanzisList = Pinyin2Hanzi.transform(pinyin, 2, 10);
                for (int i = 0; i < hanzisList.size(); i++) {
                    hanzis = hanzisList.get(i);
                    for (int j = 0; j < hanzis.size(); j++) {
                        System.out.println(hanzis.get(j));
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    public static void main(String[] args) {
        String modelPath = Paths.get(System.getProperty("user.dir"), "hmm_model").toString();
        Pinyin2Hanzi.loadModel(modelPath);
        testSinglePinyin();

        String targetFileName="name_all_pinyin.txt";
        String resultFileName="name_all_pinyin_pinyin2hanzi_04.txt";
        String targetFile = Paths.get(System.getProperty("user.dir"), "data","test" ,"original", targetFileName).toString();
        String resultFile = Paths.get(System.getProperty("user.dir"), "data","test" ,"result", resultFileName).toString();
        // testPinyin2HanziStability(targetFile);
        // testPinyin2Hanzi(targetFile, resultFile);
        testSinglePinyin();
    }
}
