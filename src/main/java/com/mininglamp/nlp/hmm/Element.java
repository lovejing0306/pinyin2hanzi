package com.mininglamp.nlp.hmm;

import java.util.List;

/**
 * 这是一个元素类，用于记录在算法执行过程中每个节点的信息
 *
 * @author yifeijing
 * @version 1.0
 * @since 8.0
 */
public class Element implements Comparable<Element> {
    private int index;
    private String name;
    private float score;
    private List<String> path;

    /**
     * 构造函数
     *
     * @param name  节点的名称
     * @param score 节点的分数
     * @param path  到该节点时所存储的路径
     * @return 无
     */
    public Element(String name, float score, List<String> path) {
        this.name = name;
        this.score = score;
        this.path = path;
    }

    /**
     * 构造函数
     *
     * @param index 存放前一个节点的索引
     * @param name  节点的名称
     * @param score 节点的分数
     * @param path  到该节点时所存储的路径
     * @return 无
     */
    public Element(int index, String name, float score, List<String> path) {
        this.index = index;
        this.name = name;
        this.score = score;
        this.path = path;
    }

    /**
     * 重写了Comparable类的compareTo()方法，用于比较两个对象的大小依据分数
     *
     * @param element 接受一个Element类的实例
     * @return 返回分数较大的实例
     **/
    @Override
    public int compareTo(Element element) {     //重写compareTo方法
        //this.score>element.score?1:(this.score==element.score?0:-1)
        return element.score > this.score ? 1 : (element.score == this.score ? 0 : -1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
