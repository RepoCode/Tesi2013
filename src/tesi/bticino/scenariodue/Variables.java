package tesi.bticino.scenariodue;

import java.util.Date;
import android.app.NotificationManager;
import android.content.Context;
import android.net.wifi.WifiManager;
import bticino.btcommlib.communication.BTCommChan;
import bticino.btcommlib.communication.BTCommChanPar;
import bticino.btcommlib.communication.BTCommMgr;
import bticino.btcommlib.communication.IBTCommNotify;
import bticino.btcommlib.trace.BTLibLogger;

public class Variables {
	
	/*
	 * Variabili per la connessione a WebServerF454
	 */
	public static final String IP_ADDRESS = "192.168.1.35"; // indirizzo IP F454
	public static final String PORT_NUM = "20000"; // numero porta
	public static final String MONITOR = "RW"; 
	public static String OPEN_CMD = ""; 
	
	/*
	 * Variabili per la gestione della libreria BTCommLib
	 */
	
	/** Open Command Channel */
	public static BTCommChan _myOpenChan2 = null;
	public static BTCommMgr _cmdMgr = BTCommMgr.getInstance();
	/** Prepareare i parametri di connessione */
	public static BTCommChanPar _connPar = new BTCommChanPar();
	public static IBTCommNotify _myOpenListener = null;
	/** Oggetto logger */
	public static BTLibLogger logger = BTLibLogger.createLogger("ScenarioOpenTest");

	/*
	 *  Gestione del wifi 
	 */
	public static WifiManager.WifiLock _wifiLock = null;
	
	/*
	 * Gestore Notifiche
	 */
	public static NotificationManager mNotificationManager;
		
	/*
	 * Variabili di supporto
	 */
	public static String _regFilterString = "";
	public static boolean _logEnabled;
	public static boolean _logEvent;
	public static boolean _logCommand;
	
	public static Context _theContext = null;
	
	public static int _chiamataInArrivo = 0;
	public static int _fineChiamata = 0;
	public static int _firtStart = 0;
	public static int _chiamataPersa = 0;
	public static int _sessioneVideoSorv = 0;
	
	public static Date dataChiamata = null;
	public static Date dataAttuale = null;
	public static String dataITA = null;
		
	/*
	 * Costanti
	 */
	public static final String PREF = "Preferenze";
	public static final String START_AT_BOOT = "StartAtBoot";
	public static final Boolean DEFAULT_START_AT_BOOT_VALUE = false;
	public static final String TAG = "MyService";
	public static final int NOTIFICATION_INGOING = 1;
	public static final int NOTIFICATION_PERSISTENT = 2;
	  
}
