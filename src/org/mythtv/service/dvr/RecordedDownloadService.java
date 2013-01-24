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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.mythtv.R;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.service.MythtvService;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.dvr.ProgramList;
import org.mythtv.services.api.dvr.Programs;
import org.mythtv.services.api.dvr.impl.DvrTemplate.Endpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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
public class RecordedDownloadService extends MythtvService {

	private static final String TAG = RecordedDownloadService.class.getSimpleName();

	public static final String RECORDED_FILE = "recorded.json";
	
    public static final String ACTION_DOWNLOAD = "org.mythtv.background.recordedDownload.ACTION_DOWNLOAD";
    public static final String ACTION_PROGRESS = "org.mythtv.background.recordedDownload.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.recordedDownload.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATA = "PROGRESS_DATA";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_UPTODATE = "COMPLETE_UPTODATE";
    public static final String EXTRA_COMPLETE_OFFLINE = "COMPLETE_OFFLINE";
    
	private NotificationManager mNotificationManager;
	private int notificationId;
	
	private File recordedDirectory = null;
	private RecordedDaoHelper mRecordedDaoHelper;
	private EtagDaoHelper mEtagDaoHelper;
	
	public RecordedDownloadService() {
		super( "RecordedDownloadService" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );
		super.onHandleIntent( intent );
		
		boolean passed = true;
		
		mRecordedDaoHelper = new RecordedDaoHelper( this );
		mEtagDaoHelper = new EtagDaoHelper( this );
		
		recordedDirectory = mFileHelper.getProgramRecordedDataDirectory();
		if( null == recordedDirectory || !recordedDirectory.exists() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Program Recorded location can not be found" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, programCache does not exist" );
			return;
		}

		if( !mNetworkHelper.isMasterBackendConnected() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend unreachable" );
			completeIntent.putExtra( EXTRA_COMPLETE_OFFLINE, Boolean.TRUE );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, Master Backend unreachable" );
			return;
		}
		
		mNotificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		Programs programs = null;
    		try {
    			sendNotification();

    			download();

			} catch( Exception e ) {
				Log.e( TAG, "onHandleIntent : error", e );
				
				passed = false;
			} finally {
    			completed();

    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Recorded Programs Download Service Finished" );
    			if( null == programs ) {
    				completeIntent.putExtra( EXTRA_COMPLETE_UPTODATE, passed );
    			}
    			
    			sendBroadcast( completeIntent );
    		}
    		
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private void download() throws Exception {
		Log.v( TAG, "download : enter" );

		ETagInfo etag = mEtagDaoHelper.findByEndpointAndDataId( Endpoint.GET_RECORDED_LIST.name(), "" );
		
		ResponseEntity<ProgramList> responseEntity = mMainApplication.getMythServicesApi().dvrOperations().getRecordedList( etag );

		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			Log.i( TAG, "download : " + Endpoint.GET_RECORDED_LIST.getEndpoint() + " returned 200 OK" );
			ProgramList programList = responseEntity.getBody();

//			Log.v( TAG, "download : loaded local file" );
//			File recorded = new File( mFileHelper.getProgramRecordedDataDirectory(), "GetRecordedList.json" );
//			programList = mMainApplication.getObjectMapper().readValue( recorded, ProgramList.class );
			
			if( null != programList.getPrograms() ) {
				cleanup();

				process( programList.getPrograms() );	

				if( null != etag.getETag() ) {
					Log.i( TAG, "download : saving etag: " + etag.getETag() );
					mEtagDaoHelper.save( etag, Endpoint.GET_RECORDED_LIST.name(), "" );
				}

			}

		}

		if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
			Log.i( TAG, "download : " + Endpoint.GET_RECORDED_LIST.getEndpoint() + " returned 304 Not Modified" );

			if( null != etag.getETag() ) {
				mEtagDaoHelper.save( etag, Endpoint.GET_RECORDED_LIST.name(), "" );
			}

		}
			
		Log.v( TAG, "download : exit" );
	}

	private void cleanup() throws IOException {
		Log.v( TAG, "cleanup : enter" );
		
		FileUtils.cleanDirectory( recordedDirectory );
		
		Log.v( TAG, "cleanup : exit" );
	}

	private void process( Programs programs ) throws JsonGenerationException, JsonMappingException, IOException, RemoteException, OperationApplicationException {
		Log.v( TAG, "process : enter" );
		
		mMainApplication.getObjectMapper().writeValue( new File( recordedDirectory, RECORDED_FILE ), programs );

		int programsAdded = mRecordedDaoHelper.load( programs.getPrograms() );
		Log.v( TAG, "process : programsAdded=" + programsAdded );
		
		Log.v( TAG, "process : exit" );
	}

	// internal helpers
	
	@SuppressWarnings( "deprecation" )
	private void sendNotification() {

		long when = System.currentTimeMillis();
		notificationId = (int) when;
		
        Notification mNotification = new Notification( android.R.drawable.stat_notify_sync, getResources().getString( R.string.notification_sync_recordings ), when );

        Intent notificationIntent = new Intent();
        PendingIntent mContentIntent = PendingIntent.getActivity( this, 0, notificationIntent, 0 );

        mNotification.setLatestEventInfo( this, getResources().getString( R.string.app_name ), getResources().getString( R.string.notification_sync_recordings ), mContentIntent );

        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        mNotificationManager.notify( notificationId, mNotification );
	
	}
	
    public void completed()    {
        
    	if( null != mNotificationManager ) {
        	mNotificationManager.cancel( notificationId );
    	}
    
    }

}
