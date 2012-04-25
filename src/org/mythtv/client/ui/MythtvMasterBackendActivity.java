/**
 *  This file is part of MythTV for Android
 * 
 *  MythTV for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MythTV for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MythTV for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */
package org.mythtv.client.ui;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

/**
 * @author Daniel Frey
 * 
 */
public class MythtvMasterBackendActivity extends AbstractMythActivity implements ServiceListener {

	private static final String TAG = MythtvMasterBackendActivity.class.getSimpleName();

	private static final int DIALOG_MYTHTV_NOT_FOUND = 1;
	
	private static final String MYTHTV_MASTER_BACKEND_TYPE = "_mythbackend-master._tcp.local.";
	private static final String HOSTNAME = "mythandroid";

	private static JmDNS zeroConf = null;
	private static MulticastLock mLock = null;

	// ***************************************
	// Activity methods
	// ***************************************

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		if( android.os.Build.VERSION.SDK_INT > 9 ) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy( policy );
		}
		
		String masterBackend = getApplicationContext().getMasterBackend();
		if( null == masterBackend || "".equals( masterBackend ) ) {
			Log.v( TAG, "onCreate : masterbackend not set, checking on network" );

			try {
				startProbe();
			} catch( Exception e ) {
				Log.e( TAG, "onCreate : error", e );

				showDialog( DIALOG_MYTHTV_NOT_FOUND );
				finish();
			}
		} else {
			Log.v( TAG, "onCreate : masterbackend previously set, proceeding to dashboard" );

			proceedToDashboard();
		}

		Log.v( TAG, "onCreate : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	public void onStop() {
		Log.v( TAG, "onStop : enter" );

		super.onStop();

		Log.v( TAG, "onStop : exit" );
	}

    /* (non-Javadoc)
     * @see android.app.Activity#onCreateDialog(int)
     */
    @Override
    protected Dialog onCreateDialog( int id ) {
		Log.v( TAG, "onCreateDialog : enter" );

		switch (id) {
        	case DIALOG_MYTHTV_NOT_FOUND:
        		Log.v( TAG, "onCreateDialog : exit, returning 'MythTV Not Found Dialog'" );
        		return new AlertDialog.Builder( this )
                	.setTitle( "MythTV Backend Not Found!" )
                	.setMessage( "Please make sure you are connected to Wifi before continuing." )
                	.setCancelable( false )
                	.setNegativeButton( "Close", new DialogInterface.OnClickListener() {
                		public void onClick( DialogInterface dialog, int id ) {
                			dialog.cancel();
                		}
                	}).create();
        }
        
		Log.v( TAG, "onCreateDialog : exit" );
        return null;
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

		Log.w( TAG, "serviceAdded : " + event.getDNS().getServiceInfo( event.getType(), event.getName() ).toString() );
		
		final String hostname = event.getDNS().getServiceInfo( event.getType(), event.getName() ).getInet4Address().getHostAddress();
		final int port = event.getDNS().getServiceInfo( event.getType(), event.getName() ).getPort();
		Log.w( TAG, "serviceAdded : masterbackend=" + ( "http://" + hostname + ":" + port + "/" ) );

		getApplicationContext().setMasterBackend( ( "http://" + hostname + ":" + port + "/" ) );
		
		proceedToDashboard();

		Log.v( TAG, "serviceAdded : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jmdns.ServiceListener#serviceRemoved(javax.jmdns.ServiceEvent)
	 */
	public void serviceRemoved( ServiceEvent event ) {
		Log.v( TAG, "serviceRemoved : enter" );

		Log.w( TAG, "serviceRemoved : event=" + event.toString() );

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

		Log.w( TAG, "serviceResolved : event=" + event.toString() );

		Log.v( TAG, "serviceResolved : exit" );
	}

	// internal helpers

	private void proceedToDashboard() {
		Log.v( TAG, "proceedToDashboard : enter" );

		startActivity( new Intent( this, HomeActivity.class ) );
		finish();

		Log.v( TAG, "proceedToDashboard : exit" );
	}

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

		WifiInfo wifiinfo = wifi.getConnectionInfo();
		int intaddr = wifiinfo.getIpAddress();

		byte[] byteaddr = new byte[] { (byte) ( intaddr & 0xff ), (byte) ( intaddr >> 8 & 0xff ),
				(byte) ( intaddr >> 16 & 0xff ), (byte) ( intaddr >> 24 & 0xff ) };
		InetAddress addr = InetAddress.getByAddress( byteaddr );
		Log.d( TAG, "startProbe : wifi address=" + addr.toString() );
		
		// start multicast lock
		mLock = wifi.createMulticastLock( "mythtv_lock" );
		mLock.setReferenceCounted( true );
		mLock.acquire();

		zeroConf = JmDNS.create( addr, HOSTNAME );
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
