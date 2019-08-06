package com.example.teachingbox3;

import android.content.res.Resources;

/**
 * Created by 孟东风 on {DATE}.
 */
public class ShaderManager {
    final static int shaderCount=2;
    final static String[][] shaderName=
            {
                    {"vertex.sh","frag.sh"},
                    {"vertex.sh","frag2.sh"},
                    {"vertex.sh","frag3.sh"},
                    {"vertex.sh","frag4.sh"},
                    {"vertex.sh","frag5.sh"},
                    {"vertex.sh","frag6.sh"},
                    {"vertex.sh","frag7.sh"},
            };
    static String[]mVertexShader=new String[shaderCount];
    static String[]mFragmentShader=new String[shaderCount];
    static int[] program=new int[shaderCount];

    public static void loadCodeFromFile(Resources r)
    {
        for(int i=0;i<shaderCount;i++)
        {
            //加载顶点着色器的脚本内容
            mVertexShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][0],r);
            //加载片元着色器的脚本内容
            mFragmentShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][1], r);
        }
    }
    //编译3D物体的shader
    public static void compileShader()
    {
        for(int i=0;i<shaderCount;i++)
        {
            program[i]=ShaderUtil.createProgram(mVertexShader[i], mFragmentShader[i]);
            System.out.println("mProgram "+program[i]);
        }
    }
    //这里返回的是纹理带光照的shader程序
    public static int getTextureLightShaderProgram()
    {
        return program[0];
    }
    //这里返回的是颜色的shader程序
    public static int getColorShaderProgram()
    {
        return program[1];
    }
}
