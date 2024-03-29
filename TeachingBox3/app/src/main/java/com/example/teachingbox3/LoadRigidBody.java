package com.example.teachingbox3;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * Created by 孟东风 on {DATE}.
 */
public class LoadRigidBody {

    int mProgram;
    LoadedObjectVertexNormal lovo;
    RigidBody body;

    public LoadRigidBody(int mProgram, float mass, LoadedObjectVertexNormal lovo, float cx, float cy, float cz, DiscreteDynamicsWorld dynamicsWorld) {
        this.lovo = lovo;//保存加载物体对象索引
        this.mProgram = mProgram;//保存着色器程序索引
        CollisionShape colShape;
        colShape = lovo.loadShape;//保存碰撞形状
        boolean isDynamic = (mass != 0f);//物体是否可动
        Vector3f localInertia = new Vector3f(0f, 0f, 0f);//创建惯性向量
        if(isDynamic){
            colShape.calculateLocalInertia(mass,localInertia);//计算惯性
        }
        //刚体的移动设置
        Transform startTransform=new Transform();//创建刚体的初始变换对象
        startTransform.setIdentity();//初始化变换对象
        startTransform.origin.set(new Vector3f(0,0,0));//设置变换的初始位置
        //创建刚体的运动状态对象
        DefaultMotionState myMotionState=new DefaultMotionState(startTransform);
        //创建刚体信息对象
        RigidBodyConstructionInfo cInfo=new RigidBodyConstructionInfo(mass,myMotionState,colShape,localInertia);
        body=new RigidBody(cInfo);  //创建刚体
        body.setRestitution(1f);  //设置反弹系数
        body.setFriction(0.1f);  //设置摩擦系数
        //        body.setActivationState(DISABLE_DEACTIVATION);
        dynamicsWorld.addRigidBody(body);//将刚体添加进物理世界
        dynamicsWorld.addCollisionObject(body);
    }

    public void drawSelf(){
        //  lovo.initShader(mProgram);//初始化着色器
        MatrixState.pushMatrix();//保存现场
        //获取这个箱子的变换信息对象
        Transform trans=body.getMotionState().getWorldTransform(new Transform());
        body.setWorldTransform(trans);
        //进行移位变换
        MatrixState.translate(trans.origin.x,trans.origin.y,trans.origin.z);
        Quat4f ro=trans.getRotation(new Quat4f());//获取旋转的四元数对象
        if(ro.x!=0||ro.y!=0||ro.z!=0){
            float[] fa=SYSUtil.fromSYStoAXYZ(ro);//将四元数转换成AXYZ形式
            MatrixState.rotate(fa[0],fa[1],fa[2],fa[3]);//执行旋转
        }
        lovo.drawSelf();//调用加载物体对象的绘制方法
        MatrixState.popMatrix();//恢复现场
    }
}
