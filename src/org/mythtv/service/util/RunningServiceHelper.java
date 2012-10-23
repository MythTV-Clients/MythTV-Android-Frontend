/**
 * 
 */
package org.mythtv.service.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class RunningServiceHelper {

	private static final String TAG = RunningServiceHelper.class.getSimpleName();
	
	private Context mContext;
	
	public RunningServiceHelper( Context context ) {
		mContext = context;
	}
	
	public boolean isServiceRunning( String serviceName ) {
		Log.v( TAG, "isServiceRunning : enter" );
		Log.d( TAG, "isServiceRunning : checking for running server '" + serviceName + "'" );
		
		ActivityManager manager = (ActivityManager) mContext.getSystemService( Context.ACTIVITY_SERVICE );

		for( RunningServiceInfo service : manager.getRunningServices( Integer.MAX_VALUE ) ) {

			if( serviceName.equals( service.service.getClassName() ) ) {

				Log.v( TAG, "isServiceRunning : exit, '" + serviceName + "' is running" );
				return true;
			}
		}

		Log.v( TAG, "isServiceRunning : exit" );
		return false;
	}
}
