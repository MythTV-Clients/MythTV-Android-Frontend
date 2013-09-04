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
import org.mythtv.service.MythtvService;
import org.mythtv.service.dvr.v26.RecordingRuleHelperV26;
import org.mythtv.service.dvr.v27.RecordingRuleHelperV27;
import org.mythtv.services.api.ApiVersion;

import android.content.Intent;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class RecordingRuleDownloadService extends MythtvService {

	private static final String TAG = RecordingRuleDownloadService.class.getSimpleName();

    public static final String ACTION_DOWNLOAD = "org.mythtv.background.recordingRuleDownload.ACTION_DOWNLOAD";
    public static final String ACTION_PROGRESS = "org.mythtv.background.recordingRuleDownload.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.recordingRuleDownload.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATA = "PROGRESS_DATA";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_UPTODATE = "COMPLETE_UPTODATE";
    public static final String EXTRA_COMPLETE_OFFLINE = "COMPLETE_OFFLINE";
    
	public RecordingRuleDownloadService() {
		super( "RecordingRuleDownloadService" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );
		super.onHandleIntent( intent );
		
		boolean passed = true;
		
		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile( this );
		
		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		try {

    			ApiVersion apiVersion = ApiVersion.valueOf( locationProfile.getVersion() );
    			switch( apiVersion ) {
    				case v026 :
    					
    					passed = RecordingRuleHelperV26.process( this, locationProfile );
    					
    					break;
    				case v027 :

    					passed = RecordingRuleHelperV27.process( this, locationProfile );

    					break;
    					
    				default :
    					
    					passed = RecordingRuleHelperV26.process( this, locationProfile );

    					break;
    			}

			} catch( Exception e ) {
				Log.e( TAG, "onHandleIntent : error", e );
				
				passed = false;
			} finally {

    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Recording Rules Download Service Finished" );
   				completeIntent.putExtra( EXTRA_COMPLETE_UPTODATE, passed );
    			
    			sendBroadcast( completeIntent );
    		}
    		
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

}
