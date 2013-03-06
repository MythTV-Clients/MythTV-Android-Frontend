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
package org.mythtv.service.preferences;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.preferences.LocationProfileConstants;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.dvr.ProgramList;
import org.mythtv.services.api.dvr.Programs;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class PreferencesRecordedDownloadService extends MythtvService {

	private static final String TAG = PreferencesRecordedDownloadService.class.getSimpleName();

	public static final String RECORDED_FILE = "recorded.json";
	
    public static final String ACTION_DOWNLOAD = "org.mythtv.background.preferencesRecordedDownload.ACTION_DOWNLOAD";
    public static final String ACTION_COMPLETE = "org.mythtv.background.preferencesRecordedDownload.ACTION_COMPLETE";

    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_UPTODATE = "COMPLETE_UPTODATE";
    public static final String EXTRA_COMPLETE_OFFLINE = "COMPLETE_OFFLINE";
    
    private LocationProfileDaoHelper locationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
    
	public PreferencesRecordedDownloadService() {
		super( "PreferencesRecordedDownloadService" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );
		super.onHandleIntent( intent );
				
		Bundle extras = intent.getExtras();
		long id = extras.getLong( LocationProfileConstants._ID );
		if( id <= 0 ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "No ID passed in" );
			completeIntent.putExtra( EXTRA_COMPLETE_OFFLINE, Boolean.TRUE );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, No ID passed in" );
			return;
		}

		boolean passed = true;
		
		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		LocationProfile profile = locationProfileDaoHelper.findOne( this, id );
    		if( null == profile ) {
    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Location Profile not found" );
    			completeIntent.putExtra( EXTRA_COMPLETE_OFFLINE, Boolean.TRUE );
    			sendBroadcast( completeIntent );

    			Log.d( TAG, "onHandleIntent : exit, Location Profile not found" );
    			return;
    		}
    		
    		if( !NetworkHelper.getInstance().isMasterBackendConnected( this, profile ) ) {
    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend not connected" );
    			completeIntent.putExtra( EXTRA_COMPLETE_OFFLINE, Boolean.TRUE );
    			sendBroadcast( completeIntent );

    			Log.d( TAG, "onHandleIntent : exit, Master Backend not connected" );
    			return;
    		}
    		
    		Programs programs = null;
    		try {

    			programs = download( profile );
    			if( null != programs ) {
    				
    				profile.setVersion( programs.getVersion() );
    				profile.setProtocolVersion( programs.getProtocolVersion() );
    				
    				locationProfileDaoHelper.save( this, profile );
    				
    				Log.v( TAG, "onHandleIntent : location profile updated" );
    			}

			} catch( Exception e ) {
				Log.e( TAG, "onHandleIntent : error", e );
				
				passed = false;
			} finally {

    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Preferences Recorded Programs Download Service Finished" );
    			if( null == programs ) {
    				completeIntent.putExtra( EXTRA_COMPLETE_UPTODATE, passed );
    			}
    			
    			sendBroadcast( completeIntent );
    		}
    		
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private Programs download( LocationProfile profile ) throws Exception {
		Log.v( TAG, "download : enter" );

		ETagInfo etag = ETagInfo.createEmptyETag();
		
		ResponseEntity<ProgramList> responseEntity = mMainApplication.getMythServicesApi( profile ).dvrOperations().getRecordedList( etag );

		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			ProgramList programList = responseEntity.getBody();

			if( null != programList.getPrograms() ) {

				return programList.getPrograms();	

			}

		}

		Log.v( TAG, "download : exit" );
		return null;
	}

}
