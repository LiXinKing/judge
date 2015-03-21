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

//������Ϊ���ݽ����࣬���������������������,�������滻����ߵ�����

public class Waitting extends Service
{

	  MyBinder binder=new MyBinder();
	  SensorManager mySensorManager;	//SensorManager��������	
	  Sensor mygyroscope; 	//�����Ǵ�����
	  Sensor myaccelerometer;    //���ٶȴ�����������������
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
        //���SensorManager����
        mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);	
        //��ȡȱʡ�������Ǵ�����
        mygyroscope=mySensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //��ȡȱʡ�����Լ��ٶȴ�����
        myaccelerometer=mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mySensorManager.registerListener(
				mySensorListener0, 		//��Ӽ���
				mygyroscope, 		//����������
				SensorManager.SENSOR_DELAY_NORMAL	//�������¼����ݵ�Ƶ��
		);
		//�������ٶȴ�����
	mySensorManager.registerListener(
				mySensorListener1, 		//��Ӽ���
				myaccelerometer, 		//����������
				SensorManager.SENSOR_DELAY_NORMAL	//�������¼����ݵ�Ƶ��
		);


	}
	public void onDestroy()
	{
		super.onDestroy();
		mySensorManager.unregisterListener(mySensorListener0);//ȡ��ע�������
		mySensorManager.unregisterListener(mySensorListener1);
		
	}
	public boolean onUnbind(Intent intent)
	{	
//		Log.v("jieshu", "kk");
		return true;
		
	}
	private SensorEventListener mySensorListener0 = 
		new SensorEventListener(){//����ʵ����SensorEventListener�ӿڵĴ�����������
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy){}
		@Override
		public void onSensorChanged(SensorEvent event){

				float []values=event.values;//��ȡ����������������
				float offset=(float)Math.sqrt(values[0]*values[0]+values[1]*values[1]+values[2]*values[2]);
				//�����Ǵ������仯
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
		new SensorEventListener(){//����ʵ����SensorEventListener�ӿڵĴ�����������
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy){}
		@Override
		public void onSensorChanged(SensorEvent event){
			Log.v("mm", "jiasu");

				float []values=event.values;//��ȡ����������������
				

				if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
				  
						k[0]=values[0];
						k[1]=values[1];
						k[2]=values[2];
						Log.v("TYPE_ACCELEROMETER", String.valueOf(k[0]));
						
				}
				}	
		};
	
	}
