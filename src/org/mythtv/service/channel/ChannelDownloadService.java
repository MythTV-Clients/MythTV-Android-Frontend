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
import java.util.ArrayList;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.channel.ChannelDaoHelper;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.NetworkHelper;
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
public class ChannelDownloadService extends MythtvService {

	private static final String TAG = ChannelDownloadService.class.getSimpleName();

	private static final String CHANNELS_FILE_PREFIX = "channels_";
	private static final String CHANNELS_FILE_EXT = ".json";
	
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
	private ChannelDaoHelper mChannelDaoHelper;
	private LocationProfileDaoHelper mLocationProfileDaoHelper;
	private EtagDaoHelper mEtagDaoHelper;
	
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
		
		mChannelDaoHelper = new ChannelDaoHelper( this );
		mLocationProfileDaoHelper = new LocationProfileDaoHelper( this );
		mEtagDaoHelper = new EtagDaoHelper( this );
		
		channelDirectory = mFileHelper.getChannelDataDirectory();
		if( null == channelDirectory || !channelDirectory.exists() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Channel location can not be found" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, channelDirectory does not exist" );
			return;
		}

		if( !NetworkHelper.getInstance().isMasterBackendConnected() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend unreachable" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, Master Backend unreachable" );
			return;
		}
		
		mNotificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		boolean passed = true;
    		
    		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile();
    		Log.v( TAG, "onHandleIntent : get video sources for host [" + locationProfile.getHostname() + ":" + locationProfile.getUrl() + "]" );

    		try {
    			ETagInfo etag = mEtagDaoHelper.findByEndpointAndDataId( Endpoint.GET_VIDEO_SOURCE_LIST.name(), null );
    			
				ResponseEntity<VideoSourceList> responseEntity = mMainApplication.getMythServicesApi( locationProfile ).channelOperations().getVideoSourceList( etag );
				if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
					Log.i( TAG, "onHandleIntent : response returned HTTP 200" );
					
					//sendNotification();
					
					VideoSourceList videoSourceList = responseEntity.getBody();
					
					if( null != videoSourceList ) {

						// holder for all downloaded channel lists
						List<ChannelInfos> allChannelLists = new ArrayList<ChannelInfos>();
						
						int nap = 1000; // 500ms & 1ms fail
						for( VideoSource videoSource : videoSourceList.getVideoSources().getVideoSources() ) {
							Log.i( TAG, "onHandleIntent : videoSourceId = '" + videoSource.getId() + "'" );
							
							// Download the channel listing, return list
							Log.i( TAG, "onHandleIntent : downloading channels" );
							ChannelInfos channelInfos = download( videoSource.getId() );
							if( null != channelInfos ) {
								allChannelLists.add( channelInfos );

								// Save the file locally
								Log.i( TAG, "onHandleIntent : save the file locally" );
								process( videoSource.getId(), channelInfos  );
							}
							
							// wait a second before downloading the next one (if there are more than one video source)
							if(  videoSourceList.getVideoSources().getVideoSources().size() > 1 ) {
								Log.i( TAG, "onHandleIntent : sleeping " + nap + " ms" );
								Thread.sleep( nap );
							}
						}

						// Process the combined lists of downloaded channels
						if( null != allChannelLists && !allChannelLists.isEmpty() ) {
							Log.i( TAG, "onHandleIntent : process all channels" );

							int channelsProcessed = mChannelDaoHelper.load( allChannelLists );
							Log.v( TAG, "process : channelsProcessed=" + channelsProcessed );
							
						}
						
					}

				}					
			
			} catch( Exception e ) {
				Log.e( TAG, "onHandleIntent : error handling files", e );
				
				passed = false;
			} finally {
    			completed();

    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Channels Download Service Finished" );
   				completeIntent.putExtra( EXTRA_COMPLETE_UPTODATE, passed );
    			
    			sendBroadcast( completeIntent );
    		}
    		
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private ChannelInfos download( int sourceId ) throws Exception {
		Log.v( TAG, "download : enter" );

		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile();
		Log.v( TAG, "download : get recorded for host [" + locationProfile.getHostname() + ":" + locationProfile.getUrl() + ", video source=" + sourceId + "]" );

		ETagInfo etag = ETagInfo.createEmptyETag(); //mEtagDaoHelper.findByEndpointAndDataId( Endpoint.GET_CHANNEL_INFO_LIST.name(), String.valueOf( sourceId ) );
//		if( etag.isEmptyEtag() ) {
//			Log.v( TAG, "download : creating empty etag" );
//			etag = ETagInfo.createEmptyETag();
//		}
		
		ResponseEntity<ChannelInfoList> responseEntity = mMainApplication.getMythServicesApi( locationProfile ).channelOperations().getChannelInfoList( sourceId, 0, -1, etag );

		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			Log.i( TAG, "download : " + Endpoint.GET_CHANNEL_INFO_LIST.getEndpoint() + " returned 200 OK" );

			ChannelInfoList channelInfoList = responseEntity.getBody();
			if( null != channelInfoList ) {

				if( null != etag.getETag() ) {
					Log.i( TAG, "download : " + Endpoint.GET_CHANNEL_INFO_LIST.getEndpoint() + " returned 200 OK" );

					mEtagDaoHelper.save( etag, Endpoint.GET_CHANNEL_INFO_LIST.name(), String.valueOf( sourceId ) );
				}

				if( null != channelInfoList.getChannelInfos() ) {
					Log.v( TAG, "download : exit, returning channelInfos" );

					return channelInfoList.getChannelInfos();
				}

			}

		}

//		if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
//			Log.i( TAG, "download : " + Endpoint.GET_CHANNEL_INFO_LIST.getEndpoint() + " returned 304 Not Modified" );
//
//			mEtagDaoHelper.save( etag, Endpoint.GET_CHANNEL_INFO_LIST.name(), String.valueOf( sourceId ) );
//		}
			
		Log.v( TAG, "download : exit" );
		return null;
	}

	private void process( int videoSourceId, ChannelInfos channelInfos ) throws JsonGenerationException, JsonMappingException, IOException, RemoteException, OperationApplicationException {
		Log.v( TAG, "process : enter" );
		
		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile();
		Log.v( TAG, "process : saving recorded for host [" + locationProfile.getHostname() + ":" + locationProfile.getUrl() + "]" );

		mMainApplication.getObjectMapper().writeValue( new File( channelDirectory, CHANNELS_FILE_PREFIX + videoSourceId + "_" + locationProfile.getHostname() + CHANNELS_FILE_EXT ), channelInfos );
		Log.v( TAG, "process : saved channels to " + channelDirectory.getAbsolutePath() );

		Log.v( TAG, "process : exit" );
	}

	// internal helpers
	
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
