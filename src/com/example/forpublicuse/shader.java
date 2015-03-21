package com.example.forpublicuse;

import android.content.res.Resources;


public class shader {
	final static String[][] shaderName=
	{
		{"vertex1.sh","frag1.sh"},
		{"vertex.sh","frag.sh"},
		{"vertexforcompare.sh","fragforcompare.sh"},
		{"vertexcover.sh","fragcover.sh"}
	};
	static String[]mVertexShader=new String[shaderName.length];//顶点着色器字符串数组
	static String[]mFragmentShader=new String[shaderName.length];//片元着色器字符串数组
	static int[] program=new int[shaderName.length];//程序数组
	//加载shader字符串
	public static void loadCodeFromFile(Resources r)
	{
		for(int i=0;i<shaderName.length;i++)
		{
			//加载顶点着色器的脚本内容       
	        mVertexShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][0],r);
	        //加载片元着色器的脚本内容 
	        mFragmentShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][1], r);
		}	
	}
	//编译shader
	public static void compileShader()
	{
		for(int i=0;i<shaderName.length;i++)
		{
			program[i]=ShaderUtil.createProgram(mVertexShader[i], mFragmentShader[i]);
		}
	}
	//这里返回三角形shader
	public static int getTrangleShaderProgram()
	{
		return program[0];
	}
	public static int getRectangularShaderProgram()
	{
		return program[1];
	}
	public static int getcubeShaderProgram()
	{
		return program[2];
	}
	public static int getcoverShaderProgram()
	{
		return program[3];
	}
}
