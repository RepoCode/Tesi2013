package tesi.bticino.scenariodue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceReceiver extends BroadcastReceiver{

	private static final String TAG = "TiwizTutorial";

	@Override
	public void onReceive(Context context, Intent intent) {
		//controllo le preferenze condivise per essere sicuro di dover avviare il servizio
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {  
			   Intent pushIntent = new Intent(context, IncomingService.class);  
			   context.startService(pushIntent);  
		}else
			Log.d(TAG,"Ricevuto intent diverso: " + intent.getAction());
	}

}
