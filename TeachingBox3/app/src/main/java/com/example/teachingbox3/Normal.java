package com.example.teachingbox3;

import java.util.Set;

/**
 * Created by 孟东风 on {DATE}.
 */
public class Normal {
    public  static final float DIFF=0.0000001f;//判断两个法向量是否相同的阈值

    float nx,ny,nz;
    public Normal(float nx,float ny,float nz){
        this.nx=nx;
        this.ny=ny;
        this.nz=nz;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Normal){  //instanceof未判断一个对象是否未某一类型数据
            Normal tn=(Normal)o;
            if(Math.abs(nx-tn.nx)<DIFF&&Math.abs(ny-tn.ny)<DIFF&&Math.abs(nz-tn.nz)<DIFF){
                return true;
            }
            else {
                return false;
            }
        }
        else{
            return false;
        }
    }

    //由于要用到HashSet,因此一定要重写hashCode方法
    @Override
    public int hashCode(){
        return 1;
    }

    //求法向量平均值的工具方法
    public static float[] getAverage(Set<Normal> sn){
        //存放法向量和的数组
        float[] result=new float[3];
        //把集合中所有的法向量求和
        for(Normal n:sn){
            result[0]+=n.nx;
            result[1]+=n.ny;
            result[2]+=n.nz;
        }
        //将求和后的法向量规格化
        return LoadUtil.vectorNormal(result);
    }
}
