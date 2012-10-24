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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.http.EtagConstants;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.UrlUtils;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.ProgramList;
import org.mythtv.services.api.dvr.Programs;
import org.mythtv.services.api.dvr.impl.DvrTemplate.Endpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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

	private NotificationManager mNotificationManager;
	private int notificationId;
	
	private File recordedDirectory = null;
	private RecordedProcessor mRecordedProcessor;
	
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
		
		mRecordedProcessor = new RecordedProcessor( this );

		recordedDirectory = mFileHelper.getProgramRecordedDataDirectory();
		if( null == recordedDirectory || !recordedDirectory.exists() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Program Recorded location can not be found" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, programCache does not exist" );
			return;
		}

		if( !isBackendConnected() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend unreachable" );
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

    			programs = download();
    			if( null != programs ) {
    				cleanup();
    				
    				process( programs );
    				
    				cleanupRecordedArtwork( programs );
    				
    				downloadBanners();
    			}
			} catch( JsonGenerationException e ) {
				Log.e( TAG, "onHandleIntent : error generating json", e );
			} catch( JsonMappingException e ) {
				Log.e( TAG, "onHandleIntent : error mapping json", e );
			} catch( IOException e ) {
				Log.e( TAG, "onHandleIntent : error handling files", e );
			} finally {
    			completed();

    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Recorded Programs Download Service Finished" );
    			if( null == programs ) {
    				completeIntent.putExtra( EXTRA_COMPLETE_UPTODATE, Boolean.TRUE );
    			}
    			
    			sendBroadcast( completeIntent );
    		}
    		
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private Programs download() {
		Log.v( TAG, "download : enter" );

//		Cursor etags = getContentResolver().query( EtagConstants.CONTENT_URI, null, null, null, null );
//		while( etags.moveToNext() ) {
//			Long id = etags.getLong( etags.getColumnIndexOrThrow( EtagConstants._ID ) );
//			String endpoint = etags.getString( etags.getColumnIndexOrThrow( EtagConstants.FIELD_ENDPOINT ) );
//			String value = etags.getString( etags.getColumnIndexOrThrow( EtagConstants.FIELD_VALUE ) );
//			
//			Log.v( TAG, "download : etag=" + id + ", endpoint=" + endpoint + ", value=" + value );
//		}
//		etags.close();
		
		Long id = null;
		ETagInfo etag = ETagInfo.createEmptyETag();
		Cursor cursor = getContentResolver().query( Uri.withAppendedPath( EtagConstants.CONTENT_URI, "endpoint" ), null, EtagConstants.FIELD_ENDPOINT + " = ?" ,new String[] { Endpoint.GET_RECORDED_LIST.name() }, null );
		if( cursor.moveToFirst() ) {
			id = cursor.getLong( cursor.getColumnIndexOrThrow( EtagConstants._ID ) );
			String value = cursor.getString( cursor.getColumnIndexOrThrow( EtagConstants.FIELD_VALUE ) );
			
			etag.setETag( value );
			Log.v( TAG, "download : etag=" + etag.getETag() );
		}
		cursor.close();
		
		ResponseEntity<ProgramList> responseEntity = mMainApplication.getMythServicesApi().dvrOperations().getRecordedList( etag );

		try {
		
			if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
				ProgramList programList = responseEntity.getBody();
				
				ContentValues values = new ContentValues();
				values.put( EtagConstants.FIELD_ENDPOINT, Endpoint.GET_RECORDED_LIST.name() );
				values.put( EtagConstants.FIELD_VALUE, etag.getETag() );
				values.put( EtagConstants.FIELD_DATE, ( new DateTime() ).getMillis() );
				
				if( null == id ) {
					Log.v( TAG, "download : adding new etag" );
					
					getContentResolver().insert( EtagConstants.CONTENT_URI, values );
				} else {
					Log.v( TAG, "download : updating existing etag" );

					getContentResolver().update( ContentUris.withAppendedId( EtagConstants.CONTENT_URI, id ), values, null, null );
				}
				
				Log.v( TAG, "download : exit" );
				return programList.getPrograms();
			}

			if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
				Log.i( TAG, "download : " + Endpoint.GET_RECORDED_LIST.getEndpoint() + " returned 304 Not Modified" );

				ContentValues values = new ContentValues();
				values.put( EtagConstants.FIELD_ENDPOINT, Endpoint.GET_RECORDED_LIST.name() );
				values.put( EtagConstants.FIELD_VALUE, etag.getETag() );
				values.put( EtagConstants.FIELD_DATE, ( new DateTime() ).getMillis() );
				getContentResolver().update( ContentUris.withAppendedId( EtagConstants.CONTENT_URI, id ), values, null, null );

			}
			
		} catch( Exception e ) {
			Log.e( TAG, "download : error downloading upcoming program list" );
		}
		
		Log.v( TAG, "download : exit" );
		return null;
	}

	private void cleanup() throws IOException {
		Log.v( TAG, "cleanup : enter" );
		
		FileUtils.cleanDirectory( recordedDirectory );
//		FileUtils.cleanDirectory( mFileHelper.getProgramGroupsDataDirectory() );
		
		Log.v( TAG, "cleanup : exit" );
	}

	private void process( Programs programs ) throws JsonGenerationException, JsonMappingException, IOException {
		Log.v( TAG, "process : enter" );
		
		List<Program> filteredPrograms = new ArrayList<Program>();
		if( null != programs ) {
			for( Program program : programs.getPrograms() ) {
				if( !program.getRecording().getRecordingGroup().equalsIgnoreCase( "livetv" ) ) {
					filteredPrograms.add( program );
				}
			}
		}
		programs.setPrograms( filteredPrograms );
		
		mMainApplication.getObjectMapper().writeValue( new File( recordedDirectory, RECORDED_FILE ), programs );

		int programsAdded = mRecordedProcessor.processPrograms( programs );
		Log.v( TAG, "process : programsAdded=" + programsAdded );
		
		Log.v( TAG, "process : exit" );
	}

	// internal helpers
	
	private void cleanupRecordedArtwork( Programs programs ) throws IOException {
		Log.v( TAG, "cleanupRecordedArtwork : enter" );
		
		Map<String, Boolean> directories = new HashMap<String, Boolean>();
		
		Log.v( TAG, "cleanupRecordedArtwork : Existing Artwork Directories" );
		File programGroupsDirectory = mFileHelper.getProgramGroupsDataDirectory();
		if( null != programGroupsDirectory && programGroupsDirectory.exists() ) {
			Log.v( TAG, "cleanupRecordedArtwork : programGroups exists" );

			for( String directory : programGroupsDirectory.list() ) {
				Log.v( TAG, "cleanupRecordedArtwork : directory=" + directory );
				
				if( !directories.containsKey( directories ) ) {
					directories.put( directory, Boolean.FALSE );
				}
			}
		}
		
		for( Program program : programs.getPrograms() ) {
			String encodedTitle = UrlUtils.encodeUrl( program.getTitle() );
			if( directories.containsKey( encodedTitle ) && !directories.get( encodedTitle ).booleanValue() ) {
				directories.put( encodedTitle, Boolean.TRUE );
				
				Log.v( TAG, "cleanupRecordedArtwork : marking directory '" + encodedTitle + "' to be saved" );
			}
		}
		
		for( String directory : directories.keySet() ) {
			if( !directories.get( directory ).booleanValue() ) {
				FileUtils.cleanDirectory( new File( programGroupsDirectory, directory ) );

				Log.v( TAG, "cleanupRecordedArtwork : deleted artwork directory '" + directory + "'" );
			}
		}
		
		Log.v( TAG, "cleanupRecordedArtwork : exit" );
	}
	
	private void downloadBanners() {
		Log.v( TAG, "downloadBanners : enter" );
		
		Cursor cursor = getContentResolver().query( ProgramConstants.CONTENT_URI_RECORDED, new String[] { ProgramConstants._ID, ProgramConstants.FIELD_TITLE }, null, null, null );
		while( cursor.moveToNext() ) {
			Long id = cursor.getLong( cursor.getColumnIndexOrThrow( ProgramConstants._ID ) );
	        String title = cursor.getString( cursor.getColumnIndexOrThrow( ProgramConstants.FIELD_TITLE ) );

			File programGroupDirectory = mFileHelper.getProgramGroupDirectory( title );
			if( null != programGroupDirectory && programGroupDirectory.exists() ) {
					
				File banner = new File( programGroupDirectory, BannerDownloadService.BANNER_FILE );
				if( !banner.exists() ) {
					Intent downloadBannerIntent = new Intent( BannerDownloadService.ACTION_DOWNLOAD );
					downloadBannerIntent.putExtra( BannerDownloadService.BANNER_RECORDED_ID, id );
					startService( downloadBannerIntent );
				}
					
			}

		}
		cursor.close();
		
		Log.v( TAG, "downloadBanners : exit" );
	}
	
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
