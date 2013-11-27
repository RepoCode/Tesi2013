package tesi.bticino.scenariodue;

import tesi.bticino.scenarioOpenWebNet.R;
import bticino.btcommlib.communication.BTChanTypes;
import bticino.btcommlib.communication.BTCommErr;
import bticino.btcommlib.communication.IBTCommNotify;
import bticino.btcommlib.domain.highlevel.DefaultMsgEventStatus;
import bticino.btcommlib.domain.highlevel.DefaultMsgEventStatusDimension;
import bticino.btcommlib.domain.util.BTOpenMsgType;
import bticino.btcommlib.exceptions.LibException;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class IncomingService extends Service {

	private String _portValue = null;
	private String _ipAddressValue = null;
	private String _modeValue = null;
	public Context _ThisContext;
	
	  @Override
	  public IBinder onBind(Intent intent) {
		return null;
	  }
	
	  @Override
	  public void onCreate() {
		Log.d(Variables.TAG, "onCreate");
		Variables.mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		if (wifiManager != null) {
			Variables._wifiLock = wifiManager.createWifiLock("Backup wifi lock");
		}
		
		_ThisContext= IncomingService.this;
		
	  }

	  @Override
	  public void onDestroy() {
		Toast.makeText(this, "IncomingService Stoppato", Toast.LENGTH_LONG).show();
		Log.d(Variables.TAG, "onDestroy");
		FunctionF454.deleteServiceIcon();
		Variables._firtStart = 0;
		
	  }
	
	@Override
	  public void onStart(Intent intent, int startid) {
		if(Variables._firtStart==0){
			Toast.makeText(this, "IncomingService Avviato", Toast.LENGTH_LONG).show();
			Variables._firtStart=1;
		}
		Log.d(Variables.TAG, "onStart");
		FunctionF454.ServiceIcon(_ThisContext);
		connectionToF454();
		
		
	 }
	
	  public void connectionToF454(){
		  Variables._wifiLock.acquire();
		  Variables.logger.info("CONNECTION START");
		try {
			  //Get Open Command Channel
			  Variables._myOpenChan2 = Variables._cmdMgr.getCommChan(BTChanTypes.Open);
			  //Select events to handle
			  Variables._myOpenChan2.handleEvent(BTOpenMsgType.EventStatus.name(), DefaultMsgEventStatus.class);
			  Variables._myOpenChan2.handleEvent(BTOpenMsgType.EventStatusDimension.name(), DefaultMsgEventStatusDimension.class);
	  
			  //Create listener for events and errors
			  Variables._myOpenListener = new IBTCommNotify() {
				  @Override
				  public void notifyEvent(Object highLevelEventMsg) {
					  Variables.logger.info("OnEventReceived: "+ highLevelEventMsg.getClass().getName());
					  DefaultMsgEventStatus mmes = (DefaultMsgEventStatus) highLevelEventMsg;
					  Variables.logger.info("NOTA: "+mmes.getOpenRawMsg());
					  FunctionF454.checkOpenFrame(mmes, _ThisContext);
				  }
				  @Override
				  public void notifyError(BTCommErr errorMsg) {
					  Variables.logger.error("OnErrorReceived Error: " + errorMsg.getErrEnum() + " Error Message: " + errorMsg.getErrArgs() + " Error Arguments: " + errorMsg.getErrArgs());
					  
				  }
			  };
			  //Register for events/errors notify
			  Variables._myOpenChan2.addListner(Variables._myOpenListener);
	
		  } catch (LibException e) {
			  Variables.logger.error(e.getMessage());
		  	  return;
		  } 
		  
		  Variables.logger.info("PARAMETRI SETTATI"); 	
		  /* Set parametri di connessione */
		  _portValue = Variables.PORT_NUM;
		  _ipAddressValue = Variables.IP_ADDRESS;
		  _modeValue = Variables.MONITOR;
		  // setting parametri connessione canale bticino
		  Variables._connPar.setConnPar(getString(R.string.IP), _ipAddressValue);
		  Variables._connPar.setConnPar(getString(R.string.PORT), _portValue);
		  Variables._connPar.setConnPar(getString(R.string.MODE), _modeValue);		
		  Variables.logger.info("CHIAMO ASYNK"); 
		  //connessione al gateway
		  new ConnectionAsynk().execute();
		  if(Variables._firtStart==0){
			  Toast.makeText(this, "Connessione Avvenuta", Toast.LENGTH_LONG).show();
		  }
		  Variables._wifiLock.release();
	}

	/* Handler per la gestione connessione */
	  public class ConnectionAsynk extends AsyncTask<Activity, Integer, BTCommErr> {
		  protected BTCommErr doInBackground(Activity... actpar) {
			  // connessione
			  return Variables._myOpenChan2.connect(Variables._connPar);
		  }

		  protected void onProgressUpdate(Integer... progress) {
			  //setProgressPercent(progress[0]);
		  }

		  protected void onPostExecute(Long result) {
			  //showDialog("Downloaded " + result + " bytes");
		  }
	  }
	  
}

