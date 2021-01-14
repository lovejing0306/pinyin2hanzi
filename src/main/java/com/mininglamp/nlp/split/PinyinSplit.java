package com.mininglamp.nlp.split;


import com.mininglamp.nlp.hmm.Element;
import com.mininglamp.nlp.tool.Tool;
import org.json.JSONObject;
import com.mininglamp.nlp.hmm.Element;
import com.mininglamp.nlp.tool.Tool;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 将输入的拼音字符串进行切分
 *
 * @author yifeijing
 * @version 1.0
 * @since 8.0
 **/
public class PinyinSplit {
    private static JSONObject START_DICT;
    private static JSONObject TRANSITION_DICT;
    private static String PINYIN_DIC;

    private static final DoubleArrayTrie trie = new DoubleArrayTrie();
    private static final String DATA = "data";
    private static final String DEFAULT = "default";

    private static final float PROB = (float) Math.log(0.0000000001);


    /**
     * 获取拼音的起始概率
     *
     * @param state 拼音字符串
     * @return 返回拼音的起始概率
     **/
    private static float start(String state) throws Exception {
        if (START_DICT == null) throw new Exception("没有正确加载拼音模型！");
        //加入state字符编码转换？
        float prob = PROB;

        JSONObject startData = START_DICT.getJSONObject(DATA);
        float startDefault = (float) START_DICT.getDouble(DEFAULT);
        if (startData.has(state)) {
            prob = (float) startData.getDouble(state);
        } else {
            prob = startDefault;
        }

        return prob;
    }


    /**
     * 获取转移概率
     *
     * @param fromState 起始的拼音
     * @param toState   终止的拼音
     * @return 返回起始拼音->终止拼音的概率
     **/
    private static float transition(String fromState, String toState) throws Exception {
        if (TRANSITION_DICT == null) throw new Exception("没有正确加载拼音模型！");
        //加入fromState和toState字符集转换？
        float prob = PROB;
        JSONObject transData = TRANSITION_DICT.getJSONObject(DATA);
        float transDefault = (float) TRANSITION_DICT.getDouble(DEFAULT);
        if (transData.has(fromState)) {
            JSONObject json = transData.getJSONObject(fromState);
            if (json.has(toState)) {
                prob = (float) json.getDouble(toState);
            } else if (json.has(DEFAULT)) {
                prob = (float) json.getDouble(DEFAULT);
            }
        } else {
            prob = transDefault;
        }

        return prob;
    }

    /**
     * 计算切分出的拼音字符串的概率
     *
     * @param pinyin 切分的拼音
     * @return 返回计算出的概率
     **/
    private static float calScore(String pinyin) throws Exception {
        if (pinyin == null || "".equals(pinyin) || pinyin.length() == 0) {
            throw new Exception("输入不能为空！");
        }
        String[] pinyinArray = pinyin.split("#");
        String cur = pinyinArray[0];

        float score = start(cur);
        for (int i = 1; i < pinyinArray.length; i++) {
            score += transition(cur, pinyinArray[i]);
            cur = pinyinArray[i];
        }
        return score;
    }

    /**
     * 从多个候选的拼音序列切分中找出最优结果
     *
     * @param pinyins 所有的切分结果
     * @param num     最优结果个数
     * @return 返回最优的结果
     **/
    public static List<String> getOptimumPinyin(List<String> pinyins, int num) throws Exception {
        if (pinyins == null || pinyins.size() == 0) {
            throw new Exception("输入不能为空！");
        }
        if (num <= 0) {
            throw new Exception("参数num不合法！");
        }
        if (num >= pinyins.size()) {
            return pinyins;
        }
        List<Element> elements = new ArrayList<>();
        Element element = null;
        for (int i = 0; i < pinyins.size(); i++) {
            element = new Element(pinyins.get(i), calScore(pinyins.get(i)), null);
            elements.add(element);
        }
        Collections.sort(elements);

        List<String> pinyinList = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            pinyinList.add(elements.get(i).getName());
        }

        return pinyinList;
    }


    /**
     * 使用字典树+优先遍历的方式切分拼音字符串
     *
     * @param spell 拼音串序列
     * @return 返回切分后的拼音字符串
     **/
    public static List<String> splitByTrieTree(String spell) throws Exception {
        if (PINYIN_DIC == null) throw new Exception("没有正确加载拼音模型！");
        List<String> segs = trie.splitAllWords(spell);
        return segs;
    }

    /**
     * 加载拼音模型
     *
     * @param pinyinModelPath 存放模型的文件夹位置
     **/
    public static void loadPinyinModel(String pinyinModelPath) {
        START_DICT = Tool.readJson(Paths.get(pinyinModelPath, "pinyin_hmm_start.json").toString());
        TRANSITION_DICT = Tool.readJson(Paths.get(pinyinModelPath, "pinyin_hmm_transition.json").toString());
        PINYIN_DIC = Paths.get(pinyinModelPath, "pinyin.dat").toString();
        try {
            trie.open(PINYIN_DIC);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
