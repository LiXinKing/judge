package com.example.opengldemo3;



import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.example.R;
import com.example.entrance.ready;
import com.example.forpublicuse.Constant;
import com.example.forpublicuse.MatrixState;



import android.R.integer;
import android.opengl.Matrix;
import android.opengl.Visibility;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.method.MovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


public class MainActivity extends Activity {
	static float yAngle0;// 绕y轴旋转的角度
	static float xAngle0;// 绕x轴旋转的角度
	static float zAngle0;// 绕z轴旋转的角度
    private MySurfaceView mGLSurfaceView;
	final int CONTROL_L=110;
	final int CONTROL_W=120;
	final int CONTROL_H=130;  
	final int CONTROL_R=140;//定义的菜单的标识，供单机事件调用
	final int CONTROL_COVER=150;//定义的菜单的标识，供单机事件调用
	final int CONTROL_lock=160;
	boolean control_X=false;
	boolean control_Y=false;
	boolean control_Z=false;

	//下面三个参数是存储每个seekbar的progress，以避免在不断的进入onProgressChanged后造成的数据变形
	float ux=1;
	float uy=1;
	float uz=1;
	float ua=1;
	Timer timer0=new Timer();//读取数据的计时new
	ArrayList<float[]> floatarray;
	int start,end;
    int tt=0;
    public static float gravity=0.0f;

	static float screenWidth;//屏幕宽度
	static float screenHeight;//屏幕高度
	
	static TextView txtacc;
	static TextView txtg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置为横屏
		DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth=dm.widthPixels;			//dm.widthPixels    获取屏幕横向分辨率
        screenHeight=dm.heightPixels;		//dm.heightPixels	获取屏幕竖向分辨率	
		// 初始化GLSurfaceView
		floatarray=(ArrayList<float[]>) ready.floatArray;
		start=ready.startnumber;
		end=ready.endnumber;	
		
		mGLSurfaceView = new MySurfaceView(this,floatarray,start,end);
		// 切换到主界面
		setContentView(R.layout.activity_main);	
		final SeekBar seekBar1 = (SeekBar) findViewById(R.id.seekbar1);
		final SeekBar seekBar2 = (SeekBar)findViewById(R.id.seekbar2);
		final SeekBar seekBar3 = (SeekBar)findViewById(R.id.seekbar3);
		final SeekBar seekBar4 = (SeekBar)findViewById(R.id.seekbar4);
		txtacc=(TextView)findViewById(R.id.txtacc);
		txtg=(TextView)findViewById(R.id.txtg);
	    MainActivity.txtacc.setVisibility(8);
	    MainActivity.txtg.setVisibility(8);
		  seekBar1.setVisibility(8);//0x000000008是view中的常数是GONE
		  seekBar2.setVisibility(8);
		  seekBar3.setVisibility(8);
		  seekBar4.setVisibility(8);
		 class listener implements SeekBar.OnSeekBarChangeListener{
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				//当控制条超时关闭是应该把控制端也相应的关闭
				final Handler handler=new Handler(){
					public void handleMessage(Message msgMessage) {
	
					if(msgMessage.what==112){
						seekBar1.setVisibility(8);
						seekBar2.setVisibility(8);
						seekBar3.setVisibility(8);
						seekBar4.setVisibility(8);
						if(control_X=true)control_X=false;
						if(control_Y=true)control_Y=false;
						if(control_Z=true)control_Z=false;
						
						Log.v("msgMessage", "ok");
					}	
		

					}
				};

		
				final Timer timer=new Timer();
				timer.schedule(new TimerTask() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub


			     Message msgMessage=new Message();
			     msgMessage.what=112;
			     handler.sendMessage(msgMessage);
			     handler.removeMessages(112);
					}
				}, 0,500);
				if(seekBar.equals(seekBar4)){
					ready.Datahandle(start, end);
				}
		
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
		
			}
			//progress的范围为0~100，根据屏幕的尺寸除以100以得到一个适当值
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
		
			if(seekBar.equals(seekBar1)){
//	            ux=Rectangular.UNIT_SIZE_X/ux;
//	            Rectangular.UNIT_SIZE_X=ux*(float)progress/500;
//				ux=(float)progress/500;
//				Constant.scaling=Constant.scaling*progress/50;
				Constant.Xscaling=(float) (progress)/50;
				Log.v("process", String.valueOf(Constant.Xscaling));
			}
			if(seekBar.equals(seekBar2)){
//				uy=Rectangular.UNIT_SIZE_Y/uy;
//				Rectangular.UNIT_SIZE_Y=uy*(float)progress/500;
//				uy=(float)progress/500;
				Constant.Yscaling=(float) (progress)/50;
			}
			if(seekBar.equals(seekBar3)){
//				uz=Rectangular.UNIT_SIZE_Z/uz;
//				Rectangular.UNIT_SIZE_Z=uz*(float)progress/500;
//				uz=(float)progress/500;
				Constant.Zscaling=(float) (progress)/50;
			}
			if(seekBar.equals(seekBar4)){
				
				gravity=(float)progress/50;
				
				
			}



			}
		}
		listener listener = new listener();
		seekBar1.setOnSeekBarChangeListener(listener);
		seekBar2.setOnSeekBarChangeListener(listener);
		seekBar3.setOnSeekBarChangeListener(listener);
		seekBar4.setOnSeekBarChangeListener(listener);
		
		//进度条尝试，将进度条数据读出作为正方体大小
		LinearLayout ll = (LinearLayout) findViewById(R.id.main_liner);
		ll.addView(mGLSurfaceView);



    }

	//添加控制菜单，四个调控的主要选项
    public boolean onCreateOptionsMenu(Menu menu)
    {  
        menu.add(0, CONTROL_R, 0, "RESET");
        menu.add(0, CONTROL_COVER, 0, "COVER_RESET");
        menu.add(0, CONTROL_lock, 0, "lock");
        menu.add(0, CONTROL_L, 0, "长");
        menu.add(0, CONTROL_W, 0, "宽");
        menu.add(0, CONTROL_H, 0, "高");
		return super.onCreateOptionsMenu(menu);
    	
    	
    	
    }
    //菜单的回调事件，用监听的方式有点复杂的样子
    public boolean onOptionsItemSelected(MenuItem mi)
    {   SeekBar seekBar1 = (SeekBar) findViewById(R.id.seekbar1);
    	SeekBar seekBar2 = (SeekBar) findViewById(R.id.seekbar2);
    	SeekBar seekBar3 = (SeekBar) findViewById(R.id.seekbar3);
    	switch (mi.getItemId()) {
		case CONTROL_L:
			if(control_X){seekBar1.setVisibility(8);
			control_X=false;}
			else {control_X=true;seekBar1.setVisibility(0);
			seekBar2.setVisibility(8);seekBar3.setVisibility(8);
			control_Y=false;control_Z=false;
}
			

			break;
		case CONTROL_W:
			if(control_Y){seekBar2.setVisibility(8);
			control_Y=false;}
			else {control_Y=true;seekBar2.setVisibility(0);
			seekBar1.setVisibility(8);seekBar3.setVisibility(8);
			control_X=false;control_Z=false;}
			break;
		case CONTROL_H:
			if(control_Z){seekBar3.setVisibility(8);
			control_Z=false;}
			else {control_Z=true;seekBar3.setVisibility(0);
			seekBar1.setVisibility(8);seekBar2.setVisibility(8);
			control_X=false;control_Y=false;
			}
			break;



		case	CONTROL_R:
//			Constant.R=100;
			Constant.Xscaling=1;
			Constant.Yscaling=1;
			Constant.Zscaling=1;
			Log.v("RRRRR", String.valueOf(Constant.R));
//			MatrixState.setCamera( 0,0,Constant.R, 0f, 0f, 0f, -1f, 0f, 0.0f);

			break;
		case	CONTROL_COVER:
			if(!MySurfaceView.lock)
			MySurfaceView.do_cover=false;
//			MatrixState.setCamera( 0,0,Constant.R, 0f, 0f, 0f, -1f, 0f, 0.0f);

			break;		
		case	CONTROL_lock:
//				MatrixState.setCamera( 0,0,Constant.R, 0f, 0f, 0f, -1f, 0f, 0.0f);
				MySurfaceView.lock=!MySurfaceView.lock;

				break;

//上面是控制条的选择程序，两次单机一个菜单有打开和关闭的效果，如果两次不是单机
//同一个菜单的话，那么就关闭其他菜单的使能端
		default:
			break;
		}
    	if(MySurfaceView.lock){
    		seekBar1.setVisibility(8);
			seekBar2.setVisibility(8);seekBar3.setVisibility(8);
    		
    	}
    		
		return true;
    	
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause(); 
    

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
    

    }
}

