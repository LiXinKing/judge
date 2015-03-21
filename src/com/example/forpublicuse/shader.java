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
	static String[]mVertexShader=new String[shaderName.length];//������ɫ���ַ�������
	static String[]mFragmentShader=new String[shaderName.length];//ƬԪ��ɫ���ַ�������
	static int[] program=new int[shaderName.length];//��������
	//����shader�ַ���
	public static void loadCodeFromFile(Resources r)
	{
		for(int i=0;i<shaderName.length;i++)
		{
			//���ض�����ɫ���Ľű�����       
	        mVertexShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][0],r);
	        //����ƬԪ��ɫ���Ľű����� 
	        mFragmentShader[i]=ShaderUtil.loadFromAssetsFile(shaderName[i][1], r);
		}	
	}
	//����shader
	public static void compileShader()
	{
		for(int i=0;i<shaderName.length;i++)
		{
			program[i]=ShaderUtil.createProgram(mVertexShader[i], mFragmentShader[i]);
		}
	}
	//���ﷵ��������shader
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
