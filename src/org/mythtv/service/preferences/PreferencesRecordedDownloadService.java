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
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.db.preferences.LocationProfileConstants;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.db.status.model.BackendStatus;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.connect.MythAccessFactory;
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
    		
    		BackendStatus status = null;
    		try {

    			status = download( profile );
    			if( null != status ) {
    				
    				profile.setVersion( status.getVersion() );
    				profile.setProtocolVersion( String.valueOf( status.getProtocolVersion() ) );
    				
    				locationProfileDaoHelper.save( this, profile );
    				
    				Log.v( TAG, "onHandleIntent : location profile updated" );
    			}

			} catch( Exception e ) {
				Log.e( TAG, "onHandleIntent : error", e );
				
				passed = false;
			} finally {

    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Preferences Recorded Programs Download Service Finished" );
    			if( null == status ) {
    				completeIntent.putExtra( EXTRA_COMPLETE_UPTODATE, passed );
    			}
    			
    			sendBroadcast( completeIntent );
    		}
    		
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private BackendStatus download( LocationProfile profile ) throws Exception {
		Log.v( TAG, "download : enter" );

		EtagInfoDelegate etag = EtagInfoDelegate.createEmptyETag();
		
		ApiVersion apiVersion = ApiVersion.valueOf( profile.getVersion() );
		switch( apiVersion ) {
			case v026 :
				ResponseEntity<org.mythtv.services.api.v026.beans.BackendStatus> backendStatusV026 = MythAccessFactory.getServiceTemplateApiByType( org.mythtv.services.api.v026.MythServices.class, profile.getUrl() ).statusOperations().getStatus( etag );
				if( backendStatusV026.getStatusCode().equals( HttpStatus.OK ) ) {

					if( null != backendStatusV026.getBody() ) {

						return convertBackendStatusV026( backendStatusV026.getBody() );

					}

				}
				
				break;
				
			case v027 :
				ResponseEntity<org.mythtv.services.api.v027.status.beans.BackendStatus> backendStatusV027 = MythAccessFactory.getServiceTemplateApiByType( org.mythtv.services.api.v027.MythServices.class, profile.getUrl() ).statusOperations().getStatus( etag );
				if( backendStatusV027.getStatusCode().equals( HttpStatus.OK ) ) {

					if( null != backendStatusV027.getBody() ) {

						return convertBackendStatusV027( backendStatusV027.getBody() );

					}

				}
				
				break;
				
			default :
				
				break;
		}
		
		Log.v( TAG, "download : exit" );
		return null;
	}

	private BackendStatus convertBackendStatusV026( org.mythtv.services.api.v026.beans.BackendStatus response ) {
		BackendStatus status = new BackendStatus();

		return status;
	}

	private BackendStatus convertBackendStatusV027( org.mythtv.services.api.v027.status.beans.BackendStatus response ) {
		BackendStatus status = new BackendStatus();
		
		return status;
	}

}
