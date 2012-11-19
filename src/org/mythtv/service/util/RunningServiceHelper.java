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
