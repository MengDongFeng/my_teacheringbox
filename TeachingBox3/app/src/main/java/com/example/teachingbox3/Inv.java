package com.example.teachingbox3;

import static com.example.teachingbox3.Constant.*;
import static com.example.teachingbox3.RunActivity.seekbar_change;
import static java.lang.Math.PI;
import static java.lang.Math.*;


/**
 * Created by 孟东风 on {DATE}.
 */
public class Inv {
    public static float[] Mat_to_pos(float T[][]) {   //矩阵转位姿
        float nx, ny, nz, ox, oy, oz, ax, ay, az, px, py, pz;
        nx = T[0][0];
        ox = T[0][1];
        ax = T[0][2];
        px = T[0][3];
        ny = T[1][0];
        oy = T[1][1];
        ay = T[1][2];
        py = T[1][3];
        nz = T[2][0];
        oz = T[2][1];
        az = T[2][2];
        pz = T[2][3];
        float ra, rb, rc;

        //zyz旋转
        rb = (float) (atan2(sqrt(pow(nz, 2) + pow(oz, 2)), az));
        if (abs(rb) < 0.0001) {
            ra = 0;
            rc = (float) (atan2(-ox, nx));
        } else if (abs(rb - PI) < 0.0001) {
            ra = 0;
            rc = (float) (atan2(ox, -nx));
        } else {
            rc = (float) (atan2(oz, -nz));
            ra = (float) (atan2(ay, ax));
        }
        float endpose[] = {px, py, pz, (float) (ra * 180 / PI), (float) (rb * 180 / PI), (float) (rc * 180 / PI)};
        return endpose;
    }

    public static float[][] Pos_to_mat(float pos[]) { //位姿转矩阵
        float tt[][] = new float[4][4];
        tt[0][3] = pos[0];
        tt[1][3] = pos[1];
        tt[2][3] = pos[2];//位置
        float a = (float) (pos[3] * PI / 180), b = (float) (pos[4] * PI / 180), c = (float) (pos[5] * PI / 180);
        //ZYZ
        tt[0][0] = (float) (cos(a) * cos(b) * cos(c) - sin(a) * sin(c));
        tt[0][1] = (float) (-cos(a) * cos(b) * sin(c) - sin(a) * cos(c));
        tt[0][2] = (float) (cos(a) * sin(b));
        tt[1][0] = (float) (sin(a) * cos(b) * cos(c) + cos(a) * sin(c));
        tt[1][1] = (float) (-sin(a) * cos(b) * sin(c) + cos(a) * cos(c));
        tt[1][2] = (float) (sin(a) * sin(b));
        tt[2][0] = (float) (-sin(b) * cos(c));
        tt[2][1] = (float) (sin(b) * sin(c));
        tt[2][2] = (float) (cos(b));

        tt[3][0] = 0;
        tt[3][1] = 0;
        tt[3][2] = 0;
        tt[3][3] = 1;//最后一行的四位
        return tt;
    }

    public static float[][] MetMuti44(float a[][], float b[][]) { //矩阵相乘
        float c[][] = new float[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                c[i][j] = 0;
                for (int k = 0; k < 4; k++) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return c;
    }

    public static float[][] Inv_matrix44(float a[][]) {  //矩阵求逆
        float b[][] = new float[4][4];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                b[i][j] = a[j][i];
                //	temp[i][j] = in[i][i];
            }
        }
        for (int i = 0; i < 3; i++) {
            b[i][3] = -(b[i][0] * a[0][3] + b[i][1] * a[1][3] + b[i][2] * a[2][3]);
        }
        b[3][0] = 0.0f;
        b[3][1] = 0.0f;
        b[3][2] = 0.0f;
        b[3][3] = 1.0f;
        return b;
    }

    public static float[][] DH_mat(float a, float d, float alpha, float theta) {
        float aa[][] = new float[4][4];
        aa[0][0] = (float) (cos(theta));
        aa[0][1] = (float) (-sin(theta));
        aa[0][2] = 0;
        aa[0][3] = a;
        aa[1][0] = (float) (sin(theta) * cos(alpha));
        aa[1][1] = (float) (cos(theta) * cos(alpha));
        aa[1][2] = (float) (-sin(alpha));
        aa[1][3] = (float) (-d * sin(alpha));
        aa[2][0] = (float) (sin(theta) * sin(alpha));
        aa[2][1] = (float) (cos(theta) * sin(alpha));
        aa[2][2] = (float) (cos(alpha));
        aa[2][3] = (float) (d * cos(alpha));
        aa[3][0] = 0;
        aa[3][1] = 0;
        aa[3][2] = 0;
        aa[3][3] = 1;
        return aa;
    }

    public static void DD_Calculate(float x, float y, float z)   //角度变化
    {
        float a = (float) (endpose[5] * PI / 180);   //rz
        float b = (float) (endpose[4] * PI / 180);  //ry
        if (abs(sin(b)) > 0.001)  //弧度b不为0时
        {
            DD[0][0] = (float) (-cos(a) * cos(b) / sin(b));
            DD[0][1] = (float) (-cos(b) * sin(a) / sin(b));
            DD[0][2] = 1;
            DD[1][0] = (float) (-sin(a));
            DD[1][1] = (float) (cos(a));
            DD[1][2] = 0;
            DD[2][0] = (float) (cos(a) / sin(b));
            DD[2][1] = (float) (sin(a) / sin(b));
            DD[2][2] = 0;
            float m = 0, n = 0, p = 0;  //分别 代表三个欧拉角上的增量
            p = DD[0][0] * x + DD[0][1] * y + DD[0][2] * z;
            n = DD[1][0] * x + DD[1][1] * y + DD[1][2] * z;
            m = DD[2][0] * x + DD[2][1] * y + DD[2][2] * z;
            float endpos_target[] = endpose;
            endpos_target[3] += m;
            endpos_target[4] += n;
            endpos_target[5] += p;
            inv_Solve(endpos_target); //求反解
            seekbar_change(0);
        }
    }

    //求两个向量的叉积
    public static float[] getCrossProduct(float x1, float y1, float z1, float x2, float y2, float z2) {
        //两矢量叉积的矢量在XYZ轴的分量ABC
        float A = y1 * z2 - y2 * z1;
        float B = z1 * x2 - z2 * x1;
        float C = x1 * y2 - x2 * y1;
        return new float[]{A, B, C};
    }

    //向量规格化
    public static float[] vectorNormal(float[] vector) {
        //求向量的模
        float module = (float) Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
        return new float[]{vector[0] / module, vector[1] / module, vector[2] / module};
    }

    public static void inv_Solve(float endp[]) {   //求反解程序
        float t70_inv[][] = Pos_to_mat(endp);  //位姿转矩阵
        //下面是未解决项
        float T07[][] = new float[4][4];  //尚未初始化
        float TransMatrix[][] = new float[4][4];

        float p[] = new float[3];

        float x1[] = new float[8], x2[] = new float[8], x3[] = new float[8], x4[] = new float[8], x5[] = new float[8], x6[] = new float[8], x7[] = new float[8], x8[] = new float[8];

        float x0[] = {1.0f, 0.0f, 0.0f};
        float b_unit[] = new float[3];
        float Beta = 0.0f;     //臂角,未定，后需改
        float z3[] = new float[3];
        float y3[] = new float[3];
        float xx3[] = new float[3];

        T07 = MetMuti44(TransMatrix, tt7_inv);
        float px = T07[0][3], py = T07[1][3], pz = T07[2][3];

        /*
        float t01[][], t12[][], t23[][], t34[][], t45[][], t56[][], t67[][];   //调用inv中的DH_mat方法计算连续变化矩阵
        t01 = DH_mat(a[0], d[0], alpha[0], x1[i]);
        t12 = DH_mat(a[1], d[1], alpha[1], x2[i]);
        t23 = DH_mat(a[2], d[2], alpha[2], x3[i]);
        t34 = DH_mat(a[3], d[3], alpha[3], x4[i]);
        t45 = DH_mat(a[4], d[4], alpha[4], x4[i]);
        t56 = DH_mat(a[5], d[5], alpha[5], x5[i]);
        t67 = DH_mat(a[6], d[6], alpha[6], x6[i]);

        float t02[][], t03[][], t04[][], t05[][], t06[][], t07[][], t0_end[][];   //连续变换矩阵相乘得到关节变化矩阵
        t02 = MetMuti44(t01, t12);
        t03 = MetMuti44(t02, t23);
        t04 = MetMuti44(t03, t34);
        t05 = MetMuti44(t04, t45);
        t06 = MetMuti44(t05, t56);
        t07 = MetMuti44(t06, t67);
        t0_end = MetMuti44(t07, tt7);  //此中tt6即为原始矩阵
         */

        float alfa = (float) (acos(((pow(px, 2) + pow(py, 2) + pow(pz, 2)) + pow(d3, 2) - pow(d4, 2)) / (2.0 * sqrt(pow(px, 2) + pow(py, 2) + pow(pz, 2)) * d4)));

        x4[0] = (float) (-acos(((pow(px, 2) + pow(py, 2) + pow(pz, 2)) - pow(d3, 2) - pow(d4, 2)) / (2.0 * d3 * d4)));
        x4[1] = x4[0];
        x4[2] = x4[0];
        x4[3] = x4[0];
        x4[4] = (float) (acos(((pow(px, 2) + pow(py, 2) + pow(pz, 2)) - pow(d3, 2) - pow(d4, 2)) / (2.0 * d3 * d4)));
        x4[5] = x4[4];
        x4[6] = x4[4];
        x4[7] = x4[4];

        p[0] = px;
        p[1] = py;
        p[2] = pz;

        //求单位向量
        float[] p_unit = vectorNormal(p);
        //通过求两个向量的叉积计算法向量
        float[] a_unit = vectorNormal(getCrossProduct(x0[0], x0[1], x0[2], p_unit[0], p_unit[1], p_unit[2]));
        //求叉积
        float[] a_unit_vertical = getCrossProduct(p_unit[0], p_unit[1], p_unit[2], a_unit[0], a_unit[1], a_unit[2]);

        for (int i = 0; i < 3; i++) {
            b_unit[i] = (float) (a_unit[i] * cos(Beta) + a_unit_vertical[i] * sin(Beta));
            z3[i] = (float) (p_unit[i] * cos(alfa) + b_unit[i] * sin(alfa));
        }

        if (abs(x4[0]) < 1e-8) {
            x2[0] = (float) (acos(z3[2]));
            x2[1] = x2[0];
            x2[2] = -x2[0];
            x2[3] = -x2[0];
            for (int i = 0; i < 4; i++) {
                x3[i] = angle_in[2];
                if (abs(x2[i]) < 1e-6) {
                    x1[i] = angle_in[0];
                } else {
                    x1[i] = (float) (atan2(z3[1] / sin(x2[i]), z3[0] / sin(x2[i])));
                }
            }
        } else {
            //求叉积
            float[] y3_1 = getCrossProduct(p_unit[0], p_unit[1], p_unit[2], z3[0], z3[1], z3[2]);
            //求单位向量
            y3 = vectorNormal(y3_1);
            //求叉积
            xx3 = getCrossProduct(y3[0], y3[1], y3[2], z3[0], z3[1], z3[2]);

            x2[0] = (float) (acos(z3[2]));
            x2[1] = x2[0];
            x2[2] = -x2[0];
            x2[3] = -x2[0];
            for (int i = 0; i < 4; i++) {
                if (abs(x2[i]) < 1e-6) {
                    x1[i] = angle_in[0];
                    x3[i] = (float) (atan2(z3[1], x3[0]) - x1[i]);
                } else {
                    x1[i] = (float) (atan2(z3[1] / sin(x2[i]), z3[0] / sin(x2[i])));
                    x3[i] = (float) (atan2(y3[1] / sin(x2[i]), xx3[2] / sin(x2[i])));
                }
            }
        }

        float T1[][] = new float[4][4], T2[][] = new float[4][4], T3[][] = new float[4][4], T4[][] = new float[4][4], T04[][] = new float[4][4], T02[][] = new float[4][4], T03[][] = new float[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                T1[i][j] = 0.0f;
                T2[i][j] = 0.0f;
                T3[i][j] = 0.0f;
                T4[i][j] = 0.0f;
            }
        }

        T1[0][0] = (float) cos(x1[0]);
        T1[0][1] = (float) -sin(x1[0]);
        T1[1][0] = (float) sin(x1[0]);
        T1[1][1] = (float) cos(x1[0]);
        T1[2][2] = 1.0f;
        T1[3][3] = 1.0f;

        T2[0][0] = (float) cos(x2[0]);
        T2[0][1] = (float) -sin(x2[0]);
        T2[1][2] = 1.0f;
        T2[2][0] = (float) -sin(x2[0]);
        T2[2][1] = (float) -cos(x2[0]);
        T2[3][3] = 1.0f;

        T3[0][0] = (float) cos(x3[0]);
        T3[0][1] = (float) -sin(x3[0]);
        T3[1][2] = -1.0f;
        T3[2][0] = (float) sin(x3[0]);
        T3[2][1] = (float) cos(x3[0]);
        T3[3][3] = 1.0f;

        T4[0][0] = (float) cos(x4[0]);
        T4[0][1] = (float) -sin(x4[0]);
        T4[1][2] = 1.0f;
        T4[2][0] = (float) -sin(x4[0]);
        T4[2][1] = (float) -cos(x4[0]);
        T4[3][3] = 1.0f;

        T02 = MetMuti44(T1, T2);
        T03 = MetMuti44(T02, T3);
        T04 = MetMuti44(T03, T4);

        float Inv_T04[][] = Inv_matrix44(T04);
        float T57_2_1[][] = MetMuti44(Inv_T04, TransMatrix);

        T1[0][0] = (float) cos(x1[2]);
        T1[0][1] = (float) -sin(x1[2]);
        T1[1][0] = (float) sin(x1[2]);
        T1[1][1] = (float) cos(x1[2]);
        T1[2][2] = 1.0f;
        T1[3][3] = 1.0f;

        T2[0][0] = (float) cos(x2[2]);
        T2[0][1] = (float) -sin(x2[2]);
        T2[1][2] = 1.0f;
        T2[2][0] = (float) -sin(x2[2]);
        T2[2][1] = (float) -cos(x2[2]);
        T2[3][3] = 1.0f;

        T3[0][0] = (float) cos(x3[2]);
        T3[0][1] = (float) -sin(x3[2]);
        T3[1][2] = -1.0f;
        T3[2][0] = (float) sin(x3[2]);
        T3[2][1] = (float) cos(x3[2]);
        T3[3][3] = 1.0f;

        T4[0][0] = (float) cos(x4[2]);
        T4[0][1] = (float) -sin(x4[2]);
        T4[1][2] = 1.0f;
        T4[2][0] = (float) -sin(x4[2]);
        T4[2][1] = (float) -cos(x4[2]);
        T4[3][3] = 1.0f;

        T02 = MetMuti44(T1, T2);
        T03 = MetMuti44(T02, T3);
        T04 = MetMuti44(T03, T4);

   //     Inv_T04[][]=Inv_matrix44(T04);   //暂时看不出来哪里有问题


        float T57_2_2[][] = MetMuti44(Inv_T04, TransMatrix);




       /*
        float t01[][], t12[][], t23[][], t34[][], t45[][], t56[][], t67[][];   //调用inv中的DH_mat方法计算连续变化矩阵
        for (int i = 0; i < 1; i++) {

            t01 = DH_mat(a[0], d[0], alpha[0], x1[i]);
            t12 = DH_mat(a[1], d[1], alpha[1], x2[i]);
            t23 = DH_mat(a[2], d[2], alpha[2], x3[i]);
            t34 = DH_mat(a[3], d[3], alpha[3], x4[i]);
            t45 = DH_mat(a[4], d[4], alpha[4], x4[i]);
            t56 = DH_mat(a[5], d[5], alpha[5], x5[i]);
            t67 = DH_mat(a[6], d[6], alpha[6], x6[i]);

            float t02[][], t03[][], t04[][], t05[][], t06[][], t07[][], t0_end[][];   //连续变换矩阵相乘得到关节变化矩阵
            t02 = MetMuti44(t01, t12);
            t03 = MetMuti44(t02, t23);
            t04 = MetMuti44(t03, t34);
            t05 = MetMuti44(t04, t45);
            t06 = MetMuti44(t05, t56);
            t07 = MetMuti44(t06, t67);
            t0_end = MetMuti44(t07, tt7);  //此中tt6即为原始矩阵

            //  新矩阵求逆
            float inv_t04[][];
            inv_t04 = Inv_matrix44(t04);
            float T57_2_1[][];
            T57_2_1 = MetMuti44(inv_t04, t07);    //这里求的是5到7的
        }
        */
    }

    public static float chooseSolve(float Angle_now[], float Solve[][]) {
        float MinSum = 10000.0f;
        float Mindex = -1;
        for (int index = 0; index < 8; index++) {
            float Angle_Sum = 0.0f;
            for (int i = 0; i < 7; i++) {
                Angle_Sum = (float) (Angle_Sum + abs(Solve[i][index] - Angle_now[i]));
            }
            if (Angle_Sum < MinSum) {
                MinSum = Angle_Sum;
                Mindex = index;
            }
        }
        return Mindex;
    }
}


        /*
        //输入aa为末端位姿
        float t86[][] = new float[8][6];
        float t60_inv[][]=Pos_to_mat(endp);  //位姿转矩阵
        float result[]=new float[6];
        float T60[][]=MetMuti44(t60_inv, tt7_inv);        //t60_inv为输入的位姿转换到基坐标上的位姿，乘以tt6_inv表示6坐标系的位姿  tt6_inv表示末端执行器的逆运动
        float nx, ny, nz, ox, oy, oz, ax, ay, az, px, py, pz;
        nx=T60[0][0]; ox=T60[0][1]; ax=T60[0][2]; px=T60[0][3];
        ny=T60[1][0]; oy=T60[1][1]; ay=T60[1][2]; py=T60[1][3];
        nz=T60[2][0]; oz=T60[2][1]; az=T60[2][2]; pz=T60[2][3];
        //求解关节一
        float x1[]=new float[2];
        x1[0]=(float)(atan2(py, px));
        if(x1[0]>PI)    { x1[1]=(float)(x1[0]-PI); }
        else            { x1[1]=(float)(x1[0]+PI); }  //关节一存在两个解
        //求解关节三
        float x3[]=new float[2];
        float k=(float)((pow(px,2)+pow(py,2)+pow(pz,2)-pow(a[2],2)-pow(a[3],2)-pow(d[3],2))/(2*a[2]));
        x3[0]=(float)(atan2(a[3], d[3])-atan2(k, sqrt(pow(a[3], 2)+pow(d[3], 2)-pow(k, 2))));
        x3[1]=(float)(atan2(a[3], d[3])-atan2(k, -sqrt(pow(a[3], 2)+pow(d[3], 2)-pow(k, 2))));  //关节三有两个解

        //求解关节2和关节4
        float x2, x4;
        int index=0;
        for (int i=0; i < 2; i++) {
            for (int j=0; j < 2; j++) {
                float s1=(float)(sin(x1[i])),c1=(float)(cos(x1[i])),s3=(float)(sin(x3[j])),c3=(float)(cos(x3[j]));
                float s23=(float)(((-a[3]-a[2]*c3)*pz+(c1*px+s1*py)*(a[2]*s3-d[3])) / (pow(pz, 2)+pow(c1*px+s1*py, 2)));
                float c23=(float)(((-d[3]+a[2]*s3)*pz+(c1*px+s1*py)*(a[2]*c3+a[3])) / (pow(pz, 2)+pow(c1*px+s1*py, 2)));
                x2=(float)(atan2(s23, c23)-x3[j]);//x2
                //关节4第一种解
                x4=(float)(atan2((-ax*s1+ay*c1), (-ax*c1*c23-ay*s1*c23+az*s23)));   //x4
                //求解关节5
                float s4=(float)(sin(x4)),c4=(float)(cos(x4));
                float s5=-ax*(c1*c23*c4+s1*s4)-ay*(s1*c23*c4-c1*s4)+az*s23*c4;
                float c5=-ax*c1*s23-ay*s1*s23-az*c23;
                float x5=(float)(atan2(s5, c5));   //x5
                //求解关节6
                float s6=-nx*(c1*c23*s4-s1*c4)-ny*(s1*c23*s4+c1*c4)+nz*s23*s4;
                float c6=nx*((c1*c23*c4+s1*s4)*c5-c1*s23*s5)+ny*((s1*c23*c4-c1*s4)*c5-s1*s23*s5)-nz*(s23*c4*c5+c23*s5);
                float x6=(float)(atan2(s6, c6));   //x6
                if (abs(x5) <= 0.001) {   //关节4和6平行时，4不变
                    x4=0;   //x4
                    s4=(float)(sin(x4)); c4=(float)(cos(x4));
                    s6=-nx*(c1*c23*s4-s1*c4)-ny*(s1*c23*s4+c1*c4)+nz*s23*s4;
                    c6=nx*((c1*c23*c4+s1*s4)*c5-c1*s23*s5)+ny*((s1*c23*c4-c1*s4)*c5-s1*s23*s5)-nz*(s23*c4*c5+c23*s5);
                    x6=(float)(atan2(s6, c6));  //x6
                }
                t86[index][0]=x1[i];   t86[index][1]=x2;
                t86[index][2]=x3[j];   t86[index][3]=x4;
                t86[index][4]=x5;      t86[index][5]=x6;

                //关节4第二种解
                if(x4>PI) { x4-=PI;  }
                else    { x4+=PI; }
                //求解关节5
                s4=(float)(sin(x4));   c4=(float)(cos(x4));
                s5=-ax*(c1*c23*c4+s1*s4)-ay*(s1*c23*c4-c1*s4)+az*s23*c4;
                c5=-ax*c1*s23-ay*s1*s23-az*c23;
                x5=(float)(atan2(s5, c5));
                //求解关节6
                s6=-nx*(c1*c23*s4-s1*c4)-ny*(s1*c23*s4+c1*c4)+nz*s23*s4;
                c6=nx*((c1*c23*c4+s1*s4)*c5-c1*s23*s5)+ny*((s1*c23*c4-c1*s4)*c5-s1*s23*s5)-nz*(s23*c4*c5+c23*s5);
                x6=(float)(atan2(s6, c6));
                if (abs(sin(x5)) <= 0.001) {
                    x4=0;
                    s4=(float)(sin(x4));  c4=(float)(cos(x4));
                    s6=-nx*(c1*c23*s4-s1*c4)-ny*(s1*c23*s4+c1*c4)+nz*s23*s4;
                    c6=nx*((c1*c23*c4+s1*s4)*c5-c1*s23*s5)+ny*((s1*c23*c4-c1*s4)*c5-s1*s23*s5)-nz*(s23*c4*c5+c23*s5);
                    x6=(float)(atan2(s6, c6));
                }
                t86[index+4][0]=x1[i];    t86[index+4][1]=x2;
                t86[index+4][2]=x3[j];    t86[index+4][3]=x4;
                t86[index+4][4]=x5;       t86[index+4][5]=x6;
                index=index+1;
            }
        }

        for (int i=0; i < 8; i++) {
            for (int j=0; j < 6; j++) {
                t86[i][j]=(float)((t86[i][j]-theta[j]-theta_0[j])*180/PI);
            }
        }
        float cache=10000, m;   //cache存储最小值，m表示当前组的数值
        int  inde=0;  //8组反解中路径最短的反解组数
        for(int i=0; i<8; i++)
        {//找到与当前最接近的
            m=abs(joint_current[0]-t86[i][0])+abs(joint_current[1]-t86[i][1])+abs(joint_current[2]-t86[i][2])+abs(joint_current[3]-t86[i][3])+abs(joint_current[4]-t86[i][4])+abs(joint_current[5]-t86[i][5]);
            if(m<cache)   { inde=i; cache=m; }
        }
        for(int i=0; i<6; i++)
        { result[i]=t86[inde][i];   }
        if(cache>200)  //差距过大，可视为找不到合适解，位姿达不到要求 机器人不动
        {   result=joint_current;  }
        for(int i=0; i<6; i++)
        {
            joint_target[i]=result[i];
        }
    }
    */

