package com.mininglamp.nlp.hmm;

import com.mininglamp.nlp.tool.Tool;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * HMMParams类，用于加载训练好的HMM模型
 *
 * @author yifeijing
 * @version 1.0
 * @since 8.0
 **/
public class HMMParams {
    private static final Logger LOGGER = Logger.getLogger(HMMParams.class);
    private static final String DATA = "data";
    private static final String DEFAULT = "default";
    private static final float PROB = (float) Math.log(0.000000000001);

    private static JSONObject PY2HZ_DICT;
    private static JSONObject START_DICT;
    private static JSONObject EMISSION_DICT;
    private static JSONObject TRANSITION_DICT;

    /**
     * 加载HMM模型
     *
     * @param hmmModelPath HMM模型所在的文件夹的位置
     **/
    public static void loadHmmModel(String hmmModelPath) {
        PY2HZ_DICT = Tool.readJson(Paths.get(hmmModelPath, "hmm_py2hz.json").toString());
        START_DICT = Tool.readJson(Paths.get(hmmModelPath, "hmm_start.json").toString());
        EMISSION_DICT = Tool.readJson(Paths.get(hmmModelPath, "hmm_emission.json").toString());
        TRANSITION_DICT = Tool.readJson(Paths.get(hmmModelPath, "hmm_transition.json").toString());

    }

    /**
     * 获取起始汉字的概率
     *
     * @param state 单个汉字字符
     * @return 返回汉字的概率
     **/
    public static float start(String state) throws Exception {
        if (START_DICT == null) throw new Exception("没有正确加载HMM模型");
        //加入state字符编码转换？
        float prob = PROB;
        try {
            JSONObject startData = START_DICT.getJSONObject(DATA);
            float startDefault = (float) START_DICT.getDouble(DEFAULT);
            if (startData.has(state)) {
                prob = (float) startData.getDouble(state);
            } else {
                prob = startDefault;
            }
        } catch (JSONException e) {
            LOGGER.debug(e.getMessage());
        }

        return prob;
    }

    /**
     * 获取发射概率
     *
     * @param state       汉字字符
     * @param observation 拼音字符序列
     * @return 返回hanzi->拼音的概率
     **/
    public static float emission(String state, String observation) throws Exception {
        if (EMISSION_DICT == null) throw new Exception("没有正确加载HMM模型");
        //加入state和observation字符集转换？
        float prob = PROB;
        try {
            JSONObject emissionData = EMISSION_DICT.getJSONObject(DATA);
            float emissionDefault = (float) EMISSION_DICT.getDouble(DEFAULT);

            if (emissionData.has(state)) {
                JSONObject hanziJson = emissionData.getJSONObject(state);
                if (hanziJson.has(observation)) {
                    prob = (float) hanziJson.getDouble(observation);
                } else {
                    prob = emissionDefault;
                }

            } else {
                prob = emissionDefault;
            }
        } catch (JSONException e) {
            LOGGER.debug(e.getMessage());
        }
        return prob;
    }

    /**
     * 获取转移概率
     *
     * @param fromState 起始的汉字
     * @param toState   终止的汉字
     * @return 返回起始汉字->终止汉字的概率
     **/
    public static float transition(String fromState, String toState) throws Exception {
        if (TRANSITION_DICT == null) throw new Exception("没有正确加载HMM模型");
        //加入fromState和toState字符集转换？
        float prob = PROB;
        try {
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
        } catch (JSONException e) {
            LOGGER.debug(e.getMessage());
        }
        return prob;
    }

    /**
     * 获取拼音可能的汉字集
     *
     * @param observation 拼音字符串
     * @return 返回拼音对应的所有汉字
     **/
    public static List<String> getStates(String observation) throws Exception {
        if (PY2HZ_DICT == null) throw new Exception("没有正确加载HMM模型");
        List<String> hanziList = null;
        try {
            String hanziStr = PY2HZ_DICT.getString(observation);
            char[] hanziChar = hanziStr.toCharArray();
            hanziList = new ArrayList<>();
            for (int i = 0; i < hanziChar.length; i++) {
                hanziList.add(String.valueOf(hanziChar[i]));
            }
        } catch (JSONException e) {
            LOGGER.debug(e.getMessage() + observation);
        }
        return hanziList;
    }

}