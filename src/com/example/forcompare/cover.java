package com.example.forcompare;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

import com.example.forpublicuse.Constant;
import com.example.forpublicuse.MatrixState;

public class cover {
	int mProgram;//自定义渲染管线着色器程序id
    int muMVPMatrixHandle;//总变换矩阵引用
    int maPositionHandle; //顶点位置属性引用  
    int maColorHandle; //顶点颜色属性引用 
    String mVertexShader;//顶点着色器代码脚本  
    String mFragmentShader;//片元着色器代码脚本
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲
	FloatBuffer   mColorBuffer;//顶点着色数据缓冲
    int vCount=0;  
    public float vertices[]=new float[24];
    public float colors[]=new float[36];
    public cover(int program)
    {    	


    	
		this.mProgram=program;
    	//初始化顶点坐标与着色数据
    	initVertexData();
    	//初始化shader        
    	initShader();
    }
    
    //初始化顶点坐标与着色数据的方法
    public void initVertexData()
    {
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
       
    	//顶点颜色值数组，每个顶点4个色彩值RGBA

        //创建顶点着色数据缓冲
        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length*4);
        cbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mColorBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mColorBuffer.put(colors);//向缓冲区中放入顶点着色数据
        mColorBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点着色数据的初始化================end============================
    }
    //初始化shader
    public void initShader()
    {
        //获取程序中顶点位置属性引用id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //获取程序中顶点颜色属性引用id  
        maColorHandle= GLES20.glGetAttribLocation(mProgram, "aColor");
        //获取程序中总变换矩阵引用id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix"); 
    }
    
    public void drawSelf()
    {        
    	 //制定使用某套shader程序
    	 GLES20.glUseProgram(mProgram);
         //将最终变换矩阵传入shader程序
         GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
         //为画笔指定顶点位置数据
         GLES20.glVertexAttribPointer  
         (
         		maPositionHandle,   
         		3, 
         		GLES20.GL_FLOAT, 
         		false,
                3*4,   
                mVertexBuffer
         );       
         //为画笔指定顶点着色数据
         GLES20.glVertexAttribPointer  
         (
        		maColorHandle, 
         		4, 
         		GLES20.GL_FLOAT, 
         		false,
                4*4,   
                mColorBuffer
         );   
         GLES20.glLineWidth(3);
         //允许顶点位置数据数组
         GLES20.glEnableVertexAttribArray(maPositionHandle);  
         GLES20.glEnableVertexAttribArray(maColorHandle);  
         //绘制立方体         
         GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0, vCount); 
    }



}
