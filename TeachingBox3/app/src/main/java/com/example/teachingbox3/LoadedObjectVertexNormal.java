package com.example.teachingbox3;

import android.opengl.GLES20;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.TriangleIndexVertexArray;
import com.bulletphysics.extras.gimpact.GImpactMeshShape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by 孟东风 on {DATE}.
 */
public class LoadedObjectVertexNormal {

    int mProgram;

    private int vCount;

    FloatBuffer mVertexBuffer;//顶点坐标数据缓冲
    FloatBuffer mNormalBuffer;//顶点法向量数据缓冲

    String mVertexShader;//顶点着色器代码脚本
    String mFragmentShader;//片元着色器代码脚本

    int muMVPMatrixHandle;//总变换矩阵引用
    int muMMatrixHandle;//位置旋转变换矩阵
    int maPositionHandle;//顶点位置属性引用
    int maNormalHandle;//顶点法向量属性引用
    int maLightLocationHandle;//光源位置属性引用
    int maCameraHandle;//摄像机位置属性引用

    CollisionShape loadShape;

    float[] vertices;
    float[] normals;

    public LoadedObjectVertexNormal(int index,MySurfaceView mv,float[] vertices,float[] normals){
        //初始化顶点坐标与着色数据
        initVertexData(vertices,normals);
        //初始化shader
        initShader(index, mv);
        //复制过来的
        this.vertices=vertices;
        this.normals=normals;

        //顶点坐标数据的初始化================begin============================
        vCount=vertices.length/3;
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================

        //顶点法向量数据的初始化================begin============================

        ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length*4);
        nbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = nbb.asFloatBuffer();//转换为Float型缓冲

        mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点着色数据的初始化================end============================

        //为刚体的顶点分配缓冲
        ByteBuffer gVertices= ByteBuffer.allocateDirect(vCount*3*4).order(ByteOrder.nativeOrder());
        for(int i=0;i<vertices.length;i++)//将顶点数据添加到缓冲中
        {
            gVertices.putFloat(i*4,vertices[i]);
        }
        gVertices.position(0);//设置缓冲区起始位置

        //刚体索引缓冲
        ByteBuffer gIndices= ByteBuffer.allocateDirect(vCount*4).order(ByteOrder.nativeOrder());
        for(int i=0;i<vCount;i++)//将索引数据添加到缓冲中
        {
            gIndices.putInt(i);
        }
        gIndices.position(0);//设置缓冲区起始位置


        int vertStride = 4*3;//顶点数据间隔
        int indexStride = 4*3;//索引数据间隔
        TriangleIndexVertexArray indexVertexArrays=  //创建三角形索引顶点数组
                new TriangleIndexVertexArray
                        (
                                vCount/3,//三角形的个数
                                gIndices, //索引缓冲
                                indexStride,//索引间隔
                                vCount, //顶点个数
                                gVertices, //顶点缓冲
                                vertStride//顶点间隔
                        );
        //创建曲面形状
        GImpactMeshShape trimesh = new GImpactMeshShape(indexVertexArrays);
        trimesh.updateBound();
        loadShape =trimesh;//保存碰撞形状
    }

    //初始化顶点坐标和着色数据的方法
    public void initVertexData(float[] vertices, float[] normals) {

        //顶点坐标的初始化
        vCount = vertices.length / 3;

        //创建顶点坐标的数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为浮点型数据缓存
        mVertexBuffer.put(vertices);//向缓存区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓存区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题

        //顶点法向量数据的初始化
        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length * 4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mNormalBuffer = vbb.asFloatBuffer();//转换为浮点型数据缓存
        mNormalBuffer.put(normals);//向缓存区中放入顶点坐标数据
        mNormalBuffer.position(0);//设置缓存区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
    }

    //初始化shader
    public void initShader(int i, MySurfaceView mv) {
        //加载顶点着色器的脚本内容
        //frag是黑色  frag1是红色 frag2是地板颜色

        mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //加载片元着色器的脚本内容
        if (i == 1) {
            mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        } else if (i == 2) {
            mFragmentShader = ShaderUtil.loadFromAssetsFile("frag1.sh", mv.getResources());
        } else if (i == 3) {
            mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        } else if (i == 4) {
            mFragmentShader = ShaderUtil.loadFromAssetsFile("frag1.sh", mv.getResources());
        } else if (i == 5) {
            mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        } else if (i == 6) {
            mFragmentShader = ShaderUtil.loadFromAssetsFile("frag1.sh", mv.getResources());
        } else if (i == 9) {
            mFragmentShader = ShaderUtil.loadFromAssetsFile("frag2.sh", mv.getResources());
        }  else if (i == 10) {
            mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        } else if (i == 11) {
            mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
        }else if (i == 12) {
            mFragmentShader = ShaderUtil.loadFromAssetsFile("frag3.sh", mv.getResources());
        } else {
            mFragmentShader = ShaderUtil.loadFromAssetsFile("frag6.sh", mv.getResources());
        }

        //基于顶点着色器与片元着色器创建程序
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //获取程序中顶点位置属性引用
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用
        maNormalHandle= GLES20.glGetAttribLocation(mProgram, "aNormal");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
        //获取程序中光源位置引用
        maLightLocationHandle= GLES20.glGetUniformLocation(mProgram, "uLightLocation");
        //获取程序中摄像机位置引用
        maCameraHandle= GLES20.glGetUniformLocation(mProgram, "uCamera");

    }
    public  void drawSelf(){
        //制定使用某套着色器程序
        GLES20.glUseProgram(mProgram);
        //将最终变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle,1,false,MatrixState.getFinalMatrix(),0);
        //将位置，旋转位置传入着色器程序
        GLES20.glUniformMatrix4fv(muMMatrixHandle,1,false, MatrixState.getMMatrix(),0);
        //将光源位置传入
        GLES20.glUniform3fv(maLightLocationHandle,1,MatrixState.lightPositionFB);
        //将摄像机位置传入
        GLES20.glUniform3fv(maCameraHandle,1,MatrixState.cameraFB);
        //将顶点位置数据传入渲染管线
        GLES20.glVertexAttribPointer(
                maPositionHandle,
                3,
                GLES20.GL_FLOAT,
                false,
                3*4,
                mVertexBuffer);
        //将顶点法线向量数据传入渲染管线
        GLES20.glVertexAttribPointer(
                maNormalHandle,
                3,
                GLES20.GL_FLOAT,
                false,
                3*4,
                mNormalBuffer);
        //启用顶点位置、法向量数据
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maNormalHandle);

     //   GLES20.glEnableVertexAttribArray(maTexCoorHandle);
        //绑定纹理
      //  GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
      //  GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);

        //绘制加载的物体
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,vCount);
    }
}
