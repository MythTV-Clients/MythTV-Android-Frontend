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
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.dvr.programGroup.ProgramGroup;
import org.mythtv.db.dvr.programGroup.ProgramGroupDaoHelper;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.FileHelper;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.Bool;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.MythServiceApiRuntimeException;
import org.mythtv.services.api.dvr.Program;
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
import android.os.Bundle;
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

	private static final String RECORDED_FILE_PREFIX = "recorded_";
	private static final String RECORDED_FILE_EXT = ".json";
	
    public static final String ACTION_DOWNLOAD = "org.mythtv.background.recordedDownload.ACTION_DOWNLOAD";
    public static final String ACTION_REMOVE = "org.mythtv.background.recordedDownload.ACTION_REMOVE";
    public static final String ACTION_PROGRESS = "org.mythtv.background.recordedDownload.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.recordedDownload.ACTION_COMPLETE";

    public static final String KEY_CHANNEL_ID = "KEY_CHANNEL_ID";
    public static final String KEY_START_TIMESTAMP = "KEY_START_TIMESTAMP";
    
    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATA = "PROGRESS_DATA";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_UPTODATE = "COMPLETE_UPTODATE";
    public static final String EXTRA_COMPLETE_OFFLINE = "COMPLETE_OFFLINE";
    
	private NotificationManager mNotificationManager;
	private int notificationId;
	
	private File recordedDirectory = null;
	private RecordedDaoHelper mRecordedDaoHelper = RecordedDaoHelper.getInstance();
	private ProgramGroupDaoHelper mProgramGroupDaoHelper = ProgramGroupDaoHelper.getInstance();
	private EtagDaoHelper mEtagDaoHelper = EtagDaoHelper.getInstance();
	private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	
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
		
		recordedDirectory = FileHelper.getInstance().getProgramRecordedDataDirectory();
		if( null == recordedDirectory || !recordedDirectory.exists() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Program Recorded location can not be found" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, programCache does not exist" );
			return;
		}

		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile( this );
		if( !NetworkHelper.getInstance().isMasterBackendConnected( this, locationProfile ) ) {
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

    			download( locationProfile );

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
		
		if ( intent.getAction().equals( ACTION_REMOVE ) ) {
    		Log.i( TAG, "onHandleIntent : REMOVE action selected" );

    		boolean deleted = false;
    		try {

    			Bundle extras = intent.getExtras();
    			int channelId = extras.getInt( KEY_CHANNEL_ID );
    			long startTimestamp = extras.getLong( KEY_START_TIMESTAMP );
    			
    			if( channelId == 0 || startTimestamp == 0 ) {
    				passed = false;
    			} else {
    				deleted = removeRecorded( locationProfile, channelId, new DateTime( startTimestamp ) );
    			}
    			
			} catch( Exception e ) {
				Log.e( TAG, "onHandleIntent : error", e );
				
				passed = false;
			} finally {

    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, ( "Episode" + ( !deleted ? " NOT " : " " ) + "deleted!" ) );
    			
    			sendBroadcast( completeIntent );
    		}
    		
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private void download( final LocationProfile locationProfile ) throws Exception {
		Log.v( TAG, "download : enter" );

		ETagInfo etag = mEtagDaoHelper.findByEndpointAndDataId( this, locationProfile, Endpoint.GET_RECORDED_LIST.name(), "" );
		
		ResponseEntity<ProgramList> responseEntity = mMythtvServiceHelper.getMythServicesApi( locationProfile ).dvrOperations().getRecordedList( etag );

		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			Log.i( TAG, "download : " + Endpoint.GET_RECORDED_LIST.getEndpoint() + " returned 200 OK" );
			ProgramList programList = responseEntity.getBody();

			if( null != programList.getPrograms() ) {

				process( programList.getPrograms(), locationProfile );	

				if( null != etag.getETag() ) {
					Log.i( TAG, "download : saving etag: " + etag.getETag() );
					mEtagDaoHelper.save( this, locationProfile, etag, Endpoint.GET_RECORDED_LIST.name(), "" );
				}

			}

		}

		if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
			Log.i( TAG, "download : " + Endpoint.GET_RECORDED_LIST.getEndpoint() + " returned 304 Not Modified" );

			if( null != etag.getETag() ) {
				mEtagDaoHelper.save( this, locationProfile, etag, Endpoint.GET_RECORDED_LIST.name(), "" );
			}

		}
			
		Log.v( TAG, "download : exit" );
	}

	private void process( Programs programs, final LocationProfile locationProfile ) throws JsonGenerationException, JsonMappingException, IOException, RemoteException, OperationApplicationException {
		Log.v( TAG, "process : enter" );
		
		Log.v( TAG, "process : saving recorded for host [" + locationProfile.getHostname() + ":" + locationProfile.getUrl() + "]" );
		
		mMainApplication.getObjectMapper().writeValue( new File( recordedDirectory, RECORDED_FILE_PREFIX + locationProfile.getHostname() + RECORDED_FILE_EXT ), programs );
		Log.v( TAG, "process : saved recorded to " + recordedDirectory.getAbsolutePath() );

		int programsAdded = mRecordedDaoHelper.load( this, locationProfile, programs.getPrograms() );
		Log.v( TAG, "process : programsAdded=" + programsAdded );
		
		Log.v( TAG, "process : exit" );
	}

	private Boolean removeRecorded( final LocationProfile locationProfile, int channelId, DateTime startTimestamp ) throws MythServiceApiRuntimeException {
		Log.v( TAG, "removeRecorded : enter" );
		
		boolean removed = false;
		
		ResponseEntity<Bool> responseEntity = mMythtvServiceHelper.getMythServicesApi( locationProfile ).dvrOperations().removeRecorded( channelId, startTimestamp );
		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			removed = responseEntity.getBody().getBool();
			
			if( removed ) {
				
				removeRecordedLocal( locationProfile, channelId, startTimestamp );
					
			}
		}

		Log.v( TAG, "removeRecorded : exit" );
		return removed;
	}
	
	private void removeRecordedLocal( final LocationProfile locationProfile, int channelId, DateTime startTimestamp ) {
		Log.v( TAG, "removeRecordedLocal : enter" );
		
		Program program = mRecordedDaoHelper.findOne( this, locationProfile, channelId, startTimestamp );
		String title = program.getTitle();
				
		Log.v( TAG, "removeRecordedLocal : deleting recorded program in content provider" );
		int deleted = mRecordedDaoHelper.delete( this, locationProfile, program );
		if( deleted == 1 ) {
			Log.v( TAG, "removeRecordedLocal : recorded program deleted, clean up program group if needed" );

			removeProgramGroupLocal( locationProfile, title );
		}
		
		Log.v( TAG, "removeRecordedLocal : exit" );
	}

	private void removeProgramGroupLocal( final LocationProfile locationProfile, final String title ) {
		Log.v( TAG, "removeProgramGroupLocal : enter" );
		
		ProgramGroup programGroup = mProgramGroupDaoHelper.findByTitle( this, locationProfile, title );

		List<Program> programs = mRecordedDaoHelper.findAllByTitle( this, locationProfile, title );
		if( null == programs || programs.isEmpty() ) {

			mProgramGroupDaoHelper.delete( this, programGroup );
			
		}

		Log.v( TAG, "removeProgramGroupLocal : exit" );
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
