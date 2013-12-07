package com.hqkang.ChatRobot.Bayes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
/**
* 朴素贝叶斯分类器
*/
public class BayesClassifier 
{


    /**
    * 计算给定的文本属性向量X在给定的分类Cj中的类条件概率
    * <code>ClassConditionalProbability</code>连乘值
    * @param X 给定的文本属性向量
    * @param Cj 给定的类别
    * @return 分类条件概率连乘值，即<br>
    */
    static double calcProd(List<String> X, String Cj) 
    {
        double ret = 1.0;
        // 类条件概率连乘
        for (String Xi:X)
        {

            //因为结果过小，因此在连乘之前放大10倍，这对最终结果并无影响，因为我们只是比较概率大小而已
            ret +=Math.log(ClassConditionalProbability.calculatePxc(Xi, Cj));
        }
        // 再乘以先验概率
        ret += Math.log(PriorProbability.calculatePc(Cj));
        return ret;
    }

    /**
    * 对给定的文本进行分类
    * @param text 给定的文本
    * @return 分类结果
    */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String classify(List<String> terms) 
    {
        if(terms.size()<2) return "useless";
        Set<String> Classes = TrainingDataManager.getTrainingClassifications();//分类
        double probility = 0.0;
        List<ClassifyResult> crs = new ArrayList<ClassifyResult>();//分类结果
        for (String Ci:Classes) 
        {
        	//第i个分类 Ci
            probility = calcProd(terms, Ci);//计算给定的文本属性向量terms在给定的分类Ci中的分类条件概率
            //保存分类结果
            ClassifyResult cr = new ClassifyResult();
            cr.classification = Ci;//分类
            cr.probility = probility;//关键字在分类的条件概率
            System.out.println("In process.");
            System.out.println(Ci + "：" + probility);
            crs.add(cr);
        }
        //对最后概率结果进行排序
        java.util.Collections.sort(crs,new Comparator() 
        {
            public int compare(final Object o1,final Object o2) 
            {
                final ClassifyResult m1 = (ClassifyResult) o1;
                final ClassifyResult m2 = (ClassifyResult) o2;
                final double ret = m1.probility - m2.probility;
                if (ret < 0) 
                {
                    return 1;
                } 
                else 
                {
                    return -1;
                }
            }
        });
        //返回概率最大的分类
        return crs.get(0).classification;
    }
}