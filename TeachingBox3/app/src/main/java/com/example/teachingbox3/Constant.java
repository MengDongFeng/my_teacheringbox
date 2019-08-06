package com.example.teachingbox3;

import static java.lang.Math.PI;

/**
 * Created by 孟东风 on {DATE}.
 */
public class Constant {
    //视场参数
    public static float View_radius = 2000;  //人眼距离视觉中心点的距离
    public static float a_horizontal = 0;  //水平与水平方向的角度
    public static float cx = 0, cy = 0, cz = 0;   //观察时人眼的位置
    public static float centre_x = 0, centre_y = 0, centre_z = 0;//视觉中心点
    public static float orientation_x = 0, orientation_y = 1, orientation_z = 0;
    //正反解参数
    public static float alpha[] = {0.0f, (float)(-PI/2),(float)(PI/2),(float)(-PI/2),(float)(PI/2),(float)(-PI/2),(float)(PI/2)};
    public static float theta_0[] = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f ,0.0f};
    public static float theta[] = {0.0f, 0.0f,0.0f, 0.0f,0.0f,0.0f,0.0f};
    //angle_in = [-45; 45; 0; 45; 0; -45; 0];

    public static float a[] = {0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f};
    public static float d[] = {0.0f, 0.0f, 450.0f,0.0f, 440.0f, 0.0f, 0.0f};   //D-H参数

    public static float d3=450,d4=440;

    //    public static float a[] = {0.0f, 0.0f, 360.0f, 90.0f, 0.0f, 0.0f};
    //    public static float d[] = {0.0f, 0.0f, 0.0f, 376.5f, 0.0f, 0.0f};   //D-H参数

    public static float tt7[][] = {{1,0,0,0,},{0,1,0,0},{0,0,1,0},{0,0,0,1}};   //指的是关节6到末端手臂的变换矩阵
    public static float tt7_inv[][] = {{1,0,0,0},{0,1,0,0},{0,0,1,-240},{0,0,0,1}};
    // public static float tt6[][] = {{-1,0,0,0,},{0,-1,0,0},{0,0,1,119},{0,0,0,1}};   //指的是关节6到末端手臂的变换矩阵
    //    public static float tt6_inv[][] = {{-1,0,0,0},{0,-1,0,0},{0,0,1,-119},{0,0,0,1}};

    public static int jiaju_index;//夹具标号，0代表无夹具，1代表有夹具
    public static float joint_target[] = {0,-90,0,0, 0,0, 0};//关节预期值，可通过seekbar控制，也可在程序中赋值
    public static float joint_current[] = {0,-90,0,0, 0,0, 0};//关节当前值，会随运动自动更新
 //   public static float joint_target[] = {-45, 45,0, 45, 0, -45, 0};//关节预期值，可通过seekbar控制，也可在程序中赋值
   // public static float joint_current[] = {-45, 45,0, 45, 0, -45, 0};//关节当前值，会随运动自动更新
    //  public static float joint_target[] = {0, -90, 180, 0, 0, 0};//关节预期值，可通过seekbar控制，也可在程序中赋值
    //  public static float joint_current[] = {0, -90, 180, 0, 0, 0};//关节当前值，会随运动自动更新

    //三维模型拖动时用
    public static float lastxMove = 0;
    public static float moveDistanceX;
    public static float lastFingerDis;
    public static float endpose[] = {0, 0, 0, 0, 0, 0};    //实时计算的末端位姿
    public static float DD[][] = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
    public static final float joint_left_extreme[] = {-200, -180, -360, -180,-360 -360, -360}, joint_right_extreme[] = {200, 0, 360,240, 360,180, 360, 360};

    public static String IP;

    public static float joint_from_pc[]={0,-90,180,0,0,0}, endpos_from_pc[]={490,0,450,0,90,0};

    public static float angle_in[] = {-45, 45, 0, 45, 0, -45, 0};
}
