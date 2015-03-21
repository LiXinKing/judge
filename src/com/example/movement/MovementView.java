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
    private SceneRenderer mRenderer;//场景渲染器	  
    MovementPicture movementPicture;
    int texId;//纹理标识
	float rotation;
	float twistingRatio;//三角形扭转的缩放比例
	int symbol=1,distance=1;
	float currRatio=0.0001f;//每次变化的角度比例
	float cz=1500;
    
	public MovementView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染  
        setKeepScreenOn(true);
        
	}
	private class SceneRenderer implements GLSurfaceView.Renderer 
    {  
    	
        public void onDrawFrame(GL10 gl) 
        { 	
        	//清除深度缓冲与颜色缓冲
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            MatrixState.setCamera(0,0,cz,0,0,0,0f,1.0f,0.0f);
            MatrixState.pushMatrix();
            movementPicture.drawSelf(texId,twistingRatio);
            MatrixState.popMatrix();
            Log.v("onDrawFrame", "OK");
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            float ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1,2000);
            MatrixState.setCamera(0,0,30,0,0,0,0f,1.0f,0.0f);
            
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //设置屏幕背景色RGBA
            GLES20.glClearColor(0f,0f,0f,1.0f); 
            //打开深度检测 
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES20.glDisable(GLES20.GL_CULL_FACE);
            //初始化变换矩阵
            MatrixState.setInitStack();
            //加载shader
            shader.loadCodeFromFile(MovementView.this.getResources());
            //编译shader
            shader.compileShader();
            movementPicture=new MovementPicture(shader.getTrangleShaderProgram(),
            		MovementMethod.screenHeight,MovementMethod.screenWidth);//初始化矩阵
            initTexture();//初始化纹理
            
            //创建一个线程，定时摆动树干
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
			Resources r=this.getResources();//获取资源
			texId=Constant.initTexture();
			
		}
	
}
