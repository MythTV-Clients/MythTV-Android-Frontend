/**
 *  This file is part of MythTV for Android
 * 
 *  MythTV for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MythTV for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MythTV for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * This software can be found at <https://github.com/MythTV-Android/mythtv-for-android/>
 *
 */
package org.mythtv.service.guide;

import java.io.File;
import java.io.IOException;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.guide.ProgramGuideWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGuideDownloadService extends MythtvService {

	private static final String TAG = ProgramGuideDownloadService.class.getSimpleName();

	private static final Integer MAX_HOURS = 288;
	
    public static final String ACTION_DOWNLOAD = "org.mythtv.background.programGuideDownload.ACTION_DOWNLOAD";
    public static final String ACTION_PROGRESS = "org.mythtv.background.programGuideDownload.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.programGuideDownload.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATE = "PROGRESS_DATE";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_DOWNLOADED = "COMPLETE_DOWNLOADED";

	private NotificationManager mNotificationManager;
	private int notificationId;
	
	public ProgramGuideDownloadService() {
		super( "ProgamGuideDownloadService" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );
		super.onHandleIntent( intent );
		
		mNotificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

        if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		download();
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private void download() {
//		Log.v( TAG, "download : enter" );
		
		boolean newDataDownloaded = false;
		
		sendNotification();
		
		DateTime start = new DateTime();
		start = start.withTime( 0, 0, 0, 001 );
		
		File programGuideCache = mFileHelper.getProgramGuideDataDirectory();
		if( programGuideCache.exists() ) {

			for( int currentHour = 0; currentHour < MAX_HOURS; currentHour++ ) {

				String sStart = fileDateTimeFormatter.print( start );
				String filename = sStart + FILENAME_EXT;
				File file = new File( programGuideCache, filename );
				if( !file.exists() ) {
					DateTime end = new DateTime( start );
					end = end.withTime( start.getHourOfDay(), 59, 59, 999 );
					Log.i( TAG, "download : starting download for " + DateUtils.dateTimeFormatter.print( start ) + ", end time=" + DateUtils.dateTimeFormatter.print( end ) );

					ETagInfo etag = ETagInfo.createEmptyETag();
					ResponseEntity<ProgramGuideWrapper> responseEntity = mMainApplication.getMythServicesApi().guideOperations().getProgramGuide( start, end, 1, -1, false, etag );
					if( null != responseEntity ) {

						if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {

							Intent progressIntent = new Intent( ACTION_PROGRESS );

							try {
								ProgramGuideWrapper programGuide = responseEntity.getBody();
								
								mObjectMapper.writeValue( file, programGuide.getProgramGuide() );

								newDataDownloaded = true;

								progressIntent.putExtra( EXTRA_PROGRESS, "Completed downloading file for " + sStart );
								progressIntent.putExtra( EXTRA_PROGRESS_DATE, DateUtils.dateTimeFormatter.print( start ) );
							} catch( JsonGenerationException e ) {
								Log.e( TAG, "download : JsonGenerationException - error downloading file for " + sStart, e );

								progressIntent.putExtra( EXTRA_PROGRESS_ERROR, "error downloading file for " + sStart + ": " + e.getLocalizedMessage() );
							} catch( JsonMappingException e ) {
								Log.e( TAG, "download : JsonMappingException - error downloading file for " + sStart, e );

								progressIntent.putExtra( EXTRA_PROGRESS_ERROR, "error downloading file for " + sStart + ": " + e.getLocalizedMessage() );
							} catch( IOException e ) {
								Log.e( TAG, "download : IOException - error downloading file for " + sStart, e );

								progressIntent.putExtra( EXTRA_PROGRESS_ERROR, "IOException - error downloading file for " + sStart + ": " + e.getLocalizedMessage() );
							}

							sendBroadcast( progressIntent );

						}

					}

				}

				start = start.plusHours( 1 );
			}

		}
		
		completed();
		
		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE, "Program Guide Download Service Finished" );
		completeIntent.putExtra( EXTRA_COMPLETE_DOWNLOADED, newDataDownloaded );
		sendBroadcast( completeIntent );
		
//		Log.v( TAG, "download : exit" );
	}
	
	// internal helpers
	
	@SuppressWarnings( "deprecation" )
	private void sendNotification() {

		long when = System.currentTimeMillis();
		notificationId = (int) when;
		
        Notification mNotification = new Notification( android.R.drawable.stat_notify_sync, getResources().getString( R.string.notification_sync_program_guide ), when );

        Intent notificationIntent = new Intent();
        PendingIntent mContentIntent = PendingIntent.getActivity( this, 0, notificationIntent, 0 );

        mNotification.setLatestEventInfo( this, getResources().getString( R.string.app_name ), getResources().getString( R.string.notification_sync_program_guide ), mContentIntent );

        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        mNotificationManager.notify( notificationId, mNotification );
	
	}
	
    public void completed()    {
        mNotificationManager.cancel( notificationId );
    }

}
