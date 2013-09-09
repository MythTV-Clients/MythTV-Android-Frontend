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
package org.mythtv.service.util;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.services.api.connect.MythAccessFactory;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class NetworkHelper {

	private static final String TAG = NetworkHelper.class.getSimpleName();
	
	private static NetworkHelper singleton = null;
	
	/**
	 * Returns the one and only NetworkHelper. init() must be called before 
	 * any 
	 * @return
	 */
	public static NetworkHelper getInstance() {
		if( null == singleton ) {
			
			synchronized( NetworkHelper.class ) {

				if( null == singleton ) {
					singleton = new NetworkHelper();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private NetworkHelper() { }
	
	/**
	 * Returns true if a network connection is detected
	 * @return
	 */
	public boolean isNetworkConnected( final Context context ) {
//		Log.v( TAG, "isNetworkConnected : enter" );
		
		/* Check if we're not initialized */
		if( null == context ) {
//			Log.e(TAG, "NetworkHelper not initialized");
			throw new RuntimeException( "NetworkHelper is not initialized" );
		}

		final ConnectivityManager cm = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
		final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if( networkInfo == null || !networkInfo.isConnectedOrConnecting() ) {
			Log.w( TAG, "isNetworkConnected : no network connection found" );

			return false;
		}

//		Log.v( TAG, "isNetworkConnected : exit" );
		return true;
	}
	
	/**
	 * Returns true if a connection can be made to the given location profile.
	 * Three attempts are made before a false return.
	 * @param profile
	 * @return
	 */
	public boolean isMasterBackendConnected( final Context context, LocationProfile profile ) {
//		Log.v( TAG, "isMasterBackendConnected : profile=" + profile.toString() );
		
		/* Check if we're not initialized */
		if( null == context ) {
//			Log.e(TAG, "NetworkHelper not initialized");
			throw new IllegalArgumentException( "NetworkHelper is not initialized" );
		}
		
		if( null == profile ) {
//			Log.e(TAG, "NetworkHelper not initialized");
			throw new IllegalArgumentException( "LocationProfile is required" );
		}
		
		if( !isNetworkConnected( context ) ) {
			return false;
		}

		if( !MythAccessFactory.isServerReachable( profile.getUrl() ) ) {
			Log.w( TAG, "process : Master Backend '" + profile.getHostname() + "' is unreachable" );
			
			return false;
		}

//		Log.w( TAG, "isMasterBackendConnected : exit" );
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
}
