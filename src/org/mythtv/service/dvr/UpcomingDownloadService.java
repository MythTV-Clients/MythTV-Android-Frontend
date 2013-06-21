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
import java.text.DecimalFormat;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.dvr.UpcomingDaoHelper;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.db.http.EtagInfoDelegate;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.FileHelper;
import org.mythtv.service.util.NetworkHelper;
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
public class UpcomingDownloadService extends MythtvService {

	private static final String TAG = UpcomingDownloadService.class.getSimpleName();
	private static final DecimalFormat formatter = new DecimalFormat( "###" );

	private static final String UPCOMING_FILE_PREFIX = "upcoming_";
	private static final String UPCOMING_FILE_EXT = ".json";
	
    public static final String ACTION_DOWNLOAD = "org.mythtv.background.upcomingDownload.ACTION_DOWNLOAD";
    public static final String ACTION_PROGRESS = "org.mythtv.background.upcomingDownload.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.upcomingDownload.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_FILENAME = "PROGRESS_FILENAME";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_UPTODATE = "COMPLETE_UPTODATE";
    public static final String EXTRA_COMPLETE_OFFLINE = "COMPLETE_OFFLINE";
    
	private NotificationManager mNotificationManager;
	private Notification mNotification = null;
	private PendingIntent mContentIntent = null;
	private int notificationId;
	
	private File upcomingDirectory = null;
	private UpcomingDaoHelper mUpcomingDaoHelper = UpcomingDaoHelper.getInstance();
	private EtagDaoHelper mEtagDaoHelper = EtagDaoHelper.getInstance();
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
		
		upcomingDirectory = FileHelper.getInstance().getProgramUpcomingDataDirectory();
		if( null == upcomingDirectory || !upcomingDirectory.exists() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Program Upcoming location can not be found" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, upcomingDirectory does not exist" );
			return;
		}

		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile( this );
		if( !NetworkHelper.getInstance().isMasterBackendConnected( this, locationProfile ) ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend unreachable" );
			completeIntent.putExtra( EXTRA_COMPLETE_OFFLINE, Boolean.TRUE );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, programCache does not exist" );
			return;
		}
		
		mNotificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		boolean passed = true;
    		
    		try {
    			sendNotification();

    			download( locationProfile );

			} catch( Exception e ) {
				Log.e( TAG, "onHandleIntent : error loading upcoming data", e );
				
				passed = false;
			} finally {
    			completed();

    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Upcoming Programs Download Service Finished" );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Recorded Programs Download Service Finished" );
   				completeIntent.putExtra( EXTRA_COMPLETE_UPTODATE, passed );
    			
    			sendBroadcast( completeIntent );
    		}
    		
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private void download( final LocationProfile locationProfile ) throws Exception {
		Log.v( TAG, "download : enter" );
		
		EtagInfoDelegate etag = mEtagDaoHelper.findByEndpointAndDataId( this, locationProfile, Endpoint.GET_UPCOMING_LIST.name(), "" );
		
		ResponseEntity<ProgramList> responseEntity = mMythtvServiceHelper.getMythServicesApi( locationProfile ).dvrOperations().getUpcomingList( -1, -1, false, etag );
		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			Log.i( TAG, "download : " + Endpoint.GET_UPCOMING_LIST.getEndpoint() + " returned 200 OK" );
	
			ProgramList programList = responseEntity.getBody();
			if( null != programList ) {
				
				if( null != programList.getPrograms() ) {
					
					process( programList.getPrograms(), locationProfile );
				
					if( null != etag.getValue() ) {
						
						etag.setEndpoint( Endpoint.GET_UPCOMING_LIST.name() );
						etag.setDate( new DateTime() );
						etag.setMasterHostname( locationProfile.getHostname() );
						etag.setLastModified( new DateTime() );
						mEtagDaoHelper.save( this, locationProfile, etag );
					}
						
				}
			
			}

		}
		
		if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
			Log.i( TAG, "download : " + Endpoint.GET_UPCOMING_LIST.getEndpoint() + " returned 304 Not Modified" );
			
			if( null != etag.getValue() ) {

				etag.setDate( new DateTime() );
				etag.setLastModified( new DateTime() );
				mEtagDaoHelper.save( this, locationProfile, etag );
			}
			
		}
		
		Log.v( TAG, "download : exit" );
	}

	private void process( Programs programs, final LocationProfile locationProfile ) throws JsonGenerationException, JsonMappingException, IOException, RemoteException, OperationApplicationException {
		Log.v( TAG, "process : enter" );
		
		Log.v( TAG, "process : saving upcoming for host [" + locationProfile.getHostname() + ":" + locationProfile.getUrl() + "]" );

		mMainApplication.getObjectMapper().writeValue( new File( upcomingDirectory, UPCOMING_FILE_PREFIX + locationProfile.getHostname() + UPCOMING_FILE_EXT ), programs );
		Log.v( TAG, "process : saved upcoming to " + upcomingDirectory.getAbsolutePath() );
		
		int programsAdded = mUpcomingDaoHelper.load( this, locationProfile, programs.getPrograms() );
		Log.v( TAG, "process : programsAdded=" + programsAdded );

		Log.v( TAG, "process : exit" );
	}

	// internal helpers
	
	@SuppressWarnings( "deprecation" )
	private void sendNotification() {

		long when = System.currentTimeMillis();
		notificationId = (int) when;
		
        mNotification = new Notification( android.R.drawable.stat_notify_sync, getResources().getString( R.string.notification_sync_upcoming ), when );

        Intent notificationIntent = new Intent();
        mContentIntent = PendingIntent.getActivity( this, 0, notificationIntent, 0 );

        mNotification.setLatestEventInfo( this, getResources().getString( R.string.app_name ), getResources().getString( R.string.notification_sync_upcoming ), mContentIntent );

        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        mNotificationManager.notify( notificationId, mNotification );
	
	}
	
    @SuppressWarnings( "deprecation" )
	public void progressUpdate( double percentageComplete ) {

    	CharSequence contentText = formatter.format( percentageComplete ) + "% complete";

    	mNotification.setLatestEventInfo( this, getResources().getString( R.string.notification_sync_upcoming ), contentText, mContentIntent );
    	mNotificationManager.notify( notificationId, mNotification );
    }

    public void completed()    {
    	
    	if( null != mNotificationManager ) {
    		mNotificationManager.cancel( notificationId );
    	}
    	
    }

}
