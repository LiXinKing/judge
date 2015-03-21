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
	int mProgram;// 自定义渲染管线着色器程序id
	int muMVPMatrixHandle;// 总变换矩阵引用
    int muMMatrixHandle;//位置、旋转变换矩阵引用
	int maPositionHandle; // 顶点位置属性引用
    int maNormalHandle; //顶点法向量属性引用
    int maLightLocationHandle;//光源位置属性引用
    int maCameraHandle; //摄像机位置属性引用 
    int muLightDirectionHandle;//定向光一致性引用
    int maColorHandle; //顶点颜色属性引用  
    
//    ArrayList<float[]> floatarray;
	FloatBuffer mColorBuffer;
	FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
	FloatBuffer mNormalBuffer;//顶点法向量数据缓冲
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
    float[] m = new float[16];//仿射变换的矩阵    
	AABBBox preBox;//仿射变换之前的包围盒
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
		// 初始化顶点坐标与着色数据
		initVertexData();
		// 初始化shader
		initShader();

	}

	// 初始化顶点坐标数据的方法
	public void initVertexData() {
		// 顶点坐标数据的初始化================begin============================
		verticescube=new float[]{
				i0,j0,k0,	i1,j1,k1,	i5,j5,k5,		
				i0,j0,k0,	i5,j5,k5,	i3,j3,k3,			
				
				i7,j7,k7,		i4,j4,k4,	i2,j2,k2,		 
				i6,j6,k6,		i7,j7,k7,	i2,j2,k2,	 
				
				i3,j3,k3,	i2,j2,k2,	i0,j0,k0,		 
				i3,j3,k3,	i6,j6,k6,	i2,j2,k2,		
				
				i1,j1,k1,	i4,j4,k4,	i5,j5,k5,			 
				i7,j7,k7,	i5,j5,k5,	i4,j4,k4,	//正向看过去逆时针为显示		
				
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
						ic7,jc7,kc7,	ic5,jc5,kc5,	ic4,jc4,kc4,	//正向看过去逆时针为显示		
						
						ic2,jc2,kc2,	ic1,jc1,kc1,	ic0,jc0,kc0,		 
						ic4,jc4,kc4,	ic1,jc1,kc1,	ic2,jc2,kc2,		 
						
						ic7,jc7,kc7,	ic3,jc3,kc3,	ic5,jc5,kc5,				  
						ic6,jc6,kc6,	ic3,jc3,kc3,	ic7,jc7,kc7,			
						  };
		        vCount = cubeChange.length / 3;
				// 创建顶点坐标数据缓冲
				// vertices.length*4是因为一个整数四个字节
				ByteBuffer vbb = ByteBuffer.allocateDirect(cubeChange.length * 4);
				vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
				mVertexBuffer = vbb.asFloatBuffer();// 转换为int型缓冲
				mVertexBuffer.put(cubeChange);// 向缓冲区中放入顶点坐标数据
				mVertexBuffer.position(0);// 设置缓冲区起始位置

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
		// 创建顶点坐标数据缓冲
		// vertices.length*4是因为一个整数四个字节
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mVertexBuffer = vbb.asFloatBuffer();// 转换为int型缓冲
		mVertexBuffer.put(vertices);// 向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);// 设置缓冲区起始位置
		  }
    	//初始化包围盒
    	preBox = new AABBBox(verticescube);

		// 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
		// 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
		if(drawchoice){
			float[] colorArraycube=new float[]{
					1,0,0,0,	1,0,0,0		,1,0,0,0,
					1,0,0,0,	1,0,0,0		,1,0,0,0,
					1,0,0,0,	1,0,0,0		,1,0,0,0,
					1,0,0,0,	1,0,0,0		,1,0,0,0,//红色
					
					0,1,0,0,	0,1,0,0		,0,1,0,0,
					0,1,0,0,	0,1,0,0		,0,1,0,0,
					0,1,0,0,	0,1,0,0		,0,1,0,0,
					0,1,0,0,	0,1,0,0		,0,1,0,0,// 绿色
					
					0,0,1,0,	0,0,1,0		,0,0,1,0,
					0,0,1,0,	0,0,1,0		,0,0,1,0,
					0,0,1,0,	0,0,1,0		,0,0,1,0,
					0,0,1,0,	0,0,1,0		,0,0,1,0,//蓝色
					
			};
			ByteBuffer cbb=ByteBuffer.allocateDirect(colorArraycube.length*4);
			cbb.order(ByteOrder.nativeOrder());	//设置字节顺序为本地操作系统顺序
			mColorBuffer=cbb.asFloatBuffer();
			mColorBuffer.put(colorArraycube);
			mColorBuffer.position(0);
		}
		else{
	 colorArray=new float[]{
				0,0,0,0,			0,0,0,0	,//黑色
				0,1,0,0,			0,1,0,0	,//绿色
				0,0,1,0,			0,0,1,0	,//蓝色
				1,1,0,0,			1,1,0,0	,//黄色
				
				0,1,1,0,			0,1,1,0	,//青色
				1,0,1,0,			1,0,1,0	,//粉红色
				1,0,0,0,			1,0,0,0	,//红色
				0,0,0,0,			0,0,0,0	,//黑色
				
/*				0,0,0,0,			0,0,0,0	,
				0,0,0,0,			0,0,0,0	,
				0,0,0,0,			0,0,0,0	,*/
		};
		ByteBuffer cbb=ByteBuffer.allocateDirect(colorArray.length*4);
		cbb.order(ByteOrder.nativeOrder());	//设置字节顺序为本地操作系统顺序
		mColorBuffer=cbb.asFloatBuffer();
		mColorBuffer.put(colorArray);
		mColorBuffer.position(0);
		}
		

		 
	}

	// 初始化shader
	public void initShader() {//初始化initShader的变化，将程序引用统一到shader中去
		// 获取程序中顶点位置属性引用
		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		// 获取程序中总变换矩阵引用
		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //获取位置、旋转变换矩阵引用
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");  
        //获取程序中顶点颜色属性引用id  
        maColorHandle= GLES20.glGetAttribLocation(mProgram, "aColor");
        //获取程序中摄像机位置引用
        maCameraHandle=GLES20.glGetUniformLocation(mProgram, "uCamera"); 

	}
    //复制变换矩阵
    public void copyM(){
    	for(int i=0;i<16;i++){
    		m[i]=MatrixState.getMMatrix()[i];
    	}
    }	
	//获得中心点位置和长宽高的方法
    public AABBBox getCurrBox(){
    	return preBox.setToTransformedBox(m);//获取变换后的包围盒
    
    }
    //触控后的动作，根据需要要做相应改动
	public void changeOnTouch(boolean flag){
		if (flag) {
			drawchoice=true;
		} else {
			drawchoice=false;
		}	
	}
	public void drawSelf() {
		
		copyM();
		// 制定使用某套着色器程序
		GLES20.glUseProgram(mProgram);
		// 将最终变换矩阵传入着色器程序
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false,
				MatrixState.getFinalMatrix(), 0); 
        //将位置、旋转变换矩阵传入着色器程序
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);

        //将光源位置传入着色器程序   
       // GLES20.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
        //注意，在这里可以选择是用定向光照还是定位光照，但是选择以后不能把不用的光照方式传入到
        //着色器当中去
        //将摄像机位置传入着色器程序   
        GLES20.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
		// 将顶点位置数据传入渲染管线
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT,
				false, 3 * 4, mVertexBuffer);
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
        if(drawchoice){
    		// 启用顶点位置数据
    		GLES20.glEnableVertexAttribArray(maPositionHandle); 
            GLES20.glEnableVertexAttribArray(maColorHandle);  //启用顶点颜色数据

    		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);
        	
        }
        else
        {
        GLES20.glLineWidth(3);
		// 启用顶点位置数据
		GLES20.glEnableVertexAttribArray(maPositionHandle); 
        GLES20.glEnableVertexAttribArray(maColorHandle);  //启用顶点颜色数据

		GLES20.glDrawArrays(GLES20.GL_LINES, 0, vCount);}
	}


}