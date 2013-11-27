package tesi.bticino.scenariodue;

import java.util.ArrayList;

import tesi.bticino.scenarioOpenWebNet.R;
import bticino.btcommlib.communication.BTCommErr;
import com.gstreamer.GStreamer;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
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

public class IncomingCall extends FragmentActivity implements SurfaceHolder.Callback {

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

		setContentView(R.layout.activity_incoming_call);
		setupActionBar();
		
		ImageButton accept = (ImageButton) this.findViewById(R.id.button_accept);
		accept.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		// GESTIONE AUDIO
	    	}
	    });
	    
	    ImageButton refuse = (ImageButton) this.findViewById(R.id.button_refuses);
	    refuse.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		try {
	    			// CHIUDI TUTTO 
		    		// 1. invia open chiusura " *6*9**## "
		    		sendStopAll();
		    		// 2. chiudi activity
		    		startMain();
    			} catch (Exception e) {
    				finish(); 
    				return;
    			}	
	    	}
	    });
	    
	    SurfaceView sv = (SurfaceView) this.findViewById(R.id.surface_audio);
	    SurfaceHolder sh = sv.getHolder();
	    sh.addCallback(this);
	    
	    nativeInit();
	    
	    if(checkChiamataPersa()){
	    	//DIALOG CHIAMATA PERSA
	    	chiamataPersaDialog cpDialog = new chiamataPersaDialog();
	    	cpDialog.show(getSupportFragmentManager(), "chiamataPersaDialog");
	    	Variables.logger.error("CHIAMATA PERSA");
	    }else{
	    	Variables.logger.error("CHIAMATA ATTIVA");
	    	try {
		    	sendVideo();
		    	// CONTROLLO FINE CHIAMATA
		    	new Thread(new Runnable() {
		            public void run() {
		            	while (Variables._chiamataInArrivo==1){
				    		if(checkFineChiamata()){
				    		   fineChiamataDialog fcDialog = new fineChiamataDialog();
						       fcDialog.show(getSupportFragmentManager(), "fineChiamataDialog");
				    		}
				    	}
		            }
		        }).start();
			} catch (Exception e) {
				finish(); 
				return;
			}
	    }   
	}
	
	protected void onStart(){
		super.onStart();
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
        System.loadLibrary("Incoming");
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
    
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
		
	
	private void sendVideo() {
		  /* Set Frame di richiesta video --> 2 */
		String _frame= FunctionF454.getFrameWithIp(2);
		  if ((Variables._myOpenChan2 != null)) {
			  BTCommErr theErr;
			  ArrayList<String> outResult = new ArrayList<String>();
			  if (!isAValidOpenFrame(_frame)){
				  Variables.logger.info("Frame non valido");
			  }else {
				  theErr = Variables._myOpenChan2.send(_frame, outResult);
				  is_playing_desired = true;
				  nativePlay();
				  if (!theErr.isErr()) {
					  //FunctionF454.logFrame(FrameDir.TX, _frame);
					  Variables.logger.info("messaggio: video in esecuzione");
					  Toast.makeText(Variables._theContext,  "video in esecuzione", Toast.LENGTH_SHORT).show();
				  }else {
					  Variables.logger.info("messaggio: problema video");
					  Toast.makeText(Variables._theContext,  "problema video", Toast.LENGTH_SHORT).show();
					  toast.show();
				  }	
			  }
		  }else{
			  Variables.logger.info("messaggio: nessuna connessione");
		  	  Toast.makeText(Variables._theContext, "nessuna connessione" , Toast.LENGTH_SHORT).show();
		  }
	}
	
	
	private void sendStopAll() {
		/* Set Frame di chiusura e spegnimento */
		String _frame= "*6*9**##";
		  if ((Variables._myOpenChan2 != null)) {
			  BTCommErr theErr;
			  ArrayList<String> outResult = new ArrayList<String>();
			  if (!isAValidOpenFrame(_frame)){
				  Variables.logger.info("Frame non valido");
			  }	  
			  else {
				  theErr = Variables._myOpenChan2.send(_frame, outResult);
				  is_playing_desired = true;
				  nativePlay();
				  if (!theErr.isErr()) {
					  Variables.logger.info("messaggio: chiuso tutto");
				  }else {
					  Variables.logger.info("messaggio: non sono riuscito a chiudere");
				  }	
			  }
		  }else
			  Variables.logger.info("messaggio: nessuna connessione");
	  }
	
	
	  /* Controllo validità del Frame */
	  private boolean isAValidOpenFrame(String theFrame) {
		  boolean retCode = true;
		  if ((theFrame == null) || (theFrame.length() == 0) || (!theFrame.contains("*")) || (!theFrame.endsWith("##")))
			  retCode = false;
		  return (retCode);
	   }
	  
	    // Chiamato dal codice nativo. Setta il contenuto del messaggio relativo alla pipeline
	    private void setMessage(final String message) {
	        final TextView tv = (TextView) this.findViewById(R.id.textview_message2);
	        runOnUiThread (new Runnable() {
	          public void run() {
	            tv.setText(message);
	          }
	        });
	    }
	   
	    @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	            // do nothing; 
	            return false;
	        }
	        return super.onKeyDown(keyCode, event);
	    }
	    
	    // Dialog di avviso termine video!
	    @SuppressLint("ValidFragment")
		private class fineChiamataDialog extends DialogFragment {
	        @Override
	        public Dialog onCreateDialog(Bundle savedInstanceState) {
	            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	            builder.setMessage("Chiamata Terminata!")
	            	   .setPositiveButton("Chiudi", new DialogInterface.OnClickListener() {
	                       public void onClick(DialogInterface dialog, int id) {
	                           // chiudi IncomingCall
	                    	   startMain();	
	                       }
	                   });
	            return builder.create();
	        }
	     }
	    
	 // Dialog di avviso chiamata persa!
	    @SuppressLint("ValidFragment")
		private class chiamataPersaDialog extends DialogFragment {
	        @Override
	        public Dialog onCreateDialog(Bundle savedInstanceState) {
	            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	            builder.setMessage("Chiamata persa! Hanno citofonato alle: "+Variables.dataITA)
	            	   .setPositiveButton("Chiudi", new DialogInterface.OnClickListener() {
	                       public void onClick(DialogInterface dialog, int id) {
	                           // chiudi IncomingCall
	                    	   startMain();
	                       }
	                   });
	            return builder.create();
	        }
	     }
	    
	    private boolean checkChiamataPersa(){
	    	Variables.dataAttuale = FunctionF454.getData();	    	
	    	long diff = Variables.dataAttuale.getTime() - Variables.dataChiamata.getTime();
	    	if(diff >= 30000){
	    		Variables._chiamataInArrivo=0;
	    		return true;
	    	}else{
	    		return false;
	    	}	
	    }
	    
	    private boolean checkFineChiamata(){
	    	Variables.dataAttuale = FunctionF454.getData();
	    	long diff = Variables.dataAttuale.getTime() - Variables.dataChiamata.getTime();
	    	if(diff >= 30000){
	    		Variables._chiamataInArrivo=0;
	    		return true;
	    	}else{
	    		return false;
	    	}	
	    }
	    
	    private void startMain(){
	    	finish();
   			nativeFinalize();
   			Variables._chiamataInArrivo=0;
   			Intent i = new Intent(IncomingCall.this, MainActivity.class);
   			startActivity(i);
	    }
	    

}