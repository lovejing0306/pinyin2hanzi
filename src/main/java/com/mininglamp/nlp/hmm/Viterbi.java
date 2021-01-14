package com.mininglamp.nlp.hmm;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 将拼音转换成汉字
 *
 * @author yifeijing
 * @version 1.0
 * @since 8.0
 **/
public class Viterbi {
    private static final float MIN_PROB = (float) Math.log(3.14e-20);
    private static final Logger LOGGER = Logger.getLogger(Viterbi.class);

    /**
     * 使用viterbi算法实现拼音转汉字
     *
     * @param observations 拼音列表
     * @param pathNum      候选的汉字序列数目
     * @return 返回转换好的汉字序列
     * @throws NullPointerException
     **/
    public static List<String> viterbi(List<String> observations, int pathNum) {
        List<String> hanzis = null;

        try {
            List<List<Element>> lists = new ArrayList<>();
            int t = 0;
            String curObs = observations.get(t);           //当前拼音
            List<String> curStates = HMMParams.getStates(curObs);    //当前拼音对应的汉字
            List<Element> elements = new ArrayList<>();    //存放每个state计算的分数和路径信息
            float score = Float.MIN_VALUE;
            Element element = null;
            for (int i = 0; i < curStates.size(); i++) {
                String state = curStates.get(i);
                score = Math.max(HMMParams.start(state), MIN_PROB) +
                        Math.max(HMMParams.emission(state, curObs), MIN_PROB);
                List<String> path = new ArrayList<String>();
                path.add(state);
                element = new Element(i, state, score, path);
                elements.add(element);
            }

            lists.add(elements);
            int q = 0;   //lists列表的index

            List<Element> elements2;
            List<Element> elements3 = new ArrayList<>();
            for (int i = 1; i < observations.size(); i++) {
                elements2 = new ArrayList<>();
                curObs = observations.get(i);
                curStates.clear();
                curStates = HMMParams.getStates(curObs);

                for (int j = 0; j < curStates.size(); j++) {
                    String state = curStates.get(j);
                    float preScore = Float.MIN_VALUE;
                    for (int k = 0; k < lists.get(q).size(); k++) {
                        preScore = lists.get(q).get(k).getScore();
                        score = preScore + Math.max(HMMParams.transition(lists.get(q).get(k).getName(), state), MIN_PROB) +
                                Math.max(HMMParams.emission(state, curObs),
                                        MIN_PROB);

                        List<String> path = new ArrayList<>();
                        element = new Element(k, state, score, path);
                        elements3.add(element);
                    }
                    Collections.sort(elements3);
                    int epoch = elements3.size() < pathNum ? elements3.size() : pathNum;
                    for (int k = 0; k < epoch; k++) {
                        int index=elements3.get(k).getIndex();
                        for(int p=0; p<lists.get(q).get(index).getPath().size(); p++){
                            elements3.get(k).getPath().add(lists.get(q).get(index).getPath().get(p));
                        }
                        elements3.get(k).getPath().add(state);
                        elements2.add(elements3.get(k));
                    }
                    elements3.clear();

                }
                lists.add(elements2);
                q++;
            }

            //将List类型的路径转换成String类型的路径
            Collections.sort(lists.get(q));
            int epoch = lists.get(q).size() < pathNum ? lists.get(q).size() : pathNum;
            hanzis = new ArrayList<>();
            for (int i = 0; i < epoch; i++) {
                hanzis.add(String.join("", lists.get(q).get(i).getPath()));
            }
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
        }
        return hanzis;
    }

}