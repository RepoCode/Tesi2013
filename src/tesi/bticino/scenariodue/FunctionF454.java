package tesi.bticino.scenariodue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import tesi.bticino.scenarioOpenWebNet.R;
import bticino.btcommlib.domain.highlevel.DefaultMsgEventStatus;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;

public class FunctionF454 {
	
	static String getFrameWithIp(int command){
		  String _frameWithIp = null;
		  
		  /*
		   * command 1 videosorveglianza
		   * "*6*31#IP1#IP2#IP3#IP4#PORTA*4000##"; MJPEG
		   * "*6*32#IP1#IP2#IP3#IP4#PORTA*4000##"; H264	
		   * -------------------------------------------
		   * command 2 chiamata
		   * "*7*31#IP1#IP2#IP3#IP4#PORTA*##"; MJPEG  -> 4
		   * "*7*32#IP1#IP2#IP3#IP4#PORTA*##"; H264	  -> 13
		   * -------------------------------------------
		   * command 3 audio
		   * *7*54#PORTA_RX#IP1#IP2#IP3#IP4#PORTA_TX##
		   */
		  
		  WifiManager wifiMan = (WifiManager) Variables._theContext.getSystemService(Context.WIFI_SERVICE);
		  WifiInfo wifiInf = wifiMan.getConnectionInfo();
		  int ipAddress = wifiInf.getIpAddress();
		  String _ip = String.format("%d#%d#%d#%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
		  
		  switch(command){
		  	case 1:
		  		_frameWithIp = "*6*32#"+_ip+"#20000*4000##";
		  		break;
		  	case 2:
		  		_frameWithIp = "*7*13#"+_ip+"#20000*##";
		  		break;
		  	case 3:
		  		_frameWithIp = "*7*54#10000#"+_ip+"#20000##";
		  }
		  
		  return _frameWithIp;
		  
	 }
	
	
	  
	  public static Date getData(){
		  	Calendar cal = Calendar.getInstance();
	        return cal.getTime();
	  }
	  
	  
	  public static void checkOpenFrame(DefaultMsgEventStatus mmes, Context context){
		  Variables.logger.info("CHECK");
		  String frame = mmes.getOpenRawMsg();
		  
		  if (frame.equalsIgnoreCase("*8*1#1#4#20*10##")){
			  sendSimpleNotification(context);
			  Variables._chiamataInArrivo=1;
			  Variables.logger.info("NOTIFICA");
			  Variables.dataChiamata= FunctionF454.getData();
		      SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ITALY);
		      Variables.dataITA = sdf.format(Variables.dataChiamata);
			  Variables.logger.error("DATA CHIAMATA"+Variables.dataChiamata.toString());
		  }  
		  
		  if (frame.equalsIgnoreCase("*6*9**##")){
			  Variables.mNotificationManager.cancel(Variables.NOTIFICATION_INGOING);
			  Variables._chiamataInArrivo=0;
		  }  
	
		  // "*7*0*##":   
		
	  }
	  
	 public static void sendSimpleNotification(Context _Contesto) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(_Contesto);
        // Titolo e testo della notifica
        notificationBuilder.setContentTitle("Nuova Chiamata");
        notificationBuilder.setContentText("Rispondi alla chiamata");
        // Testo che compare nella barra di stato non appena compare la notifica
        notificationBuilder.setTicker("Stanno Citofonando");
        // Data e ora della notifica
        notificationBuilder.setWhen(System.currentTimeMillis());
        // Icona della notifica
        notificationBuilder.setSmallIcon(R.drawable.ic_stat_ingoing);
        // Creiamo il pending intent che verrà lanciato quando la notifica
        // viene premuta
        Intent notificationIntent = new Intent(_Contesto, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(_Contesto, 0, notificationIntent, 0);       
        notificationBuilder.setContentIntent(contentIntent);
        // Impostiamo il suono, le luci e la vibrazione di default
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.FLAG_AUTO_CANCEL);        
        notificationBuilder.setAutoCancel(true); 
        Variables.mNotificationManager.notify(Variables.NOTIFICATION_INGOING, notificationBuilder.build());
	}
	  
	public static void deleteServiceIcon() {
		Variables.mNotificationManager.cancel(Variables.NOTIFICATION_PERSISTENT);
    }
	  
	public static void ServiceIcon(Context _Contesto) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(_Contesto);
        // Titolo e testo della notifica
        notificationBuilder.setContentTitle("Servizio Attivo");
        notificationBuilder.setContentText("Pronto a ricevere chiamate");
        // Testo che compare nella barra di stato non appena compare la notifica
        notificationBuilder.setTicker("Bticino in esecuzione");
        // Data e ora della notifica
        notificationBuilder.setWhen(System.currentTimeMillis());
        // Icona della notifica
        notificationBuilder.setSmallIcon(R.drawable.ic_stat_b);   
        notificationBuilder.setOngoing(true);    
        // Impostiamo il suono, le luci e la vibrazione di default
        //notificationBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
        Variables.mNotificationManager.notify(Variables.NOTIFICATION_PERSISTENT, notificationBuilder.build());
	}
	
	public static void deleteIncomingNotification() {
		Variables.mNotificationManager.cancel(Variables.NOTIFICATION_INGOING);
    }
	
	
}
