package tesi.bticino.scenariodue;

import java.util.ArrayList;

import com.gstreamer.GStreamer;

import tesi.bticino.scenarioOpenWebNet.R;
import bticino.btcommlib.communication.BTCommErr;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;

public class VideoSurveillance extends FragmentActivity implements SurfaceHolder.Callback  {
	
	private native void nativeInit();     // Initialize native code, build pipeline, etc
	private native void nativeFinalize(); // Destroy pipeline and shutdown native code
	private native void nativePlay();     // Set pipeline to PLAYING
	private native void nativePause();    // Set pipeline to PAUSED
	private static native boolean nativeClassInit(); // Initialize native class: cache Method IDs for callbacks
	private native void nativeSurfaceInit(Object surface);
	private native void nativeSurfaceFinalize();
	private long native_custom_data;      // Native code will use this to keep private data
	 
	private boolean is_playing_desired;   // Whether the user asked to go to PLAYING
	
	private String _portValue = null;
	private String _ipAddressValue = null;
	private String _modeValue = null;
	private String _frame = null;;
	private Toast toast;
	private String  messaggio;
	
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		try {
	    	GStreamer.init(this);
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			finish(); 
			return;
		}
		
		setContentView(R.layout.activity_video_surv);
		setupActionBar();
		
		//bottone per l'avvio della sessione di videosorveglianza
	    final ImageButton surveillance = (ImageButton) this.findViewById(R.id.button_play_audio);
	    surveillance.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		try{
	    	    	sendVideo();
	    	    	if(Variables._sessioneVideoSorv == 1){
		    			surveillance.setEnabled(false);
			    		Handler h = new Handler();
			    	    h.postDelayed(new Runnable() {
			    	        public void run() {
			 					endVideoDialog evDialog = new endVideoDialog();
			 					evDialog.show(getSupportFragmentManager(), "endVideoDialog");
			 					Variables._sessioneVideoSorv = 0;
			 					nativeFinalize();
			    	        }}, 60000);
		    		}		
	    	    }catch (Exception e) {
	    			return;
	    	    }
	    		
	    	}
	    });
	    
	    //inizializzazione Survace visualizzazione video
	    SurfaceView sv = (SurfaceView) this.findViewById(R.id.surface_audio);
	    SurfaceHolder sh = sv.getHolder();
	    sh.addCallback(this);
	    
	    //inizializzazione GStreamer
	    nativeInit();
	}
	
			

	public boolean onOptionsItemSelected(MenuItem item) {
		if(Variables._sessioneVideoSorv == 0){
			switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				NavUtils.navigateUpFromSameTask(this);
				//startMainActivity();
				return true;
			}
			return super.onOptionsItemSelected(item);
		}else {
			switch (item.getItemId()) {
			case android.R.id.home:
				Toast.makeText(Variables._theContext, "Attendi!!!", Toast.LENGTH_SHORT).show();
				return true;
			}
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	private void onGStreamerInitialized () {
        Log.i ("GStreamer", "Gst initialized. Restoring state, playing:" + is_playing_desired);
        // Restore previous playing state
        if (is_playing_desired) {
            nativePlay();
        } else {
            nativePause();
        }
    }
 
    static {
        System.loadLibrary("gstreamer_android");
        System.loadLibrary("ScenarioDue");
        nativeClassInit();
    }

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("GStreamer", "Surface changed to format " + format + " width " + width + " height " + height);
        nativeSurfaceInit (holder.getSurface());
    }
 
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("GStreamer", "Surface created: " + holder.getSurface());
    }
 
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("GStreamer", "Surface destroyed");
        nativeSurfaceFinalize ();
    }
	
	
	private void sendVideo() {
		  /* Set Frame di richiesta video sorveglianza --> 1 */
		  String _frame= FunctionF454.getFrameWithIp(1);
		  if (Variables._myOpenChan2 != null) {
			  BTCommErr theErr;
			  ArrayList<String> outResult = new ArrayList<String>();
			  if (!isAValidOpenFrame(_frame)){
				//logError("Frame non valido ");
			  }	  
			  else {
				  theErr = Variables._myOpenChan2.send(_frame, outResult);
				  is_playing_desired = true;
				  nativePlay();
				  if (!theErr.isErr()) {
					  //FunctionF454.logFrame(FrameDir.TX, _frame);
					  messaggio = getString(R.string.messaggio_video_play);
					  Variables._sessioneVideoSorv = 1;
					  toast = Toast.makeText(Variables._theContext,  messaggio, Toast.LENGTH_SHORT);
					  toast.show();
				  }else {
					  messaggio = getString(R.string.messaggio_problema_video);
					  toast = Toast.makeText(Variables._theContext,  messaggio, Toast.LENGTH_SHORT);
					  toast.show();
				  }	
			  }
		  }else{
		  	  messaggio = getString(R.string.messaggio_no_connessione);
		  	  toast = Toast.makeText(Variables._theContext,  messaggio, Toast.LENGTH_SHORT);
		  	  toast.show();
		  }
			 
	  }
	
	 /* Controllo validità del Frame */
	  private boolean isAValidOpenFrame(String theFrame) {
		  boolean retCode = true;
		  if ((theFrame == null) || (theFrame.length() == 0) || (!theFrame.contains("*")) || (!theFrame.endsWith("##")))
			  retCode = false;
		  return (retCode);
	  }
	  
	// Chiamata dal codice nativo. Imposta lo stato della pipeline, visualizzato nella UI.
	    private void setMessage(final String message) {
	        final TextView tv = (TextView) this.findViewById(R.id.text_message_sorv);
	        runOnUiThread (new Runnable() {
	          public void run() {
	            tv.setText(message);
	          }
	        });
	    }
	    
	    // Dialog di avviso termine video (60 secondi)!
	    @SuppressLint("ValidFragment")
		public class endVideoDialog extends DialogFragment {
	        @Override
	        public Dialog onCreateDialog(Bundle savedInstanceState) {
	            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	            builder.setMessage(R.string.dialog_endVideo)
	            		.setPositiveButton(R.string.endVideo, new DialogInterface.OnClickListener() {
	                       public void onClick(DialogInterface dialog, int id) {
	                           //chiudi l'activity
	                    	   finish();
	                    	   NavUtils.navigateUpFromSameTask(VideoSurveillance.this);	   
	                       }
	                   });
	            return builder.create();
	        }
	    }
	    
	    @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        	if(Variables._sessioneVideoSorv == 0){
	        		finish();
	        		NavUtils.navigateUpFromSameTask(this);
					return true;
				}else{
					//non fare niente
					Toast.makeText(Variables._theContext, "Attendi!!!" , Toast.LENGTH_SHORT).show();
					return false;
				}
	        }
	        return super.onKeyDown(keyCode, event);
	    }
	    
	    
	    public void startMainActivity(){
	    	finish();
			Intent i = new Intent(VideoSurveillance.this, MainActivity.class);
			startActivity(i);
	    }

}
