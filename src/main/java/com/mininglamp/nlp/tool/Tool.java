package com.mininglamp.nlp.tool;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 工具类
 *
 * @author yifeijing
 * @version 1.0
 * @since 8.0
 **/
public class Tool {
    private static final Logger logger = Logger.getLogger(Tool.class);
    private static final Set<String> PINYIN_ALL= new HashSet<String>(){
        {
            add("gu");
            add("qiao");
            add("qian");
            add("qve");
            add("ge");
            add("gang");
            add("ga");
            add("lian");
            add("liao");
            add("rou");
            add("zong");
            add("tu");
            add("seng");
            add("yve");
            add("ti");
            add("te");
            add("jve");
            add("ta");
            add("nong");
            add("zhang");
            add("fan");
            add("ma");
            add("gua");
            add("die");
            add("gui");
            add("guo");
            add("gun");
            add("sang");
            add("diu");
            add("zi");
            add("ze");
            add("za");
            add("chen");
            add("zu");
            add("ba");
            add("dian");
            add("diao");
            add("nei");
            add("suo");
            add("sun");
            add("zhao");
            add("sui");
            add("kuo");
            add("kun");
            add("kui");
            add("cao");
            add("zuan");
            add("kua");
            add("den");
            add("lei");
            add("neng");
            add("men");
            add("mei");
            add("tiao");
            add("geng");
            add("chang");
            add("cha");
            add("che");
            add("fen");
            add("chi");
            add("fei");
            add("chu");
            add("shui");
            add("me");
            add("tuan");
            add("mo");
            add("mi");
            add("mu");
            add("dei");
            add("cai");
            add("zhan");
            add("zhai");
            add("can");
            add("ning");
            add("wang");
            add("pie");
            add("beng");
            add("zhuang");
            add("tan");
            add("tao");
            add("tai");
            add("song");
            add("ping");
            add("hou");
            add("cuan");
            add("lan");
            add("lao");
            add("fu");
            add("fa");
            add("jiong");
            add("mai");
            add("xiang");
            add("mao");
            add("man");
            add("a");
            add("jiang");
            add("zun");
            add("bing");
            add("su");
            add("si");
            add("sa");
            add("se");
            add("ding");
            add("xuan");
            add("zei");
            add("zen");
            add("kong");
            add("pang");
            add("jie");
            add("jia");
            add("jin");
            add("lo");
            add("lai");
            add("li");
            add("peng");
            add("jiu");
            add("yi");
            add("yo");
            add("ya");
            add("cen");
            add("dan");
            add("dao");
            add("ye");
            add("dai");
            add("zhen");
            add("bang");
            add("nou");
            add("yu");
            add("weng");
            add("en");
            add("ei");
            add("kang");
            add("dia");
            add("er");
            add("ru");
            add("keng");
            add("re");
            add("ren");
            add("gou");
            add("ri");
            add("tian");
            add("qi");
            add("shua");
            add("shun");
            add("shuo");
            add("qun");
            add("yun");
            add("xun");
            add("fiao");
            add("zan");
            add("zao");
            add("rang");
            add("xi");
            add("yong");
            add("zai");
            add("guan");
            add("guai");
            add("dong");
            add("kuai");
            add("ying");
            add("kuan");
            add("xu");
            add("xia");
            add("xie");
            add("yin");
            add("rong");
            add("xin");
            add("tou");
            add("nian");
            add("niao");
            add("xiu");
            add("fo");
            add("kou");
            add("niang");
            add("hua");
            add("hun");
            add("huo");
            add("hui");
            add("shuan");
            add("quan");
            add("shuai");
            add("chong");
            add("bei");
            add("ben");
            add("kuang");
            add("dang");
            add("sai");
            add("ang");
            add("sao");
            add("san");
            add("reng");
            add("ran");
            add("rao");
            add("ming");
            add("null");
            add("lie");
            add("lia");
            add("min");
            add("pa");
            add("lin");
            add("mian");
            add("mie");
            add("liu");
            add("zou");
            add("miu");
            add("nen");
            add("kai");
            add("kao");
            add("kan");
            add("ka");
            add("ke");
            add("yang");
            add("ku");
            add("deng");
            add("dou");
            add("shou");
            add("chuang");
            add("nang");
            add("feng");
            add("meng");
            add("cheng");
            add("di");
            add("de");
            add("da");
            add("bao");
            add("gei");
            add("du");
            add("gen");
            add("qu");
            add("shu");
            add("sha");
            add("she");
            add("ban");
            add("shi");
            add("bai");
            add("nun");
            add("nuo");
            add("sen");
            add("lve");
            add("kei");
            add("fang");
            add("teng");
            add("xve");
            add("lun");
            add("luo");
            add("ken");
            add("wa");
            add("wo");
            add("ju");
            add("tui");
            add("wu");
            add("le");
            add("ji");
            add("huang");
            add("tuo");
            add("cou");
            add("la");
            add("mang");
            add("ci");
            add("tun");
            add("tong");
            add("ca");
            add("pou");
            add("ce");
            add("gong");
            add("cu");
            add("lv");
            add("dun");
            add("pu");
            add("ting");
            add("qie");
            add("yao");
            add("lu");
            add("pi");
            add("po");
            add("suan");
            add("chua");
            add("chun");
            add("chan");
            add("chui");
            add("gao");
            add("gan");
            add("zeng");
            add("gai");
            add("xiong");
            add("tang");
            add("pian");
            add("piao");
            add("cang");
            add("heng");
            add("xian");
            add("xiao");
            add("bian");
            add("biao");
            add("zhua");
            add("duan");
            add("cong");
            add("zhui");
            add("zhuo");
            add("zhun");
            add("hong");
            add("shuang");
            add("juan");
            add("zhei");
            add("pai");
            add("shai");
            add("shan");
            add("shao");
            add("pan");
            add("pao");
            add("nin");
            add("hang");
            add("nie");
            add("zhuai");
            add("zhuan");
            add("yuan");
            add("niu");
            add("na");
            add("miao");
            add("guang");
            add("ne");
            add("hai");
            add("han");
            add("hao");
            add("wei");
            add("wen");
            add("ruan");
            add("cuo");
            add("cun");
            add("cui");
            add("bin");
            add("bie");
            add("mou");
            add("nve");
            add("shen");
            add("shei");
            add("fou");
            add("xing");
            add("qiang");
            add("nuan");
            add("pen");
            add("pei");
            add("rui");
            add("run");
            add("ruo");
            add("sheng");
            add("dui");
            add("bo");
            add("bi");
            add("bu");
            add("chuan");
            add("qing");
            add("chuai");
            add("duo");
            add("o");
            add("chou");
            add("ou");
            add("zui");
            add("luan");
            add("zuo");
            add("jian");
            add("jiao");
            add("sou");
            add("wan");
            add("jing");
            add("qiong");
            add("wai");
            add("long");
            add("yan");
            add("liang");
            add("lou");
            add("huan");
            add("hen");
            add("hei");
            add("huai");
            add("shang");
            add("jun");
            add("hu");
            add("ling");
            add("ha");
            add("he");
            add("zhu");
            add("ceng");
            add("zha");
            add("zhe");
            add("zhi");
            add("qin");
            add("pin");
            add("ai");
            add("chai");
            add("qia");
            add("chao");
            add("ao");
            add("an");
            add("qiu");
            add("ni");
            add("zhong");
            add("zang");
            add("nai");
            add("nan");
            add("nao");
            add("chuo");
            add("tie");
            add("you");
            add("nu");
            add("nv");
            add("zheng");
            add("leng");
            add("zhou");
            add("lang");
            add("e");
            add("ai");
            add("ao");
            add("ou");
            add("er");
            add("an");
            add("en");
        }
    };


    /**
     * 获取主目录路径
     *
     * @return 返回主目录路径
     **/
    private static String pwd() {
        return System.getProperty("user.dir") + "\\";
    }

    /**
     * 读取json文件
     *
     * @param fileName json文件名
     * @return 返回json对象
     **/
    public static JSONObject readJson(String fileName) {
        JSONObject jsonObject = null;
        try {
            File file = new File(fileName);
            String content = FileUtils.readFileToString(file, "utf-8");
            jsonObject = new JSONObject(content);
        } catch (FileNotFoundException e) {
            logger.debug(e.getMessage());
        } catch (IOException e) {
            logger.debug(e.getMessage());
        } catch (JSONException e) {
            logger.debug(e.getMessage());
        }
        return jsonObject;
    }

    /**
     * 规范化拼音
     *
     * @param pinyin 输入完整的单个拼音
     * @return 返回规范化后的拼音
     **/
    public static String normalizePinyin(String pinyin) throws Exception {
        if (pinyin == null || pinyin == "" || pinyin.equals("") || pinyin.length() == 0) throw new Exception("输入的字符串不能为空！");
        if(pinyin.length()==1){
            return pinyin;
        }
        String str = "";
        if (pinyin.contains("ue")) {
            for(int i=0; i<pinyin.length()-1; i++ ){
                if(pinyin.charAt(i)=='u'&&pinyin.charAt(i+1)=='e'){
                    if(i==0){
                        continue;
                    }else{
                        if(pinyin.charAt(i-1)=='q'
                                || pinyin.charAt(i-1)=='y'
                                || pinyin.charAt(i-1)=='j'
                                || pinyin.charAt(i-1)=='l'
                                || pinyin.charAt(i-1)=='x'
                                || pinyin.charAt(i-1)=='n'){
                            str=str+pinyin.substring(str.length(), i)+"ve";
                            i++;
                        }else{
                            continue;
                        }
                    }
                }
            }
            if(str.length()!=pinyin.length()){
                str+=pinyin.substring(str.length(), pinyin.length());
            }
        } else if ("ng" == pinyin || pinyin.equals("ng")) {
            str = "en";
        } else {
            str = pinyin;
        }
        return str;
    }


    /**
     * 过滤拼音,去除拼音字符串中的非法字符
     *
     * @param pinyins 输入要过滤的字符串
     * @return 返回过滤后的字符串
     **/
    public static String filterPinyin(String pinyins) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < pinyins.length(); i++) {
            if (pinyins.charAt(i) >= 'a' && pinyins.charAt(i) <= 'z') {
                sb.append(pinyins.charAt(i));
            }
        }
        String result = sb.toString();
        return result;
    }


    /**
     * 去除拼音数组中不是拼音的拼音
     *
     * @param pinyins 输入拼音字符串列表
     * @return 返回符合要求的拼音列表
     * @throws NullPointerException
     **/
    public static List<String> removeErrorPinyin(List<String> pinyins) throws Exception {
        if (pinyins == null || pinyins.size() == 0) throw new Exception("无效的参数！");
        List<String> observations = new ArrayList<>();
        for (int i = 0; i < pinyins.size(); i++) {
            try {
                if (PINYIN_ALL.contains(pinyins.get(i))) {
                    observations.add(pinyins.get(i));
                } else {
                    continue;
                }
            } catch (Exception e) {
                logger.debug(e.getMessage());
                continue;
            }
        }
        return observations;
    }


    /**
     * 规范化拼音数组
     *
     * @param pinyins 拼音数组
     * @return 返回规范化后的拼音列表
     * @throws NullPointerException
     **/
    public static List<String> normalizePingyins(String[] pinyins) throws Exception {
        if (pinyins == null || pinyins.length == 0) throw new Exception("无效的参数！");
        List<String> observations = new ArrayList<>();
        for (int i = 0; i < pinyins.length; i++) {
            observations.add(normalizePinyin(pinyins[i]));
        }
        return observations;
    }

}