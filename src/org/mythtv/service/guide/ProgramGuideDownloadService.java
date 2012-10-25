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
package org.mythtv.service.guide;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.guide.ProgramGuide;
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
	private static final DecimalFormat formatter = new DecimalFormat( "###" );

	public static final Integer MAX_HOURS = 288;
	
    public static final String ACTION_DOWNLOAD = "org.mythtv.background.programGuideDownload.ACTION_DOWNLOAD";
    public static final String ACTION_PROGRESS = "org.mythtv.background.programGuideDownload.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.programGuideDownload.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATE = "PROGRESS_DATE";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_DOWNLOADED = "COMPLETE_DOWNLOADED";

	private NotificationManager mNotificationManager;
	private Notification mNotification = null;
	private PendingIntent mContentIntent = null;
	private int notificationId;
	
	private File programGuideCache = null;
	
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
		
		programGuideCache = mFileHelper.getProgramGuideDataDirectory();
		if( null == programGuideCache || !programGuideCache.exists() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Program Guide Cache location can not be found" );
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
		
		if( programGuideCache.list().length < MAX_HOURS ) {

			mNotificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

			if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
				Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

				boolean newDataDownloaded = false;

				try {
					sendNotification();

					DateTime start = new DateTime();
					start = start.withTime( 0, 0, 0, 001 );

					for( int currentHour = 0; currentHour < MAX_HOURS; currentHour++ ) {

						File file = new File( programGuideCache, DateUtils.fileDateTimeFormatter.print( start ) + FILENAME_EXT );
						if( !file.exists() ) {

							ProgramGuide programGuide = download( start );
							if( null != programGuide ) {

								newDataDownloaded = process( file, programGuide );
							}

						}

						start = start.plusHours( 1 );

						double percentage = ( (float) currentHour / (float) MAX_HOURS ) * 100;
						progressUpdate( percentage );
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
					completeIntent.putExtra( EXTRA_COMPLETE, "Program Guide Download Service Finished" );
					completeIntent.putExtra( EXTRA_COMPLETE_DOWNLOADED, newDataDownloaded );
					sendBroadcast( completeIntent );
				}

			}
		}
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private ProgramGuide download( DateTime start ) {
		Log.v( TAG, "download : enter" );
		
		DateTime end = new DateTime( start );
		end = end.withTime( start.getHourOfDay(), 59, 59, 999 );
		Log.i( TAG, "download : starting download for " + DateUtils.dateTimeFormatter.print( start ) + ", end time=" + DateUtils.dateTimeFormatter.print( end ) );

		ETagInfo etag = ETagInfo.createEmptyETag();
		ResponseEntity<ProgramGuideWrapper> responseEntity = mMainApplication.getMythServicesApi().guideOperations().getProgramGuide( start, end, 1, -1, true, etag );

		try {

			if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
				ProgramGuideWrapper programGuide = responseEntity.getBody();
					
				Log.v( TAG, "download : exit" );
				return programGuide.getProgramGuide();
			}
			
		} catch( Exception e ) {
			Log.e( TAG, "download : error downloading program guide" );
		}
			
		Log.v( TAG, "download : exit" );
		return null;
	}
	
	private boolean process( File file, ProgramGuide programGuide ) throws JsonGenerationException, JsonMappingException, IOException {
		Log.v( TAG, "process : enter" );
		
		List<String> callsigns = new ArrayList<String>();
		List<ChannelInfo> channels = new ArrayList<ChannelInfo>();
		for( ChannelInfo channel : programGuide.getChannels() ) {
			if( channel.isVisable() ) {
				if( !callsigns.contains( channel.getCallSign() ) ) {
					channels.add( channel );

					callsigns.add( channel.getCallSign() );
				}
			}
		}
		if( null != channels && !channels.isEmpty() ) {
			Collections.sort( channels );
		}

		programGuide.setChannels( channels );

		mMainApplication.getObjectMapper().writeValue( file, programGuide );
		
		Log.v( TAG, "process : exit" );
		return true;
	}
	
	@SuppressWarnings( "deprecation" )
	private void sendNotification() {

		long when = System.currentTimeMillis();
		notificationId = (int) when;
		
        mNotification = new Notification( android.R.drawable.stat_notify_sync, getResources().getString( R.string.notification_sync_program_guide ), when );

        Intent notificationIntent = new Intent();
        mContentIntent = PendingIntent.getActivity( this, 0, notificationIntent, 0 );

        mNotification.setLatestEventInfo( this, getResources().getString( R.string.app_name ), getResources().getString( R.string.notification_sync_program_guide ), mContentIntent );

        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        mNotificationManager.notify( notificationId, mNotification );
	
	}
	
    @SuppressWarnings( "deprecation" )
	public void progressUpdate( double percentageComplete ) {

    	CharSequence contentText = formatter.format( percentageComplete ) + "% complete";

    	mNotification.setLatestEventInfo( this, getResources().getString( R.string.notification_sync_program_guide ), contentText, mContentIntent );
    	mNotificationManager.notify( notificationId, mNotification );
    }

    public void completed()    {

    	if( null != mNotificationManager ) {
    		mNotificationManager.cancel( notificationId );
    	}
    	
    }

}
