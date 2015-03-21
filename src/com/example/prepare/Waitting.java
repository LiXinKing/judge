package com.example.prepare;



import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

//此类作为数据接收类，传过来的数据在这里接收,在这里替换长宽高的数据

public class Waitting extends Service
{

	  MyBinder binder=new MyBinder();
	  SensorManager mySensorManager;	//SensorManager对象引用	
	  Sensor mygyroscope; 	//陀螺仪传感器
	  Sensor myaccelerometer;    //加速度传感器（包括重力）
	  float []k={0,0,0,0,0,0};
	  float GYRMIN=0,ACCMIN=0;


    public   class MyBinder extends Binder{
    		float[] getparameter(){
    				float[]k0=k;
    					return k0;
	
    					}
    		}
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
	
		return binder;
	}
	public void onCreate(){	
		Log.v("kaishi","kk");
		super.onCreate();
        //获得SensorManager对象
        mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);	
        //获取缺省的陀螺仪传感器
        mygyroscope=mySensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //获取缺省的线性加速度传感器
        myaccelerometer=mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mySensorManager.registerListener(
				mySensorListener0, 		//添加监听
				mygyroscope, 		//传感器类型
				SensorManager.SENSOR_DELAY_NORMAL	//传感器事件传递的频度
		);
		//监听加速度传感器
	mySensorManager.registerListener(
				mySensorListener1, 		//添加监听
				myaccelerometer, 		//传感器类型
				SensorManager.SENSOR_DELAY_NORMAL	//传感器事件传递的频度
		);


	}
	public void onDestroy()
	{
		super.onDestroy();
		mySensorManager.unregisterListener(mySensorListener0);//取消注册监听器
		mySensorManager.unregisterListener(mySensorListener1);
		
	}
	public boolean onUnbind(Intent intent)
	{	
//		Log.v("jieshu", "kk");
		return true;
		
	}
	private SensorEventListener mySensorListener0 = 
		new SensorEventListener(){//开发实现了SensorEventListener接口的传感器监听器
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy){}
		@Override
		public void onSensorChanged(SensorEvent event){

				float []values=event.values;//获取传感器的三个数据
				float offset=(float)Math.sqrt(values[0]*values[0]+values[1]*values[1]+values[2]*values[2]);
				//陀螺仪传感器变化
				if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
					if(offset>GYRMIN){

						k[3]=(float) ((float)360*(values[0]/Math.PI));
						k[4]=(float) ((float)360*(values[1]/Math.PI));
						k[5]=(float) ((float)360*(values[2]/Math.PI));
						//values[0]*0.02
						}
				}	
			}
			
		
	};
	private SensorEventListener mySensorListener1 = 
		new SensorEventListener(){//开发实现了SensorEventListener接口的传感器监听器
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy){}
		@Override
		public void onSensorChanged(SensorEvent event){
			Log.v("mm", "jiasu");

				float []values=event.values;//获取传感器的三个数据
				

				if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
				  
						k[0]=values[0];
						k[1]=values[1];
						k[2]=values[2];
						Log.v("TYPE_ACCELEROMETER", String.valueOf(k[0]));
						
				}
				}	
		};
	
	}
