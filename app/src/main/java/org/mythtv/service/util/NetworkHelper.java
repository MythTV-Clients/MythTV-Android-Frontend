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

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedInputStream;
import java.io.InputStream;
import org.mythtv.client.ui.preferences.LocationProfile;

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
		
        boolean isOK = false;

        /* Check if we're not initialized */
		if( null == context ) {
//			Log.e(TAG, "NetworkHelper null context" );
			throw new IllegalArgumentException( "NetworkHelper is not initialized" );
		}
		
		if( null == profile ) {
//			Log.e(TAG, "NetworkHelper null profile" );
			return isOK;
		}
		
		if( !isNetworkConnected( context ) ) {
//			Log.e(TAG, "NetworkHelper isNetworkConnected failed" );
			return isOK;
		}

        try {
            final URL url = new URL( profile.getUrl() + "Myth/GetHostName" );
            final HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();

            /*
             * TODO: As of MythTV 0.27, conversations are NOT persistent. So,
             * close each when done. Watch out if MythTV changes this. The same
             * is true for isFrontendConnected below. MythTV (incorrectly returns
             * Keep-Alive even though it shouldn't.) Trac ticket:
             * http://code.mythtv.org/trac/ticket/11894
             *
             * This needs more invistication, as of 0.28-pre-2119, this is no
             * longer true, but the window is may be only 1 second before a
             * a new conversation is started, reverting the Close for now.
             */

            urlcon.setRequestProperty("Connection", "Keep-Alive");

            /*
             * No need to gzip the (very short) response
             */

            urlcon.addRequestProperty("Accept-Encoding", "identity");
            urlcon.addRequestProperty("User-Agent","MAF");
            isOK = urlcon.getResponseCode() == HttpURLConnection.HTTP_OK;

            /*
             * If we've got a response, read all the bits so that TCP
             * can make an orderly close of the conversation. Prevents
             * RSTs (resets.)
             */

            if(urlcon.getContentLength() > 0)
            {
            	byte[] drainResponse = new byte[ 64 ];
            	InputStream bis = new BufferedInputStream( urlcon.getInputStream() );
            	while( bis.read( drainResponse , 0, 64 ) > 0 );
            }

            urlcon.disconnect();
        } catch( Exception e ) {
        	Log.e(TAG, "GetHost failure" );
			isOK = false;
		}
		
//		Log.w( TAG, "isMasterBackendConnected : exit, isOK = "  + isOK );
		return isOK;
	}

	public boolean isFrontendConnected( final Context context, LocationProfile profile, String frontendUrl ) {
		
        boolean isOK = false;

        /* Check if we're not initialized */
		if( null == context ) {
//			Log.e(TAG, "NetworkHelper not initialized");
			throw new IllegalArgumentException( "NetworkHelper is not initialized" );
		}
		
		if( null == profile ) {
//			Log.e(TAG, "NetworkHelper not initialized");
			throw new IllegalArgumentException( "LocationProfile is required" );
		}

		// first check if the master backend is still connected
		if( !isMasterBackendConnected( context, profile ) ) {
			return isOK;
		}

        try {
            final URL url = new URL( frontendUrl + "Frontend/GetStatus" );
            final HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
            urlcon.setRequestProperty("Connection", "Keep-Alive");
            urlcon.addRequestProperty("User-Agent","MAF");
            isOK = urlcon.getResponseCode() == HttpURLConnection.HTTP_OK;
            if(urlcon.getContentLength() > 0)
            {
            	byte[] drainResponse = new byte[ 64 ];
            	InputStream bis = new BufferedInputStream( urlcon.getInputStream() );
            	while( bis.read( drainResponse , 0, 64 ) > 0 );
            }

            urlcon.disconnect();
        } catch( Exception e ) {
			isOK = false;
		}
		
//		Log.w( TAG, "isMasterBackendConnected : exit" );
		return isOK;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
}
