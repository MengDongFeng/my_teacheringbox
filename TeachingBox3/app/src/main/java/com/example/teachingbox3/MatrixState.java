package com.example.teachingbox3;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Stack;

import static java.lang.Math.PI;

/**
 * Created by 孟东风 on {DATE}.
 */
public class MatrixState {
    private static float[] mProjectMatrix=new float[16];//投影用的4x4矩阵
    private static float[] mVMatrix=new float[16];//摄像机位置朝向9参数矩阵
    private static float[] currentMatrix;//当前变换矩阵
    public static float[] lightLocation=new float[]{0,0,0};//定位光光源位置
    public static FloatBuffer cameraFB;
    public static FloatBuffer lightPositionFB;

    public static Stack<float[]> mStack=new Stack<float[]>();//保护变换矩阵的栈
    //欧拉变换zyx
    public static void Euler_ZYX(float x, float y, float z, float a, float b, float c) {
        Matrix.translateM(currentMatrix, 0, x, y, z);
        Matrix.rotateM(currentMatrix, 0, (float) (c * 180 / PI), 0, 0, 1);
        Matrix.rotateM(currentMatrix, 0, (float) (b * 180 / PI), 0, 1, 0);
        Matrix.rotateM(currentMatrix, 0, (float) (a * 180 / PI), 1, 0, 0);
    }
    //获取不变换初等矩阵
    //stack 堆栈
    public static void setInitStack(){
        currentMatrix=new float[16];
        Matrix.setRotateM(currentMatrix,0,0,1,0,0);
    }

    public static void pushMatrix(){
        mStack.push(currentMatrix.clone());
    }

    public static void popMatrix(){
        currentMatrix=mStack.pop();
    }

    public static void translate(float x,float y,float z){  //设置平移
        Matrix.translateM(currentMatrix,0,x,y,z);
    }
    //rotate旋转；设置绕xyz轴转动
    public static void rotate(float angle,float x,float y,float z){
        Matrix.rotateM(currentMatrix,0,angle,x,y,z);
    }

    //设置摄像机
    public static void setCamera(
            float cx,	//摄像机位置x
            float cy,   //摄像机位置y
            float cz,   //摄像机位置z
            float tx,   //摄像机目标点x
            float ty,   //摄像机目标点y
            float tz,   //摄像机目标点z
            float upx,  //摄像机UP向量X分量
            float upy,  //摄像机UP向量Y分量
            float upz   //摄像机UP向量Z分量
    ){
        Matrix.setLookAtM(
                mVMatrix,
                0,
                cx,
                cy,
                cz,
                tx,
                ty,
                tz,
                upx,
                upy,
                upz);

        float[] cameraLocation=new float[3];//摄像机位置
        cameraLocation[0]=cx;
        cameraLocation[1]=cy;
        cameraLocation[2]=cz;

        ByteBuffer libb=ByteBuffer.allocateDirect(3*4);
        libb.order(ByteOrder.nativeOrder());
        cameraFB=libb.asFloatBuffer();
        cameraFB.put(cameraLocation);
        cameraFB.position(0);

    }

    //设置透视投影参数
    public static void setProjectFrustum(
            float left,
            float right,
            float bottom,
            float top,
            float near,
            float far){
        Matrix.frustumM(mProjectMatrix,0,left,right,bottom,top,near,far);
    }

    //设置正交投影参数
    public static void setProjectOrtho(
            float left,
            float right,
            float bottom,
            float top,
            float near,
            float far){
        Matrix.orthoM(mProjectMatrix,0,left,right,bottom,top,near,far);
    }

    //获取具体物体的总变换矩阵
    static float[] mMVPMatrix=new float[16];

    public static float[] getFinalMatrix(){
        Matrix.multiplyMM(mMVPMatrix,0,mVMatrix,0,currentMatrix,0);
        Matrix.multiplyMM(mMVPMatrix,0,mProjectMatrix,0,mMVPMatrix,0);
        return  mMVPMatrix;
    }

    //获取具体物体的变化矩阵
    public static float[] getMMatrix(){
        return currentMatrix;
    }

    //设置灯光位置的方法
    public static void setLightLocation(float x,float y,float z)
    {
        lightLocation[0]=x;
        lightLocation[1]=y;
        lightLocation[2]=z;
        ByteBuffer llbb = ByteBuffer.allocateDirect(3*4);
        llbb.order(ByteOrder.nativeOrder());//设置字节顺序
        lightPositionFB=llbb.asFloatBuffer();
        lightPositionFB.put(lightLocation);
        lightPositionFB.position(0);

    }
}
