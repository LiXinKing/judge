package com.example.entrance;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.PrivateCredentialPermission;

import com.example.R;
import com.example.forpublicuse.Constant;
import com.example.opengldemo3.MainActivity;
import com.example.opengldemo3.MySurfaceView;
import com.example.prepare.judge;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.format.Time;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ready extends Activity {
	public static ArrayList<float[]> floatcollectArray=new ArrayList<float[]>();
	public static ArrayList<float[]> floatArray=new ArrayList<float[]>();
	private ArrayList<String> buffersearch=new ArrayList<String>();
	private ArrayList<Integer> bufferforsearch=new ArrayList<Integer>();
	public static Bitmap bmp;
	public static int i=0;//屏保时间启动时间标志
	public static int startnumber=0,endnumber=0;
	final Timer timer=new Timer();//屏幕保护计时参数
	Handler handler;//数据接收端
	static EditText end;       
	static EditText start;
	boolean runflag=true;//多线程计时使能
	static float speedx=0f;//初始状态的各个方向的速度
	static float speedy=0;
//	static float speedy=(float) (Math.PI*2);
	static float speedz=0f;
	float anglex=0,angley=0,anglez=0;//初始状态的各个方向的便宜角度
    static float t=(float) 0.001;//采样间隔时间
    static float positionx;//位置参数
	static float positiony;
	static float positionz;
    static float[] mRotationMatrix = new float[9]; 
    static float[] mRotationMatrixfore = new float[9]; 

    private int index;
//    EditText starText;
    public void onCreate(Bundle savedInstanceState) {
    
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.startit);    	
		start=(EditText)findViewById(R.id.start);
    	end=(EditText)findViewById(R.id.end);
		handler=new Handler(){
			public void handleMessage(Message msgMessage) {
			if(msgMessage.what==110){
				shot();//窗口截图
				ComponentName componentName=new ComponentName("com.example",
						"com.example.movement.MovementMethod");
				
				Intent intent=new Intent();
				intent.setComponent(componentName);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			}
		};

		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
		 i++;
		 Log.v("screenhold", String.valueOf(i));
		 if(i==10&&runflag){//10秒启动另一个activity
	     Message msgMessage=new Message();
	     msgMessage.what=110;
	     handler.sendMessage(msgMessage);}
		 if(start.hasFocus()||end.hasFocus())i=0;
		 if(!runflag)i=0;
			}
		}, 1000,1000);
	

		
}
	public boolean onTouchEvent(MotionEvent e){
		start.setFocusable(false);
		end.setFocusable(false);
		i=0;
		return true;	
	}
	public void onClick_start(View view) {
		Log.v("onClick_start", "OK");
		start.setFocusable(true);
		start.setFocusableInTouchMode(true);
		start.requestFocusFromTouch();
		start.requestFocus();
	}
	public void onClick_end(View view) {
		end.setFocusable(true);
		end.setFocusableInTouchMode(true);
		end.requestFocusFromTouch();
		end.requestFocus();
	}
	public void onClick_read(View view) throws Exception 
	{	long time0=System.currentTimeMillis();
		
		String receiveString; 
		String filename="//sdcard/sensortestacc.txt";
		if(!new File(filename).exists())
		{
			Toast.makeText(this, "No files available", Toast.LENGTH_LONG).show();
			return;
		}
		FileInputStream fis ;
		fis = new FileInputStream(filename);

		byte[] buff=new byte[24];
		int hasread=0;
		StringBuilder sBuilder=new StringBuilder();
			while((hasread=fis.read(buff))>0)//读取文件中的数据，多次调用read函数光标始终在往下走
			{	
				sBuilder.append(new String(buff, 0, hasread));
					
			}
			receiveString=sBuilder.toString();
			int measure=receiveString.length();
			StringBuilder bufferbuild = new StringBuilder();
			ArrayList<Float> floatbuffer=new ArrayList<Float>();
			ArrayList<float[]> floatcollectArray1=new ArrayList<float[]>();
			int m0=0;
			for(int i=0,k=0;i<measure;i++)
			{	
				char c = receiveString.charAt(i);	
			if((c==32)){//32的时候是空格将记的数据存入数组中
					
					String collect=bufferbuild.toString();
					bufferbuild.delete(0, collect.length());
					Log.v("strtest", collect);
					
					try{
						float m=Float.parseFloat(collect);
						Log.v("numtest", String.valueOf(m));
						floatbuffer.add(m);}
					catch (Exception NumberFormatException) {

						Log.v("NumberFormatException", "OK");
					}
			//这里try和catch必须要用，否则会出现错误，用了之后不用改也有效果
			//可能是系统为了避免可能存在的错误
		
				}else if (c==10) {//10的时候是换行
					m0++;
					
					Log.v("log", String.valueOf(m0));
					String collect=bufferbuild.toString();
					bufferbuild.delete(0, collect.length());
					try{
					float m=Float.parseFloat(collect);
					floatbuffer.add(m);}
					catch (Exception NumberFormatException) {

						Log.v("NumberFormatException", "OK");
					}
					Float[] arraybuffer=new Float[floatbuffer.size()];
					for (int j = 0; j < floatbuffer.size(); j++) 
					{arraybuffer[j] = floatbuffer.get(j);}
					
					float[] buffer=new float[arraybuffer.length+3];
					for(int j=0;j<arraybuffer.length;j++)
					{
						buffer[j]=arraybuffer[j].floatValue();
					}
					
					floatcollectArray1.add(buffer);
					
					floatbuffer.clear();
					

					
				}
				else if(((47<c)&&(c<58))||c==46||c==45||c==69) bufferbuild.append(c);

			}
			floatcollectArray=(ArrayList<float[]>) floatcollectArray1.clone();
			floatcollectArray1.clear();
			Log.v("onClick_caculation1", String.valueOf(floatcollectArray.size()));


	    	Log.v("onClick_caculation2", String.valueOf(floatcollectArray.size()));
				long time1=System.currentTimeMillis();
				long time=time1-time0;
				Toast.makeText(this, "Done          用时为"+time+"毫秒"+" number:"+floatcollectArray.size(), Toast.LENGTH_SHORT).show();

		}

	public void onClick_display(View view){
		EditText start=(EditText)findViewById(R.id.start);
		EditText end=(EditText)findViewById(R.id.end);
		if((!start.getEditableText().toString().equals(""))&&(!end.getEditableText().toString().equals("")))
		{ 	startnumber=Integer.parseInt(start.getEditableText().toString());
			endnumber=Integer.parseInt(end.getEditableText().toString());}


		if((start.getEditableText().toString().equals(""))||(end.getEditableText().toString().equals(""))||
			(endnumber<=startnumber)){
			Toast.makeText(this, "numbers wrong!", Toast.LENGTH_LONG).show();
			return;
			
		}

			if(endnumber>floatcollectArray.size()){
				Toast.makeText(this, "numbers wrong!", Toast.LENGTH_LONG).show();
				return;
			}
			Datahandle(startnumber,endnumber);

	ComponentName componentName=new ComponentName("com.example",
		"com.example.opengldemo3.MainActivity");
		Intent intent=new Intent();
		intent.setComponent(componentName);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);

	}
	public void onClick_search(View view){

		ArrayList<float[]> prepareArrayList=judge.mmprepareArrayList;
		Log.v("prepareArrayList", String.valueOf(prepareArrayList.size()));
		float x0 = 0,x1=0,x2=0,y0=0,y1=0,y2=0;		
		
		for(float[] cc : prepareArrayList){
			x0+=cc[0];x1+=cc[1];x2+=cc[2];
			y0+=cc[3];y1+=cc[4];y2+=cc[5];
		}Log.v("buffersearch", "OK3");
		x0=x0/prepareArrayList.size();
		x1=x1/prepareArrayList.size();
		x2=x2/prepareArrayList.size();
		y0=y0/prepareArrayList.size();
		y1=y1/prepareArrayList.size();
		y2=y2/prepareArrayList.size();
		float xaverage=(float) Math.sqrt(x0*x0+x1*x1+x2*x2);
		float yaverage=(float) Math.sqrt(y0*y0+y1*y1+y2*y2);	

		for(int i=0;i<floatcollectArray.size();i++){
			float[] buffer=floatcollectArray.get(i);
			float bufferacc=(float) Math.sqrt(buffer[0]*buffer[0]+buffer[1]*buffer[1]+buffer[2]*buffer[2]);
			float buffergry=(float) Math.sqrt(buffer[4]*buffer[4]+buffer[5]*buffer[5]+buffer[6]*buffer[6]);
		//不得不采用这个方法，java的泛型确实有很大缺陷啊！！
			if((bufferacc<xaverage)||(buffergry<yaverage)){
				bufferforsearch.add(i);
	
			}
		}
	
		if(bufferforsearch.isEmpty()){
			Toast.makeText(this, "未找到起点", Toast.LENGTH_LONG).show();
			return;
		}
		int buff=bufferforsearch.get(0);
//				Log.v("judge", String.valueOf(buffersearch.size()));
		for(int i=0;i<bufferforsearch.size()-1;i++){
			int buff0=bufferforsearch.get(i);
			int buff1=bufferforsearch.get(i+1);
			if((buff1-buff0)==1)continue;//连续的没有数据，那么到下一个点
			buffersearch.add("第"+String.valueOf(buff0)+"个点可以作为起点");
//			buffersearch.add("第"+String.valueOf(buff1)+"个点可以作为终点");
			}
	
//		buffersearch.add("第"+String.valueOf(bufferforsearch.get(bufferforsearch.size()-1))+"个点可以作为起点");
		Log.v("buffersearch", "OK");
		 Object[] choicesString= buffersearch.toArray() ;//供选择的起点
		 
		 String[] choicesStringbuf =new String[choicesString.length];
		 for(int i=0;i<choicesString.length;i++){
			 choicesStringbuf[i]=(String)choicesString[i];
		 }
		 final String[] choicesStringend=choicesStringbuf;
		


		new AlertDialog.Builder(this).setTitle("选择起点")
		.setSingleChoiceItems(choicesStringend, -1, new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				index = which;

			}
		}).setPositiveButton("确定", new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{	String mmbufString=choicesStringend[index];
				
				ready.start.setText(String.valueOf(mmbufString.charAt(1)));
			}
		}).setNegativeButton("取消", new OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{	
//				String mmbufString=choicesStringend[index];
//				ready.end.setText(String.valueOf(mmbufString.charAt(1)));
			}
		}).show();


	}

    public static void Datahandle(int start,int end ){
		
    	float[][] result=null;
    	float[][] resultfore=null;
    	 ArrayList<float[]> bufferArrayList=new ArrayList<float[]>();

    	 floatArray=(ArrayList<float[]>) floatcollectArray.clone();
    	for( int i=start,k=start;i<end;i++){
    		float[] array;
    		float[] arrayfore;
    		if(k==(end-1)){
        		 array=floatArray.get(k);
        		arrayfore=floatArray.get(k);	
    		}else{
    		 array=floatArray.get(k);
    		 arrayfore=floatArray.get(k+1);
    		 }
    		


        	float [][] buffer={{array[0],0,0},
        						{array[1],0,0},
        						{array[2],0,0}};
        	float [][] bufferfore={{arrayfore[0],0,0},
					{arrayfore[1],0,0},
					{arrayfore[2],0,0}};
        	

        	Log.v("ang", String.valueOf(array[0]));
        	float[] rotationVector=new float[3];
        	rotationVector[0]=0;
        	rotationVector[1]=0;
        	rotationVector[2]=0;   
        	float[] rotationVectorfore=new float[3];
        	rotationVectorfore[0]=0;
        	rotationVectorfore[1]=0;
        	rotationVectorfore[2]=0; 

    		getRotationMatrixFromVector(mRotationMatrix,rotationVector);
    		getRotationMatrixFromVector(mRotationMatrixfore,rotationVectorfore);
			float[][] mk={{mRotationMatrix[0],mRotationMatrix[1],mRotationMatrix[2]},
					{mRotationMatrix[3],mRotationMatrix[4],mRotationMatrix[5]},
					{mRotationMatrix[6],mRotationMatrix[7],mRotationMatrix[8]}};
			float[][] mkfore={{mRotationMatrixfore[0],mRotationMatrixfore[1],mRotationMatrixfore[2]},
					{mRotationMatrixfore[3],mRotationMatrixfore[4],mRotationMatrixfore[5]},
					{mRotationMatrixfore[6],mRotationMatrixfore[7],mRotationMatrixfore[8]}};

        	float accelerationx=(float)array[0];
        	float accelerationy=(float)array[1];
        	float accelerationz=(float)array[2]; 
        	float accelerationxfore=(float)arrayfore[0];
        	float accelerationyfore=(float)arrayfore[1];
        	float accelerationzfore=(float)arrayfore[2]; 
        	float dt=(float)(arrayfore[3]-array[3]);

        	speedx+=(float) ((accelerationx+accelerationxfore)*dt*t*0.5);
    		speedy+=(float) ((accelerationy+accelerationyfore)*dt*t*0.5);
    		speedz+=(float) ((accelerationz+accelerationzfore)*dt*t*0.5);	
    	
    		array[8]=speedx;
    		array[9]=speedy;
    		array[10]=speedz;
    		

    		floatArray.remove(i);
    		floatArray.add(i, array);


    		k++;
    	}
    	for( int k=start;k<end;k++){
    		float[] array;
    		float[] arrayfore;
    		if(k==(end-1)){
        		 array=floatArray.get(k);
        		arrayfore=floatArray.get(k);	
    		}else{
    		 array=floatArray.get(k);
    		 arrayfore=floatArray.get(k+1);}
    		float speedx,speedxfore;
    		float speedy,speedyfore;
    		float speedz,speedzfore;
    		speedx=array[8];speedy=array[9];speedz=array[10];
    		speedxfore=arrayfore[8];speedyfore=arrayfore[9];speedzfore=arrayfore[10];
    		float dt=(float)(arrayfore[3]-array[3]);
//    		float dt=10;
        	positionx=(float) ((speedx+speedxfore)*dt*t*0.5);
        	positiony=(float) ((speedy+speedyfore)*dt*t*0.5);
        	positionz=(float) ((speedz+speedzfore)*dt*t*0.5);
    		array[4]=positionx;
    		array[5]=positiony;
    		array[6]=positionz;
    		floatArray.remove(k);
    		floatArray.add(k, array);
    	
    	}

    	speedx=0;
    	speedy=0;
    	speedz=0;


    }
   static float caculaterlength(float x0,float x1,float x2,float y0,float y1,float y2){
    		float result=(float) Math.sqrt((x0-y0)*(x0-y0)+
        			(x1-y1)*(x1-y1)+(x2-y2)*
        			(x2-y2));
    		return result;
    	}
    private static float[][] maxtrixmutiply(float[][] maxtrileft, float[][] maxtriright) {
    	float[][] result = {{0,0,0},{0,0,0},{0,0,0}} ;
		// TODO Auto-generated method stub

    	for(int i=0;i<maxtrileft.length;i++)
    		for(int j=0;j<maxtrileft[0].length;j++){
    			result[i][j]=maxtrileft[i][0]*maxtriright[0][j]+
    			maxtrileft[i][1]*maxtriright[1][j]+maxtrileft[i][2]*maxtriright[2][j];
    		}
		return result;
	}//一个矩阵乘法
    private static void getRotationMatrixFromVector(float[] R, float[] rotationVector) {

        float q0;
        float q1 = rotationVector[0];
        float q2 = rotationVector[1];
        float q3 = rotationVector[2];

        if (rotationVector.length == 4) {
            q0 = rotationVector[3];
        } else {
            q0 = 1 - q1*q1 - q2*q2 - q3*q3;
            q0 = (q0 > 0) ? (float)Math.sqrt(q0) : 0;
        }

        float sq_q1 = 2 * q1 * q1;
        float sq_q2 = 2 * q2 * q2;
        float sq_q3 = 2 * q3 * q3;
        float q1_q2 = 2 * q1 * q2;
        float q3_q0 = 2 * q3 * q0;
        float q1_q3 = 2 * q1 * q3;
        float q2_q0 = 2 * q2 * q0;
        float q2_q3 = 2 * q2 * q3;
        float q1_q0 = 2 * q1 * q0;

        if(R.length == 9) {
            R[0] = 1 - sq_q2 - sq_q3;
            R[1] = q1_q2 - q3_q0;
            R[2] = q1_q3 + q2_q0;

            R[3] = q1_q2 + q3_q0;
            R[4] = 1 - sq_q1 - sq_q3;
            R[5] = q2_q3 - q1_q0;

            R[6] = q1_q3 - q2_q0;
            R[7] = q2_q3 + q1_q0;
            R[8] = 1 - sq_q1 - sq_q2;
        } else if (R.length == 16) {
            R[0] = 1 - sq_q2 - sq_q3;
            R[1] = q1_q2 - q3_q0;
            R[2] = q1_q3 + q2_q0;
            R[3] = 0.0f;

            R[4] = q1_q2 + q3_q0;
            R[5] = 1 - sq_q1 - sq_q3;
            R[6] = q2_q3 - q1_q0;
            R[7] = 0.0f;

            R[8] = q1_q3 - q2_q0;
            R[9] = q2_q3 + q1_q0;
            R[10] = 1 - sq_q1 - sq_q2;
            R[11] = 0.0f;

            R[12] = R[13] = R[14] = 0.0f;
            R[15] = 1.0f;
        }
    }//自源码中提取的从传感器中获取旋转矩阵的方法
    //三阶行列式的计算
    public static float getHL3(float[] input) {  
    	float unm1=input[0]*(input[4]*input[8]-input[5]*input[7]);
    	float unm2=-input[1]*(input[3]*input[8]-input[5]*input[6]);
    	float unm3=input[2]*(input[3]*input[7]-input[4]*input[6]);
        return unm1+unm2+unm3;
    }  
    //自写一个三阶矩阵求逆的方法吧
    private static float[][] matrixinversion(float[] input){
    	//求代数余子式
    	float[] buffer1=new float[9];

    	for(int i=0;i<input.length;i++){
    		float[] buffer0=input.clone();
    		if(i%3==0){buffer0[i]=1;buffer0[i+1]=0;buffer0[i+2]=0;}
    		if(i%3==1){buffer0[i-1]=0;buffer0[i]=1;buffer0[i+1]=0;}
    		if(i%3==2){buffer0[i-2]=0;buffer0[i-1]=0;buffer0[i]=1;}
    		buffer1[i]=getHL3(buffer0)/getHL3(input);
    		if(i%2==1)buffer1[i]=-buffer1[i];
    	}
    	  float[][] buffer=	{{buffer1[0],buffer1[1],buffer1[2]},
    						{buffer1[3],buffer1[4],buffer1[5]},
    						{buffer1[6],buffer1[7],buffer1[8]}}; 	
    	  return buffer;
    }
    private void shot() {   
        View view = getWindow().getDecorView();   
        Display display = this.getWindowManager().getDefaultDisplay();   
        view.layout(0, 0, display.getWidth(), display.getHeight());   
        view.setDrawingCacheEnabled(true);//允许当前窗口保存缓存信息，这样getDrawingCache()方法才会返回一个Bitmap    
        bmp = Bitmap.createBitmap(view.getDrawingCache());   
 
    } 
	
	
    @Override
    protected void onResume() {
        super.onResume(); 
        runflag=true;
        
        Log.v("yytest", "onResume");
    }
    protected void onStop(){
    	super.onStop();
    	runflag=false;//停止计时
    	 Log.v("yytest", "onstop");
    }

    @Override
    protected void onPause() {
    	runflag=false;//停止计时
        super.onPause();
        Log.v("yytest", "onPause");
    }
    protected void onDestroy(){
    	timer.cancel();
        super.onDestroy();
        Log.v("yytest", "onDestroy");

    }
	
	
	
	
	
	
	
}