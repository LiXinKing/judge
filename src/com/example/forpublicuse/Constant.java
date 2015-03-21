package com.example.forpublicuse;

import java.io.IOException;
import java.io.InputStream;

import com.example.entrance.ready;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;



//此类作为数据接收类，传过来的数据在这里接收,在这里替换长宽高的数据

public class Constant 
{
	/* public static float UNIT_SIZE_X=1;
	 public static float UNIT_SIZE_Y=1;
	 public static float UNIT_SIZE_Z=1;	*/
	 public static float R=100;//视野的半径
	 public static float scaling=0.01f;//矩形的缩放大小
	 public static float Dscaling=16f;//距离的缩放大小
	 
	 public static float Xscaling=1f;//矩形X的缩放大小
	 public static float Yscaling=1f;//矩形Y的缩放大小
	 public static float Zscaling=1f;//矩形Z缩放大小
	 
//	  public static float scaling=1f;//矩形的缩放大小
//	 public static float Dscaling=40000f;//距离的缩放大小
 
	 public static float Density=2;//插值密度，越大越秘籍
	 public static float UNIT_SIZE=5;
//	 public static float ratio;//屏幕的宽高比例，放在这里有点别扭，但是复用次数不多，没有什么影响
		//加载纹理的方法
		public static int initTexture(Resources r,int drawableId)//textureId
		{
			//生成纹理ID
			int[] textures = new int[1];
			GLES20.glGenTextures
			(
					1,          //产生的纹理id的数量X
					textures,   //纹理id的数组
					0           //偏移量
			);
			int textureId=textures[0];    
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);
	        //通过输入流加载图片===============begin===================
	        InputStream is = r.openRawResource(drawableId);
	        Bitmap bitmapTmp;
	        try 
	        {
	        	bitmapTmp = BitmapFactory.decodeStream(is);
	        } 
	        finally 
	        {
	            try 
	            {
	                is.close();
	            } 
	            catch(IOException e) 
	            {
	                e.printStackTrace();
	            }
	        }
	        //实际加载纹理
	        GLUtils.texImage2D
	        (
	        		GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
	        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
	        		bitmapTmp, 			  //纹理图像
	        		0					  //纹理边框尺寸
	        );
	        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
	        return textureId;
		}
		//加载纹理的方法
		public static int initTexture()//textureId
		{
			//生成纹理ID
			int[] textures = new int[1];
			GLES20.glGenTextures
			(
					1,          //产生的纹理id的数量
					textures,   //纹理id的数组
					0           //偏移量
			);
			int textureId=textures[0];    
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);
	        //通过输入流加载图片===============begin===================
	        Bitmap bitmapTmp;
	        bitmapTmp=ready.bmp;
	        //实际加载纹理
	        GLUtils.texImage2D
	        (
	        		GLES20.GL_TEXTURE_2D,   //纹理类型，在OpenGL ES中必须为GL10.GL_TEXTURE_2D
	        		0, 					  //纹理的层次，0表示基本图像层，可以理解为直接贴图
	        		bitmapTmp, 			  //纹理图像
	        		0					  //纹理边框尺寸
	        );
	        bitmapTmp.recycle(); 		  //纹理加载成功后释放图片
	        return textureId;
		}

	
	}






