package tesi.bticino.scenariodue;

import tesi.bticino.scenarioOpenWebNet.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;



public class MainActivity extends FragmentActivity {

	  static final int PROGRESS_DIALOG = 0;
	  static int nuovachiamata = 0;
	  ProgressDialog progressDialog = null;
	  
	  static Intent intent_service;

	  public static SharedPreferences _sharedSettings = null;
	  
	 
	  // Called when the activity is first created.
	  @Override
	  public void onCreate(Bundle savedInstanceState)
	  {
		  super.onCreate(savedInstanceState);
	        
		  Variables._theContext = this;
	      
	      setContentView(R.layout.activity_main);
	      
	      // Preferences manager
		  _sharedSettings = getSharedPreferences(Variables.PREF, 0);
		  		   
		  
		  Button start_video = (Button) this.findViewById(R.id.button_start_video_sorv);
		  start_video.setOnClickListener(new OnClickListener() {
			  public void onClick(View v) {
				  startVideoSurveillanceActivity();
			  }
		  });
		  
		  Button start_audio = (Button) this.findViewById(R.id.button_start_audio);
		  start_audio.setOnClickListener(new OnClickListener() {
			  public void onClick(View v) {
				  startTestAudioActivity();
			  }
		  });
		       
		  intent_service = new Intent(this, IncomingService.class);
		  startService(intent_service);	
		
	    } 
	 
	    protected void onDestroy() {
	        super.onDestroy();
	    }
	    
	    	 
	    protected void onStart(){
	    	super.onStart();
	    	
	    }
	    
	    @Override
		 protected void onResume() {   
		    super.onResume();
		    if (Variables._chiamataInArrivo==1){
			  startIncomingCallActivity();
			}else{
				nuovachiamata=1;
			}
		    
		    // controllo chiamata in arrivo mentre main in esecuzione
		    new Thread(new Runnable() {
	            public void run() {
	            	while (true){
	    		    	if(checkNuovaChiamata() && nuovachiamata==1){
	    		    		nuovachiamata=0;
	    		    		Variables.logger.error("CHIAMATA IN ARRIVO");
	    		    		FunctionF454.deleteIncomingNotification();
	    		    		finish();
	    		    		Intent i = new Intent(MainActivity.this, IncomingCall.class);
	    					startActivity(i);	
	    					break;
	    		    	}
	    		    }
	            }
	        }).start();
		    
		  }

		  /* Creazione del Menu Opzioni */
		  @Override
		  public boolean onCreateOptionsMenu(Menu menu) {
		      // Inflate gli elementi del menu da usare nella action bar
		      MenuInflater inflater = getMenuInflater();
		      inflater.inflate(R.menu.menu_main, menu);
		      return super.onCreateOptionsMenu(menu);
		  }
	
		  public boolean onOptionsItemSelected(MenuItem item) {
			  // setting delle funzionalita degli item del menu
			  switch (item.getItemId()) {
			  case R.id.action_exit:
				   finish();
				   break;
			  case R.id.action_video:
				  startVideoSurveillanceActivity();
			      break;	
			  case R.id.action_info:
				  startInfoActivity();
			      break;
			  case R.id.action_close:
				  endServiceDialog esDialog = new endServiceDialog();
				  esDialog.show(getSupportFragmentManager(), "endServiceDialog");
			      break;	
			  }  
			  return true;
		}  
		
		private void startVideoSurveillanceActivity(){
			Intent i = new Intent(MainActivity.this, VideoSurveillance.class);
			startActivity(i);
		}
		
		private void startTestAudioActivity(){
			Intent i = new Intent(MainActivity.this, TestAudio.class);
			startActivity(i);
		}
		
		private void startIncomingCallActivity(){
			finish();
			Intent i = new Intent(MainActivity.this, IncomingCall.class);
			startActivity(i);
		}
		
		private void startInfoActivity(){
			Intent i = new Intent(MainActivity.this, Information.class);
			startActivity(i);
		}
		
		// Dialog di avviso interruzione del servizio (uscita dall'app)!
	    @SuppressLint("ValidFragment")
		private class endServiceDialog extends DialogFragment {
	        @Override
	        public Dialog onCreateDialog(Bundle savedInstanceState) {
	            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	            builder.setMessage(R.string.dialog_endService)
	            	   .setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {
	                       public void onClick(DialogInterface dialog, int id) {
	                           // chiudi l'activity e il service e disconnetti
	                    	   Variables._myOpenChan2.disconnect();
	                    	   finish();
	         			       stopService(intent_service);     
	                       }
	                   })
			           .setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int id) {
		                        // non fare niente 	   
		                    }
		               });
	            return builder.create();
	        }
	    }
	    
	    private boolean checkNuovaChiamata(){
	    	if(Variables._chiamataInArrivo==1){
	    		return true;
	    	}else{
	    		return false;
	    	}
	    }
}