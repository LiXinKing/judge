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
    private SceneRenderer mRenderer;//������Ⱦ��	  
    Rectangular  recttangular; 
	float mPreviousY=0;
	float mPreviousX=0;
	float xAngle=0;
	float yAngle=90;
	float xAngle0=45;
	float yAngle0=0;
    float TOUCH_SCALE_FACTOR=(float)20.0/320;//�Ƕȵ�����
    float FACTOR=1.26f;
    ArrayList<float[]> mmArrayList;
    int startnumber,endnumber;
	float left;
    float right;
	float top;
	float bottom;
	float near;
	float far;
	float ratio;//��Ļ�Ŀ�߱���
	float R=100;//�����õ���ϵ��
	int  backindex;
    int draw=1;
    Context clickContext;
	ListView lv = null;//���ڶ���ѡ��
	int[] choicecover=new int[10];
	static boolean do_cover=false;//s�Ƿ�������ߵ��м���ɫ
	static boolean lock=false;
//	Rectangular aRectangularformove=new Rectangular(shader.getRectangularShaderProgram());

	
	//�ɴ��������б�
	ArrayList<Rectangular> lovnList=new ArrayList<Rectangular>();
	int checkedIndex=-1;
    
    private ScaleGestureDetector mScaleDetector;//˫ָ�źϵ�ʶ��
    private GestureDetector gd;
	public MySurfaceView(Context context,ArrayList<float[]> floatarray,int start,int end) {
        super(context);
        clickContext=context;
        mmArrayList= floatarray;
        Log.v("floatarray", String.valueOf(mmArrayList.size()));
        startnumber=start;
        endnumber=end;
        this.setEGLContextClientVersion(2); //����ʹ��OPENGL ES2.0
        mRenderer = new SceneRenderer();	//����������Ⱦ��
        setRenderer(mRenderer);				//������Ⱦ��		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ   
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

			//�������任��AB�����λ��
			float[] AB=OnScreenCaculater.calculateABPosition
			(
				x, //���ص�X����
				y, //���ص�Y����
				MainActivity.screenWidth, //��Ļ���
				MainActivity.screenHeight, //��Ļ����
				left, //�ӽ�left��topֵ
				top,
				near, //�ӽ�near��farֵ
				far
			);
			
			//����AB,ò����java�Դ���vector��ͻ��
			Vector3 start = new Vector3(AB[0], AB[1], AB[2]);//���
			Vector3 end = new Vector3(AB[3], AB[4], AB[5]);//�յ�
			Vector3 dir = end.minus(start);//���Ⱥͷ���
			/*
			 * ����AB�߶���ÿ�������Χ�е���ѽ���(��A������Ľ���)��
			 * ����¼����ѽ�����������б��е�����ֵ
			 */
			//��¼�б���ʱ����С������ֵ
    		checkedIndex = -1;//���Ϊû��ѡ���κ�����
    		int tmpIndex=-1;//��¼��A�����������������ʱֵ
    		float minTime=1;//��¼�б�������������AB�ཻ�����ʱ��
    		for(int i=0;i<lovnList.size();i++){//�����б��е�����
    			AABBBox box = lovnList.get(i).getCurrBox(); //�������AABB��Χ��   
				float t = box.rayIntersect(start, dir, null);//�����ཻʱ��
    			
    			if (t <= minTime) {
					minTime = t;//��¼��Сֵ
					tmpIndex = i;//��¼��Сֵ����
				}
    		}
    		checkedIndex=tmpIndex;//������������checkedIndex��    
    		if(!lock){
    		changeObj(checkedIndex);//�ı䱻ѡ������	
    		Log.v("dd1", String.valueOf(checkedIndex));
        	if(Constant.Dscaling==130&&checkedIndex!=-1){
//    		if(checkedIndex!=-1){
        		 AlertDialog alertDialog=new AlertDialog.Builder(clickContext).setTitle("����Ϣ")
        		 .setMessage("�켣����ɫ    x�᣺��ɫ   y�᣺��ɫ  z�᣺��ɫ"+"       "
        				 +"���ٶ�X:"+String.valueOf(mmArrayList.get(checkedIndex)[0])+"m/s2"+"     "
        				 +"���ٶ�Y:"+String.valueOf(mmArrayList.get(checkedIndex)[1])+"m/s2"+"     "
        				 +"���ٶ�Z:"+String.valueOf(mmArrayList.get(checkedIndex)[2])+"m/s2"+"     "
        				 +"��z��ת:"+String.valueOf(mmArrayList.get(checkedIndex)[12])+"��"+"     "
        				 +"��x��ת:"+String.valueOf(mmArrayList.get(checkedIndex)[13])+"��"+"     "
        				 +"��y��ת:"+String.valueOf(mmArrayList.get(checkedIndex)[14])+"��"+"     "
        				 )
        		 
        		 .setNegativeButton("ȷ��", 
        				 new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						}
						).setPositiveButton("�����߼串��", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							final String[] colorString=new String[]{"��ɫ0","��ɫ","��ɫ","��ɫ","��ɫ","��ɫ","��ɫ","��ɫ1"};
							changeObj(-1);			//�ȹر�cube����ʾ�Ŷ�
							dialog.dismiss();
							AlertDialog addAlertDialog=new AlertDialog.Builder(clickContext).setTitle("ѡ�������ߵı��")
							.setMultiChoiceItems(colorString, new boolean[]{false,false,false,false,false,false,false,false}, 
									new DialogInterface.OnMultiChoiceClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which, boolean isChecked) {
											// TODO Auto-generated method stub
											
										}
									}).setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
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
													"δ������ȷ��ѡ��").show();
											do_cover=false;

										}
										
										}
										
									}).setNegativeButton("ȡ��", null).create();
							 lv=addAlertDialog.getListView();
							addAlertDialog.show();
						}
        		 }).show();
        	}
    		}
			break;
        
		case MotionEvent.ACTION_MOVE:

	            float dy = y - mPreviousY;//���㴥�ر�Yλ�� 
	            float dx = x - mPreviousX;//���㴥�ر�Yλ�� 
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
	            //������Ӱ����λ��
	            float cy=(float)(Constant.R*Math.cos(Math.toRadians(yAngle))*Math.sin(Math.toRadians(xAngle)));
	            float cz=(float)(Constant.R*Math.sin(Math.toRadians(yAngle)));
	            float cx=(float) (Constant.R*Math.cos(Math.toRadians(yAngle))*Math.cos(Math.toRadians(xAngle)));
	            //������Ӱ�������ϵ�����

	            
	            float upx=(float) (-Math.cos(Math.toRadians(xAngle))*Math.sin(Math.toRadians(yAngle)));
	            float upy=(float) (-Math.sin(Math.toRadians(xAngle))*Math.sin(Math.toRadians(yAngle)));
	            float upz=(float) (Math.cos(Math.toRadians(yAngle)));
	           /* if(xAngle<0){
	            	upx=-upx;
	            	upy=-upy;
	            	upz=-upz;
	            }*/
	            MatrixState.setCamera(cx, cy, cz, 0, 0, 0, upx, upy, upz); //������Ӱ��
	            //������TextView����
	            xAngle0=(float) (180* Math.asin(cy/Math.sqrt(cx*cx+cy*cy+cz*cz))/Math.PI);
	            yAngle0=(float) (180* Math.asin(cz/Math.sqrt(cx*cx+cz*cz))/Math.PI);
				break;
		}
		mPreviousX=x;
		mPreviousY=y;

		
		return true;
		
			}
	//�ı��б����±�Ϊindex������
	public void changeObj(int index){
		

			
		
		if(index != -1){//��������屻ѡ��
			backindex=index;
	       	 Constant.scaling=0.08f;
	    	 Constant.Dscaling=130;

    		for(int i=0;i<lovnList.size();i++){
    			if(i==index){//�ı�ѡ�е�����
    				lovnList.get(i).changeOnTouch(true);
    			}
    			else{//�ָ���������
    				lovnList.get(i).changeOnTouch(false);
    			}
    		}
        }
    	else{//���û�����屻ѡ��

    		for(int i=0;i<lovnList.size();i++){//�ָ���������			
    			lovnList.get(i).changeOnTouch(false);
    		}
    	}
	}
    class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener 
	{  //˫ָ�ź϶���ʶ��

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
           // һ��Ҫ����true�Ż����onScale()�������   
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

        	//�����Ȼ�������ɫ����
        	
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            //��ʼ����Դλ��

          
            Log.v("testConstant",String.valueOf(Constant.R));
            Drawable();
            
            //MatrixState.pushMatrix();
            //recttangular.drawSelf();
            //MatrixState.popMatrix();
        }  

        public void onSurfaceChanged(GL10 gl, int width, int height) {
        	
            //�����Ӵ���С��λ�� 
        	GLES20.glViewport(0, 0, width, height); 
        	//����GLSurfaceView�Ŀ�߱�
            ratio = (float) width / height;
            //���ô˷����������͸��ͶӰ����
            left=right=ratio;
            top=bottom=1;
            near=10f;
            far=150;
			// ���ô˷����������͸��ͶӰ����
            MatrixState.setProjectFrustum(-left, right, -bottom, top, near, far);
			// ���ô˷������������9����λ�þ���
            //��ʼ���任����
            MatrixState.setInitStack();
//          	MatrixState.translate((float) (1*Constant.Dscaling), (float)(0*Constant.Dscaling),(float) (0*Constant.Dscaling));
	        Log.v("onSurfaceChanged", "OK1");

            
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		
        //	Constant.UNIT_SIZE=MainActivity.UNIT_SIZE/100;
        	Log.v("onSurfaceCreated", "OK");
            //������Ļ����ɫRGBA
            GLES20.glClearColor(1f,1f,1f, 1.0f);  
            //����shader
            shader.loadCodeFromFile(MySurfaceView.this.getResources());
            //����shader
            shader.compileShader();
          //  recttangular=new Rectangular(shader.getRectangularShaderProgram());
            //����ȼ��
//            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //�򿪱������   
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
    			
    			float[] point=new float[]{vertices[u],vertices[u+1],vertices[u+2],1};//������ת�����������
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
    		
    		rotionchange(i-startnumber,rotionoffset);	//��һ����ת����
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
	float[] chMatriy=new float[16];//��ת�Ĵ���Ϊ����z������x�ᣬ����y��
	

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
	float[] pointz=new float[]{0,0,ok[2]*Constant.scaling,1};//��������
	
	float[] pointxy=new float[]{ok[0]*Constant.scaling,ok[1]*Constant.scaling,0,1};
	float[] pointxz=new float[]{ok[0]*Constant.scaling,0,ok[2]*Constant.scaling,1};
	float[] pointyz=new float[]{0,ok[1]*Constant.scaling,ok[2]*Constant.scaling,1};//��������
	float[] pointxyz=new float[]{ok[0]*Constant.scaling,ok[1]*Constant.scaling,ok[2]*Constant.scaling,1};//��������
	
	//ע�����ת����Ϊ����������z��ʱ˳ʱ����ת
	//��z����ת
	Matrix.setRotateM(chMatriz, 0, ok[12], 0, 0, 1);
	Matrix.multiplyMV(resultx, 0, chMatriz, 0, pointx, 0);
	Matrix.multiplyMV(resulty, 0, chMatriz, 0, pointy, 0);
	Matrix.multiplyMV(resultz, 0, chMatriz, 0, pointz, 0);
	
	Matrix.multiplyMV(resultxy, 0, chMatriz, 0, pointxy, 0);
	Matrix.multiplyMV(resultxz, 0, chMatriz, 0, pointxz, 0);
	Matrix.multiplyMV(resultyz, 0, chMatriz, 0, pointyz, 0);
	Matrix.multiplyMV(resultxyz, 0, chMatriz, 0, pointxyz, 0);
	
	//��x����ת
	Matrix.setRotateM(chMatrix, 0, ok[13], resultx[0], resultx[1], resultx[2]);
	Matrix.multiplyMV(resultx, 0, chMatrix, 0, resultx, 0);
	Matrix.multiplyMV(resulty, 0, chMatrix, 0, resulty, 0);
	Matrix.multiplyMV(resultz, 0, chMatrix, 0, resultz, 0);
	
	Matrix.multiplyMV(resultxy, 0, chMatrix, 0, resultxy, 0);
	Matrix.multiplyMV(resultxz, 0, chMatrix, 0, resultxz, 0);
	Matrix.multiplyMV(resultyz, 0, chMatrix, 0, resultyz, 0);
	Matrix.multiplyMV(resultxyz, 0, chMatrix, 0, resultxyz, 0);
	//��y����ת
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
    



	

