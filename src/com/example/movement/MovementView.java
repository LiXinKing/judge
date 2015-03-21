package com.example.movement;





import javax.microedition.khronos.egl.EGLConfig;

import com.example.R;
import com.example.forpublicuse.Constant;
import com.example.forpublicuse.MatrixState;
import com.example.forpublicuse.shader;
import javax.microedition.khronos.opengles.GL10;



import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;



public class MovementView extends GLSurfaceView {
    private SceneRenderer mRenderer;//������Ⱦ��	  
    MovementPicture movementPicture;
    int texId;//�����ʶ
	float rotation;
	float twistingRatio;//������Ťת�����ű���
	int symbol=1,distance=1;
	float currRatio=0.0001f;//ÿ�α仯�ĽǶȱ���
	float cz=1500;
    
	public MovementView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
        this.setEGLContextClientVersion(2); //����ʹ��OPENGL ES2.0
        mRenderer = new SceneRenderer();	//����������Ⱦ��
        setRenderer(mRenderer);				//������Ⱦ��		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ  
        setKeepScreenOn(true);
        
	}
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {  
    	
        public void onDrawFrame(GL10 gl) 
        { 	
        	//�����Ȼ�������ɫ����
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            MatrixState.setCamera(0,0,cz,0,0,0,0f,1.0f,0.0f);
            MatrixState.pushMatrix();
            movementPicture.drawSelf(texId,twistingRatio);
            MatrixState.popMatrix();
            Log.v("onDrawFrame", "OK");
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //�����Ӵ���С��λ�� 
        	GLES20.glViewport(0, 0, width, height); 
        	//����GLSurfaceView�Ŀ�߱�
            float ratio = (float) width / height;
            //���ô˷����������͸��ͶӰ����
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1,2000);
            MatrixState.setCamera(0,0,30,0,0,0,0f,1.0f,0.0f);
            
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //������Ļ����ɫRGBA
            GLES20.glClearColor(0f,0f,0f,1.0f); 
            //����ȼ�� 
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //�򿪱������   
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            //��ʼ���任����
            MatrixState.setInitStack();
            //����shader
            shader.loadCodeFromFile(MovementView.this.getResources());
            //����shader
            shader.compileShader();
            movementPicture=new MovementPicture(shader.getTrangleShaderProgram(),
            		MovementMethod.screenHeight,MovementMethod.screenWidth);//��ʼ������
            initTexture();//��ʼ������
            
            //����һ���̣߳���ʱ�ڶ�����
            new Thread()
            {
        		@Override
            	public void run()
            	{
        			while(true)
        			{	Log.v("twistingRatio", String.valueOf(twistingRatio));
        				twistingRatio=twistingRatio+symbol*currRatio;
        				cz=cz+distance*6;
        				Log.v("twistingRatio", String.valueOf(twistingRatio));
        				if(twistingRatio>0.016f)
        				{	
        					twistingRatio=0.016f;
        					symbol=-symbol;
        				}
        				if(twistingRatio<-0.016f)
        				{
        					twistingRatio=-0.016f;
        					symbol=-symbol;
        				}
        				if(cz>1500)
        				{	
        					cz=1500;
        					distance=-distance;
        				}
        				if(cz<1000)
        				{
        					cz=1000;
        					distance=-distance;
        				}
        				try
        				{
        					Thread.sleep(25);
        				}
        				catch(Exception e)
        				{
        					e.printStackTrace();
        				}
        			}
            	}
            }.start();


        }

 }
	
		public void initTexture() {
			// TODO Auto-generated method stub
			Resources r=this.getResources();//��ȡ��Դ
			texId=Constant.initTexture();
			
		}
	
}
