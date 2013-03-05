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
package org.mythtv.service;

import org.mythtv.client.MainApplication;
import org.mythtv.service.util.FileHelper;
import org.mythtv.service.util.NetworkHelper;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class MythtvService extends IntentService {

	protected static final String TAG = MythtvService.class.getSimpleName();
	
	public static final String FILENAME_EXT = ".json";
    
    protected MainApplication mMainApplication;
	
    public static final String ACTION_CONNECT = "org.mythtv.background.ACTION_CONNECT";
    public static final String ACTION_COMPLETE = "org.mythtv.background.ACTION_COMPLETE";

    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_ONLINE = "COMPLETE_ONLINE";

	public MythtvService() {
		super( "MythtvService" );
	}

	public MythtvService( String name ) {
		super( name );

	}

	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {

		mMainApplication = (MainApplication) MythtvService.this.getApplicationContext();

		if ( intent.getAction().equals( ACTION_CONNECT ) ) {
			Log.v( TAG, "onHandleIntent : checking master backend connection" );

			Boolean connected = NetworkHelper.getInstance().isMasterBackendConnected( this );
			Log.v( TAG, "onHandleIntent : connected=" + connected );

			Intent completeIntent = new Intent( ACTION_COMPLETE );
   			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend Connect Attempt Finished" );
			completeIntent.putExtra( EXTRA_COMPLETE_ONLINE, connected );
    			
   			sendBroadcast( completeIntent );

		}
		
	}

}
