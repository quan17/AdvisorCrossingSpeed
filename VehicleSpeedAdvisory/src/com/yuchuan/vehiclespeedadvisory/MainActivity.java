package com.yuchuan.vehiclespeedadvisory;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.yuchuan.speedoview.SpeedometerGauge;

public class MainActivity extends Activity {
	
	private Timer timerGr;
	private SpeedometerGauge speedometer;
	float SpeedO=0;
	
	LinearLayout linear;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		acquireWakeLock();
		
		locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		GetProvider();
		OpenGPS();
		location = locationManager.getLastKnownLocation(provider);
		UpdateWithNewLocation(location);
		locationManager.requestLocationUpdates(provider, 200, (float) 0.05, locationListener);
		
	
//		 timerGr = new Timer();
////		 Timer timerAdS = new Timer(); 
//	        timerGr.scheduleAtFixedRate(new MyTimeTask(), 2000, 1000);
//	        timerGr.scheduleAtFixedRate(new MySpeedTask(), 2000, 1000);
	        
        ResultShowing =(TextView) findViewById(R.id.message); 
        SignalShowing =(TextView) findViewById(R.id.SignalStatus); 
		
	        speedometer = (SpeedometerGauge) findViewById(R.id.speedometer);
	        speedometer.setMaxSpeed(70);
	        speedometer.setLabelConverter(new SpeedometerGauge.LabelConverter() {
	            @Override
	            public String getLabelFor(double progress, double maxProgress) {
	                return String.valueOf((int) Math.round(progress));
	            }
	        });
	        speedometer.setMaxSpeed(70);
	        speedometer.setMajorTickStep(10);
	        speedometer.setMinorTicks(4);
//	        speedometer.addColoredRange(10, 55, Color.GREEN);
//	        speedometer.setSpeed(SpeedO);
	        speedometer.setSpeed(0);
	        
	        
	        linear = (LinearLayout) findViewById(R.id.parent);

			ViewTreeObserver vto2 = linear.getViewTreeObserver();
			vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					linear.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					int size = 0;
					if (linear.getHeight() >= linear.getWidth()) {
						size = linear.getHeight() - 10;
						Log.e("Sizes", ""+size);
					} else {
						size = linear.getWidth() - 10;
						Log.e("Sizes", ""+size);
					}
					speedometer.setLayoutParams(new LayoutParams(size, size));
				}
			});
	        
	}
	
	
	@Override
    public void onDestroy(){
    	super.onDestroy();
    	releaseWakeLock();
    	timerGr.cancel();
    }
	
	@Override
	protected void onStart(){
		super.onStart();
//		speedometer.setSpeed(SpeedO);
		timerGr = new Timer();
//		timerGr.scheduleAtFixedRate(new MyChecklocationTask(), 2000, 1000);
//		if(CheckLoation(location)){
	//		Timer timerAdS = new Timer(); 
	        timerGr.scheduleAtFixedRate(new MyTimeTask(), 2000, 1000);
	        timerGr.scheduleAtFixedRate(new MySpeedTask(), 2000, 1000);
//		}
		
	}
	
//	private class MyChecklocationTask extends TimerTask{ 
//        @Override 
//        public void run() { 
//        	if(CheckLoation(location)){
//        		//		Timer timerAdS = new Timer(); 
//        		        timerGr.scheduleAtFixedRate(new MyTimeTask(), 2000, 1000);
//        		        timerGr.scheduleAtFixedRate(new MySpeedTask(), 2000, 1000);
//        			}
//               
//        }
//    }
//	

	
	/*------------------ Green Remain Time module-----------------------  */
	TextView ResultShowing=null;
	TextView SignalShowing=null;
	int GrTime;
	
//	Resources resources=getBaseContext().getResources();
//	Drawable SignalDrawable=resources.getDrawable(R.drawable.textview_style);
	
	private Handler mHandler = new Handler(){ 
         
	        public void handleMessage(Message msg) { 
	            switch (msg.what) { 
	            case 1: 
	                ResultShowing.setText("GreenRamain:"+GrTime) ;
	                if(GrTime==-2) 
//	                	SignalDrawable.setColorFilter(Color.YELLOW, null);
//	                	SignalShowing.setBackgroundColor(Color.YELLOW);
	                	SignalShowing.setBackgroundResource(R.drawable.textview_style_yellow);
	                else{
	                	if(GrTime<1)
//	                		 SignalDrawable.setColorFilter(Color.YELLOW, null);
//	                		SignalShowing.setBackgroundColor(Color.RED);
	                		SignalShowing.setBackgroundResource(R.drawable.textview_style_red);
	                	else 
//	                		 SignalDrawable.setColorFilter(Color.YELLOW, null);
//	                		SignalShowing.setBackgroundColor(Color.GREEN);
	                		SignalShowing.setBackgroundResource(R.drawable.textview_style_green);
	                }
	                break;
	            case 2:
	            	SpeedRange(AdSpeed);
	            	break;
	            } super.handleMessage(msg);  
	        }; 
	    };
	private class MyTimeTask extends TimerTask{ 
        @Override 
        public void run() { 
        	GrTime=DBManagerSignal.RunJDBC();
            Message message = new Message(); 
            message.what = 1; 
            mHandler.sendMessage(message); 
               
        }
    }
	/*------------------ Green Remain Time module-----------------------  */
	
	int AdSpeed=0;
	private class MySpeedTask extends TimerTask{ 
        @Override 
        public void run() { 
        	AdSpeed=DBManagerAdvSpeed.getAdvSpeed();
            Message message = new Message(); 
            message.what = 2; 
            mHandler.sendMessage(message); 
               
        }
    }
	
	
	
	
	
	
	/*------------------ GPS module-----------------------  */
	
	private LocationManager locationManager;
	private String provider;
	private Location location;
	

	private float UnitSpeed = 2.2369f;
	private String UnitSpeedString = "MPH";
	
	private void OpenGPS() {        

        if (locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
          ||locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)) {
           Toast.makeText(this, "GPS already Set", Toast.LENGTH_SHORT).show();
           return;
        }

        Toast.makeText(this, "GPS not set yet", Toast.LENGTH_SHORT).show();
//       	Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
//        	startActivityForResult(intent,0); 
    }

    private void GetProvider(){

          Criteria criteria = new Criteria();
          criteria.setAccuracy(Criteria.ACCURACY_FINE);
          criteria.setAltitudeRequired(false);
          criteria.setBearingRequired(true);
          criteria.setCostAllowed(true);
          criteria.setSpeedRequired(true);
          criteria.setPowerRequirement(Criteria.POWER_LOW);
//        provider = locationManager.GPS_PROVIDER;
          provider = locationManager.getBestProvider(criteria,true);   
       }

    private final LocationListener locationListener = new LocationListener(){
    	@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			UpdateWithNewLocation(location);
			
		}
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			UpdateWithNewLocation(null);
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}
    };
    
    private void UpdateWithNewLocation(Location location) {
        String latLongString;
//        TextView myLocationText= (TextView)findViewById(R.id.BMWSpeedTitle);
        if (location != null) {
        	double lat =location.getLatitude();
        	double lng =location.getLongitude();
        	latLongString = "Latitude:" + lat + "/nLongitude:" + lng;
        }
        else {
        latLongString = "None GPS Info";
        }
        try{
        UseSpeed(location);
        } catch (Exception e) {
      	  e.printStackTrace();
//      	TextView TestView=(TextView)findViewById(R.id.GnCDTitle);
//		 TestView.setText("No speed detection");
      	 }

    }
    private void UseSpeed(Location location){
    	
    	SpeedO=location.getSpeed()*UnitSpeed;
    	 speedometer.setSpeed(SpeedO);
//		TextView TestView=(TextView)findViewById(R.id.);
//		TestView.setText(String.valueOf(SpeedO)+UnitSpeedString);
		
    }
    
//    private boolean CheckLoation(Location location){
//		double CheckDis=gps2m(location.getLatitude(),location.getLongitude(),40.744716, -74.179836);
//		Log.e("Distance", ""+CheckDis);
//		float[] results=new float[1];
//		location.distanceBetween(40.449439,-74.095391,40.744716, -74.179836, results);
//		Log.e("Distance2", ""+results[0]);
////		if (results[0]<91.44)
//		if (CheckDis<91.44)
//			return true;
//		else
//			return false;
//    	
//
//    }
    
    
    private final double EARTH_RADIUS = 6378137.0;  

    private double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {

           double radLat1 = (lat_a * Math.PI / 180.0);
           double radLat2 = (lat_b * Math.PI / 180.0);
           double a = radLat1 - radLat2;
           double b = (lng_a - lng_b) * Math.PI / 180.0;
           double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                  + Math.cos(radLat1) * Math.cos(radLat2)
                  * Math.pow(Math.sin(b / 2), 2)));
           s = s * EARTH_RADIUS;
           s = Math.round(s * 10000) / 10000;
           return s;
        }
    
    /*------------------ GPS module-----------------------  */
    
 
    
    private void SpeedRange(int MSpeed){
    	if(MSpeed==0){
    			speedometer.addColoredRange(0, 100, Color.WHITE);
    			SignalShowing.setText("Prepare to \n Stop");
    	}
    	else 
    		if(MSpeed>0)
    		{
    			if(MSpeed<10)	MSpeed=10;
    			speedometer.addColoredRange(0, 100, Color.WHITE);
    			speedometer.addColoredRange(10, MSpeed, Color.RED);
    			SignalShowing.setText("10~"+MSpeed+"\n"+"MPH");
    		}        	
    		else
    		{
    			if(-MSpeed>55)	MSpeed=-55;
    			speedometer.addColoredRange(0, 100, Color.WHITE);
    			speedometer.addColoredRange(-MSpeed, 55, Color.RED);
    			SignalShowing.setText(""+-MSpeed+"~55\n"+"MPH");
    		}
    	
    }
    
    
    
    WakeLock wakeLock = null;  
    //acquireWakeLock£¬keep cpu working  
    
	private void acquireWakeLock()  
    {  
        if (null == wakeLock)  
        {  
            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);  
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,  this.getClass().getCanonicalName());
//           wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "PostLocationService");  
            if (null != wakeLock && !wakeLock.isHeld())   
            {  
                wakeLock.acquire(); 
            }  
        }  
    }  
      
    //releaseWakeLock
    private void releaseWakeLock()  
    {  
        if (null != wakeLock&& wakeLock.isHeld())  
        {  
            wakeLock.release();  
            wakeLock = null;  
        }  
    }  

    
}
