package com.example.movement;



import com.example.R;
import com.example.entrance.ready;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class MovementMethod extends Activity {
	static float screenWidth;
	static float screenHeight;
    private MovementView mMovementView;
    public void onCreate(Bundle savedInstanceState) {
    	Log.v("MovementMethod", "OK");
		WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        Log.v("screen", String.valueOf(screenWidth));
        Log.v("screen", String.valueOf(screenHeight));
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mMovementView = new MovementView(this);
		setContentView(R.layout.movement);
		LinearLayout ll = (LinearLayout) findViewById(R.id.movement);
		ll.addView(mMovementView);
		
    }
	public boolean onTouchEvent(MotionEvent e){
		MovementMethod.this.finish();
		return true;	
	}
    @Override
    protected void onResume() {
      super.onResume();
        mMovementView.onResume();  
        
    }

    @Override
    protected void onPause() {
    
    	MovementMethod.this.finish(); //不结束掉被其它程序占用内存返回后出错，初步猜测与该程序的线程有关
        super.onPause();
    }    
    protected void onDestroy(){
        super.onDestroy();
        ready.i=0;
    }
}
