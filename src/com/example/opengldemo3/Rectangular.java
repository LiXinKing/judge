package com.example.opengldemo3;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.example.entrance.ready;
import com.example.forpublicuse.Constant;
import com.example.forpublicuse.MatrixState;
import com.example.touch.AABBBox;

import android.R.bool;
import android.R.integer;
import android.R.string;
import android.opengl.GLES20;
import android.util.Log;

public class Rectangular {
	int mProgram;// �Զ�����Ⱦ������ɫ������id
	int muMVPMatrixHandle;// �ܱ任��������
    int muMMatrixHandle;//λ�á���ת�任��������
	int maPositionHandle; // ����λ����������
    int maNormalHandle; //���㷨������������
    int maLightLocationHandle;//��Դλ����������
    int maCameraHandle; //�����λ���������� 
    int muLightDirectionHandle;//�����һ��������
    int maColorHandle; //������ɫ��������  
    
//    ArrayList<float[]> floatarray;
	FloatBuffer mColorBuffer;
	FloatBuffer mVertexBuffer;// �����������ݻ���
	FloatBuffer mNormalBuffer;//���㷨�������ݻ���
	int vCount = 0;
	float r =0.8f;
	static float UNIT_SIZE_X=0.05f;
	static float UNIT_SIZE_Y=0.05f;
	static float UNIT_SIZE_Z=0.05f;	
	 float x0=0,y0=0,z0=0;
	 float x1=0,y1=0,z1=0;
	 float x2=0,y2=0,z2=0;
	 float x3=0,y3=0,z3=0;
	 float x4=0,y4=0,z4=0;
	 float x5=0,y5=0,z5=0;
	 float x6=0,y6=0,z6=0;
	 float x7=0,y7=0,z7=0;

	 float i0=0,j0=0,k0=0;
	 float i1=0,j1=0,k1=0;
	 float i2=0,j2=0,k2=0;
	 float i3=0,j3=0,k3=0;
	 float i4=0,j4=0,k4=0;
	 float i5=0,j5=0,k5=0;
	 float i6=0,j6=0,k6=0;
	 float i7=0,j7=0,k7=0;
	 

	 float vertices[];
	 float verticescube[];
	 float[] colorArray;
    float[] m = new float[16];//����任�ľ���    
	AABBBox preBox;//����任֮ǰ�İ�Χ��
//	float size=1;
	boolean drawchoice=false; 

	public Rectangular(int program) {
//		floatarray=(ArrayList<float[]>) ready.floatcollectArray;	
		/*vertices=new float[]
		                   {                
					   0,0,0,0.8f,0.8f,0f,        	
		            	0.5f,0,0,1.3f,0.8f,0.8f,
		            	0,0.5f,0,0.8f,1.3f,0.8f,
		            	0,0,0.5f,0.8f,0.8f,1.3f,
		            	0.5f,0,0.5f,1.3f,0.8f,1.3f,
		            	0,0.5f,0.5f,0.8f,1.3f,1.3f,
		            	0.5f,0.5f,0,1.3f,1.3f,0.8f,
		            	0.5f,0.5f,0.5f,1.3f,1.3f,1.3f*/

												

		this.mProgram=program;
		// ��ʼ��������������ɫ����
		initVertexData();
		// ��ʼ��shader
		initShader();

	}

	// ��ʼ�������������ݵķ���
	public void initVertexData() {
		// �����������ݵĳ�ʼ��================begin============================
		verticescube=new float[]{
				i0,j0,k0,	i1,j1,k1,	i5,j5,k5,		
				i0,j0,k0,	i5,j5,k5,	i3,j3,k3,			
				
				i7,j7,k7,		i4,j4,k4,	i2,j2,k2,		 
				i6,j6,k6,		i7,j7,k7,	i2,j2,k2,	 
				
				i3,j3,k3,	i2,j2,k2,	i0,j0,k0,		 
				i3,j3,k3,	i6,j6,k6,	i2,j2,k2,		
				
				i1,j1,k1,	i4,j4,k4,	i5,j5,k5,			 
				i7,j7,k7,	i5,j5,k5,	i4,j4,k4,	//���򿴹�ȥ��ʱ��Ϊ��ʾ		
				
				i2,j2,k2,	i1,j1,k1,	i0,j0,k0,		 
				i4,j4,k4,	i1,j1,k1,	i2,j2,k2,		 
				
				i7,j7,k7,	i3,j3,k3,	i5,j5,k5,				  
				i6,j6,k6,	i3,j3,k3,	i7,j7,k7,			
				  };
		  if(drawchoice){
			  float ic0=i0;
			  float jc0=j0;
			  float kc0=k0;
			  
			  float ic1=i0+(i1-i0)*Constant.Yscaling;
			  float jc1=j0+(j1-j0)*Constant.Yscaling;
			  float kc1=k0+(k1-k0)*Constant.Yscaling;
			  
			  float ic2=i0+(i2-i0)*Constant.Zscaling;
			  float jc2=j0+(j2-j0)*Constant.Zscaling;
			  float kc2=k0+(k2-k0)*Constant.Zscaling;
			  
			  float ic3=i0+(i3-i0)*Constant.Xscaling;
			  float jc3=j0+(j3-j0)*Constant.Xscaling;
			  float kc3=k0+(k3-k0)*Constant.Xscaling;
			  
			  float ic4=(ic1-i0)+(ic2-i0)+i0;
			  float jc4=(jc1-j0)+(jc2-j0)+j0;
			  float kc4=(kc1-k0)+(kc2-k0)+k0;
			  
			  float ic5=(ic1-i0)+(ic3-i0)+i0;
			  float jc5=(jc1-j0)+(jc3-j0)+j0;
			  float kc5=(kc1-k0)+(kc3-k0)+k0;
			  
			  float ic6=(ic2-i0)+(ic3-i0)+i0;
			  float jc6=(jc2-j0)+(jc3-j0)+j0;
			  float kc6=(kc2-k0)+(kc3-k0)+k0;
			  
			  float ic7=(ic1-i0)+(ic2-i0)+(ic3-i0)+i0;
			  float jc7=(jc1-j0)+(jc2-j0)+(jc3-j0)+j0;
			  float kc7=(kc1-k0)+(kc2-k0)+(kc3-k0)+k0;
			  

			  float cubeChange[]=new float[]{
						ic0,jc0,kc0,	ic1,jc1,kc1,	ic5,jc5,kc5,		
						ic0,jc0,kc0,	ic5,jc5,kc5,	ic3,jc3,kc3,			
						
						ic7,jc7,kc7,		ic4,jc4,kc4,	ic2,jc2,kc2,		 
						ic6,jc6,kc6,		ic7,jc7,kc7,	ic2,jc2,kc2,	 
						
						ic3,jc3,kc3,	ic2,jc2,kc2,	ic0,jc0,kc0,		 
						ic3,jc3,kc3,	ic6,jc6,kc6,	ic2,jc2,kc2,		
						
						ic1,jc1,kc1,	ic4,jc4,kc4,	ic5,jc5,kc5,			 
						ic7,jc7,kc7,	ic5,jc5,kc5,	ic4,jc4,kc4,	//���򿴹�ȥ��ʱ��Ϊ��ʾ		
						
						ic2,jc2,kc2,	ic1,jc1,kc1,	ic0,jc0,kc0,		 
						ic4,jc4,kc4,	ic1,jc1,kc1,	ic2,jc2,kc2,		 
						
						ic7,jc7,kc7,	ic3,jc3,kc3,	ic5,jc5,kc5,				  
						ic6,jc6,kc6,	ic3,jc3,kc3,	ic7,jc7,kc7,			
						  };
		        vCount = cubeChange.length / 3;
				// ���������������ݻ���
				// vertices.length*4����Ϊһ�������ĸ��ֽ�
				ByteBuffer vbb = ByteBuffer.allocateDirect(cubeChange.length * 4);
				vbb.order(ByteOrder.nativeOrder());// �����ֽ�˳��
				mVertexBuffer = vbb.asFloatBuffer();// ת��Ϊint�ͻ���
				mVertexBuffer.put(cubeChange);// �򻺳����з��붥����������
				mVertexBuffer.position(0);// ���û�������ʼλ��

		  }
		  else{
		vertices=new float[]
{           i0,j0,k0,			 x0,y0,z0,        	
			i1,j1,k1,			 x1,y1,z1,						 
			
			i2,j2,k2,			 x2,y2,z2,
			i3,j3,k3,			 x3,y3,z3,

			i4,j4,k4,			 x4,y4,z4, 
			i5,j5,k5,			 x5,y5,z5,
			
			i6,j6,k6,			 x6,y6,z6,
			i7,j7,k7,			 x7,y7,z7,
			
/*			i0,j0,k0,			i1,j1,k1,
			i0,j0,k0,			i2,j2,k2,
			i0,j0,k0,			i3,j3,k3,*/
			
			};
        vCount = vertices.length / 3;
		// ���������������ݻ���
		// vertices.length*4����Ϊһ�������ĸ��ֽ�
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());// �����ֽ�˳��
		mVertexBuffer = vbb.asFloatBuffer();// ת��Ϊint�ͻ���
		mVertexBuffer.put(vertices);// �򻺳����з��붥����������
		mVertexBuffer.position(0);// ���û�������ʼλ��
		  }
    	//��ʼ����Χ��
    	preBox = new AABBBox(verticescube);

		// �ر���ʾ�����ڲ�ͬƽ̨�ֽ�˳��ͬ���ݵ�Ԫ�����ֽڵ�һ��Ҫ����ByteBuffer
		// ת�����ؼ���Ҫͨ��ByteOrder����nativeOrder()�������п��ܻ������
		if(drawchoice){
			float[] colorArraycube=new float[]{
					1,0,0,0,	1,0,0,0		,1,0,0,0,
					1,0,0,0,	1,0,0,0		,1,0,0,0,
					1,0,0,0,	1,0,0,0		,1,0,0,0,
					1,0,0,0,	1,0,0,0		,1,0,0,0,//��ɫ
					
					0,1,0,0,	0,1,0,0		,0,1,0,0,
					0,1,0,0,	0,1,0,0		,0,1,0,0,
					0,1,0,0,	0,1,0,0		,0,1,0,0,
					0,1,0,0,	0,1,0,0		,0,1,0,0,// ��ɫ
					
					0,0,1,0,	0,0,1,0		,0,0,1,0,
					0,0,1,0,	0,0,1,0		,0,0,1,0,
					0,0,1,0,	0,0,1,0		,0,0,1,0,
					0,0,1,0,	0,0,1,0		,0,0,1,0,//��ɫ
					
			};
			ByteBuffer cbb=ByteBuffer.allocateDirect(colorArraycube.length*4);
			cbb.order(ByteOrder.nativeOrder());	//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
			mColorBuffer=cbb.asFloatBuffer();
			mColorBuffer.put(colorArraycube);
			mColorBuffer.position(0);
		}
		else{
	 colorArray=new float[]{
				0,0,0,0,			0,0,0,0	,//��ɫ
				0,1,0,0,			0,1,0,0	,//��ɫ
				0,0,1,0,			0,0,1,0	,//��ɫ
				1,1,0,0,			1,1,0,0	,//��ɫ
				
				0,1,1,0,			0,1,1,0	,//��ɫ
				1,0,1,0,			1,0,1,0	,//�ۺ�ɫ
				1,0,0,0,			1,0,0,0	,//��ɫ
				0,0,0,0,			0,0,0,0	,//��ɫ
				
/*				0,0,0,0,			0,0,0,0	,
				0,0,0,0,			0,0,0,0	,
				0,0,0,0,			0,0,0,0	,*/
		};
		ByteBuffer cbb=ByteBuffer.allocateDirect(colorArray.length*4);
		cbb.order(ByteOrder.nativeOrder());	//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
		mColorBuffer=cbb.asFloatBuffer();
		mColorBuffer.put(colorArray);
		mColorBuffer.position(0);
		}
		

		 
	}

	// ��ʼ��shader
	public void initShader() {//��ʼ��initShader�ı仯������������ͳһ��shader��ȥ
		// ��ȡ�����ж���λ����������
		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		// ��ȡ�������ܱ任��������
		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //��ȡλ�á���ת�任��������
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");  
        //��ȡ�����ж�����ɫ��������id  
        maColorHandle= GLES20.glGetAttribLocation(mProgram, "aColor");
        //��ȡ�����������λ������
        maCameraHandle=GLES20.glGetUniformLocation(mProgram, "uCamera"); 

	}
    //���Ʊ任����
    public void copyM(){
    	for(int i=0;i<16;i++){
    		m[i]=MatrixState.getMMatrix()[i];
    	}
    }	
	//������ĵ�λ�úͳ���ߵķ���
    public AABBBox getCurrBox(){
    	return preBox.setToTransformedBox(m);//��ȡ�任��İ�Χ��
    
    }
    //���غ�Ķ�����������ҪҪ����Ӧ�Ķ�
	public void changeOnTouch(boolean flag){
		if (flag) {
			drawchoice=true;
		} else {
			drawchoice=false;
		}	
	}
	public void drawSelf() {
		
		copyM();
		// �ƶ�ʹ��ĳ����ɫ������
		GLES20.glUseProgram(mProgram);
		// �����ձ任��������ɫ������
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
				MatrixState.getFinalMatrix(), 0); 
        //��λ�á���ת�任��������ɫ������
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);

        //����Դλ�ô�����ɫ������   
       // GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
        //ע�⣬���������ѡ�����ö�����ջ��Ƕ�λ���գ�����ѡ���Ժ��ܰѲ��õĹ��շ�ʽ���뵽
        //��ɫ������ȥ
        //�������λ�ô�����ɫ������   
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
		// ������λ�����ݴ�����Ⱦ����
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
				false, 3 * 4, mVertexBuffer);
        //Ϊ����ָ��������ɫ����
        GLES20.glVertexAttribPointer  
        (
       		maColorHandle, 
        		4, 
        		GLES20.GL_FLOAT, 
        		false,
               4*4,   
               mColorBuffer
        );   
        if(drawchoice){
    		// ���ö���λ������
    		GLES20.glEnableVertexAttribArray(maPositionHandle); 
            GLES20.glEnableVertexAttribArray(maColorHandle);  //���ö�����ɫ����

    		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
        	
        }
        else
        {
        GLES20.glLineWidth(3);
		// ���ö���λ������
		GLES20.glEnableVertexAttribArray(maPositionHandle); 
        GLES20.glEnableVertexAttribArray(maColorHandle);  //���ö�����ɫ����

		GLES20.glDrawArrays(GLES20.GL_LINES, 0, vCount);}
	}


}