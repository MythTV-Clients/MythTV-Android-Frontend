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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.preferences.LocationProfileDaoHelper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * @author Daniel Frey
 *
 */
public class NetworkHelper {

	private static final String TAG = NetworkHelper.class.getSimpleName();
	
	private Context mContext;
	private LocationProfileDaoHelper mLocationProfileDaoHelper;
	
	public static NetworkHelper newInstance( Context context ) {
		return new NetworkHelper( context );
	}
	
	protected NetworkHelper( Context context ) {
		this.mContext = context;
		
		this.mLocationProfileDaoHelper = new LocationProfileDaoHelper( mContext );
	}
	
	public boolean isNetworkConnected() {
        Log.v( TAG, "isNetworkConnected : enter" );
	
        final ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService( Context.CONNECTIVITY_SERVICE );
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if( networkInfo == null || !networkInfo.isConnectedOrConnecting() ) {
            Log.e( TAG, "isNetworkConnected : exit, no connection found" );
            
            return false;
        }

        Log.v( TAG, "isNetworkConnected : exit" );
        return true;
	}
	
	public boolean isMasterBackendConnected() {
		Log.v( TAG, "isMasterBackendConnected : enter" );
		
		if( isNetworkConnected() ) {
			
			try {
			
				if( null == mLocationProfileDaoHelper.findConnectedProfile() ) {
					Log.e( TAG, "isMasterBackendConnected : exit, no backend selected" );
					
					return false;
				}
				
				URL url = new URL( mLocationProfileDaoHelper.findConnectedProfile().getUrl() + "Myth/GetHostName" );

				HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
				urlc.setRequestProperty( "User-Agent", "Android Application:MythTV_Android_Frontent" );
				urlc.setRequestProperty( "Connection", "close" );
				urlc.setConnectTimeout( 1000 * 10 ); // mTimeout is in seconds
				urlc.connect();
				if( urlc.getResponseCode() == 200 ) {
					InputStream in = new BufferedInputStream(urlc.getInputStream());
					byte[] hostname = new byte[ 128 ];
					while( in.read( hostname , 0, 128 ) > 0 );
					Log.v( TAG, "isMasterBackendConnected : exit" );
					
					return true;
				}
			} catch( MalformedURLException e ) {
				Log.w( TAG, "isMasterBackendConnected : error, connecting with backend url", e );
			} catch( IOException e ) {
				Log.w( TAG, "isMasterBackendConnected : error, connecting to backend", e );
			}

		}
		
		Log.v( TAG, "isMasterBackendConnected : exit, master backend could not be reached" );
		return false;
	}
	
	public boolean isMasterBackendConnected( LocationProfile profile ) {
		Log.v( TAG, "isMasterBackendConnected : enter" );
		Log.v( TAG, "isMasterBackendConnected : profile=" + profile.toString() );
		
		if( isNetworkConnected() ) {
			
			try {
			
				URL url = new URL( profile.getUrl() + "Myth/GetHostName" );

				HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
				urlc.setRequestProperty( "User-Agent", "Android Application:MythTV_Android_Frontent" );
				urlc.setRequestProperty( "Connection", "close" );
				urlc.setConnectTimeout( 1000 * 10 ); // mTimeout is in seconds
				urlc.connect();
				if( urlc.getResponseCode() == 200 ) {
					InputStream in = new BufferedInputStream(urlc.getInputStream());
					byte[] hostname = new byte[ 128 ];
					while( in.read( hostname , 0, 128 ) > 0 );
					Log.v( TAG, "isMasterBackendConnected : exit" );
					
					return true;
				}
			} catch( MalformedURLException e ) {
				Log.w( TAG, "isMasterBackendConnected : error, connecting with backend url", e );
			} catch( IOException e ) {
				Log.w( TAG, "isMasterBackendConnected : error, connecting to backend", e );
			}

		}
		
		Log.v( TAG, "isMasterBackendConnected : exit, master backend could not be reached" );
		return false;
	}

}
