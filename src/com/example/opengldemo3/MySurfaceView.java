package com.example.opengldemo3;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



import com.example.forcompare.cover;
import com.example.forcompare.cube;
import com.example.forpublicuse.Constant;
import com.example.forpublicuse.MatrixState;
import com.example.forpublicuse.shader;
import com.example.movement.MovementView;
import com.example.touch.AABBBox;
import com.example.touch.OnScreenCaculater;
import com.example.touch.Vector3;

import android.R.integer;
import android.R.string;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.Parameters;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ListView;

import com.example.*;

public class MySurfaceView extends GLSurfaceView {
    private SceneRenderer mRenderer;//场景渲染器	  
    Rectangular  recttangular; 
	float mPreviousY=0;
	float mPreviousX=0;
	float xAngle=0;
	float yAngle=90;
	float xAngle0=45;
	float yAngle0=0;
    float TOUCH_SCALE_FACTOR=(float)20.0/320;//角度的缩放
    float FACTOR=1.26f;
    ArrayList<float[]> mmArrayList;
    int startnumber,endnumber;
	float left;
    float right;
	float top;
	float bottom;
	float near;
	float far;
	float ratio;//屏幕的宽高比例
	float R=100;//缩放用到的系数
	int  backindex;
    int draw=1;
    Context clickContext;
	ListView lv = null;//用于多项选择
	int[] choicecover=new int[10];
	static boolean do_cover=false;//s是否进行两线的中间着色
	static boolean lock=false;
//	Rectangular aRectangularformove=new Rectangular(shader.getRectangularShaderProgram());

	
	//可触控物体列表
	ArrayList<Rectangular> lovnList=new ArrayList<Rectangular>();
	int checkedIndex=-1;
    
    private ScaleGestureDetector mScaleDetector;//双指张合的识别
    private GestureDetector gd;
	public MySurfaceView(Context context,ArrayList<float[]> floatarray,int start,int end) {
        super(context);
        clickContext=context;
        mmArrayList= floatarray;
        Log.v("floatarray", String.valueOf(mmArrayList.size()));
        startnumber=start;
        endnumber=end;
        this.setEGLContextClientVersion(2); //设置使用OPENGL ES2.0
        mRenderer = new SceneRenderer();	//创建场景渲染器
        setRenderer(mRenderer);				//设置渲染器		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//设置渲染模式为主动渲染   
        mScaleDetector=new ScaleGestureDetector(context,new ScaleListener());
        gd=new GestureDetector(context,new OnDoubleClick());
		MatrixState.setCamera( 0,0,Constant.R, 0f, 0f, 0f, -1f, 0f, 0.0f);
	}
class OnDoubleClick extends GestureDetector.SimpleOnGestureListener{
	 public boolean onDoubleTap(MotionEvent e) {
		 Log.v("dd2","OK");
		 if(!lock){
       	 Constant.scaling=0.01f;
    	 Constant.Dscaling=16;
    	 }
		return false;
		 
	 }
}
	public boolean onTouchEvent(MotionEvent e){
        Log.v("onClick_caculation11", String.valueOf(startnumber));
        Log.v("onClick_caculation11", String.valueOf(endnumber));
		mScaleDetector.onTouchEvent(e);
		gd.onTouchEvent(e);
		float x=e.getX();
		float y=e.getY();
		switch (e.getAction()) {
        case MotionEvent.ACTION_DOWN:
        	Log.v("dd1", "kk");

			//计算仿射变换后AB两点的位置
			float[] AB=OnScreenCaculater.calculateABPosition
			(
				x, //触控点X坐标
				y, //触控点Y坐标
				MainActivity.screenWidth, //屏幕宽度
				MainActivity.screenHeight, //屏幕长度
				left, //视角left、top值
				top,
				near, //视角near、far值
				far
			);
			
			//射线AB,貌似与java自带的vector冲突了
			Vector3 start = new Vector3(AB[0], AB[1], AB[2]);//起点
			Vector3 end = new Vector3(AB[3], AB[4], AB[5]);//终点
			Vector3 dir = end.minus(start);//长度和方向
			/*
			 * 计算AB线段与每个物体包围盒的最佳交点(与A点最近的交点)，
			 * 并记录有最佳交点的物体在列表中的索引值
			 */
			//记录列表中时间最小的索引值
    		checkedIndex = -1;//标记为没有选中任何物体
    		int tmpIndex=-1;//记录与A点最近物体索引的临时值
    		float minTime=1;//记录列表中所有物体与AB相交的最短时间
    		for(int i=0;i<lovnList.size();i++){//遍历列表中的物体
    			AABBBox box = lovnList.get(i).getCurrBox(); //获得物体AABB包围盒   
				float t = box.rayIntersect(start, dir, null);//计算相交时间
    			
    			if (t <= minTime) {
					minTime = t;//记录最小值
					tmpIndex = i;//记录最小值索引
				}
    		}
    		checkedIndex=tmpIndex;//将索引保存在checkedIndex中    
    		if(!lock){
    		changeObj(checkedIndex);//改变被选中物体	
    		Log.v("dd1", String.valueOf(checkedIndex));
        	if(Constant.Dscaling==130&&checkedIndex!=-1){
//    		if(checkedIndex!=-1){
        		 AlertDialog alertDialog=new AlertDialog.Builder(clickContext).setTitle("点信息")
        		 .setMessage("轨迹：红色    x轴：绿色   y轴：蓝色  z轴：黄色"+"       "
        				 +"加速度X:"+String.valueOf(mmArrayList.get(checkedIndex)[0])+"m/s2"+"     "
        				 +"加速度Y:"+String.valueOf(mmArrayList.get(checkedIndex)[1])+"m/s2"+"     "
        				 +"加速度Z:"+String.valueOf(mmArrayList.get(checkedIndex)[2])+"m/s2"+"     "
        				 +"绕z旋转:"+String.valueOf(mmArrayList.get(checkedIndex)[12])+"度"+"     "
        				 +"绕x旋转:"+String.valueOf(mmArrayList.get(checkedIndex)[13])+"度"+"     "
        				 +"绕y旋转:"+String.valueOf(mmArrayList.get(checkedIndex)[14])+"度"+"     "
        				 )
        		 
        		 .setNegativeButton("确定", 
        				 new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						}
						).setPositiveButton("设置线间覆盖", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							final String[] colorString=new String[]{"黑色0","绿色","蓝色","黄色","青色","粉色","红色","黑色1"};
							changeObj(-1);			//先关闭cube的显示才对
							dialog.dismiss();
							AlertDialog addAlertDialog=new AlertDialog.Builder(clickContext).setTitle("选择两条线的标号")
							.setMultiChoiceItems(colorString, new boolean[]{false,false,false,false,false,false,false,false}, 
									new DialogInterface.OnMultiChoiceClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which, boolean isChecked) {
											// TODO Auto-generated method stub
											
										}
									}).setPositiveButton("确定", new DialogInterface.OnClickListener(){
										@Override
										public void onClick(DialogInterface dialog,int which) {									
											 String s="OK";	
											 int j=0;
											// TODO Auto-generated method stub
										for(int i=0;i<colorString.length;i++)
										{
											if(lv.getCheckedItemPositions().get(i))
												choicecover[j++]=i;
										}
										if (lv.getCheckedItemPositions().size() == 2)
										{
											new AlertDialog.Builder(clickContext).setMessage(s)
													.show();
											do_cover=true;
											
										}
										else
										{
											new AlertDialog.Builder(clickContext).setMessage(
													"未进行正确的选择").show();
											do_cover=false;

										}
										
										}
										
									}).setNegativeButton("取消", null).create();
							 lv=addAlertDialog.getListView();
							addAlertDialog.show();
						}
        		 }).show();
        	}
    		}
			break;
        
		case MotionEvent.ACTION_MOVE:

	            float dy = y - mPreviousY;//计算触控笔Y位移 
	            float dx = x - mPreviousX;//计算触控笔Y位移 
	            yAngle -= dy * TOUCH_SCALE_FACTOR;
	            xAngle -= dx * TOUCH_SCALE_FACTOR;
	            if(xAngle>90)
	            {
	            	xAngle=90;
	            }
	            else if(xAngle<-90)
	            {
	            	xAngle=-90;
	            }

	            if(yAngle>90)
	            {
	            	yAngle=90;
	            }
	            else if(yAngle<0)
	            {
	            	yAngle=0;
	            }
	            //计算摄影器的位置
	            float cy=(float)(Constant.R*Math.cos(Math.toRadians(yAngle))*Math.sin(Math.toRadians(xAngle)));
	            float cz=(float)(Constant.R*Math.sin(Math.toRadians(yAngle)));
	            float cx=(float) (Constant.R*Math.cos(Math.toRadians(yAngle))*Math.cos(Math.toRadians(xAngle)));
	            //计算摄影器的向上的向量

	            
	            float upx=(float) (-Math.cos(Math.toRadians(xAngle))*Math.sin(Math.toRadians(yAngle)));
	            float upy=(float) (-Math.sin(Math.toRadians(xAngle))*Math.sin(Math.toRadians(yAngle)));
	            float upz=(float) (Math.cos(Math.toRadians(yAngle)));
	           /* if(xAngle<0){
	            	upx=-upx;
	            	upy=-upy;
	            	upz=-upz;
	            }*/
	            MatrixState.setCamera(cx, cy, cz, 0, 0, 0, upx, upy, upz); //设置摄影器
	            //传到给TextView的数
	            xAngle0=(float) (180* Math.asin(cy/Math.sqrt(cx*cx+cy*cy+cz*cz))/Math.PI);
	            yAngle0=(float) (180* Math.asin(cz/Math.sqrt(cx*cx+cz*cz))/Math.PI);
				break;
		}
		mPreviousX=x;
		mPreviousY=y;

		
		return true;
		
			}
	//改变列表中下标为index的物体
	public void changeObj(int index){
		

			
		
		if(index != -1){//如果有物体被选中
			backindex=index;
	       	 Constant.scaling=0.08f;
	    	 Constant.Dscaling=130;

    		for(int i=0;i<lovnList.size();i++){
    			if(i==index){//改变选中的物体
    				lovnList.get(i).changeOnTouch(true);
    			}
    			else{//恢复其他物体
    				lovnList.get(i).changeOnTouch(false);
    			}
    		}
        }
    	else{//如果没有物体被选中

    		for(int i=0;i<lovnList.size();i++){//恢复其他物体			
    			lovnList.get(i).changeOnTouch(false);
    		}
    	}
	}
    class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener 
	{  //双指张合动作识别

	    @Override
	    public boolean onScale(ScaleGestureDetector detector)
	    {	
	   	if((R/=detector.getScaleFactor())<10)
	    		Constant.R=R=13;
	    	else if ((R/=detector.getScaleFactor())>140)
	    		Constant.R=R=140;
	    	else
	    		Constant.R=(R/=detector.getScaleFactor());
		Log.v("RRRRR", String.valueOf(Constant.R));

	        return true;
	    }
        public boolean onScaleBegin(ScaleGestureDetector detector) {  
           // TODO Auto-generated method stub   
           // 一定要返回true才会进入onScale()这个函数   
           return true;  
        } 
       public void onScaleEnd(ScaleGestureDetector detector) {  
           // TODO Auto-generated method stub   
  
       } 


       
   
	}	
private class SceneRenderer implements GLSurfaceView.Renderer 
    {  
    	


		public void onDrawFrame(GL10 gl) 
        { 	
        //Constant.UNIT_SIZE=MainActivity.UNIT_SIZE/100;

        	//清除深度缓冲与颜色缓冲
        	
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            //初始化光源位置

          
            Log.v("testConstant",String.valueOf(Constant.R));
            Drawable();
            
            //MatrixState.pushMatrix();
            //recttangular.drawSelf();
            //MatrixState.popMatrix();
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
        	
            //设置视窗大小及位置 
        	GLES20.glViewport(0, 0, width, height); 
        	//计算GLSurfaceView的宽高比
            ratio = (float) width / height;
            //调用此方法计算产生透视投影矩阵
            left=right=ratio;
            top=bottom=1;
            near=10f;
            far=150;
			// 调用此方法计算产生透视投影矩阵
            MatrixState.setProjectFrustum(-left, right, -bottom, top, near, far);
			// 调用此方法产生摄像机9参数位置矩阵
            //初始化变换矩阵
            MatrixState.setInitStack();
//          	MatrixState.translate((float) (1*Constant.Dscaling), (float)(0*Constant.Dscaling),(float) (0*Constant.Dscaling));
	        Log.v("onSurfaceChanged", "OK1");

            
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		
        //	Constant.UNIT_SIZE=MainActivity.UNIT_SIZE/100;
        	Log.v("onSurfaceCreated", "OK");
            //设置屏幕背景色RGBA
            GLES20.glClearColor(1f,1f,1f, 1.0f);  
            //加载shader
            shader.loadCodeFromFile(MySurfaceView.this.getResources());
            //编译shader
            shader.compileShader();
          //  recttangular=new Rectangular(shader.getRectangularShaderProgram());
            //打开深度检测
//            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //打开背面剪裁   
            GLES20.glEnable(GLES20.GL_CULL_FACE);
//	        Log.v("mmArrayList11", String.valueOf(mmArrayList.size()));
//            MatrixState.setInitStack();
            lovnList.clear();
            for(int i=startnumber;i<endnumber;i++){
            	Rectangular aRectangular=new Rectangular(shader.getRectangularShaderProgram());
            	aRectangular.i0=0;aRectangular.j0=0;aRectangular.k0=0;
            	aRectangular.x0=0;aRectangular.y0=0;aRectangular.z0=0;
            	aRectangular.i1=0;aRectangular.j1=0;aRectangular.k1=0;
            	aRectangular.x1=0;aRectangular.y1=0;aRectangular.z1=0;
            	aRectangular.i2=0;aRectangular.j2=0;aRectangular.k2=0;
            	aRectangular.x2=0;aRectangular.y2=0;aRectangular.z2=0;
            	aRectangular.i3=0;aRectangular.j3=0;aRectangular.k3=0;
            	aRectangular.x3=0;aRectangular.y3=0;aRectangular.z3=0;
            	
            	aRectangular.i4=0;aRectangular.j4=0;aRectangular.k4=0;
            	aRectangular.x4=0;aRectangular.y4=0;aRectangular.z4=0;
            	aRectangular.i5=0;aRectangular.j5=0;aRectangular.k5=0;
            	aRectangular.x5=0;aRectangular.y5=0;aRectangular.z5=0;
            	aRectangular.i6=0;aRectangular.j6=0;aRectangular.k6=0;
            	aRectangular.x6=0;aRectangular.y6=0;aRectangular.z6=0;
            	aRectangular.i7=0;aRectangular.j3=0;aRectangular.k3=0;
            	aRectangular.x7=0;aRectangular.y7=0;aRectangular.z7=0;
            	lovnList.add(aRectangular);      
            }

            initfordraw();


        }
        
        public void Drawable()
        {       
        	initfordraw();
        	float[] ok;
   
//            MatrixState.pushMatrix();
            float distance=0;    
	        MatrixState.pushMatrix();
	        cube cube=new cube(shader.getcubeShaderProgram());
	        cube.drawSelf();
	        MatrixState.popMatrix(); 
  
            MatrixState.pushMatrix();
            if(backindex!=-1){
            	 MatrixState.translate(-lovnList.get(backindex).i0,
            			 -lovnList.get(backindex).j0, -lovnList.get(backindex).k0); 

            }
//            MatrixState.translate(-lovnList.get(endnumber/2).i0/15,-lovnList.get(endnumber/2).j0/15, -lovnList.get(endnumber/2).k0/15);
        for(int i=startnumber;i<endnumber;i++){


	     
	        MatrixState.pushMatrix();
	           if(do_cover){  
	        	   cover cover=new cover(shader.getcoverShaderProgram());
	        for(int m=0;m<6;m++){
	        	cover.vertices[m]=lovnList.get(i-startnumber).vertices[m+choicecover[0]*6];
	        	cover.vertices[m+12]=lovnList.get(i-startnumber).vertices[m+choicecover[0]*6];
	        }
	        for(int m=0;m<6;m++){
	        	cover.vertices[m+6]=lovnList.get(i-startnumber).vertices[m+choicecover[1]*6];
	           	cover.vertices[m+18]=lovnList.get(i-startnumber).vertices[m+choicecover[1]*6];
	        }

	        for(int m=0;m<4;m++){
	         	cover.colors[m]=lovnList.get(i-startnumber).colorArray[m+choicecover[0]*8];
	         	cover.colors[m+8]=lovnList.get(i-startnumber).colorArray[m+choicecover[0]*8];
	         	cover.colors[m+16]=lovnList.get(i-startnumber).colorArray[m+choicecover[0]*8];
	         	cover.colors[m+24]=lovnList.get(i-startnumber).colorArray[m+choicecover[0]*8];
	        }
	        for(int m=0;m<4;m++){
	         	cover.colors[m+4]=lovnList.get(i-startnumber).colorArray[m+choicecover[1]*8];
	          	cover.colors[m+12]=lovnList.get(i-startnumber).colorArray[m+choicecover[1]*8];
	        	cover.colors[m+20]=lovnList.get(i-startnumber).colorArray[m+choicecover[1]*8];
	        	cover.colors[m+28]=lovnList.get(i-startnumber).colorArray[m+choicecover[1]*8];
	        }
	        cover.initVertexData();
	        cover.drawSelf();
	        }
	        lovnList.get(i-startnumber).drawSelf();
        	MatrixState.popMatrix(); 

        }
        
        MatrixState.popMatrix(); 
        }
        
        float[] getpoint( float[] m,float[] vertices){
        
    	    float[] transformedCorners=new float[3];
    		float[] tmpResult=new float[4];
    	    int count=0;
    		for(int i=0,u=0;i<vertices.length/6;i++){
    			
    			float[] point=new float[]{vertices[u],vertices[u+1],vertices[u+2],1};//将顶点转换成齐次坐标
    			u=u+6;
    			Matrix.multiplyMV(tmpResult, 0, m, 0, point, 0);
    			transformedCorners[count++]=tmpResult[0];
    			transformedCorners[count++]=tmpResult[1];
    			transformedCorners[count++]=tmpResult[2];	
    			Log.v("transformedCorners", String.valueOf(count));
    		}
    
			return transformedCorners;
        	
        }

        	
        }
public void initfordraw() {

	// TODO Auto-generated method stub
    for(int i=startnumber;i<endnumber;i++){
    	Rectangular aRectangular=lovnList.get(i-startnumber);
    	aRectangular.i0=0;aRectangular.j0=0;aRectangular.k0=0;
    	aRectangular.x0=0;aRectangular.y0=0;aRectangular.z0=0;
    	aRectangular.i1=0;aRectangular.j1=0;aRectangular.k1=0;
    	aRectangular.x1=0;aRectangular.y1=0;aRectangular.z1=0;
    	aRectangular.i2=0;aRectangular.j2=0;aRectangular.k2=0;
    	aRectangular.x2=0;aRectangular.y2=0;aRectangular.z2=0;
    	aRectangular.i3=0;aRectangular.j3=0;aRectangular.k3=0;
    	aRectangular.x3=0;aRectangular.y3=0;aRectangular.z3=0;
    	
    	aRectangular.i4=0;aRectangular.j4=0;aRectangular.k4=0;
    	aRectangular.x4=0;aRectangular.y4=0;aRectangular.z4=0;
    	aRectangular.i5=0;aRectangular.j5=0;aRectangular.k5=0;
    	aRectangular.x5=0;aRectangular.y5=0;aRectangular.z5=0;
    	aRectangular.i6=0;aRectangular.j6=0;aRectangular.k6=0;
    	aRectangular.x6=0;aRectangular.y6=0;aRectangular.z6=0;
    	aRectangular.i7=0;aRectangular.j3=0;aRectangular.k3=0;
    	aRectangular.x7=0;aRectangular.y7=0;aRectangular.z7=0;
    }

 for(int i=startnumber;i<endnumber;i++){
 	float[] ok=mmArrayList.get(i);
	if(i==(endnumber-1)){
  	lovnList.get(i-startnumber).x0=lovnList.get(i-startnumber).x0+ok[4]*Constant.Dscaling;
  	lovnList.get(i-startnumber).y0=lovnList.get(i-startnumber).y0+ok[5]*Constant.Dscaling;
  	lovnList.get(i-startnumber).z0=lovnList.get(i-startnumber).z0+ok[6]*Constant.Dscaling;
		continue;
	}
	lovnList.get(i-startnumber).x0=lovnList.get(i-startnumber).x0+ok[4]*Constant.Dscaling;
	lovnList.get(i-startnumber).y0=lovnList.get(i-startnumber).y0+ok[5]*Constant.Dscaling;
	lovnList.get(i-startnumber).z0=lovnList.get(i-startnumber).z0+ok[6]*Constant.Dscaling;
	

	lovnList.get(i-startnumber+1).i0=lovnList.get(i-startnumber).x0;
	lovnList.get(i-startnumber+1).j0=lovnList.get(i-startnumber).y0;
	lovnList.get(i-startnumber+1).k0=lovnList.get(i-startnumber).z0;

	lovnList.get(i-startnumber+1).x0=lovnList.get(i-startnumber).x0;
	lovnList.get(i-startnumber+1).y0=lovnList.get(i-startnumber).y0;
	lovnList.get(i-startnumber+1).z0=lovnList.get(i-startnumber).z0;
//	  	lovnList.get(i-startnumber).initVertexData();
// 	lovnList.get(i-startnumber).initShader();
    }

for(int i=startnumber;i<endnumber;i++){
   	float[] ok=mmArrayList.get(i);
   	if(endnumber==1)break;
	if(i==(endnumber-1)){
			float[][] rotionoffset = new float[7][4];
			if(i==mmArrayList.size()-1)
				rotionchange(i-startnumber,rotionoffset);
			else 
				rotionchange(i-startnumber+1,rotionoffset);
				
			
   		lovnList.get(i-startnumber).i1=lovnList.get(i-startnumber-1).x1;
   		lovnList.get(i-startnumber).j1=lovnList.get(i-startnumber-1).y1;
   		lovnList.get(i-startnumber).k1=lovnList.get(i-startnumber-1).z1;
   		
   		lovnList.get(i-startnumber).i2=lovnList.get(i-startnumber-1).x2;
   		lovnList.get(i-startnumber).j2=lovnList.get(i-startnumber-1).y2;
   		lovnList.get(i-startnumber).k2=lovnList.get(i-startnumber-1).z2;
   		
   		lovnList.get(i-startnumber).i3=lovnList.get(i-startnumber-1).x3;
   		lovnList.get(i-startnumber).j3=lovnList.get(i-startnumber-1).y3;
   		lovnList.get(i-startnumber).k3=lovnList.get(i-startnumber-1).z3;
   		
		lovnList.get(i-startnumber).i4=lovnList.get(i-startnumber-1).x4;
		lovnList.get(i-startnumber).j4=lovnList.get(i-startnumber-1).y4;
		lovnList.get(i-startnumber).k4=lovnList.get(i-startnumber-1).z4;
		
		lovnList.get(i-startnumber).i5=lovnList.get(i-startnumber-1).x5;
		lovnList.get(i-startnumber).j5=lovnList.get(i-startnumber-1).y5;
		lovnList.get(i-startnumber).k5=lovnList.get(i-startnumber-1).z5;
		
		lovnList.get(i-startnumber).i6=lovnList.get(i-startnumber-1).x6;
		lovnList.get(i-startnumber).j6=lovnList.get(i-startnumber-1).y6;
		lovnList.get(i-startnumber).k6=lovnList.get(i-startnumber-1).z6;
	
		lovnList.get(i-startnumber).i7=lovnList.get(i-startnumber-1).x7;
		lovnList.get(i-startnumber).j7=lovnList.get(i-startnumber-1).y7;
		lovnList.get(i-startnumber).k7=lovnList.get(i-startnumber-1).z7;
   		
   		lovnList.get(i-startnumber).x1=lovnList.get(i-startnumber).x0+rotionoffset[0][0];
   		lovnList.get(i-startnumber).y1=lovnList.get(i-startnumber).y0+rotionoffset[0][1];
   		lovnList.get(i-startnumber).z1=lovnList.get(i-startnumber).z0+rotionoffset[0][2];
   		

   		
   		lovnList.get(i-startnumber).x2=lovnList.get(i-startnumber).x0+rotionoffset[1][0];
   		lovnList.get(i-startnumber).y2=lovnList.get(i-startnumber).y0+rotionoffset[1][1];
   		lovnList.get(i-startnumber).z2=lovnList.get(i-startnumber).z0+rotionoffset[1][2];
   		

   		
   		lovnList.get(i-startnumber).x3=lovnList.get(i-startnumber).x0+rotionoffset[2][0];
   		lovnList.get(i-startnumber).y3=lovnList.get(i-startnumber).y0+rotionoffset[2][1];
   		lovnList.get(i-startnumber).z3=lovnList.get(i-startnumber).z0+rotionoffset[2][2];
   		
   		lovnList.get(i-startnumber).x4=lovnList.get(i-startnumber).x0+rotionoffset[3][0];
   		lovnList.get(i-startnumber).y4=lovnList.get(i-startnumber).y0+rotionoffset[3][1];
   		lovnList.get(i-startnumber).z4=lovnList.get(i-startnumber).z0+rotionoffset[3][2];
   		
   		lovnList.get(i-startnumber).x5=lovnList.get(i-startnumber).x0+rotionoffset[4][0];
   		lovnList.get(i-startnumber).y5=lovnList.get(i-startnumber).y0+rotionoffset[4][1];
   		lovnList.get(i-startnumber).z5=lovnList.get(i-startnumber).z0+rotionoffset[4][2];
   		
   		lovnList.get(i-startnumber).x6=lovnList.get(i-startnumber).x0+rotionoffset[5][0];
   		lovnList.get(i-startnumber).y6=lovnList.get(i-startnumber).y0+rotionoffset[5][1];
   		lovnList.get(i-startnumber).z6=lovnList.get(i-startnumber).z0+rotionoffset[5][2];
   		
   		lovnList.get(i-startnumber).x7=lovnList.get(i-startnumber).x0+rotionoffset[6][0];
   		lovnList.get(i-startnumber).y7=lovnList.get(i-startnumber).y0+rotionoffset[6][1];
   		lovnList.get(i-startnumber).z7=lovnList.get(i-startnumber).z0+rotionoffset[6][2];
      	lovnList.get(i-startnumber).initVertexData();
			continue;
		}

    	float[] ok1=mmArrayList.get(i+1);
    	if(i==startnumber){
    		float[][] rotionoffset = new float[7][4];
    		
    		rotionchange(i-startnumber,rotionoffset);	//第一个旋转增量
   		lovnList.get(i-startnumber).i1=lovnList.get(i-startnumber).i0+rotionoffset[0][0];
//   		lovnList.get(i-startnumber).i1=lovnList.get(i-startnumber).i0;
   		lovnList.get(i-startnumber).j1=lovnList.get(i-startnumber).j0+rotionoffset[0][1];
   		lovnList.get(i-startnumber).k1=lovnList.get(i-startnumber).k0+rotionoffset[0][2];
   		
   		lovnList.get(i-startnumber).i2=lovnList.get(i-startnumber).i0+rotionoffset[1][0];
   		lovnList.get(i-startnumber).j2=lovnList.get(i-startnumber).j0+rotionoffset[1][1];
//   		lovnList.get(i-startnumber).j2=lovnList.get(i-startnumber).j0;
   		lovnList.get(i-startnumber).k2=lovnList.get(i-startnumber).k0+rotionoffset[1][2];
   		
   		lovnList.get(i-startnumber).i3=lovnList.get(i-startnumber).i0+rotionoffset[2][0];
   		lovnList.get(i-startnumber).j3=lovnList.get(i-startnumber).j0+rotionoffset[2][1];
   		lovnList.get(i-startnumber).k3=lovnList.get(i-startnumber).k0+rotionoffset[2][2];
   		
   		lovnList.get(i-startnumber).i4=lovnList.get(i-startnumber).i0+rotionoffset[3][0];
   		lovnList.get(i-startnumber).j4=lovnList.get(i-startnumber).j0+rotionoffset[3][1];
   		lovnList.get(i-startnumber).k4=lovnList.get(i-startnumber).k0+rotionoffset[3][2];
   		
   		lovnList.get(i-startnumber).i5=lovnList.get(i-startnumber).i0+rotionoffset[4][0];
   		lovnList.get(i-startnumber).j5=lovnList.get(i-startnumber).j0+rotionoffset[4][1];
   		lovnList.get(i-startnumber).k5=lovnList.get(i-startnumber).k0+rotionoffset[4][2];
   		
   		lovnList.get(i-startnumber).i6=lovnList.get(i-startnumber).i0+rotionoffset[5][0];
   		lovnList.get(i-startnumber).j6=lovnList.get(i-startnumber).j0+rotionoffset[5][1];
   		lovnList.get(i-startnumber).k6=lovnList.get(i-startnumber).k0+rotionoffset[5][2];
   		
   		lovnList.get(i-startnumber).i7=lovnList.get(i-startnumber).i0+rotionoffset[6][0];
   		lovnList.get(i-startnumber).j7=lovnList.get(i-startnumber).j0+rotionoffset[6][1];
   		lovnList.get(i-startnumber).k7=lovnList.get(i-startnumber).k0+rotionoffset[6][2];
//   		lovnList.get(i-startnumber).k3=lovnList.get(i-startnumber).k0;
   		rotionchange(i-startnumber+1,rotionoffset);
   		
   		lovnList.get(i-startnumber).x1=lovnList.get(i-startnumber).x0+rotionoffset[0][0];
//   		lovnList.get(i-startnumber).x1=lovnList.get(i-startnumber).x0;
   		lovnList.get(i-startnumber).y1=lovnList.get(i-startnumber).y0+rotionoffset[0][1];
   		lovnList.get(i-startnumber).z1=lovnList.get(i-startnumber).z0+rotionoffset[0][2];
   		
   		lovnList.get(i-startnumber).x2=lovnList.get(i-startnumber).x0+rotionoffset[1][0];
   		lovnList.get(i-startnumber).y2=lovnList.get(i-startnumber).y0+rotionoffset[1][1];
//   		lovnList.get(i-startnumber).y2=lovnList.get(i-startnumber).y0;
   		lovnList.get(i-startnumber).z2=lovnList.get(i-startnumber).z0+rotionoffset[1][2];
   		
   		lovnList.get(i-startnumber).x3=lovnList.get(i-startnumber).x0+rotionoffset[2][0];
   		lovnList.get(i-startnumber).y3=lovnList.get(i-startnumber).y0+rotionoffset[2][1];
   		lovnList.get(i-startnumber).z3=lovnList.get(i-startnumber).z0+rotionoffset[2][2];
   		
   		lovnList.get(i-startnumber).x4=lovnList.get(i-startnumber).x0+rotionoffset[3][0];
   		lovnList.get(i-startnumber).y4=lovnList.get(i-startnumber).y0+rotionoffset[3][1];
   		lovnList.get(i-startnumber).z4=lovnList.get(i-startnumber).z0+rotionoffset[3][2];
   		
   		lovnList.get(i-startnumber).x5=lovnList.get(i-startnumber).x0+rotionoffset[4][0];
   		lovnList.get(i-startnumber).y5=lovnList.get(i-startnumber).y0+rotionoffset[4][1];
   		lovnList.get(i-startnumber).z5=lovnList.get(i-startnumber).z0+rotionoffset[4][2];
   		
   		lovnList.get(i-startnumber).x6=lovnList.get(i-startnumber).x0+rotionoffset[5][0];
   		lovnList.get(i-startnumber).y6=lovnList.get(i-startnumber).y0+rotionoffset[5][1];
   		lovnList.get(i-startnumber).z6=lovnList.get(i-startnumber).z0+rotionoffset[5][2];
   		
   		lovnList.get(i-startnumber).x7=lovnList.get(i-startnumber).x0+rotionoffset[6][0];
   		lovnList.get(i-startnumber).y7=lovnList.get(i-startnumber).y0+rotionoffset[6][1];
   		lovnList.get(i-startnumber).z7=lovnList.get(i-startnumber).z0+rotionoffset[6][2];
//   		lovnList.get(i-startnumber).z3=lovnList.get(i-startnumber).z0;
      	lovnList.get(i-startnumber).initVertexData();
      	continue;
    	}
    	
		lovnList.get(i-startnumber).i1=lovnList.get(i-startnumber-1).x1;
//		lovnList.get(i-startnumber).i1=lovnList.get(i-startnumber).i0;
		lovnList.get(i-startnumber).j1=lovnList.get(i-startnumber-1).y1;
		lovnList.get(i-startnumber).k1=lovnList.get(i-startnumber-1).z1;
		
		lovnList.get(i-startnumber).i2=lovnList.get(i-startnumber-1).x2;
		lovnList.get(i-startnumber).j2=lovnList.get(i-startnumber-1).y2;
//		lovnList.get(i-startnumber).j2=lovnList.get(i-startnumber).j0;
		lovnList.get(i-startnumber).k2=lovnList.get(i-startnumber-1).z2;
		
		lovnList.get(i-startnumber).i3=lovnList.get(i-startnumber-1).x3;
		lovnList.get(i-startnumber).j3=lovnList.get(i-startnumber-1).y3;
		lovnList.get(i-startnumber).k3=lovnList.get(i-startnumber-1).z3;
		
		lovnList.get(i-startnumber).i4=lovnList.get(i-startnumber-1).x4;
		lovnList.get(i-startnumber).j4=lovnList.get(i-startnumber-1).y4;
		lovnList.get(i-startnumber).k4=lovnList.get(i-startnumber-1).z4;
		
		lovnList.get(i-startnumber).i5=lovnList.get(i-startnumber-1).x5;
		lovnList.get(i-startnumber).j5=lovnList.get(i-startnumber-1).y5;
		lovnList.get(i-startnumber).k5=lovnList.get(i-startnumber-1).z5;
		
		lovnList.get(i-startnumber).i6=lovnList.get(i-startnumber-1).x6;
		lovnList.get(i-startnumber).j6=lovnList.get(i-startnumber-1).y6;
		lovnList.get(i-startnumber).k6=lovnList.get(i-startnumber-1).z6;
	
		lovnList.get(i-startnumber).i7=lovnList.get(i-startnumber-1).x7;
		lovnList.get(i-startnumber).j7=lovnList.get(i-startnumber-1).y7;
		lovnList.get(i-startnumber).k7=lovnList.get(i-startnumber-1).z7;
		
		float[][] rotionoffset = new float[7][4];
		rotionchange(i-startnumber+1,rotionoffset);
		lovnList.get(i-startnumber).x1=lovnList.get(i-startnumber).x0+rotionoffset[0][0];
//		lovnList.get(i-startnumber).x1=lovnList.get(i-startnumber).x0;
		lovnList.get(i-startnumber).y1=lovnList.get(i-startnumber).y0+rotionoffset[0][1];
		lovnList.get(i-startnumber).z1=lovnList.get(i-startnumber).z0+rotionoffset[0][2];
		
		lovnList.get(i-startnumber).x2=lovnList.get(i-startnumber).x0+rotionoffset[1][0];
		lovnList.get(i-startnumber).y2=lovnList.get(i-startnumber).y0+rotionoffset[1][1];
//		lovnList.get(i-startnumber).y2=lovnList.get(i-startnumber).y0;
		lovnList.get(i-startnumber).z2=lovnList.get(i-startnumber).z0+rotionoffset[1][2];
		
		lovnList.get(i-startnumber).x3=lovnList.get(i-startnumber).x0+rotionoffset[2][0];
		lovnList.get(i-startnumber).y3=lovnList.get(i-startnumber).y0+rotionoffset[2][1];
		lovnList.get(i-startnumber).z3=lovnList.get(i-startnumber).z0+rotionoffset[2][2];
		
		lovnList.get(i-startnumber).x4=lovnList.get(i-startnumber).x0+rotionoffset[3][0];
		lovnList.get(i-startnumber).y4=lovnList.get(i-startnumber).y0+rotionoffset[3][1];
		lovnList.get(i-startnumber).z4=lovnList.get(i-startnumber).z0+rotionoffset[3][2];
		
		lovnList.get(i-startnumber).x5=lovnList.get(i-startnumber).x0+rotionoffset[4][0];
		lovnList.get(i-startnumber).y5=lovnList.get(i-startnumber).y0+rotionoffset[4][1];
		lovnList.get(i-startnumber).z5=lovnList.get(i-startnumber).z0+rotionoffset[4][2];
		
		lovnList.get(i-startnumber).x6=lovnList.get(i-startnumber).x0+rotionoffset[5][0];
		lovnList.get(i-startnumber).y6=lovnList.get(i-startnumber).y0+rotionoffset[5][1];
		lovnList.get(i-startnumber).z6=lovnList.get(i-startnumber).z0+rotionoffset[5][2];
		
		lovnList.get(i-startnumber).x7=lovnList.get(i-startnumber).x0+rotionoffset[6][0];
		lovnList.get(i-startnumber).y7=lovnList.get(i-startnumber).y0+rotionoffset[6][1];
		lovnList.get(i-startnumber).z7=lovnList.get(i-startnumber).z0+rotionoffset[6][2];
		
		lovnList.get(i-startnumber).initVertexData();

 }


		}


void rotionchange(int num,float[][] result){
	float[] chMatriz=new float[16];
	float[] chMatrix=new float[16];
	float[] chMatriy=new float[16];//饶转的次序为先绕z轴再绕x轴，再绕y轴
	

	float[] resultx=new float[4];
	float[] resulty=new float[4];
	float[] resultz=new float[4];
	
	float[] resultxy=new float[4];
	float[] resultxz=new float[4];
	float[] resultyz=new float[4];
	float[] resultxyz=new float[4];
	
	float[] ok=mmArrayList.get(num);
	
	float[] pointx=new float[]{ok[0]*Constant.scaling,0,0,1};
	float[] pointy=new float[]{0,ok[1]*Constant.scaling,0,1};
	float[] pointz=new float[]{0,0,ok[2]*Constant.scaling,1};//三个向量
	
	float[] pointxy=new float[]{ok[0]*Constant.scaling,ok[1]*Constant.scaling,0,1};
	float[] pointxz=new float[]{ok[0]*Constant.scaling,0,ok[2]*Constant.scaling,1};
	float[] pointyz=new float[]{0,ok[1]*Constant.scaling,ok[2]*Constant.scaling,1};//三个向量
	float[] pointxyz=new float[]{ok[0]*Constant.scaling,ok[1]*Constant.scaling,ok[2]*Constant.scaling,1};//三个向量
	
	//注意此旋转方向为当你正对这z轴时顺时针旋转
	//绕z轴旋转
	Matrix.setRotateM(chMatriz, 0, ok[12], 0, 0, 1);
	Matrix.multiplyMV(resultx, 0, chMatriz, 0, pointx, 0);
	Matrix.multiplyMV(resulty, 0, chMatriz, 0, pointy, 0);
	Matrix.multiplyMV(resultz, 0, chMatriz, 0, pointz, 0);
	
	Matrix.multiplyMV(resultxy, 0, chMatriz, 0, pointxy, 0);
	Matrix.multiplyMV(resultxz, 0, chMatriz, 0, pointxz, 0);
	Matrix.multiplyMV(resultyz, 0, chMatriz, 0, pointyz, 0);
	Matrix.multiplyMV(resultxyz, 0, chMatriz, 0, pointxyz, 0);
	
	//绕x轴旋转
	Matrix.setRotateM(chMatrix, 0, ok[13], resultx[0], resultx[1], resultx[2]);
	Matrix.multiplyMV(resultx, 0, chMatrix, 0, resultx, 0);
	Matrix.multiplyMV(resulty, 0, chMatrix, 0, resulty, 0);
	Matrix.multiplyMV(resultz, 0, chMatrix, 0, resultz, 0);
	
	Matrix.multiplyMV(resultxy, 0, chMatrix, 0, resultxy, 0);
	Matrix.multiplyMV(resultxz, 0, chMatrix, 0, resultxz, 0);
	Matrix.multiplyMV(resultyz, 0, chMatrix, 0, resultyz, 0);
	Matrix.multiplyMV(resultxyz, 0, chMatrix, 0, resultxyz, 0);
	//绕y轴旋转
	Matrix.setRotateM(chMatriy, 0, ok[14], resulty[0], resulty[1], resulty[2]);
	Matrix.multiplyMV(resultx, 0, chMatriy, 0, resultx, 0);
	Matrix.multiplyMV(resulty, 0, chMatriy, 0, resulty, 0);
	Matrix.multiplyMV(resultz, 0, chMatriy, 0, resultz, 0);
	
	Matrix.multiplyMV(resultxy, 0, chMatriy, 0, resultxy, 0);
	Matrix.multiplyMV(resultxz, 0, chMatriy, 0, resultxz, 0);
	Matrix.multiplyMV(resultyz, 0, chMatriy, 0, resultyz, 0);
	Matrix.multiplyMV(resultxyz, 0, chMatriy, 0, resultxyz, 0);
	

	result[0]=resultx;
	result[1]=resulty;
	result[2]=resultz;
	
	result[3]=resultxy;
	result[4]=resultxz;
	result[5]=resultyz;
	result[6]=resultxyz;
}

}
    



	

