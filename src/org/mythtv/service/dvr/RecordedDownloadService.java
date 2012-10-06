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

import org.mythtv.R;
import org.mythtv.service.MythtvService;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.dvr.ProgramList;
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
public class RecordedDownloadService extends MythtvService {

	private static final String TAG = RecordedDownloadService.class.getSimpleName();

	public static final String RECORDED_FILE = "recorded.json";
	
    public static final String ACTION_DOWNLOAD = "org.mythtv.background.recordedDownload.ACTION_DOWNLOAD";
    public static final String ACTION_PROGRESS = "org.mythtv.background.recordedDownload.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.recordedDownload.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATE = "PROGRESS_DATE";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";

	private NotificationManager mNotificationManager;
	private int notificationId;
	
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
		
		mNotificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		download();
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private void download() {
		Log.v( TAG, "download : enter" );
		
		sendNotification();
		
		File programCache = mFileHelper.getProgramDataDirectory();
		if( programCache.exists() ) {
			
			ETagInfo etag = ETagInfo.createEmptyETag();
			ResponseEntity<ProgramList> responseEntity = mMainApplication.getMythServicesApi().dvrOperations().getRecordedList( etag );
			if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {

				Intent progressIntent = new Intent( ACTION_PROGRESS );

				File existing = new File( programCache, RECORDED_FILE );
				if( existing.exists() ) {
					existing.delete();
				}

				try {
					ProgramList programList = responseEntity.getBody();
					mObjectMapper.writeValue( new File( programCache, RECORDED_FILE ), programList.getPrograms() );

					Log.i( TAG, "download : downloaded 'recorded'" );
					progressIntent.putExtra( EXTRA_PROGRESS, "downloaded 'recorded'" );
				} catch( JsonGenerationException e ) {
					Log.e( TAG, "download : JsonGenerationException - error downloading file for 'recorded'", e );

					progressIntent.putExtra( EXTRA_PROGRESS_ERROR, "error downloading file for 'recorded': " + e.getLocalizedMessage() );
				} catch( JsonMappingException e ) {
					Log.e( TAG, "download : JsonGenerationException - error downloading file for 'recorded'", e );

					progressIntent.putExtra( EXTRA_PROGRESS_ERROR, "error downloading file for 'recorded': " + e.getLocalizedMessage() );
				} catch( IOException e ) {
					Log.e( TAG, "download : JsonGenerationException - error downloading file for 'recorded'", e );

					progressIntent.putExtra( EXTRA_PROGRESS_ERROR, "IOException - error downloading file for 'recorded': " + e.getLocalizedMessage() );
				}

				sendBroadcast( progressIntent );

			}
			
		}
		
		completed();
		
		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE, "Recorded Programs Download Service Finished" );
		sendBroadcast( completeIntent );
		
		Log.v( TAG, "download : exit" );
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
        mNotificationManager.cancel( notificationId );
    }

}
