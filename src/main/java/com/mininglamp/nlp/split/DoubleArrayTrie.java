/**
 * DoubleArrayTrie: Java implementation of Darts (Double-ARray Trie System)
 * <p>
 * <p>
 * Copyright(C) 2001-2007 Taku Kudo &lt;taku@chasen.org&gt;<br />
 * Copyright(C) 2009 MURAWAKI Yugo &lt;murawaki@nlp.kuee.kyoto-u.ac.jp&gt;
 * Copyright(C) 2012 KOMIYA Atsushi &lt;komiya.atsushi@gmail.com&gt;
 * </p>
 * <p>
 * <p>
 * The contents of this file may be used under the terms of either of the GNU
 * Lesser General Public License Version 2.1 or later (the "LGPL"), or the BSD
 * License (the "BSD").
 * </p>
 * <p>
 * 数据集按字典顺序从小到大排，否则Trie树在构建的过程中会出错，因为Node.left和Node.right不对.
 */
package com.mininglamp.nlp.split;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class DoubleArrayTrie {
    private final static int BUF_SIZE = 16384;
    private final static int UNIT_SIZE = 8; // size of int + int

    //字典树的结点, 每个结点只有4个成员(整型), 但包含树的基本信息: 左右结点
    private static class Node {
        int code;   //当前结点的字符编码(一般为unicode编码)
        int depth;  //当前结点的深度
        int left;  //当前节点的子节点集在数据集中的左边界索引 (即已排序List,以当前结点开头的词的开始位置)
        int right;  //当前节点的子节点集在数据集中的右边界索引 (即已排序List,以当前结点开头的词的结束位置)
    }

    ;

    private int check[];  //当前状态的父状态
    private int base[];  //状态数组

    private boolean used[];
    private int size;
    private int allocSize;
    private List<String> key;
    private int keySize;
    private int length[];
    private int value[];
    private int progress;
    private int nextCheckPos;
    // boolean no_delete_;
    int error_;

    // int (*progressfunc_) (size_t, size_t);

    // inline _resize expanded
    private int resize(int newSize) {
        int[] base2 = new int[newSize];
        int[] check2 = new int[newSize];
        boolean used2[] = new boolean[newSize];
        if (allocSize > 0) {
            System.arraycopy(base, 0, base2, 0, allocSize);
            System.arraycopy(check, 0, check2, 0, allocSize);
            System.arraycopy(used, 0, used2, 0, allocSize);
        }

        base = base2;
        check = check2;
        used = used2;

        return allocSize = newSize;
    }

    /**
     * 获取当前结点的所有子结点.
     *
     * @param parent
     * @param siblings
     * @return parent的子节点的个数, 即同一层有多少不同的字.
     */
    private int fetch(Node parent, List<Node> siblings) {
        if (error_ < 0)
            return 0;

        int prev = 0; // prev指向上一个结点,

        //获取父节点的子节点集的索引边界
        for (int i = parent.left; i < parent.right; i++) {
            if ((length != null ? length[i] : key.get(i).length()) < parent.depth)
                continue;

            String tmp = key.get(i);  //数据集(已排序)的第i个词

            int cur = 0;
            if ((length != null ? length[i] : tmp.length()) != parent.depth)  //当前词的长度大于父节点的深度,所以要处理该结点
                cur = (int) tmp.charAt(parent.depth) + 1;   //按层添加, 这里添加的是ascii + 1

            if (prev > cur) {// 按照key排序的规则, cur >= prev
                error_ = -3;
                return 0;
            }

            //遇到未添加的子节点, 则创建节点, 添加当前结点的兄弟节点.
            if (cur != prev || siblings.size() == 0) {
                Node tmp_node = new Node();
                tmp_node.depth = parent.depth + 1;
                tmp_node.code = cur;
                tmp_node.left = i;
                if (siblings.size() != 0)
                    siblings.get(siblings.size() - 1).right = i;  // 上一个结点的右边界更新为当前节点的位置.即遇到同级别不同的字, 则确定上一个字(结点)的右边界

                siblings.add(tmp_node);
            }

            prev = cur;
        }

        if (siblings.size() != 0) //更新最后一个结点的右边界,即parent的右边界
            siblings.get(siblings.size() - 1).right = parent.right;

        return siblings.size();
    }

    /**
     * 根据子结点集去构建双数组 base和check
     * 需要反复扫描数据集,
     * @param siblings
     * @return
     */
    private int insert(List<Node> siblings) {
        if (error_ < 0)  // fetch等操作没有错误
            return 0;

        //初始化状态索引
        int begin = 0;
        //pos: 初始化合适的开始位置(用到nextCheckPos变量判断冲突), 防止每次都从头开始检查非冲突点
        int pos = ((siblings.get(0).code + 1 > nextCheckPos) ? siblings.get(0).code + 1
                : nextCheckPos) - 1;
        //为了计算冲突率
        int nonzero_num = 0;
        int first = 0;

        if (allocSize <= pos)
            resize(pos + 1);

        outer:
        while (true) {
            pos++;

            if (allocSize <= pos)
                resize(pos + 1);

            //check数组的pos位置已经被占用, 发生冲突
            if (check[pos] != 0) {
                nonzero_num++;
                continue;
            } else if (first == 0) {
                nextCheckPos = pos;
                first = 1;
            }

            begin = pos - siblings.get(0).code;//子结点的开始状态索引位
            if (check.length <= (begin + siblings.get(siblings.size() - 1).code)) {
//				// progress can be zero
                double l = (1.05 > 1.0 * keySize / (progress + 1)) ? 1.05 : 1.0
                        * keySize / (progress + 1);
                resize((int) (allocSize * l));

            }

            //开始位被占用, 则继续下一位探测
            if (used[begin])
                continue;

            //检查子结点是否全部转移成功
            for (int i = 1; i < siblings.size(); i++)
                if (check[begin + siblings.get(i).code] != 0)
                    continue outer;

            break;
        }

        // -- Simple heuristics --
        // if the percentage of non-empty contents in check between the
        // index
        // 'next_check_pos' and 'check' is greater than some constant value
        // (e.g. 0.9),
        // new 'next_check_pos' index is written by 'check'.
        //根据冲突率, 更新下一个检查点的位置
        if (1.0 * nonzero_num / (pos - nextCheckPos + 1) >= 0.95)
            nextCheckPos = pos;

        used[begin] = true;
        size = (size > begin + siblings.get(siblings.size() - 1).code + 1) ? size
                : begin + siblings.get(siblings.size() - 1).code + 1;  //size为实际使用的base和check数组长度, 是begin 和 code决定的.

        // 真正更新check数组和base数组的值
        for (int i = 0; i < siblings.size(); i++)
            check[begin + siblings.get(i).code] = begin;

        for (int i = 0; i < siblings.size(); i++) {
            List<Node> new_siblings = new ArrayList<Node>();

            // 代表当前结点无子结点, 说明当前词处理完成, 则将base数组对应的字置为负, 表示当前词的状态转移结束, 设为负是添加叶子结点
            if (fetch(siblings.get(i), new_siblings) == 0) {
                base[begin + siblings.get(i).code] = (value != null) ? (-value[siblings
                        .get(i).left] - 1) : (-siblings.get(i).left - 1);

                if (value != null && (-value[siblings.get(i).left] - 1) >= 0) {
                    error_ = -2;
                    return 0;
                }

                progress++;
                // if (progress_func_) (*progress_func_) (progress,
                // keySize);
            } else {
                // 这里使用递归, 处理所有结点
                int h = insert(new_siblings);
                base[begin + siblings.get(i).code] = h;
            }
        }
        return begin;
    }

    public DoubleArrayTrie() {
        check = null;
        base = null;
        used = null;
        size = 0;
        allocSize = 0;
        // no_delete_ = false;
        error_ = 0;
    }

    // no deconstructor

    // set_result omitted
    // the search methods returns (the list of) the value(s) instead
    // of (the list of) the pair(s) of value(s) and length(s)

    // set_array omitted
    // array omitted

    void clear() {
        // if (! no_delete_)
        check = null;
        base = null;
        used = null;
        allocSize = 0;
        size = 0;
        // no_delete_ = false;
    }

    public int getUnitSize() {
        return UNIT_SIZE;
    }

    public int getSize() {
        return size;
    }

    public int getTotalSize() {
        return size * UNIT_SIZE;
    }

    public int getNonzeroSize() {
        int result = 0;
        for (int i = 0; i < size; i++)
            if (check[i] != 0)
                result++;
        return result;
    }

    public int build(List<String> key) {
        return build(key, null, null, key.size());
    }

    /**
     * 构建双数组 (传入已排序的List)
     * 构建双数组的过程大致可以分为两部分: 先去查找非冲突点，然后再做状态转移去更新base和check数组.
     * @param _key
     * @param _length
     * @param _value
     * @param _keySize
     * @return
     */
    public int build(List<String> _key, int _length[], int _value[],
                     int _keySize) {
        if (_keySize > _key.size() || _key == null)
            return 0;

        // progress_func_ = progress_func;
        key = _key;
        length = _length;
        keySize = _keySize;
        value = _value;
        progress = 0;

        resize(65536 * 32);//因为check和base是普通数组, 当数据量增大时, 需要扩大数组

        base[0] = 1;  //初始化, 初始状态
        nextCheckPos = 0;  //控制, 当冲突率比较大时,可以跳过冲突区

        //01. 构建字典树, 初始化根结点
        Node root_node = new Node();
        root_node.left = 0;  //根节点, 左右边界是全部列表
        root_node.right = keySize;
        root_node.depth = 0;

        //02. 扫描字典树, 设置根结点的所有子结点. 即列表中的第1个字(trie树的第1层结点)
        List<Node> siblings = new ArrayList<Node>();
        fetch(root_node, siblings);
        //03. 根据子结点集去构建双数组 base和check, 其中用到了递归
        insert(siblings);

        // size += (1 << 8 * 2) + 1; // ???
        // if (size >= allocSize) resize (size);

        //释放中间数据占用的内存
        used = null;
        key = null;

        return error_;
    }

    public void open(String fileName) throws IOException {
        File file = new File(fileName);
        size = (int) file.length() / UNIT_SIZE;
        check = new int[size];
        base = new int[size];

        DataInputStream is = null;
        try {
            is = new DataInputStream(new BufferedInputStream(
                    new FileInputStream(file), BUF_SIZE));
            for (int i = 0; i < size; i++) {
                base[i] = is.readInt();
                check[i] = is.readInt();
            }
        } finally {
            if (is != null)
                is.close();
        }
    }

    public void save(String fileName) throws IOException {
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(fileName)));
            for (int i = 0; i < size; i++) {
                out.writeInt(base[i]);
                out.writeInt(check[i]);
            }
            out.close();
        } finally {
            if (out != null)
                out.close();
        }
    }

    /**
     * 精确查找
     * @param key
     * @return
     */
    public int exactMatchSearch(String key) {
        return exactMatchSearch(key, 0, 0, 0);
    }

    public int exactMatchSearch(String key, int pos, int len, int nodePos) {
        if (len <= 0)
            len = key.length();
        if (nodePos <= 0)
            nodePos = 0;

        int result = -1;

        char[] keyChars = key.toCharArray();

        //初始状态转移, 从根节点查找
        int b = base[nodePos];
        int p;

        // 按照双数组的逻辑(如下)逐字进行状态转移 . s为当前状态的下标, t为转移状态的下标. 即 s状态在遇到c时,转移到状态t
        // base[s] + c = t        // b + int(keyChars[i]) + 1 = p
        // check[t] = base[s]           // check[p] = b
        for (int i = pos; i < len; i++) {
            p = b + (int) (keyChars[i]) + 1;
            if (p < check.length && b == check[p])
                b = base[p];
            else
                return result;  // check和base判断错误, 匹配失败. 返回-1
        }

        //如果已经匹配到最后一个字,则去判断是否base值为负, 如果为负, 则表示匹配成功
        p = b;
        if (p < check.length) {
            int n = base[p];
            if (b == check[p] && n < 0) {
                result = -n - 1;
            }
        }
        return result;
    }

    private int exactMatchSearch(char[] keyChars, int pos, int len) {

        int result = -1;

        //初始状态转移, 从根节点查找
        int b = base[0];
        int p;

        // 按照双数组的逻辑(如下)逐字进行状态转移 . s为当前状态的下标, t为转移状态的下标. 即 s状态在遇到c时,转移到状态t
        // base[s] + c = t        // b + int(keyChars[i] + 1 = p
        // check[t] = base[s]           // check[p] = b
        for (int i = pos; i < len; i++) {
            p = b + (int) (keyChars[i]) + 1;
            if (p < check.length && b == check[p])
                b = base[p];
            else
                return result;  // check和base判断错误, 匹配失败. 返回-1
        }

        //如果已经匹配到最后一个字,则去判断是否base值为负, 如果为负, 则表示匹配成功
        p = b;
        if (p < check.length) {
            int n = base[p];
            if (b == check[p] && n < 0) {
                result = -n - 1;
            }
        }
        return result;
    }

    /**
     * 最长匹配, 从字符数组中, 找到最长匹配的
     * @param keyChars 带查找的数组
     * @param pos  开始查找位置
     * @return 匹配上的词的下一个位置, 若没有匹配上, 则返回pos+1,即单字匹配
     */
    public int longestMatch(char[] keyChars, int pos) {

        //初始状态转移, 从根节点查找
        int b = base[0];
        int p;

        // 按照双数组的逻辑(如下)逐字进行状态转移 . s为当前状态的下标, t为转移状态的下标. 即 s状态在遇到c时,转移到状态t
        // base[s] + c = t              即 b + int(keyChars[i]) + 1 = p
        // check[t] = base[s]           即 check[p] = b
        int curIdx = pos;
        for (int i = pos; i < keyChars.length; i++) {
            p = b + (int) (keyChars[i]) + 1;
            if (p < check.length && b == check[p]) {
                b = base[p];
                curIdx = i;
            } else {
                break;
            }
        }

        //如果已经匹配到最后一个字,则去判断是否base值为负(是否是叶子), 如果为负, 则表示匹配成功
        p = b;
        int n = base[p];
        if (b == check[p] && n < 0) {
            return curIdx + 1;  //匹配上结果, 返回匹配上的词的下一个位置
        } else {
            int addLen = curIdx - pos;
            while (addLen > 0) {
                int exactEnd = pos + addLen + 1 > keyChars.length ? keyChars.length : pos + addLen + 1;
                int matIdx = exactMatchSearch(keyChars, pos, exactEnd);

                if (matIdx != -1) {
                    return exactEnd;
                }
                addLen--;
            }
        }
        return pos + 1;  // 匹配上的并不是叶子结点, 则返回-1;
    }


    public List<Integer> commonPrefixSearch(String key) {
        return commonPrefixSearch(key, 0, 0, 0);
    }

    /**
     * 添加重载方法进行前缀匹配, 直接传入文本的字符数组,避免每次都toCharArray().
     * @param textChars
     * @param pos
     * @param len
     * @param nodePos
     * @return
     */
    public List<Integer> commonPrefixSearch(char[] textChars, int pos, int len, int nodePos){
        List<Integer> result = new ArrayList<Integer>();
        int b = base[nodePos];
        int n;
        int p;

        for (int i = pos; i < len; i++) {
            p = b;
            if (p >= base.length) {  //边界检查
                return result;
            }
            n = base[p];

            if (b == check[p] && n < 0) {
                result.add(i);
            }

            p = b + (int) (textChars[i]) + 1;
            if (p < check.length && b == check[p])
                b = base[p];
            else
                return result;
        }

        p = b;
        if(p < base.length) {
            n = base[p];
            if (b == check[p] && n < 0) {
                result.add(len);
            }
        }

        return result;
    }

    /**
     * 用词典切分text
     * @param text  待切分的文本
     * @return 返回所有的切分候选结果(切分词以空格隔开)
     */
    public List<String> splitAllWords(String text){
        List<String> result = new ArrayList<>();
        List<List<Integer>> splitIdxArray = splitAll(text);
        for(List<Integer> splitIdx: splitIdxArray){
            result.add(getWordsString(text, splitIdx));
        }
        return result;
    }

    /**
     * 通过下标列表,从text中获取切分出的词.
     * @param text
     * @param indexList
     * @return 切分出的词的结果, 以"#"隔开
     */
    private String getWordsString(String text, List<Integer> indexList){
        int lastIdx = indexList.size() > 0? indexList.get(0): 0;
        List<String> words = new ArrayList<>();
        for(int i = 1; i < indexList.size(); i++){
            int curIdx = indexList.get(i);
            words.add(text.substring(lastIdx, curIdx));
            lastIdx = curIdx;
        }
        return String.join("#", words);
    }

    public List<List<Integer>> splitAll(String text){
        if (text == null) {
            return new ArrayList<>();
        }else {
            return splitAll(text.toCharArray(), 0, text.length());
        }
    }

    /**
     * 对字符串进行拆分,获取所有的切分候选结果.
     * @param textChars
     * @param pos
     * @param len
     * @return
     */
    public List<List<Integer>> splitAll(char[] textChars, int pos, int len){
        if (textChars == null) return new ArrayList<>();
        List<Integer> commons = commonPrefixSearch(textChars, pos, len, 0);
        //这个while只能处理拼音开头的错误
        while (commons.size() == 0){
            pos = pos + 1;
            if (pos < len) {
                return splitAll(textChars, pos, len);
            }else{
                break;
            }
        }
        //分层,递归进行匹配
        List<List<Integer>> result = new ArrayList<>();
        for(Integer i: commons){
            List<Integer> items = new ArrayList<>();
            items.add(pos);
            items.add(i);
            result.addAll(addSeq(textChars, i, len, items));
        }
        return result;
    }


    public List<List<Integer>> addSeq(char[] textChars, int pos, int len, List<Integer> resList){
        List<List<Integer>> result = new ArrayList<>();
        int b = base[0];
        int n;
        int p;

        for (int i = pos; i < len; i++) {
            p = b;
            n = base[p];

            if (b == check[p] && n < 0) {

                List<Integer> list = new ArrayList<>(resList);
                list.add(i);
                List<List<Integer>> matList = addSeq(textChars, i, len, list);
                if (matList.size() > 0) {
                    result.addAll(matList);
                }
            }

            p = b + (int) (textChars[i]) + 1;
            if (p < check.length && b == check[p])
                b = base[p];
            else
                return result;
        }

        p = b;
        if(p < base.length) {
            n = base[p];
            if (b == check[p] && n < 0) {
                resList.add(len);
            }
        }
        if (resList.get(resList.size() - 1) == len){
            result.add(resList);
        }

        return result;
    }


    public List<Integer> commonPrefixSearch(String key, int pos, int len,
                                            int nodePos) {
        if (len <= 0)
            len = key.length();
        if (nodePos <= 0)
            nodePos = 0;

        char[] keyChars = key.toCharArray();
        return commonPrefixSearch(keyChars, pos, len, nodePos);

    }

    // debug
    public void dump() {
        for (int i = 0; i < size; i++) {
            System.err.println("i: " + i + " [" + base[i] + ", " + check[i]
                    + "]");
        }
    }
}