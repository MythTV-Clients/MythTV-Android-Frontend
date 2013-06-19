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

import java.text.DecimalFormat;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.dvr.ProgramGuideDaoHelper;
import org.mythtv.db.http.EtagConstants;
import org.mythtv.db.http.EtagInfoDelegate;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.guide.ProgramGuide;
import org.mythtv.services.api.guide.ProgramGuideWrapper;
import org.mythtv.services.api.guide.impl.GuideTemplate.Endpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGuideDownloadServiceNew extends MythtvService {

	private static final String TAG = ProgramGuideDownloadServiceNew.class.getSimpleName();
	private static final DecimalFormat formatter = new DecimalFormat( "###" );
	
	public static final Integer MAX_HOURS = 288; //288

    public static final String ACTION_DOWNLOAD = "org.mythtv.background.programGuideDownloadNew.ACTION_DOWNLOAD";
    public static final String ACTION_PROGRESS = "org.mythtv.background.programGuideDownloadNew.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.programGuideDownloadNew.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATE = "PROGRESS_DATE";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_DOWNLOADED = "COMPLETE_DOWNLOADED";
    public static final String EXTRA_COMPLETE_OFFLINE = "COMPLETE_OFFLINE";

	private NotificationManager mNotificationManager;
	private Notification mNotification = null;
	private PendingIntent mContentIntent = null;
	private int notificationId = 1001;

	private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	private ProgramGuideDaoHelper mProgramGuideDaoHelper = ProgramGuideDaoHelper.getInstance(); 
	
	public ProgramGuideDownloadServiceNew() {
		super( "ProgamGuideDownloadServiceNew" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.MythtvService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.v( TAG, "onHandleIntent : enter" );
		super.onHandleIntent( intent );

		mNotificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
		
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
			
			sendNotification();
			
			boolean newDataDownloaded = false;
			
			DateTime start = new DateTime().withTimeAtStartOfDay();

			try {
				for( int i = 1; i <= MAX_HOURS; i++ ) {

					newDataDownloaded = download( start, locationProfile );

					start = start.plusHours( 1 );

					double percentage = ( (float) i / (float) MAX_HOURS ) * 100;
					progressUpdate( percentage );

				}
			} finally {
			
				completed();

				Intent completeIntent = new Intent( ACTION_COMPLETE );
				completeIntent.putExtra( EXTRA_COMPLETE, "Program Guide Download Service Finished" );
				completeIntent.putExtra( EXTRA_COMPLETE_DOWNLOADED, newDataDownloaded );
				sendBroadcast( completeIntent );
			
			}
			
		}
		
		Log.v( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private boolean download( DateTime start, LocationProfile locationProfile ) {
		Log.v( TAG, "download : enter" );
		
		boolean newDataDownloaded = false;
		
		DateTime end = new DateTime( start );
		end = end.withTime( start.getHourOfDay(), 59, 59, 999 );
		Log.i( TAG, "download : starting download for " + DateUtils.dateTimeFormatter.print( start ) + ", end time=" + DateUtils.dateTimeFormatter.print( end ) );

		String endpoint = Endpoint.GET_PROGRAM_GUIDE.name() + "_" + DateUtils.dateFormatter.print( start );
		
		Long id = null;
		EtagInfoDelegate etag = EtagInfoDelegate.createEmptyETag();
		Cursor etagCursor = getContentResolver().query( Uri.withAppendedPath( EtagConstants.CONTENT_URI, "endpoint" ), null, EtagConstants.FIELD_ENDPOINT + " = ?" ,new String[] { endpoint }, null );
		if( etagCursor.moveToFirst() ) {
			id = etagCursor.getLong( etagCursor.getColumnIndexOrThrow( EtagConstants._ID ) );
			String value = etagCursor.getString( etagCursor.getColumnIndexOrThrow( EtagConstants.FIELD_VALUE ) );
			
			etag.setETag( value );
			Log.v( TAG, "download : etag=" + etag.getETag() );
		}
		etagCursor.close();

		ResponseEntity<ProgramGuideWrapper> responseEntity = mMythtvServiceHelper.getMythServicesApi( locationProfile ).guideOperations().getProgramGuide( start, end, 1, -1, false, etag );

		try {

			if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
				ProgramGuideWrapper programGuide = responseEntity.getBody();
				
				if( null != programGuide ) {

					if( null != programGuide.getProgramGuide() ) {
						newDataDownloaded = process( programGuide.getProgramGuide(), locationProfile );
					}
					
				}

				if( null != etag.getETag() ) {
					ContentValues values = new ContentValues();
					values.put( EtagConstants.FIELD_ENDPOINT, endpoint );
					values.put( EtagConstants.FIELD_VALUE, etag.getETag() );
					values.put( EtagConstants.FIELD_DATE, ( new DateTime() ).getMillis() );

					if( null == id ) {
						Log.v( TAG, "download : adding new etag" );

						getContentResolver().insert( EtagConstants.CONTENT_URI, values );
					} else {
						Log.v( TAG, "download : updating existing etag" );

						getContentResolver().update( ContentUris.withAppendedId( EtagConstants.CONTENT_URI, id ), values, null, null );
					}
				}

				Log.v( TAG, "download : exit" );
				return newDataDownloaded;
			}
			
			if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
				
				if( null != etag.getETag() ) {
					ContentValues values = new ContentValues();
					values.put( EtagConstants.FIELD_ENDPOINT, endpoint );
					values.put( EtagConstants.FIELD_VALUE, etag.getETag() );
					values.put( EtagConstants.FIELD_DATE, ( new DateTime() ).getMillis() );

					Log.v( TAG, "download : updating existing etag" );

					getContentResolver().update( ContentUris.withAppendedId( EtagConstants.CONTENT_URI, id ), values, null, null );
				}

			}
			
		} catch( Exception e ) {
			Log.e( TAG, "download : error downloading program guide" );
		}
			
		Log.v( TAG, "download : exit" );
		return newDataDownloaded;
	}

	private boolean process( ProgramGuide programGuide, final LocationProfile locationProfile ) throws RemoteException, OperationApplicationException {
		Log.v( TAG, "process : enter" );

		for( ChannelInfo channel : programGuide.getChannels() ) {

			if( null != channel.getPrograms() && !channel.getPrograms().isEmpty() ) {
				
				for( Program program : channel.getPrograms() ) {
					program.setChannelInfo( channel );
				}
				
				mProgramGuideDaoHelper.load( this, locationProfile, channel.getPrograms() );

			}
			
		}
	
		Log.v( TAG, "process : exit" );
		return true;
	}

	@SuppressWarnings( "deprecation" )
	private void sendNotification() {

		long when = System.currentTimeMillis();
		
        mNotification = new Notification( android.R.drawable.stat_notify_sync, getResources().getString( R.string.notification_sync_program_guide ), when );

        Intent notificationIntent = new Intent();
        mContentIntent = PendingIntent.getActivity( this, 0, notificationIntent, 0 );

        mNotification.setLatestEventInfo( this, getResources().getString( R.string.app_name ), getResources().getString( R.string.notification_sync_program_guide ), mContentIntent );

        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        mNotificationManager.notify( notificationId, mNotification );
	
	}
	
    @SuppressWarnings( "deprecation" )
	private void progressUpdate( double percentageComplete ) {

    	CharSequence contentText = formatter.format( percentageComplete ) + "% complete";

    	mNotification.setLatestEventInfo( this, getResources().getString( R.string.notification_sync_program_guide ), contentText, mContentIntent );
    	mNotificationManager.notify( notificationId, mNotification );
    }

    private void completed()    {

    	if( null != mNotificationManager ) {
    		mNotificationManager.cancel( notificationId );
    	}
    	
    }
	
}
