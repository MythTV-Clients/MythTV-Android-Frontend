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
package org.mythtv.service.dvr;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.MythtvService;
import org.mythtv.service.dvr.v26.UpcomingHelperV26;
import org.mythtv.service.dvr.v27.UpcomingHelperV27;
import org.mythtv.services.api.ApiVersion;

import android.content.Intent;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class UpcomingDownloadService extends MythtvService {

	private static final String TAG = UpcomingDownloadService.class.getSimpleName();

    public static final String ACTION_DOWNLOAD = "org.mythtv.background.upcomingDownload.ACTION_DOWNLOAD";
    public static final String ACTION_PROGRESS = "org.mythtv.background.upcomingDownload.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.upcomingDownload.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_FILENAME = "PROGRESS_FILENAME";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_UPTODATE = "COMPLETE_UPTODATE";
    public static final String EXTRA_COMPLETE_OFFLINE = "COMPLETE_OFFLINE";
    
	private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();

	public UpcomingDownloadService() {
		super( "UpcomingDownloadService" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );
		super.onHandleIntent( intent );
		
		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile( this );

		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		boolean passed = true;
    		
    		try {

    			ApiVersion apiVersion = ApiVersion.valueOf( locationProfile.getVersion() );
    			switch( apiVersion ) {
    				case v026 :
    					
    					passed = UpcomingHelperV26.getInstance().process( this, locationProfile );
    					
    					break;
    				case v027 :

    					passed = UpcomingHelperV27.getInstance().process( this, locationProfile );

    					break;
    					
    				default :
    					
    					passed = UpcomingHelperV26.getInstance().process( this, locationProfile );

    					break;
    			}

			} catch( Exception e ) {
				Log.e( TAG, "onHandleIntent : error loading upcoming data", e );
				
				passed = false;
			} finally {

    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Upcoming Programs Download Service Finished" );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Recorded Programs Download Service Finished" );
   				completeIntent.putExtra( EXTRA_COMPLETE_UPTODATE, passed );
    			
    			sendBroadcast( completeIntent );
    		}
    		
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

}
