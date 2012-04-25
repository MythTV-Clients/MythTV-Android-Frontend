/**
 * 
 */
package org.mythtv.client.service;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import org.mythtv.client.MainApplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class MythtvBackendConnectionService extends Service implements ServiceListener {

	private static final String TAG = MythtvBackendConnectionService.class.getSimpleName();

    public static final String BROADCAST_ACTION = "org.mythtv.broadcast.mythtvBackendConnectionEstablished";
    private final Handler handler = new Handler();
	private Intent broadcastIntent;

    private MainApplication applicationContext;
    
	private static final String MYTHTV_MASTER_BACKEND_TYPE = "_mythtvbackend-master._tcp.local.";

	private static JmDNS zeroConf = null;
	private static MulticastLock mLock = null;

	/* (non-Javadoc)
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
		Log.d( TAG, "onCreate : enter" );

		broadcastIntent = new Intent( BROADCAST_ACTION );	

		Log.d( TAG, "onCreate : exit" );
    }
    
	/* (non-Javadoc)
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@Override
	public void onStart( Intent intent, int startId ) {
		Log.d( TAG, "onStart : enter" );

		handler.removeCallbacks( sendUpdatesToUI );
		
		applicationContext = (MainApplication) getApplicationContext();
		
		try {
			startProbe();
		} catch( IOException e ) {
			Log.d( TAG, "onStart : error", e );
		}
		
		Log.d( TAG, "onStart : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		Log.d( TAG, "onDestroy : enter" );

		try {
			if( null != zeroConf ) {
				stopProbe();
			}
		} catch( IOException e ) {
			Log.v( TAG, "onDestroy : error", e );
		}

		Log.d( TAG, "onDestroy : enter" );
	}

	@Override
	public IBinder onBind( Intent intent ) {
		Log.d( TAG, "onBind : enter" );
		Log.d( TAG, "onBind : exit" );
		return null;
	}

	// internal helpers
	
	private Runnable sendUpdatesToUI = new Runnable() {
    
		public void run() {
			Log.d( TAG, "Thread.sendUpdatesToUI.run : enter" );

			sendBroadcast( broadcastIntent );

    	    Log.d( TAG, "Thread.sendUpdatesToUI.run : exit" );
    	}

	};
    
	private void setMasterBackend( String masterBackend ) {
		Log.d( TAG, "setMythtvBackend : enter" );

		applicationContext.setMasterBackend( masterBackend );

		handler.postDelayed( sendUpdatesToUI, 1000 ); // 1 second

		Log.d( TAG, "setMythtvBackend : exit" );
	}

	// ***************************************
	// JMDNS ServiceListener methods
	// ***************************************

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jmdns.ServiceListener#serviceAdded(javax.jmdns.ServiceEvent)
	 */
	@SuppressWarnings( "deprecation" )
	public void serviceAdded( ServiceEvent event ) {
		Log.v( TAG, "serviceAdded : enter" );

		final String hostname = event.getInfo().getHostAddress();
		final int port = event.getInfo().getPort();
		Log.w( TAG, "serviceAdded : masterbackend=" + ( hostname + ":" + port ) );

		setMasterBackend( ( hostname + ":" + port ) );
		
		Log.v( TAG, "serviceAdded : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jmdns.ServiceListener#serviceRemoved(javax.jmdns.ServiceEvent)
	 */
	public void serviceRemoved( ServiceEvent event ) {
		Log.v( TAG, "serviceRemoved : enter" );

		Log.w( TAG, String.format( "serviceRemoved(event=\n%s\n)", event.toString() ) );

		Log.v( TAG, "serviceRemoved : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.jmdns.ServiceListener#serviceResolved(javax.jmdns.ServiceEvent)
	 */
	public void serviceResolved( ServiceEvent event ) {
		Log.v( TAG, "serviceResolved : enter" );

		Log.w( TAG, String.format( "serviceResolved(event=\n%s\n)", event.toString() ) );

		Log.v( TAG, "serviceResolved : exit" );
	}

	// internal helpers

	/**
	 * @throws IOException
	 */
	private void startProbe() throws IOException {
		Log.v( TAG, "startProbe : enter" );

		if( zeroConf != null ) {
			stopProbe();
		}

		// figure out our wifi address, otherwise bail
		WifiManager wifi = (WifiManager) this.getSystemService( Context.WIFI_SERVICE );

		WifiInfo wifiInfo = wifi.getConnectionInfo();
		int intaddr = wifiInfo.getIpAddress();

		byte[] byteaddr = new byte[] { (byte) ( intaddr & 0xff ), (byte) ( intaddr >> 8 & 0xff ), (byte) ( intaddr >> 16 & 0xff ), (byte) ( intaddr >> 24 & 0xff ) };
		InetAddress addr = InetAddress.getByAddress( byteaddr );
		Log.d( TAG, "startProbe : wifi address=" + addr.toString() );
		
		// start multicast lock
		mLock = wifi.createMulticastLock( "Mythtv Master Backend Remote lock" );
		mLock.setReferenceCounted( true );
		mLock.acquire();

		zeroConf = JmDNS.create( addr, addr.getHostName() );
		zeroConf.addServiceListener( MYTHTV_MASTER_BACKEND_TYPE, this );

		Log.v( TAG, "startProbe : exit" );
	}

	/**
	 * @throws IOException
	 */
	private void stopProbe() throws IOException {
		Log.v( TAG, "stopProbe : enter" );

		zeroConf.removeServiceListener( MYTHTV_MASTER_BACKEND_TYPE, this );
		zeroConf.close();
		zeroConf = null;

		mLock.release();
		mLock = null;

		Log.v( TAG, "stopProbe : exit" );
	}

}
