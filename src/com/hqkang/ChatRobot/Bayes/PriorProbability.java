package com.hqkang.ChatRobot.Bayes;

/**
* 先验概率计算
* <h3>先验概率计算</h3>
* P(c<sub>j</sub>)=N(C=c<sub>j</sub>)<b>/</b>N <br>
* 其中，N(C=c<sub>j</sub>)表示类别c<sub>j</sub>中的训练文本数量；
* N表示训练文本集总数量。
*/
public class PriorProbability 
{
    /**
    * 先验概率  P(h±) 
    * @param c 给定的分类
    * @return 给定条件下的先验概率
    */
    public static float calculatePc(String c)
    {
        float ret = 0F;
        float Nc = TrainingDataManager.getTrainingFileCountOfClassification(c);
        float N = TrainingDataManager.getTrainingFileCount();
        ret = Nc / N;
        return ret;
    }
}
