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
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.R;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.ProgramList;
import org.mythtv.services.api.dvr.Programs;
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
public class UpcomingDownloadService extends MythtvService {

	private static final String TAG = UpcomingDownloadService.class.getSimpleName();
	private static final DecimalFormat formatter = new DecimalFormat( "###" );

	public static final String UPCOMING_FILE = "upcoming.json";
	public static final String UPCOMING_FILE_EXT = "-upcoming.json";
	
    public static final String ACTION_DOWNLOAD = "org.mythtv.background.upcomingDownload.ACTION_DOWNLOAD";
    public static final String ACTION_PROGRESS = "org.mythtv.background.upcomingDownload.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.upcomingDownload.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_FILENAME = "PROGRESS_FILENAME";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";

	private NotificationManager mNotificationManager;
	private Notification mNotification = null;
	private PendingIntent mContentIntent = null;
	private int notificationId;
	
	private File programCache = null;
	
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
		
		programCache = mFileHelper.getProgramDataDirectory();
		if( null == programCache || !programCache.exists() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Program Cache location can not be found" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, programCache does not exist" );
			return;
		}

		ResponseEntity<String> hostname = mMainApplication.getMythServicesApi().mythOperations().getHostName();
		if( null == hostname || "".equals( hostname ) ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend unreachable" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, programCache does not exist" );
			return;
		}
		
		mNotificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		try {
    			sendNotification();

    			Programs programs = download();
    			if( null != programs ) {
    				cleanup();
    				
    				process( programs );
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
    			completeIntent.putExtra( EXTRA_COMPLETE, "Upcoming Programs Download Service Finished" );
    			sendBroadcast( completeIntent );
    		}
    		
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private Programs download() {
		Log.v( TAG, "download : enter" );
		
		ETagInfo etag = ETagInfo.createEmptyETag();
		ResponseEntity<ProgramList> responseEntity = mMainApplication.getMythServicesApi().dvrOperations().getUpcomingList( -1, -1, false, etag );
		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {

			try {
				ProgramList programList = responseEntity.getBody();
				return programList.getPrograms();
			} catch( Exception e ) {
				Log.e( TAG, "download : error downloading upcoming program list" );
			}
		}
		
		Log.v( TAG, "download : exit" );
		return null;
	}

	private void cleanup() throws IOException {
		Log.v( TAG, "cleanup : enter" );
		
		File existing = new File( programCache, UPCOMING_FILE );
		if( null != existing && existing.exists() ) {
			existing.delete();
			Log.v( TAG, "cleanup : deleted filename=" + UPCOMING_FILE );
			
			FilenameFilter filter = new FilenameFilter() {

				public boolean accept( File dir, String filename ) {
					return filename.endsWith( UPCOMING_FILE_EXT );
				}

			};

			for( String filename : programCache.list( filter ) ) {

				File deleted = new File( programCache, filename );
				if( deleted.delete() ) {
					Log.v( TAG, "cleanup : deleted filename=" + filename );
				}
			}

		}

		Log.v( TAG, "cleanup : exit" );
	}
	
	private void process( Programs programs ) throws JsonGenerationException, JsonMappingException, IOException {
		Log.v( TAG, "process : enter" );
		
		mMainApplication.getObjectMapper().writeValue( new File( programCache, UPCOMING_FILE ), programs );

		Map<DateTime, Programs> upcomingDates = new TreeMap<DateTime, Programs>();
		for( Program program : programs.getPrograms() ) {
			//Log.v( TAG, "download : upcoming program iteration" );

			program.setStartTime( program.getStartTime().withZone( DateTimeZone.forID( TimeZone.getDefault().getID() ) ) );
			program.setEndTime( program.getEndTime().withZone( DateTimeZone.forID( TimeZone.getDefault().getID() ) ) );

			DateTime date = program.getStartTime().withTime( 0, 0, 0, 0 ).withZone( DateTimeZone.forID( TimeZone.getDefault().getID() ) );
			if( upcomingDates.containsKey( date ) ) {
				//Log.v( TAG, "download : adding program to EXISTING " + DateUtils.dateFormatter.print( date ) );

				upcomingDates.get( date ).getPrograms().add( program );
			} else {
				//Log.v( TAG, "download : adding program to NEW " + DateUtils.dateFormatter.print( date ) );
				Programs datePrograms = new Programs();

				List<Program> dateProgramList = new ArrayList<Program>();
				dateProgramList.add( program );
				datePrograms.setPrograms( dateProgramList );

				upcomingDates.put( date, datePrograms );
			}

		}

		int count = 1;
		for( DateTime date : upcomingDates.keySet() ) {
			Programs upcomingPrograms = upcomingDates.get( date );

			String key = DateUtils.dateFormatter.print( date );

			mMainApplication.getObjectMapper().writeValue( new File( programCache, key + UPCOMING_FILE_EXT ), upcomingPrograms );
			
			double percentage = ( (float) count / (float) programs.getPrograms().size() ) * 100;
			progressUpdate( percentage );
		}

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
