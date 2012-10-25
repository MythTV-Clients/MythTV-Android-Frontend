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
package org.mythtv.service.channel;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.db.http.EtagConstants;
import org.mythtv.service.MythtvService;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.channel.ChannelInfoList;
import org.mythtv.services.api.channel.ChannelInfos;
import org.mythtv.services.api.channel.VideoSource;
import org.mythtv.services.api.channel.VideoSourceList;
import org.mythtv.services.api.channel.impl.ChannelTemplate.Endpoint;
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
public class ChannelDownloadService extends MythtvService {

	private static final String TAG = ChannelDownloadService.class.getSimpleName();

	public static final String CHANNELS_FILE = "channels.json";
	public static final String CALLSIGN_EXT = ".jpg";
	
    public static final String ACTION_DOWNLOAD = "org.mythtv.background.channelDownload.ACTION_DOWNLOAD";
    public static final String ACTION_PROGRESS = "org.mythtv.background.channelDownload.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.channelDownload.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATA = "PROGRESS_DATA";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_UPTODATE = "COMPLETE_UPTODATE";

	private NotificationManager mNotificationManager;
	private int notificationId;
	
	private File channelDirectory = null;
	private ChannelProcessor mChannelProcessor;
	
	public ChannelDownloadService() {
		super( "ChannelDownloadService" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );
		super.onHandleIntent( intent );
		
		mChannelProcessor = new ChannelProcessor( this );

		channelDirectory = mFileHelper.getChannelDataDirectory();
		if( null == channelDirectory || !channelDirectory.exists() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Channel location can not be found" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, channelDirectory does not exist" );
			return;
		}

		if( !isBackendConnected() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend unreachable" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, Master Backend unreachable" );
			return;
		}
		
//		Cursor channelCursor = getContentResolver().query( ChannelConstants.CONTENT_URI, null, null, null, null );
//		if( channelCursor.getCount() > 0 ) {
//			Intent completeIntent = new Intent( ACTION_COMPLETE );
//			completeIntent.putExtra( EXTRA_COMPLETE, "channels already loaded" );
//			sendBroadcast( completeIntent );
//
//			Log.d( TAG, "onHandleIntent : exit, Master Backend unreachable" );
//			return;
//		}
//		channelCursor.close();
		
		mNotificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		ChannelInfos channelInfos = null;
    		try {
    			sendNotification();

    			Long id = null;
    			ETagInfo etag = ETagInfo.createEmptyETag();
    			Cursor cursor = getContentResolver().query( Uri.withAppendedPath( EtagConstants.CONTENT_URI, "endpoint" ), null, EtagConstants.FIELD_ENDPOINT + " = ?" ,new String[] { Endpoint.GET_VIDEO_SOURCE_LIST.name() }, null );
    			if( cursor.moveToFirst() ) {
    				id = cursor.getLong( cursor.getColumnIndexOrThrow( EtagConstants._ID ) );
    				String value = cursor.getString( cursor.getColumnIndexOrThrow( EtagConstants.FIELD_VALUE ) );
    				
    				etag.setETag( value );
    				Log.v( TAG, "download : etag=" + etag.getETag() );
    			}
    			cursor.close();
				ResponseEntity<VideoSourceList> responseEntity = mMainApplication.getMythServicesApi().channelOperations().getVideoSourceList( etag );
				if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
					
					if( null != etag.getETag() ) {
						ContentValues values = new ContentValues();
						values.put( EtagConstants.FIELD_ENDPOINT, Endpoint.GET_VIDEO_SOURCE_LIST.name() );
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

					VideoSourceList videoSourceList = responseEntity.getBody();
					
					if( null != videoSourceList ) {
	    				cleanup();
						
						for( VideoSource videoSource : videoSourceList.getVideoSources().getVideoSources() ) {

							channelInfos = download( videoSource.getId() );
			    			if( null != channelInfos ) {

			    				process( channelInfos );
			    				
			    			}
						}

					}

					downloadChannelIcons();
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
    			completeIntent.putExtra( EXTRA_COMPLETE, "Channels Download Service Finished" );
    			if( null == channelInfos ) {
    				completeIntent.putExtra( EXTRA_COMPLETE_UPTODATE, Boolean.TRUE );
    			}
    			
    			sendBroadcast( completeIntent );
    		}
    		
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private ChannelInfos download( int sourceId ) {
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
		Cursor cursor = getContentResolver().query( Uri.withAppendedPath( EtagConstants.CONTENT_URI, "endpoint" ), null, EtagConstants.FIELD_ENDPOINT + " = ? and " + EtagConstants.FIELD_DATA_ID + " = ?" ,new String[] { Endpoint.GET_CHANNEL_INFO_LIST.name(), String.valueOf( sourceId ) }, null );
		if( cursor.moveToFirst() ) {
			id = cursor.getLong( cursor.getColumnIndexOrThrow( EtagConstants._ID ) );
			String value = cursor.getString( cursor.getColumnIndexOrThrow( EtagConstants.FIELD_VALUE ) );
			
			etag.setETag( value );
			Log.v( TAG, "download : etag=" + etag.getETag() );
		}
		cursor.close();
		
		ResponseEntity<ChannelInfoList> responseEntity = mMainApplication.getMythServicesApi().channelOperations().getChannelInfoList( sourceId, 1, -1, etag );

		try {
		
			if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
				ChannelInfoList channelInfoList = responseEntity.getBody();
				
				if( null != etag.getETag() ) {
					ContentValues values = new ContentValues();
					values.put( EtagConstants.FIELD_ENDPOINT, Endpoint.GET_CHANNEL_INFO_LIST.name() );
					values.put( EtagConstants.FIELD_VALUE, etag.getETag() );
					values.put( EtagConstants.FIELD_DATA_ID, sourceId );
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
				return channelInfoList.getChannelInfos();
			}

			if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
				Log.i( TAG, "download : " + Endpoint.GET_CHANNEL_INFO_LIST.getEndpoint() + " returned 304 Not Modified" );

				if( null != etag.getETag() ) {
					ContentValues values = new ContentValues();
					values.put( EtagConstants.FIELD_ENDPOINT, Endpoint.GET_CHANNEL_INFO_LIST.name() );
					values.put( EtagConstants.FIELD_VALUE, etag.getETag() );
					values.put( EtagConstants.FIELD_DATA_ID, sourceId );
					values.put( EtagConstants.FIELD_DATE, ( new DateTime() ).getMillis() );
					getContentResolver().update( ContentUris.withAppendedId( EtagConstants.CONTENT_URI, id ), values, null, null );
				}
				
			}
			
		} catch( Exception e ) {
			Log.e( TAG, "download : error downloading upcoming program list" );
		}
		
		Log.v( TAG, "download : exit" );
		return null;
	}

	private void cleanup() throws IOException {
		Log.v( TAG, "cleanup : enter" );
		
		FileUtils.cleanDirectory( channelDirectory );
		
		Log.v( TAG, "cleanup : exit" );
	}

	private void process( ChannelInfos channelInfos ) throws JsonGenerationException, JsonMappingException, IOException {
		Log.v( TAG, "process : enter" );
		
		mMainApplication.getObjectMapper().writeValue( new File( channelDirectory, CHANNELS_FILE ), channelInfos );

		int channelsAdded = mChannelProcessor.processChannels( channelInfos );
		Log.v( TAG, "process : channelsAdded=" + channelsAdded );
		
		Log.v( TAG, "process : exit" );
	}

	// internal helpers
	
	private void downloadChannelIcons() {
		Log.v( TAG, "downloadChannelIcons : enter" );
		
		Cursor cursor = getContentResolver().query( ChannelConstants.CONTENT_URI, new String[] { ChannelConstants._ID, ChannelConstants.FIELD_CHAN_ID, ChannelConstants.FIELD_CALLSIGN }, null, null, null );
		while( cursor.moveToNext() ) {
			Long id = cursor.getLong( cursor.getColumnIndexOrThrow( ChannelConstants._ID ) );
	        String chanId = cursor.getString( cursor.getColumnIndexOrThrow( ChannelConstants.FIELD_CHAN_ID ) );
	        String callsign = cursor.getString( cursor.getColumnIndexOrThrow( ChannelConstants.FIELD_CALLSIGN ) );
	        
			File icon = new File( channelDirectory, callsign + CALLSIGN_EXT );
			if( !icon.exists() ) {
				//Intent downloadBannerIntent = new Intent( BannerDownloadService.ACTION_DOWNLOAD );
				//downloadBannerIntent.putExtra( BannerDownloadService.BANNER_RECORDED_ID, id );
				//startService( downloadBannerIntent );
			}
				
		}
		cursor.close();
		
		Log.v( TAG, "downloadChannelIcons : exit" );
	}
	
	@SuppressWarnings( "deprecation" )
	private void sendNotification() {

		long when = System.currentTimeMillis();
		notificationId = (int) when;
		
        Notification mNotification = new Notification( android.R.drawable.stat_notify_sync, getResources().getString( R.string.notification_sync_channels ), when );

        Intent notificationIntent = new Intent();
        PendingIntent mContentIntent = PendingIntent.getActivity( this, 0, notificationIntent, 0 );

        mNotification.setLatestEventInfo( this, getResources().getString( R.string.app_name ), getResources().getString( R.string.notification_sync_channels ), mContentIntent );

        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        mNotificationManager.notify( notificationId, mNotification );
	
	}
	
    public void completed()    {
        
    	if( null != mNotificationManager ) {
        	mNotificationManager.cancel( notificationId );
    	}
    
    }

}
