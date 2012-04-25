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
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Daniel Frey
 * 
 */
public class TestJmDnsActivity extends Activity {

	private static final String TAG = TestJmDnsActivity.class.getSimpleName();

	private Context mContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		mContext = this;
		
		jmdnsThread.run();
		
		Log.v( TAG, "onCreate : exit" );
	}

	private Thread jmdnsThread = new Thread() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			Log.v( TAG, "jmdnsThread.run : enter" );

			JmDNS jmdns = null;
			WifiManager wifi = (WifiManager) mContext.getSystemService( Context.WIFI_SERVICE );
			MulticastLock lock = wifi.createMulticastLock( "fliing_lock" );
			lock.setReferenceCounted( true );
			lock.acquire();
			Log.v( TAG, "jmdnsThread.run : wifi lock acquired" );
			
			try {
				InetAddress addr = getLocalIpAddress();
				Log.v( TAG, "jmdnsThread.run : address=" + addr.toString() );

				jmdns = JmDNS.create( addr );
				Log.v( TAG, "jmdnsThread.run : jmdns created" );

				ServiceInfo[] infos = jmdns.list( "_mythbackend-master._tcp.local." );
				for( ServiceInfo info : infos ) {
					Log.v( TAG, "jmdnsThread.run : info=" + info.toString() );
				}
			} catch( Exception e ) {
				Log.e( TAG, "jmdnsThread.run : error, exception 1", e );
			} finally {
				if( jmdns != null )
					try {
						jmdns.close();
					} catch( IOException e ) {
						Log.e( TAG, "jmdnsThread.run : error, ioException 2", e );
					}
				if( lock != null ) {
					lock.release();
				}
			}

			Log.v( TAG, "jmdnsThread.run : exit" );
		}

	};

	private static InetAddress getLocalIpAddress() {
		try {
			for( Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
				NetworkInterface intf = en.nextElement();
				for( Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if( !inetAddress.isLoopbackAddress() ) {
						return inetAddress;
					}
				}
			}
		} catch( SocketException ex ) {
			Log.e( TAG, ex.toString() );
		}
		
		return null;
	}

}
