package pntanasis.android.metronome;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class MetronomeActivity extends Activity implements GestureDetector.OnGestureListener {
	
    private GestureDetector gestureDetector;
	
	private short bpm = 100;
	private short noteValue = 1;
	private short beats = 1;
	private double beatSound = 2440;
	private double sound = 6440;
    private MetronomeAsyncTask metroTask;
    private TextView currentBeat;
    private TextView bpmText;
    
    private Handler mHandler;
    
    // have in mind that: http://stackoverflow.com/questions/11407943/this-handler-class-should-be-static-or-leaks-might-occur-incominghandler
    // in this case we should be fine as no delayed messages are queued
	@SuppressLint("HandlerLeak")
	private Handler getHandler() {
    	return new Handler() {
        };
    }
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.gestureDetector = new GestureDetector(this, this);
	    Log.d("Gesture Example", "created!");        
        metroTask = new MetronomeAsyncTask();
        /* Set values and listeners to buttons and stuff */
        
        bpmText = (TextView) findViewById(R.id.bps);
        bpmText.setText(""+bpm);
        
        currentBeat = (TextView) findViewById(R.id.currentBeat);
        currentBeat.setTextColor(Color.GREEN);        
      
    }
    
    public void onPause(){
    	super.onPause();
	    Log.d("Gesture Example", "paused");    	
		metroTask.stop();
		metroTask = new MetronomeAsyncTask();
		Runtime.getRuntime().gc();
    }
    
    public void onStop(){
    	super.onStop();
	    Log.d("Gesture Example", "stopped");    	
		metroTask.stop();
		metroTask = new MetronomeAsyncTask();
		Runtime.getRuntime().gc();
    }    
    
    public void onDestroy() {
    	super.onDestroy();  // Always call the superclass
	    Log.d("Gesture Example", "destroyed");
    	metroTask.stop();
		metroTask = new MetronomeAsyncTask();
		Runtime.getRuntime().gc();
        // Stop method tracing that the activity started during onCreate()
        android.os.Debug.stopMethodTracing();
    }
        
   
    private class MetronomeAsyncTask extends AsyncTask<Void,Void,String> {
    	Metronome metronome;
    	
    	MetronomeAsyncTask() {
            mHandler = getHandler();
    		metronome = new Metronome(mHandler);
    	}

		protected String doInBackground(Void... params) {
			metronome.setBeat(beats);
			metronome.setNoteValue(noteValue);
			metronome.setBpm(bpm);
			metronome.setBeatSound(beatSound);
			metronome.setSound(sound);

			metronome.play();
			
			return null;			
		}
		
		public void stop() {
			metronome.stop();
			metronome = null;
		}
		
		public void setBpm(short bpm) {
			metronome.setBpm(bpm);
			metronome.calcSilence();
		}    	
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return true;
    }
    
    
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
	    Log.d("Gesture Example", "onFling: velocityX:" + velocityX + " velocityY:" + velocityY);
/*	    if (velocityX < -2000) {
			bpm-=20;
			if(bpm <= minBpm)
				bpm = minBpm;
	    	TextView bpmText = (TextView) findViewById(R.id.bps);
	        bpmText.setText(""+bpm);
	        metroTask.setBpm(bpm);
			return true;
	    } else if (velocityX > 2000) {
			bpm+=20;
			if(bpm >= maxBpm)
				bpm = maxBpm;
	    	TextView bpmText = (TextView) findViewById(R.id.bps);
	        bpmText.setText(""+bpm);
	        metroTask.setBpm(bpm);
			return true;
	    } */
	    return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
	    Log.d("Gesture Example", "onScroll: distanceX:" + distanceX + " distanceY:" + distanceY);
	    if (distanceX < -1) {
	        Log.d("Gesture Example", "OnScrollLeft");
	    	bpm++;
	    	TextView bpmText = (TextView) findViewById(R.id.bps);
	        bpmText.setText(""+bpm);
	        metroTask.setBpm(bpm);
	    } else if (distanceX > 1) { 
	        Log.d("Gesture Example", "OnScrollLeft");
	    	bpm--;
	    	TextView bpmText = (TextView) findViewById(R.id.bps);
	        bpmText.setText(""+bpm);
	        metroTask.setBpm(bpm);
	    } return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	    Log.d("Gesture Example", "onShowPress");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
    	Button button = (Button)findViewById(R.id.startstop);
    	String buttonText = button.getText().toString();
    	if(buttonText.equalsIgnoreCase("start")) {
    		button.setText(R.string.stop);
    		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
    			metroTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    		else
    			metroTask.execute();    		
    	} else {
    		button.setText(R.string.start);    	
    		metroTask.stop();
    		metroTask = new MetronomeAsyncTask();
    		Runtime.getRuntime().gc();
    	}
    	return true;
	}

    
}