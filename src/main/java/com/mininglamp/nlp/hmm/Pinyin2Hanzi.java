package com.mininglamp.nlp.hmm;

import org.apache.log4j.Logger;
import com.mininglamp.nlp.split.PinyinSplit;
import com.mininglamp.nlp.tool.Tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 拼音转汉字的主类
 *
 * @author yifeijing
 * @version 1.0
 * @since 8.0
 **/
public class Pinyin2Hanzi {
    //日志
    private static final Logger LOGGER = Logger.getLogger(Pinyin2Hanzi.class);
    //默认的拼音输入序列的最长长度
    private static int pinyinMaxLength = 50;
    //默认的汉字输出序列的最长长度
    private static int pathMaxNumber = 10;

    /**
     * 验证输入的拼音序列是否合法
     *
     * @param str 拼音序列
     * @throws NullPointerException
     **/
    private static void verifyStr(final String str) throws Exception {
        if (str == null || str == "" || str.equals("") || str.length() == 0) {
            throw new Exception("输入的字符串不能为空！");
        }
        if (str.length() > pinyinMaxLength) {
            throw new Exception("输入的字符串过长！");
        }
    }

    /**
     * 验证输入候选汉字序列个数是否合法
     *
     * @param pathNum 候选汉字序列的个数
     * @throws pathNum无效！
     **/
    private static void verifyPathNum(final int pathNum) throws Exception {
        if (pathNum <= 0 || pathNum > pathMaxNumber) {
            throw new Exception("pathNum无效！");
        }
    }

    /**
     * 将拼音序列转换成汉字序列
     *
     * @param str     拼音序列
     * @param pathNum 候选的汉字序列输出个数
     * @param segNum  候选的拼音分割输出个数
     * @return 返回生成的汉字序列
     * @throws NullPointerException
     **/
    public static List<List<String>> transform(final String str,
                                               final int segNum, final int pathNum) {
        List<List<String>> hanzisList = new ArrayList<>();
        List<String> hanzis;
        List<String> pinyinSeg;
        List<String> pinyinOptimum;
        try {
            verifyStr(str);
            verifyPathNum(pathNum);

            String pinyinFilter = Tool.filterPinyin(str);
            verifyStr(pinyinFilter);

            String pinyinNormalize = Tool.normalizePinyin(pinyinFilter);

            pinyinSeg = PinyinSplit.splitByTrieTree(pinyinNormalize);
            pinyinOptimum = PinyinSplit.getOptimumPinyin(pinyinSeg, segNum);

            for (int i = 0; i < pinyinOptimum.size(); i++) {
                hanzis = Viterbi.viterbi(
                        Arrays.asList(pinyinOptimum.get(i).split("#")),
                        pathNum);
                hanzisList.add(hanzis);
            }
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
        }
        return hanzisList;
    }

    /**
     * 将拼音序列转换成汉字序列
     *
     * @param modelPath 模型存放的文件位置
     **/
    public static void loadModel(final String modelPath) {
        HMMParams.loadHmmModel(modelPath);
        PinyinSplit.loadPinyinModel(modelPath);
    }

    /**
     * 设置输入拼音序列的最长的长度
     *
     * @param pinyinMaxLength 大于0的整型数字
     **/
    public static void setPinyinMaxLength(int pinyinMaxLength) {
        Pinyin2Hanzi.pinyinMaxLength = pinyinMaxLength;
    }

    /**
     * 设置输出汉字序列的最长的长度
     *
     * @param pathMaxNumber 大于0的整型数字
     **/
    public static void setPathMaxNumber(int pathMaxNumber) {
        Pinyin2Hanzi.pathMaxNumber = pathMaxNumber;
    }

}
