package com.example.forpublicuse;

import java.io.IOException;
import java.io.InputStream;

import com.example.entrance.ready;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;



//������Ϊ���ݽ����࣬���������������������,�������滻����ߵ�����

public class Constant 
{
	/* public static float UNIT_SIZE_X=1;
	 public static float UNIT_SIZE_Y=1;
	 public static float UNIT_SIZE_Z=1;	*/
	 public static float R=100;//��Ұ�İ뾶
	 public static float scaling=0.01f;//���ε����Ŵ�С
	 public static float Dscaling=16f;//��������Ŵ�С
	 
	 public static float Xscaling=1f;//����X�����Ŵ�С
	 public static float Yscaling=1f;//����Y�����Ŵ�С
	 public static float Zscaling=1f;//����Z���Ŵ�С
	 
//	  public static float scaling=1f;//���ε����Ŵ�С
//	 public static float Dscaling=40000f;//��������Ŵ�С
 
	 public static float Density=2;//��ֵ�ܶȣ�Խ��Խ�ؼ�
	 public static float UNIT_SIZE=5;
//	 public static float ratio;//��Ļ�Ŀ�߱��������������е��Ť�����Ǹ��ô������࣬û��ʲôӰ��
		//��������ķ���
		public static int initTexture(Resources r,int drawableId)//textureId
		{
			//��������ID
			int[] textures = new int[1];
			GLES20.glGenTextures
			(
					1,          //����������id������X
					textures,   //����id������
					0           //ƫ����
			);
			int textureId=textures[0];    
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);
	        //ͨ������������ͼƬ===============begin===================
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
	        //ʵ�ʼ�������
	        GLUtils.texImage2D
	        (
	        		GLES20.GL_TEXTURE_2D,   //�������ͣ���OpenGL ES�б���ΪGL10.GL_TEXTURE_2D
	        		0, 					  //����Ĳ�Σ�0��ʾ����ͼ��㣬�������Ϊֱ����ͼ
	        		bitmapTmp, 			  //����ͼ��
	        		0					  //����߿�ߴ�
	        );
	        bitmapTmp.recycle(); 		  //������سɹ����ͷ�ͼƬ
	        return textureId;
		}
		//��������ķ���
		public static int initTexture()//textureId
		{
			//��������ID
			int[] textures = new int[1];
			GLES20.glGenTextures
			(
					1,          //����������id������
					textures,   //����id������
					0           //ƫ����
			);
			int textureId=textures[0];    
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);
	        //ͨ������������ͼƬ===============begin===================
	        Bitmap bitmapTmp;
	        bitmapTmp=ready.bmp;
	        //ʵ�ʼ�������
	        GLUtils.texImage2D
	        (
	        		GLES20.GL_TEXTURE_2D,   //�������ͣ���OpenGL ES�б���ΪGL10.GL_TEXTURE_2D
	        		0, 					  //����Ĳ�Σ�0��ʾ����ͼ��㣬�������Ϊֱ����ͼ
	        		bitmapTmp, 			  //����ͼ��
	        		0					  //����߿�ߴ�
	        );
	        bitmapTmp.recycle(); 		  //������سɹ����ͷ�ͼƬ
	        return textureId;
		}

	
	}






