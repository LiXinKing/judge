package com.example.prepare;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


import com.example.R;
import com.example.prepare.Waitting.MyBinder;



import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.StaticLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;



public class judge extends Activity{

	/**
	 * @param args
	 */
	private MyBinder binder;//用于接收service端的数据
	Timer timer0=new Timer();//读取数据的计时new
	static int Doneflag=0;
	public static ArrayList<float[]> mmprepareArrayList=new ArrayList<float[]>();
	Button button;
	
	private ServiceConnection conn=new ServiceConnection() {


		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			binder=(Waitting.MyBinder)service;
			timer0.schedule(new TimerTask() {
				@Override
				public void run() {
					Log.v("00", "12345");
					// TODO Auto-generated method stub					
					float[]m=binder.getparameter();
					Log.v("acct1", String.valueOf(m[0]));
					Log.v("acct2", String.valueOf(m[1]));
					Log.v("acct3", String.valueOf(m[2]));
					float offset=(float)Math.sqrt(m[0]*m[0]+
							m[1]*m[1]+(m[2])*(m[2]));
					float[] mm={m[0],m[1],m[2],m[3],m[4],m[5]};
				if(Doneflag<18){
					if((m[2]<11.0)&&(m[2]>9.4)){
					Doneflag++;
					mm[2]=mm[2]-9.8f;
					mmprepareArrayList.add(mm);
					Log.v("Doneflag111", String.valueOf(Doneflag));
					Log.v("Doneflag122", String.valueOf(mmprepareArrayList.size()));
							}
					else {
						Doneflag=0;
						mmprepareArrayList.removeAll(mmprepareArrayList);
					}
				}else  {
//					Doneflag=19;
//					button.setVisibility(0);
		        	Message message=new Message();
		        	message.what=1;
		        	handler.sendMessage(message);
				}
				}
			    Handler handler=new Handler(){
			    	public void handleMessage(Message msg){
			    		if(msg.what==1){
			    			button.setVisibility(0);
			    		}
			    	}
			    };
								
				
			}, 0,100);
	
	
	}
	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        final Intent intent =new Intent();
        intent.setAction("com.example.prepare.Waitting");
        bindService(intent, conn, Service.BIND_AUTO_CREATE);
		setContentView(R.layout.judge);	
		button=(Button)findViewById(R.id.prepare);
		button.setVisibility(8);
    	
    }
	public void onClick_prepare(View view) {
		

		ComponentName componentName=new ComponentName("com.example",
		"com.example.entrance.ready");
		Intent intent=new Intent();
		intent.setComponent(componentName);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		
	}

}
