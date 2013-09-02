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

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.dvr.DvrEndpoint;
import org.mythtv.db.dvr.RecordingRuleDaoHelper;
import org.mythtv.db.dvr.model.RecRuleList;
import org.mythtv.db.dvr.model.RecRules;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.NetworkHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

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
    
	private RecordingRuleDaoHelper mRecordingRuleDaoHelper = RecordingRuleDaoHelper.getInstance();
	private EtagDaoHelper mEtagDaoHelper = EtagDaoHelper.getInstance();
	private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	
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
		if( !NetworkHelper.getInstance().isMasterBackendConnected( this, locationProfile ) ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend unreachable" );
			completeIntent.putExtra( EXTRA_COMPLETE_OFFLINE, Boolean.TRUE );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, Master Backend unreachable" );
			return;
		}
		
		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		RecRules recordingRules = null;
    		try {

    			download( locationProfile );

			} catch( Exception e ) {
				Log.e( TAG, "onHandleIntent : error", e );
				
				passed = false;
			} finally {

    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Recording Rules Download Service Finished" );
    			if( null == recordingRules ) {
    				completeIntent.putExtra( EXTRA_COMPLETE_UPTODATE, passed );
    			}
    			
    			sendBroadcast( completeIntent );
    		}
    		
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private void download( final LocationProfile locationProfile ) throws Exception {
		Log.v( TAG, "download : enter" );

		EtagInfoDelegate etag = mEtagDaoHelper.findByEndpointAndDataId( this, locationProfile, DvrEndpoint.GET_RECORD_SCHEDULE_LIST.name(), "" );
//		etag = EtagInfoDelegate.createEmptyETag();
		
		ResponseEntity<RecRuleList> responseEntity = mMythtvServiceHelper.getMythServicesApi( locationProfile ).dvrOperations().getRecordScheduleList( -1, -1, etag );

		DateTime date = new DateTime( DateTimeZone.UTC );
		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			Log.i( TAG, "download : " + DvrEndpoint.GET_RECORD_SCHEDULE_LIST.getEndpoint() + " returned 200 OK" );
			RecRuleList recRuleList = responseEntity.getBody();

			if( null != recRuleList.getRecRules() ) {

				process( recRuleList.getRecRules(), locationProfile );	

				if( null != etag.getValue() ) {
					Log.i( TAG, "download : saving etag: " + etag.getValue() );

					etag.setEndpoint( DvrEndpoint.GET_RECORD_SCHEDULE_LIST.name() );
					etag.setDate( date );
					etag.setMasterHostname( locationProfile.getHostname() );
					etag.setLastModified( date );
					mEtagDaoHelper.save( this, locationProfile, etag );
				}

			}

		}

		if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
			Log.i( TAG, "download : " + DvrEndpoint.GET_RECORD_SCHEDULE_LIST.getEndpoint() + " returned 304 Not Modified" );

			if( null != etag.getValue() ) {
				Log.i( TAG, "download : saving etag: " + etag.getValue() );

				etag.setLastModified( date );
				mEtagDaoHelper.save( this, locationProfile, etag );
			}

		}
			
		Log.v( TAG, "download : exit" );
	}

	private void process( RecRules recRules, final LocationProfile locationProfile ) throws JsonGenerationException, JsonMappingException, IOException, RemoteException, OperationApplicationException {
		Log.v( TAG, "process : enter" );
		
		Log.v( TAG, "process : saving recording rules for host [" + locationProfile.getHostname() + ":" + locationProfile.getUrl() + "]" );
		
		int recordingRulesAdded = mRecordingRuleDaoHelper.load( this, locationProfile, recRules.getRecRules() );
		Log.v( TAG, "process : recordingRulesAdded=" + recordingRulesAdded );
		
		Log.v( TAG, "process : exit" );
	}

}
