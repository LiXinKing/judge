package com.example.forpublicuse;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.Matrix;

public class MatrixState 
{  
	private static float[] mProjMatrix = new float[16];//4x4���� ͶӰ��
    private static float[] mVMatrix = new float[16];//�����λ�ó���9��������   
    public static float[] currMatrix;//��ǰ�任����
    public static float[] lightLocation=new float[]{0,0,0};//��λ���Դλ��
    public static FloatBuffer lightPositionFB;
    public static FloatBuffer cameraFB;  
    public static float [] lightDirection=new float[]{0,0,1};//����ⷽ������
    public static FloatBuffer lightDirectionFB;//����ⷽ���������ݻ���
    //�����任�����ջ
    static float[][] mStack=new float[10][16];
    static int stackTop=-1;
    
    public static void setInitStack()//��ȡ���任��ʼ����
    {
    	currMatrix=new float[16];
    	Matrix.setRotateM(currMatrix, 0, 0, 1, 0, 0);
//    	Matrix.translateM(currMatrix, 0, 2*Constant.Dscaling, 0, 0);
    }
    
    public static void pushMatrix()//�����任����
    {
    	stackTop++;
    	for(int i=0;i<16;i++)
    	{
    		mStack[stackTop][i]=currMatrix[i];
    	}
    }
    	
    public static void popMatrix()//�ָ��任����
    {
    	for(int i=0;i<16;i++)
    	{
    		currMatrix[i]=mStack[stackTop][i];
    	}
    	stackTop--;
    }
    
    public static void translate(float x,float y,float z)//������xyz���ƶ�
    {
    	Matrix.translateM(currMatrix, 0, x, y, z);
    }
    
    public static void rotate(float angle,float x,float y,float z)//������xyz���ƶ�
    {
    	Matrix.rotateM(currMatrix,0,angle,x,y,z);
    }
    
    public static void scale(float x,float y,float z)
    {
    	Matrix.scaleM(currMatrix,0, x, y, z);
    }
    //���������
    static ByteBuffer llbb= ByteBuffer.allocateDirect(3*4);
    static float[] cameraLocation=new float[3];//�����λ��
    public static void setCamera
    (
    		float cx,	//�����λ��x
    		float cy,   //�����λ��y
    		float cz,   //�����λ��z
    		float tx,   //�����Ŀ���x
    		float ty,   //�����Ŀ���y
    		float tz,   //�����Ŀ���z
    		float upx,  //�����UP����X����
    		float upy,  //�����UP����Y����
    		float upz   //�����UP����Z����		
    )
    {
        	Matrix.setLookAtM
            (
            		mVMatrix, 
            		0, 
            		cx,
            		cy,
            		cz,
            		tx,
            		ty,
            		tz,
            		upx,
            		upy,
            		upz
            );
        	
        	cameraLocation[0]=cx;
        	cameraLocation[1]=cy;
        	cameraLocation[2]=cz;
        	
        	llbb.clear();
            llbb.order(ByteOrder.nativeOrder());//�����ֽ�˳��
            cameraFB=llbb.asFloatBuffer();
            cameraFB.put(cameraLocation);
            cameraFB.position(0);  
    }
    
    //����͸��ͶӰ����
    public static void setProjectFrustum
    ( 
    	float left,		//near���left
    	float right,    //near���right
    	float bottom,   //near���bottom
    	float top,      //near���top
    	float near,		//near�����
    	float far       //far�����
    )
    {
    	Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }
    
    //��������ͶӰ����
    public static void setProjectOrtho
    (
    	float left,		//near���left
    	float right,    //near���right
    	float bottom,   //near���bottom
    	float top,      //near���top
    	float near,		//near�����
    	float far       //far�����
    )
    {    	
    	Matrix.orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }
    
    //��ȡ����������ܱ任����
    static float[] mMVPMatrix=new float[16];
    public static float[] getFinalMatrix()
    {	
    	Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);        
        return mMVPMatrix;
    }
    
    //��ȡ��������ı任����
    public static float[] getMMatrix()
    {       
        return currMatrix;
    }
    //���õƹ�λ�õķ���
    static ByteBuffer llbbL = ByteBuffer.allocateDirect(3*4);
    public static void setLightLocation(float x,float y,float z)
    {
    	llbbL.clear();
    	
    	lightLocation[0]=x;
    	lightLocation[1]=y;
    	lightLocation[2]=z;
    	
        llbbL.order(ByteOrder.nativeOrder());//�����ֽ�˳��
        lightPositionFB=llbbL.asFloatBuffer();
        lightPositionFB.put(lightLocation);
        lightPositionFB.position(0);
    }
    static ByteBuffer llbbL0 = ByteBuffer.allocateDirect(3*4);
    public static void setLightDirection(float x,float y,float z)//�������յĺ���
    {
    	llbbL0.clear();
    	lightDirection[0]=x;lightDirection[1]=y;lightDirection[2]=z;
    	llbbL0.order(ByteOrder.nativeOrder());
    	lightDirectionFB=llbbL0.asFloatBuffer();
    	lightDirectionFB.put(lightDirection);
    	lightDirectionFB.position(0);
    }
    //��ȡ���������������ķ���
	public static float[] getInvertMvMatrix(){
		float[] invM = new float[16];
		Matrix.invertM(invM, 0, mVMatrix, 0);//�������
		return invM;
	}
	public static float[] fromPtoPreP(float[] p){
		//ͨ����任���õ��任֮ǰ�ĵ�
		float[] inverM = getInvertMvMatrix();//��ȡ��任����
		float[] preP = new float[4];
		Matrix.multiplyMV(preP, 0, inverM, 0, new float[]{p[0],p[1],p[2],1}, 0);//��任ǰ�ĵ�
		return new float[]{preP[0],preP[1],preP[2]};//�任ǰ�ĵ���Ǳ任֮ǰ�ķ�����
	}
    
}
