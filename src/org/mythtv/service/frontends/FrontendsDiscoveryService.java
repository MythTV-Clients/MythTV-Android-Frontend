/**
 * This file is part of MythTV Android Frontend
 *
 * MythTV Android Frontend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MythTV Android Frontend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MythTV Android Frontend.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This software can be found at <https://github.com/MythTV-Clients/MythTV-Android-Frontend/>
 */
/**
 * 
 */
package org.mythtv.service.frontends;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import org.mythtv.client.ui.frontends.Frontend;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.frontends.FrontendDaoHelper;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.NetworkHelper;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

/**
 * @author dmfrey
 *
 */
public class FrontendsDiscoveryService extends MythtvService implements ServiceListener {

	private static final String TAG = FrontendsDiscoveryService.class.getSimpleName();

    public static final String ACTION_DISCOVER = "org.mythtv.background.frontends.ACTION_DISCOVER";
    public static final String ACTION_PROGRESS = "org.mythtv.background.frontends.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.frontends.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATA = "PROGRESS_DATA";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_OFFLINE = "COMPLETE_OFFLINE";

    // jmDns required fields
	private static final String MYTHTV_FRONTEND_TYPE = "_mythfrontend._tcp.local.";
	private static final String HOSTNAME = "mythandroid";
	private static JmDNS zeroConf = null;
	private static MulticastLock mLock = null;

    private FrontendDaoHelper mFrontendDaoHelper = FrontendDaoHelper.getInstance();
    
	private LocationProfile mLocationProfile;
	
	public FrontendsDiscoveryService() {
		super( "FrontendsDiscoveryService" );
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.service.MythtvService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.v( TAG, "onHandleIntent : enter" );
		super.onHandleIntent( intent );

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( this );
		if( !NetworkHelper.getInstance().isMasterBackendConnected( this, mLocationProfile ) ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend unreachable" );
			completeIntent.putExtra( EXTRA_COMPLETE_OFFLINE, Boolean.TRUE );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, Master Backend unreachable" );
			return;
		}

		if ( intent.getAction().equals( ACTION_DISCOVER ) ) {
    		Log.i( TAG, "onHandleIntent : DISCOVER action selected" );
		
			PowerManager mgr = (PowerManager) getSystemService( Context.POWER_SERVICE );
			WakeLock wakeLock = mgr.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK, "FrontendServiceDiscover" );

			try {
    			mFrontendDaoHelper.resetAllAvailable( this, mLocationProfile );
    			
				startProbe();
			} catch( IOException e ) {
				Log.e( TAG, "onHandleIntent : error", e );
			} finally {
				
    			if( wakeLock.isHeld() ) {
    				wakeLock.release();
    			}
				
				Intent completeIntent = new Intent( ACTION_COMPLETE );
				completeIntent.putExtra( EXTRA_COMPLETE, "Frontends are being discovered" );
			
				sendBroadcast( completeIntent );
			}
		}
		
		Log.v( TAG, "onHandleIntent : exit" );
	}

	// jmDns interface
	
	/* (non-Javadoc)
	 * @see javax.jmdns.ServiceListener#serviceAdded(javax.jmdns.ServiceEvent)
	 */
	@Override
	public void serviceAdded( ServiceEvent event ) {
		Log.v( TAG, "serviceAdded : enter" );

		Log.v( TAG, "serviceAdded : " + event.getDNS().getServiceInfo( event.getType(), event.getName() ).toString() );

		final String hostname = event.getDNS()
				.getServiceInfo( event.getType(), event.getName() )
				.getInet4Address().getHostAddress();
		final int port = event.getDNS().getServiceInfo( event.getType(), event.getName() ).getPort();
		Log.v( TAG, "serviceAdded : masterbackend=" + ( "http://" + hostname + ":" + port + "/" ) );

		// Dont' do both adds
		Frontend fe = new Frontend( event.getName(), "http://" + hostname + ":" + port + "/" );
		fe.setAvailable( true );

		// save frontend to the database
		mFrontendDaoHelper.save( this, mLocationProfile, fe );
		
		// send progress notification
		Intent progressIntent = new Intent( ACTION_COMPLETE );
		progressIntent.putExtra( EXTRA_PROGRESS, "Frontend saved" );
		progressIntent.putExtra( EXTRA_PROGRESS_DATA, fe.getName() );
	
		sendBroadcast( progressIntent );

		Log.v(TAG, "serviceAdded : exit");
	}

	/* (non-Javadoc)
	 * @see javax.jmdns.ServiceListener#serviceRemoved(javax.jmdns.ServiceEvent)
	 */
	@Override
	public void serviceRemoved( ServiceEvent event ) {
		// Log.v( TAG, "serviceRemoved : enter" );
		//
		// Log.v( TAG, "serviceRemoved : event=" + event.toString() );
		//
		// Log.v( TAG, "serviceRemoved : exit" );
	}

	/* (non-Javadoc)
	 * @see javax.jmdns.ServiceListener#serviceResolved(javax.jmdns.ServiceEvent)
	 */
	@Override
	public void serviceResolved( ServiceEvent event ) {
		// Log.v( TAG, "serviceResolved : enter" );
		//
		// Log.v( TAG, "serviceResolved : event=" + event.toString() );
		//
		// Log.v( TAG, "serviceResolved : exit" );
	}

	// internal helpers
	
	/**
	 * @throws IOException
	 */
	private void startProbe() throws IOException {
		Log.v( TAG, "startProbe : enter" );

		if( null != zeroConf ) {
			stopProbe();
		}

		// figure out our wifi address, otherwise bail
		WifiManager wifi = (WifiManager) getSystemService( Context.WIFI_SERVICE );

		WifiInfo wifiinfo = wifi.getConnectionInfo();
		int intaddr = wifiinfo.getIpAddress();

		byte[] byteaddr = new byte[] { 
			(byte) ( intaddr & 0xff ),
			(byte) ( intaddr >> 8 & 0xff ),
			(byte) ( intaddr >> 16 & 0xff ),
			(byte) ( intaddr >> 24 & 0xff )
		};
		InetAddress addr = InetAddress.getByAddress( byteaddr );
		Log.d( TAG, "startProbe : wifi address=" + addr.toString() );

		// start multicast lock
		mLock = wifi.createMulticastLock( "mythtv_lock" );
		mLock.setReferenceCounted( true );
		mLock.acquire();

		zeroConf = JmDNS.create( addr, HOSTNAME );
		zeroConf.addServiceListener( MYTHTV_FRONTEND_TYPE, this );

		Log.v( TAG, "startProbe : exit" );
	}

	/**
	 * @throws IOException
	 */
	private void stopProbe() throws IOException {
		Log.v( TAG, "stopProbe : enter" );

		zeroConf.removeServiceListener( MYTHTV_FRONTEND_TYPE, this );
		zeroConf.close();
		zeroConf = null;

		mLock.release();
		mLock = null;

		Log.v( TAG, "stopProbe : exit" );
	}

}
