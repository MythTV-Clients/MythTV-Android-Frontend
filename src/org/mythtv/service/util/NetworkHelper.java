/**
 * 
 */
package org.mythtv.service.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.mythtv.db.preferences.LocationProfileDaoHelper;

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
	
	private Context mContext;
	private LocationProfileDaoHelper mLocationProfileDaoHelper;
	
	public NetworkHelper( Context context ) {
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
				
				URL url = new URL( mLocationProfileDaoHelper.findConnectedProfile().getUrl() );

				HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
				urlc.setRequestProperty( "User-Agent", "Android Application:MythTV_Android_Frontent" );
				urlc.setRequestProperty( "Connection", "close" );
				urlc.setConnectTimeout( 1000 * 10 ); // mTimeout is in seconds
				urlc.connect();
				if( urlc.getResponseCode() == 200 ) {
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
