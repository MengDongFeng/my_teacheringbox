package com.example.teachingbox3;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import static com.example.teachingbox3.Constant.*;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * Created by 孟东风 on {DATE}.
 */
public class MySurfaceView extends GLSurfaceView {

    DiscreteDynamicsWorld dynamicsWorld;  //物理世界
    CollisionShape boxShape;    //这是个抽象类，碰撞体积
    private SceneRenderer mRenderer;  //这是个继承类，场景渲染器

    LoadedObjectVertexNormal[] lovoa = new LoadedObjectVertexNormal[15];

    ArrayList<LoadRigidBody> tca = new ArrayList<LoadRigidBody>();  //显示队列

    public static RigidBody body1, body2, body3, body4, body5, body6, body7, body8, body9, body10, body11;

    float index_euler[] = {0, 0, 0, 0, 0, 0, 0};

    Transform trans_1 = new Transform(), trans_2 = new Transform(), trans_3 = new Transform(), trans_4 = new Transform(), trans_5 = new Transform(), trans_6 = new Transform(), trans_7 = new Transform();

    float cc1, cc2, cc3, cc4, cc5, cc6, cc7, ss1, ss2, ss3, ss4, ss5, ss6, ss7;

    float euler_1[] = new float[3], euler_2[] = new float[3], euler_3[] = new float[3], euler_4[] = new float[3], euler_5[] = new float[3], euler_6[] = new float[3], euler_7[] = new float[3];


    public MySurfaceView(Context context) {     //继承GLSurfaceView的构造器
        super(context);
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();    //创建场景渲染器
        setRenderer(mRenderer);                //设置渲染器
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染
        initWorld();     //三维模型显示界面的初始化
        trans_1.setIdentity();
        trans_2.setIdentity();
        trans_3.setIdentity();
        trans_4.setIdentity();
        trans_5.setIdentity();
        trans_6.setIdentity();
        trans_7.setIdentity();//初始化变换
    }

    public void initWorld() {    //三维模型显示界面的初始化
        //初始化这个世界
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        Dispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000); //界面显示范围，超出此范围内的零件不显示
        Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
        int maxProxies = 30;   //任何时候的最大刚体数
        //该broadphase是一个极好的空间以消除不应碰撞的成队物体. 这是为了提高运行效率.
        //
        //您可以使用碰撞调度注册一个回调，过滤器重置broadphase代理，使碰撞系统不处理系统的其它无用部分
        AxisSweep3 broadphase = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
        //我们还需要一个"solver". 这是什么原因导致物体进行互动得当，考虑到重力，游戏逻辑等的影响，碰撞，会被制约。
        //它工作的很好，只要你不把它推向极端，对于在任何高性能仿真都有瓶颈有一些相似的可以线程模型：
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        boxShape = new BoxShape(new Vector3f(1f, 0, 0));
        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration); //创建物理世界
        dynamicsWorld.setGravity(new Vector3f(0, 0, 0));  //不设重力
    }

    private class SceneRenderer implements GLSurfaceView.Renderer {

        @Override
        public void onDrawFrame(GL10 gl) {
            gl.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);//清除深度缓冲与颜色缓冲

            MatrixState.setLightLocation(cx, cy, cz);    //初始化光源位置
            motion_calculate();
            //ondrawframe类每20mm刷新一次，在此处对模型的运动姿态进行调整
            //上面的motion对每一个关节的当前位置与目标位置进行对比，并对下一步动作进行规划。
            //运动时每一次最多只前进或后退一度，保证了动画的连贯性。
            //规划之后分别得到不同刚体的欧拉角，通过下面的程序赋给相对应的刚体。
            /*
            //赋姿态角
            MatrixUtil.setEulerZYX(trans_1.basis, euler_1[0], euler_1[1], euler_1[2]);
            MatrixUtil.setEulerZYX(trans_2.basis, euler_2[2], euler_2[1], euler_2[0]);
            MatrixUtil.setEulerZYX(trans_3.basis, euler_3[2], euler_3[1], euler_3[0]);
            MatrixUtil.setEulerZYX(trans_4.basis, euler_4[2], euler_4[1], euler_4[0]);
            MatrixUtil.setEulerZYX(trans_5.basis, euler_5[2], euler_5[1], euler_5[0]);
            MatrixUtil.setEulerZYX(trans_6.basis, euler_6[2], euler_6[1], euler_6[0]);
            MatrixUtil.setEulerZYX(trans_7.basis, euler_6[2], euler_6[1], euler_6[0]);
            */

            MatrixUtil.setEulerZYX(trans_1.basis, euler_1[0], euler_1[1], euler_1[2]);
            MatrixUtil.setEulerZYX(trans_2.basis, euler_2[2], euler_2[1], euler_2[0]);
            MatrixUtil.setEulerZYX(trans_3.basis, euler_3[2], euler_3[1], euler_3[0]);
            MatrixUtil.setEulerZYX(trans_4.basis, euler_4[2], euler_4[1], euler_4[0]);
            MatrixUtil.setEulerZYX(trans_5.basis, euler_5[2], euler_5[1], euler_5[0]);
            MatrixUtil.setEulerZYX(trans_6.basis, euler_6[2], euler_6[1], euler_6[0]);
            MatrixUtil.setEulerZYX(trans_7.basis, euler_7[2], euler_7[1], euler_7[0]);

            //赋位置
            //OPENGL坐标系 右手定则，向右X正，向上Y正，向外Z正
            trans_1.origin.set(new Vector3f(0f, 0f, 0f));
            trans_2.origin.set(new Vector3f(0f, 0f, 0f));
            trans_3.origin.set(new Vector3f((int) (-(450 * cc1 * ss2)), (int) (450 * cc2), (int) (450 * ss1 * ss2)));
            trans_4.origin.set(new Vector3f((int) (-(450 * cc1 * ss2)), (int) (450 * cc2), (int) (450 * ss1 * ss2)));
            trans_5.origin.set(new Vector3f((int) (-450 * cc1 * ss2 - 440 * cc4 * (ss1 * ss3 - cc1 * cc2 * cc3) - 440 * cc1 * ss2 * ss4), (int) (450 * cc2 + 440 * cc2 * ss4 + 440 * cc3 * cc4 * ss2), (int) (450 * ss1 * ss2 - 440 * cc4 * (cc1 * ss3 + cc2 * cc3 * ss1) + 440 * ss1 * ss2 * ss4)));
            trans_6.origin.set(new Vector3f((int) (-450 * cc1 * ss2 - 440 * cc4 * (ss1 * ss3 - cc1 * cc2 * cc3) - 440 * cc1 * ss2 * ss4), (int) (450 * cc2 + 440 * cc2 * ss4 + 440 * cc3 * cc4 * ss2), (int) (450 * ss1 * ss2 - 440 * cc4 * (cc1 * ss3 + cc2 * cc3 * ss1) + 440 * ss1 * ss2 * ss4)));
            trans_7.origin.set(new Vector3f((int) (-450 * cc1 * ss2 - 72 * ss6 * (cc4 * (ss1 * ss3 - cc1 * cc2 * cc3) + cc1 * ss2 * ss4) - 72 * cc6 * (ss5 * (cc3 * ss1 + cc1 * cc2 * ss3) + cc5 * (ss4 * (ss1 * ss3 - cc1 * cc2 * cc3) - cc1 * cc4 * ss2)) - 440 * cc4 * (ss1 * ss3 - cc1 * cc2 * cc3) - 440 * cc1 * ss2 * ss4), (int) (450 * cc2 + 72 * ss6 * (cc2 * ss4 + cc3 * cc4 * ss2) + 440 * cc2 * ss4 - 72 * cc6 * (cc5 * (cc2 * cc4 - cc3 * ss2 * ss4) + ss2 * ss3 * ss5) + 440 * cc3 * cc4 * ss2), (int) (450 * ss1 * ss2 - 72 * ss6 * (cc4 * (cc1 * ss3 + cc2 * cc3 * ss1) - ss1 * ss2 * ss4) - 440 * cc4 * (cc1 * ss3 + cc2 * cc3 * ss1) - 72 * cc6 * (ss5 * (cc1 * cc3 - cc2 * ss1 * ss3) + cc5 * (ss4 * (cc1 * ss3 + cc2 * cc3 * ss1) + cc4 * ss1 * ss2)) + 440 * ss1 * ss2 * ss4)));

            //将上面的转换附加到物理世界
            body1.setWorldTransform(trans_1);body1.proceedToTransform(trans_1);
            body2.setWorldTransform(trans_2);body2.proceedToTransform(trans_2);
            body3.setWorldTransform(trans_3);body3.proceedToTransform(trans_3);
            body4.setWorldTransform(trans_4);body4.proceedToTransform(trans_4);
            body5.setWorldTransform(trans_5);body5.proceedToTransform(trans_5);
            body6.setWorldTransform(trans_6);body6.proceedToTransform(trans_6);
            body7.setWorldTransform(trans_6);body7.proceedToTransform(trans_6);
            body8.setWorldTransform(trans_7);body8.proceedToTransform(trans_7);

            //    body9.setWorldTransform(trans_3);
            //    body9.proceedToTransform(trans_3);
            //    body10.setWorldTransform(trans_4);
            //   body10.proceedToTransform(trans_4);
            //   body11.setWorldTransform(trans_4);
            //   body11.proceedToTransform(trans_4);

            //ondrawframe类能够自动刷新，把摄像机和光源放置在在此，可以对其参数进行动态调节
            cx = (float) (View_radius * sin(a_horizontal));
            cy = View_radius + 400;
            cz = (float) (View_radius * cos(a_horizontal));
            orientation_x = 0;
            orientation_y = 1;
            orientation_z = 0;
            MatrixState.setCamera(cx, cy, cz, centre_x, centre_y, centre_z, orientation_x, orientation_y, orientation_z);

            dynamicsWorld.stepSimulation(1f / 30.f, 1); //模拟运动
            synchronized (tca) {
                //显示所有零件
                for (LoadRigidBody lovo : tca) {
                    MatrixState.pushMatrix();
                    lovo.drawSelf();
                    MatrixState.popMatrix();
                }
            }

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置
            GLES20.glViewport(0, 0, width, height);
            //计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            //setCamera前三个数表示眼睛的位置，中间三个表示注视焦点，后三个表示头顶朝向（向量）
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 2.5f, 36000f);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(1f, 1f, 1f, 1);   //设置屏幕背景色RGBA
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);     //启用深度检测
            GLES20.glEnable(GLES20.GL_CULL_FACE);    //启用背面剪裁
            MatrixState.setInitStack();//初始化变换矩阵

            //加载要绘制的物体分别为底座，关节一，关节2，关节3，关节4，关节5，关节6_1,关节6_2，关节7

            lovoa[0] = LoadUtil.loadFromFile(1, "0.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            lovoa[1] = LoadUtil.loadFromFile(2, "1.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            lovoa[2] = LoadUtil.loadFromFile(3, "2.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            lovoa[3] = LoadUtil.loadFromFile(4, "3.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            lovoa[4] = LoadUtil.loadFromFile(5, "4.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            lovoa[5] = LoadUtil.loadFromFile(6, "5.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            lovoa[6] = LoadUtil.loadFromFile(7, "6_1.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            lovoa[7] = LoadUtil.loadFromFile(8, "6_2.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            lovoa[8] = LoadUtil.loadFromFile(9, "7.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            //    lovoa[9] = LoadUtil.loadFromFile(10, "dimian.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            /*
            lovoa[7] = LoadUtil.loadFromFile(8, "88.obj", MySurfaceView.this.getResources(), MySurfaceView.this);   //夹手
            lovoa[8] = LoadUtil.loadFromFile(9, "dimian.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            lovoa[9] = LoadUtil.loadFromFile(10, "4_blackpart.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            lovoa[10] = LoadUtil.loadFromFile(11, "5_blackpart.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            lovoa[11] = LoadUtil.loadFromFile(12, "5_wenzi.obj", MySurfaceView.this.getResources(), MySurfaceView.this);
            */
            //将加载的物体转换成刚体
            LoadRigidBody part0 = new LoadRigidBody(ShaderManager.getColorShaderProgram(), 1, lovoa[0], 0, 0, 0, dynamicsWorld);//底座
            LoadRigidBody part1 = new LoadRigidBody(ShaderManager.getColorShaderProgram(), 1, lovoa[1], 0, 0, 0, dynamicsWorld);//关节一
            LoadRigidBody part2 = new LoadRigidBody(ShaderManager.getColorShaderProgram(), 1, lovoa[2], 0, 0, 0, dynamicsWorld);//关节二
            LoadRigidBody part3 = new LoadRigidBody(ShaderManager.getColorShaderProgram(), 1, lovoa[3], 0, 0, 0, dynamicsWorld);//关节三红
            LoadRigidBody part4 = new LoadRigidBody(ShaderManager.getColorShaderProgram(), 1, lovoa[4], 0, 0, 0, dynamicsWorld);//关节四红
            LoadRigidBody part5 = new LoadRigidBody(ShaderManager.getColorShaderProgram(), 1, lovoa[5], 0, 0, 0, dynamicsWorld);
            LoadRigidBody part6 = new LoadRigidBody(ShaderManager.getColorShaderProgram(), 1, lovoa[6], 0, 0, 0, dynamicsWorld);
            LoadRigidBody part7 = new LoadRigidBody(ShaderManager.getColorShaderProgram(), 1, lovoa[7], 0, 0, 0, dynamicsWorld);//part7为夹手
            LoadRigidBody part8 = new LoadRigidBody(ShaderManager.getColorShaderProgram(), 1, lovoa[8], 0, 0, 0, dynamicsWorld);
            //       LoadRigidBody part9 = new LoadRigidBody(ShaderManager.getColorShaderProgram(), 1, lovoa[9], 0, 0, 0, dynamicsWorld);
            /*
            LoadRigidBody part8 = new LoadRigidBody(ShaderManager.getColorShaderProgram(), 1, lovoa[8], 0, 0, 0, dynamicsWorld);
            LoadRigidBody part9 = new LoadRigidBody(ShaderManager.getColorShaderProgram(), 1, lovoa[9], 0, 0, 0, dynamicsWorld);
            LoadRigidBody part10 = new LoadRigidBody(ShaderManager.getColorShaderProgram(), 1, lovoa[10], 0, 0, 0, dynamicsWorld);
            LoadRigidBody part11 = new LoadRigidBody(ShaderManager.getColorShaderProgram(), 1, lovoa[11], 0, 0, 0, dynamicsWorld); //华数机器人 文字Logo
            */

            //添加到显示队列里
            tca.add(part0);
            tca.add(part1);
            tca.add(part2);
            tca.add(part3);  //关节三红
            tca.add(part4);
            tca.add(part5);
            tca.add(part6);
            tca.add(part7);
            tca.add(part8);

            //        tca.add(part9);
            /*

            tca.add(part10);
            tca.add(part11);

            switch (jiaju_index) {
                case 0:
                    break;
                case 1:
                    tca.add(part7);    //为1时添加夹手
                    break;
            }
            */

            body1 = part1.body;
            body2 = part2.body;
            body3 = part3.body;
            body4 = part4.body;
            body5 = part5.body;
            body6 = part6.body;//6_1
            body7 = part7.body;//6_2
            body8 = part8.body;
            //       body9 = part9.body;
            /*
            body8 = part8.body;

            body10 = part10.body;
            body11 = part11.body;
            */

        }
    }

    //motion_1到motion_7分别控制7个关节
    public void motion_calculate() {
        //刚体的姿态转换矩阵，ZYX
        float trans01[][] = new float[3][3], trans02[][] = new float[3][3], trans03[][] = new float[3][3], trans04[][] = new float[3][3], trans05[][] = new float[3][3], trans06[][] = new float[3][3], trans07[][] = new float[3][3];

        //各个关节是否到达目标位置
        for (int i = 0; i < 7; i++)
        //    for(int i=0; i<6; i++)
        {
            joint_target[i] = (int) (joint_target[i]);
            if (joint_target[i] != joint_current[i]) {
                if (joint_target[i] - joint_current[i] > 1) {
                    joint_current[i] += 1;
                } else if (joint_target[i] - joint_current[i] < -1) {
                    joint_current[i] -= 1;
                } else {
                    joint_current[i] = joint_target[i];
                }
                // break;
            }
        }
        //计算各关节sin，cos值，调正方向的原因是根据坐标系的右手系来判断哪个是正方向
        //[-45; 45; 0; 45; 0; -45; 0];
        //暂时未调整方向
        // cc1 = (float)(cos((joint_current[0]+45) * PI/180));      ss1 = (float)(sin((joint_current[0]+45) * PI / 180));
        cc1 = (float) (cos((joint_current[0]) * PI / 180));ss1 = (float) (sin((joint_current[0]) * PI / 180));
        cc2 = (float) (cos(-(joint_current[1]+90) * PI / 180));ss2= (float) (sin(-(joint_current[1]+90) * PI / 180));
        cc3= (float) (cos((joint_current[2]) * PI / 180));ss3 = (float) (sin((joint_current[2]) * PI / 180));
        cc4 = (float) (cos(-(joint_current[3]) * PI / 180));ss4 = (float) (sin(-(joint_current[3]) * PI / 180));
        cc5 = (float) (cos((joint_current[4]) * PI / 180));ss5 = (float) (sin((joint_current[4]) * PI / 180));
        cc6 = (float) (cos((joint_current[5]) * PI / 180));ss6 = (float) (sin((joint_current[5]) * PI / 180));
        cc7 = (float) (cos((joint_current[6]) * PI / 180));ss7 = (float) (sin((joint_current[6]) * PI / 180));

        /*
        cc1 = (float)(cos(joint_current[0] * PI/180));      ss1 = (float)(sin(joint_current[0] * PI / 180));
        cc2 = (float)(cos(-(joint_current[1]+90)*PI / 180));   ss2 = (float)(sin(-(joint_current[1]+90) * PI / 180));//g2前面加负号是为了调正方向
        cc3 = (float)(cos(-(joint_current[2]-180)*PI / 180));      ss3 = (float)(sin(-(joint_current[2]-180) * PI / 180)); //g3前面加负号是为了调正方向
        cc4 = (float)(cos(joint_current[3] * PI/180)); ss4 = (float)(sin(joint_current[3] * PI / 180));
        cc5 = (float)(cos(-joint_current[4] * PI/180));     ss5 = (float)(sin(-joint_current[4] * PI / 180));//g5前面加负号是为了调正方向
        cc6 = (float)(cos(joint_current[5] * PI/180)); ss6 = (float)(sin(joint_current[5] * PI / 180));
         */

        //机械手变换矩阵
        trans01[0][0] = cc1;trans01[0][1] = 0f;trans01[0][2] = ss1;
        trans01[1][0] = 0f;trans01[1][1] = 1f;trans01[1][2] = 0f;
        trans01[2][0] = -ss1;trans01[2][1] = 0f;trans01[2][2] = cc1;

        trans02[0][0] = cc1 * cc2;trans02[0][1] = -cc1 * ss2;trans02[0][2] = ss1;
        trans02[1][0] = ss2;trans02[1][1] = cc2;trans02[1][2] = 0f;
        trans02[2][0] = -cc2 * ss1;trans02[2][1] = ss1 * ss2;trans02[2][2] = cc1;

        trans03[0][0] = cc1 * cc2 * cc3 - ss1 * ss3;
        trans03[0][1] = -cc1 * ss2;
        trans03[0][2] = cc3 * ss1 + cc1 * cc2 * ss3;
        trans03[1][0] = cc3 * ss2;
        trans03[1][1] = cc2;
        trans03[1][2] = ss2 * ss3;
        trans03[2][0] = -cc1 * ss3 - cc2 * cc3 * ss1;
        trans03[2][1] = ss1 * ss2;
        trans03[2][2] = cc1 * cc3 - cc2 * ss1 * ss3;

        trans04[0][0] = -cc4 * (ss1 * ss3 - cc1 * cc2 * cc3) - cc1 * ss2 * ss4;
        trans04[0][1] = ss4 * (ss1 * ss3 - cc1 * cc2 * cc3) - cc1 * cc4 * ss2;
        trans04[0][2] = cc3 * ss1 + cc1 * cc2 * ss3;
        trans04[1][0] = cc2 * ss4 + cc3 * cc4 * ss2;
        trans04[1][1] = cc2 * cc4 - cc3 * ss2 * ss4;
        trans04[1][2] = ss2 * ss3;
        trans04[2][0] = ss1 * ss2 * ss4 - cc4 * (cc1 * ss3 + cc2 * cc3 * ss1);
        trans04[2][1] = ss4 * (cc1 * ss3 + cc2 * cc3 * ss1) + cc4 * ss1 * ss2;
        trans04[2][2] = cc1 * cc3 - cc2 * ss1 * ss3;

        trans05[0][0] = -cc4 * (ss1 * ss3 - cc1 * cc2 * cc3) - cc1 * ss2 * ss4;
        trans05[0][1] = ss5 * (cc3 * ss1 + cc1 * cc2 * ss3) + cc5 * (ss4 * (ss1 * ss3 - cc1 * cc2 * cc3) - cc1 * cc4 * ss2);
        trans05[0][2] = cc5 * (cc3 * ss1 + cc1 * cc2 * ss3) - ss5 * (ss4 * (ss1 * ss3 - cc1 * cc2 * cc3) - cc1 * cc4 * ss2);
        trans05[1][0] = cc2 * ss4 + cc3 * cc4 * ss2;
        trans05[1][1] = cc5 * (cc2 * cc4 - cc3 * ss2 * ss4) + ss2 * ss3 * ss5;
        trans05[1][2] = cc5 * ss2 * ss3 - ss5 * (cc2 * cc4 - cc3 * ss2 * ss4);
        trans05[2][0] = ss1 * ss2 * ss4 - cc4 * (cc1 * ss3 + cc2 * cc3 * ss1);
        trans05[2][1] = ss5 * (cc1 * cc3 - cc2 * ss1 * ss3) + cc5 * (ss4 * (cc1 * ss3 + cc2 * cc3 * ss1) + cc4 * ss1 * ss2);
        trans05[2][2] = cc5 * (cc1 * cc3 - cc2 * ss1 * ss3) - ss5 * (ss4 * (cc1 * ss3 + cc2 * cc3 * ss1) + cc4 * ss1 * ss2);

        trans06[0][0] = ss6 * (ss5 * (cc3 * ss1 + cc1 * cc2 * ss3) + cc5 * (ss4 * (ss1 * ss3 - cc1 * cc2 * cc3) - cc1 * cc4 * ss2)) - cc6 * (cc4 * (ss1 * ss3 - cc1 * cc2 * cc3) + cc1 * ss2 * ss4);
        trans06[0][1] = ss6 * (cc4 * (ss1 * ss3 - cc1 * cc2 * cc3) + cc1 * ss2 * ss4) + cc6 * (ss5 * (cc3 * ss1 + cc1 * cc2 * ss3) + cc5 * (ss4 * (ss1 * ss3 - cc1 * cc2 * cc3) - cc1 * cc4 * ss2));
        trans06[0][2] = cc5 * (cc3 * ss1 + cc1 * cc2 * ss3) - ss5 * (ss4 * (ss1 * ss3 - cc1 * cc2 * cc3) - cc1 * cc4 * ss2);
        trans06[1][0] = ss6 * (cc5 * (cc2 * cc4 - cc3 * ss2 * ss4) + ss2 * ss3 * ss5) + cc6 * (cc2 * ss4 + cc3 * cc4 * ss2);
        trans06[1][1] = cc6 * (cc5 * (cc2 * cc4 - cc3 * ss2 * ss4) + ss2 * ss3 * ss5) - ss6 * (cc2 * ss4 + cc3 * cc4 * ss2);
        trans06[1][2] = cc5 * ss2 * ss3 - ss5 * (cc2 * cc4 - cc3 * ss2 * ss4);
        trans06[2][0] = ss6 * (ss5 * (cc1 * cc3 - cc2 * ss1 * ss3) + cc5 * (ss4 * (cc1 * ss3 + cc2 * cc3 * ss1) + cc4 * ss1 * ss2)) - cc6 * (cc4 * (cc1 * ss3 + cc2 * cc3 * ss1) - ss1 * ss2 * ss4);
        trans06[2][1] = ss6 * (cc4 * (cc1 * ss3 + cc2 * cc3 * ss1) - ss1 * ss2 * ss4) + cc6 * (ss5 * (cc1 * cc3 - cc2 * ss1 * ss3) + cc5 * (ss4 * (cc1 * ss3 + cc2 * cc3 * ss1) + cc4 * ss1 * ss2));
        trans06[2][2] = cc5 * (cc1 * cc3 - cc2 * ss1 * ss3) - ss5 * (ss4 * (cc1 * ss3 + cc2 * cc3 * ss1) + cc4 * ss1 * ss2);

        trans07[0][0] = ss7 * (ss5 * (ss4 * (ss1 * ss3 - cc1 * cc2 * cc3) - cc1 * cc4 * ss2) - cc5 * (cc3 * ss1 + cc1 * cc2 * ss3)) - cc7 * (cc6 * (cc4 * (ss1 * ss3 - cc1 * cc2 * cc3) + cc1 * ss2 * ss4) - ss6 * (ss5 * (cc3 * ss1 + cc1 * cc2 * ss3) + cc5 * (ss4 * (ss1 * ss3 - cc1 * cc2 * cc3) - cc1 * cc4 * ss2)));
        trans07[0][1] = ss6 * (cc4 * (ss1 * ss3 - cc1 * cc2 * cc3) + cc1 * ss2 * ss4) + cc6 * (ss5 * (cc3 * ss1 + cc1 * cc2 * ss3) + cc5 * (ss4 * (ss1 * ss3 - cc1 * cc2 * cc3) - cc1 * cc4 * ss2));
        trans07[0][2] = -cc7 * (ss5 * (ss4 * (ss1 * ss3 - cc1 * cc2 * cc3) - cc1 * cc4 * ss2) - cc5 * (cc3 * ss1 + cc1 * cc2 * ss3)) - ss7 * (cc6 * (cc4 * (ss1 * ss3 - cc1 * cc2 * cc3) + cc1 * ss2 * ss4) - ss6 * (ss5 * (cc3 * ss1 + cc1 * cc2 * ss3) + cc5 * (ss4 * (ss1 * ss3 - cc1 * cc2 * cc3) - cc1 * cc4 * ss2)));
        trans07[1][0] = ss7 * (ss5 * (cc2 * cc4 - cc3 * ss2 * ss4) - cc5 * ss2 * ss3) + cc7 * (ss6 * (cc5 * (cc2 * cc4 - cc3 * ss2 * ss4) + ss2 * ss3 * ss5) + cc6 * (cc2 * ss4 + cc3 * cc4 * ss2));
        trans07[1][1] = cc6 * (cc5 * (cc2 * cc4 - cc3 * ss2 * ss4) + ss2 * ss3 * ss5) - ss6 * (cc2 * ss4 + cc3 * cc4 * ss2);
        trans07[1][2] = ss7 * (ss6 * (cc5 * (cc2 * cc4 - cc3 * ss2 * ss4) + ss2 * ss3 * ss5) + cc6 * (cc2 * ss4 + cc3 * cc4 * ss2)) - cc7 * (ss5 * (cc2 * cc4 - cc3 * ss2 * ss4) - cc5 * ss2 * ss3);
        trans07[2][0] = ss7 * (ss5 * (ss4 * (cc1 * ss3 + cc2 * cc3 * ss1) + cc4 * ss1 * ss2) - cc5 * (cc1 * cc3 - cc2 * ss1 * ss3)) + cc7 * (ss6 * (ss5 * (cc1 * cc3 - cc2 * ss1 * ss3) + cc5 * (ss4 * (cc1 * ss3 + cc2 * cc3 * ss1) + cc4 * ss1 * ss2)) - cc6 * (cc4 * (cc1 * ss3 + cc2 * cc3 * ss1) - ss1 * ss2 * ss4));
        trans07[2][1] = ss6 * (cc4 * (cc1 * ss3 + cc2 * cc3 * ss1) - ss1 * ss2 * ss4) + cc6 * (ss5 * (cc1 * cc3 - cc2 * ss1 * ss3) + cc5 * (ss4 * (cc1 * ss3 + cc2 * cc3 * ss1) + cc4 * ss1 * ss2));
        trans07[2][2] = ss7 * (ss6 * (ss5 * (cc1 * cc3 - cc2 * ss1 * ss3) + cc5 * (ss4 * (cc1 * ss3 + cc2 * cc3 * ss1) + cc4 * ss1 * ss2)) - cc6 * (cc4 * (cc1 * ss3 + cc2 * cc3 * ss1) - ss1 * ss2 * ss4)) - cc7 * (ss5 * (ss4 * (cc1 * ss3 + cc2 * cc3 * ss1) + cc4 * ss1 * ss2) - cc5 * (cc1 * cc3 - cc2 * ss1 * ss3));

        //分别求解欧拉角
        euler_1 = EulerSolve(1, trans01);
        euler_2 = EulerSolve(2, trans02);
        euler_3 = EulerSolve(3, trans03);
        euler_4 = EulerSolve(4, trans04);
        euler_5 = EulerSolve(5, trans05);
        euler_6 = EulerSolve(6, trans06);
        euler_7 = EulerSolve(7, trans07);
    }


    //将矩阵转换成ZYX欧拉角
    public float[] EulerSolve(int i,float index[][]) {     //求角欧拉角
        float result[] = new float[3];
        if (index[2][2] == 0 & index[2][1] == 0) {  //B（绕Y轴转角）为90或-90的特殊情况处理
            if (index_euler[i - 1] < 0) {
                result[1] = -(float) (PI / 2);
                result[0] = 0f;
                result[2] = -(float) atan2(-index[0][1], index[1][1]);
            } else {
                result[1] = (float) PI / 2;
                result[0] = 0f;
                result[2] = (float) atan2(index[0][1], index[1][1]);
            }
            index_euler[i - 1] = result[1];         //赋值
        } else {
            result[1] = (float) (atan2(-index[2][0], sqrt(index[0][0] * index[0][0] + index[1][0] * index[1][0])));
            result[0] = (float) (atan2(index[1][0], index[0][0]));
            result[2] = (float) (atan2(index[2][1], index[2][2]));
            index_euler[i - 1] = result[1];
        }
        for (int j = 0; i < 3; i++) {
            // result[j]=(float) ((int)(result[j]*100)/100);
        }
        return result;
    }

    /*
    //将矩阵转换成ZYX欧拉角
    public float[] EulerSolve(int i, float index[][]) {     //求角欧拉角
        float result[] = new float[3];
        result[0] = (float) atan2(index[2][1], index[2][2]);
        result[1] = (float) (-atan2(index[2][0], sqrt(index[1][0] * index[1][0] + index[0][0] * index[0][0])));
        result[2] = (float) atan2(index[1][0], index[0][0]);
        index_euler[i - 1] = result[1];
         return result;
    }
    */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int xMove = (int) (event.getX()), fingerDis;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) { //两点触控，两指按下时，计算两指间距
                    lastFingerDis = distanceBetweenFingers(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() > 1) {//当有两个手指在屏幕上时，为缩放状态
                    fingerDis = distanceBetweenFingers(event);
                    View_radius = (int) (View_radius * lastFingerDis / fingerDis);
                    lastFingerDis = fingerDis;
                } else if (event.getPointerCount() == 1) {
                    moveDistanceX = xMove - lastxMove;
                    a_horizontal -= moveDistanceX / (sqrt(View_radius) * 2);
                }
                break;
        }
        lastxMove = xMove;
        return true;
    }

    private int distanceBetweenFingers(MotionEvent event) {
        int disX = (int) (abs(event.getX(0) - event.getX(1)));
        int disY = (int) (abs(event.getY(0) - event.getY(1)));
        return (int) (sqrt(disX * disX + disY * disY));
    }

}









