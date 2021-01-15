# 拼音转汉字 
这是基于隐马尔可夫的拼音转汉字的开源项目，如果该项目对你有帮助欢迎点赞、打赏、抛🍌、丢🪙。
## 项目结构
```
|--hmm_model：存放用于拼音字符串切分和拼音转汉字的概率统计文件
    |--hmm_start.json：起始概率
    |--hmm_emission.json：发射概率
    |--hmm_transition.json 转移概率
    |--hmm_py2hz.json：每一个拼音对应的所有可能汉字
```
> 以上是拼音转汉字时用到的概率文件。当然这些文件也可以替换成自己训练好的概率文件，但是注意要保障命名的一致性。

```
    |--pinyin.dat：所有汉字的所有拼音（格式化后的）
    |--pinyin_all.txt：所有汉字的所有拼音
    |--pinyin_hmm_start.json：拼音起始概率
    |--pinyin_hmm_transition.json：拼音之间的转移概率
```
> 以上是切分拼音字符串时要用到的文件。其中pinyin_hmm_start.json和pinyin_hmm_transition.json可以替换成自己
训练好的文件，但同样要注意保障命名的一致性。

```
|--lib：项目中用到的额外的jar包
|--logs：生成的日志
|--src：源码
    |--hmm：生成拼音转汉字的序列
    |--split：切分拼音
    |--tool：工具
|--pinyin2hanzi.jar：为打包后文件，直接加载到项目中便可以使用
```

## 模型下载
|网盘    |链接    |
| :---: |:---:  |
|baidu| [下载 w3e0](https://pan.baidu.com/s/1xnh_PuIDhd3CtPWg4lcv0Q)|
|google| [下载](https://drive.google.com/drive/folders/1yLsQCuOcP6j06IxmPmz0HHyfx5_L-eZw?usp=sharing)|

## 使用
### 拼音分词（将拼音字符串分割成独立的拼音）
* List<String> pinyinsSeg=PinyinSplit.splitByTrieTree(String s);
  其中，参数 s 为要分割的拼音字符串，返回所有的可能的切分结果。
  若想要得到最佳的切分结果，可以将上面的切分结果传入下面的方法中。
* List<String> pinyinOptimum=getOptimumPinyin(String pinyinsSeg, int num);
  其中，参数 pinyinSeg 为上一步中切分结果；参数 num 为想要得到的最佳切分数。
### 拼音转汉字
* Pinyin2Hanzi.setPinyinMaxLength()：设置输入字符串不能超过的最大值，默认参数设置为50。（可选）
* Pinyin2Hanzi.setPathMaxNumber()：设置每组拼音串最大的候选汉字序列个数， 默认参数设置为10。（可选）
* Pinyin2Hanzi.loadModel(String modelPath):加载模型，参数modelPath为你存放模型的文件夹。（必选）
* List<List<String>> hanzisList = Pin2Hanzi.transform(String str, int segNum, int pathNum);
  其中，参数str为要转换的字符串；参数segNum为拼音分切分的候选结果；参数pathNum为每种候选切分拼音序列中得到的候选转换汉字的个数。（必选）

## 示例
### 拼音分词
* 获取所有切分候选结果
```
输入："xiannihao"
输出："xi#an#ni#ha#o" "xi#an#ni#hao" "xian#ni#ha#o" "xian#ni#hao"
```
* 获取最优的切分结果（以2个为例） 
```
输入："xiannihao"
输出："xian#ni#hao" "xi#an#ni#hao"
```
### 拼音转汉字
* segNum=1 pathNum=2
```
输入："woaibeijingtiananmentiananmenshangtaiyangsheng"
输出："我爱背景天安门天安门上太阳升" "我爱北京天安门天安门上太阳升"
```
  
* segNum=2 pathNum=5
```
输入："xiaotuan"
输出："销团" "小团" "校团" "效团" "逍抟" 
输出："小图案" "笑图案" "效途安" "校图案" "小土安" 
```
  
## 实验结果
以下是在人名数据集上做模型合并后的实验结果：

|准确率/匹配率 |0.5 | 0.6|0.65|0.7|0.75|
| :---:      |:---:  |:---: | :---: | :---: | :---: |
|原始模型         |0.86 |0.58|0.56|0.51|0.5|
|原始模型+领域模型  |0.95 |0.82|0.81|0.77|0.767|
|领域模型         |0.975|0.907|0.9|0.876|0.873|

> 模型使用过程中，建议使用自己的小领域中文语料与现有模型做合并，经测试这样可以提高现有领域拼音转汉字的准确率。

## 模型训练
请跳转至 [Pinyin2HanziPy](https://github.com/lovejing0306/Pinyin2HanziPy)
