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

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.channel.ChannelDaoHelper;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.channel.ChannelInfoList;
import org.mythtv.services.api.channel.ChannelInfos;
import org.mythtv.services.api.channel.VideoSource;
import org.mythtv.services.api.channel.VideoSourceList;
import org.mythtv.services.api.channel.impl.ChannelTemplate;
import org.mythtv.services.api.channel.impl.ChannelTemplate.Endpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Intent;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class ChannelDownloadService extends MythtvService {

	private static final String TAG = ChannelDownloadService.class.getSimpleName();

    public static final String ACTION_DOWNLOAD = "org.mythtv.background.channelDownload.ACTION_DOWNLOAD";
    public static final String ACTION_PROGRESS = "org.mythtv.background.channelDownload.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.channelDownload.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATA = "PROGRESS_DATA";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_UPTODATE = "COMPLETE_UPTODATE";

	private ChannelDaoHelper mChannelDaoHelper = ChannelDaoHelper.getInstance();
	private EtagDaoHelper mEtagDaoHelper = EtagDaoHelper.getInstance();
	
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
		
		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile( this );
		if( !NetworkHelper.getInstance().isMasterBackendConnected( this, locationProfile ) ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend unreachable" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, Master Backend unreachable" );
			return;
		}
		
		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		boolean passed = true;
    		
    		try {
    			EtagInfoDelegate etag = mEtagDaoHelper.findByEndpointAndDataId( this, locationProfile, Endpoint.GET_VIDEO_SOURCE_LIST.name(), null );
    			
				ResponseEntity<VideoSourceList> responseEntity = mMythtvServiceHelper.getMythServicesApi( locationProfile ).channelOperations().getVideoSourceList( etag );
				if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
					Log.i( TAG, "onHandleIntent : response returned HTTP 200" );
					
					VideoSourceList videoSourceList = responseEntity.getBody();
					
					if( null != videoSourceList ) {

						// holder for all downloaded channel lists
						List<ChannelInfos> allChannelLists = new ArrayList<ChannelInfos>();
						
						int nap = 1000; // 500ms & 1ms fail
						int count = 0;
						for( VideoSource videoSource : videoSourceList.getVideoSources().getVideoSources() ) {
							Log.i( TAG, "onHandleIntent : videoSourceId = '" + videoSource.getId() + "'" );
							
							DateTime date = mEtagDaoHelper.findDateByEndpointAndDataId( this, locationProfile, ChannelTemplate.Endpoint.GET_CHANNEL_INFO_LIST.name(), String.valueOf( videoSource.getId() ) );
							if( null != date ) {
								
								DateTime now = DateUtils.convertUtc( new DateTime() );
								if( now.getMillis() - date.getMillis() > 86400000 ) {

									// Download the channel listing, return list
									Log.i( TAG, "onHandleIntent : downloading channels" );
									ChannelInfos channelInfos = download( videoSource.getId(), locationProfile );
									if( null != channelInfos ) {

										allChannelLists.add( channelInfos );

									}

									// wait a second before downloading the next one (if there are more than one video source)
									if( count < videoSourceList.getVideoSources().getVideoSources().size() - 1 ) {
										Log.i( TAG, "onHandleIntent : sleeping " + nap + " ms" );
										Thread.sleep( nap );
									}

									count++;
								}
								
							} else {
								
								// Download the channel listing, return list
								Log.i( TAG, "onHandleIntent : downloading channels" );
								ChannelInfos channelInfos = download( videoSource.getId(), locationProfile );
								if( null != channelInfos ) {

									allChannelLists.add( channelInfos );

								}

								// wait a second before downloading the next one (if there are more than one video source)
								if(  count < videoSourceList.getVideoSources().getVideoSources().size() - 1 ) {
									Log.i( TAG, "onHandleIntent : sleeping " + nap + " ms" );
									Thread.sleep( nap );
								}

								count++;
							}
							
						}

						// Process the combined lists of downloaded channels
						if( null != allChannelLists && !allChannelLists.isEmpty() ) {
							Log.i( TAG, "onHandleIntent : process all channels" );

							int channelsProcessed = mChannelDaoHelper.load( this, locationProfile, allChannelLists );
							Log.v( TAG, "process : channelsProcessed=" + channelsProcessed );
							
						}
						
					}

				}					
			
			} catch( Exception e ) {
				Log.e( TAG, "onHandleIntent : error handling files", e );
				
				passed = false;
			} finally {

    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Channels Download Service Finished" );
   				completeIntent.putExtra( EXTRA_COMPLETE_UPTODATE, passed );
    			
    			sendBroadcast( completeIntent );
    		}
    		
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private ChannelInfos download( final int sourceId, final LocationProfile locationProfile ) throws Exception {
		Log.v( TAG, "download : enter" );

		EtagInfoDelegate etag = mEtagDaoHelper.findByEndpointAndDataId( this, locationProfile, Endpoint.GET_CHANNEL_INFO_LIST.name(), String.valueOf( sourceId ) );
		
		ResponseEntity<ChannelInfoList> responseEntity = mMythtvServiceHelper.getMythServicesApi( locationProfile ).channelOperations().getChannelInfoList( sourceId, 0, -1, etag );

		DateTime date = DateUtils.convertUtc( new DateTime() );
		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			Log.i( TAG, "download : " + Endpoint.GET_CHANNEL_INFO_LIST.getEndpoint() + " returned 200 OK" );

			ChannelInfoList channelInfoList = responseEntity.getBody();
			if( null != channelInfoList ) {

				etag.setEndpoint( Endpoint.GET_CHANNEL_INFO_LIST.name() );
				etag.setDataId( sourceId );
				etag.setDate( date );
				etag.setMasterHostname( locationProfile.getHostname() );
				etag.setLastModified( date );
				mEtagDaoHelper.save( this, locationProfile, etag );

				if( null != channelInfoList.getChannelInfos() ) {
					Log.v( TAG, "download : exit, returning channelInfos" );

					return channelInfoList.getChannelInfos();
				}

			}

		}

		if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
			Log.i( TAG, "download : " + Endpoint.GET_CHANNEL_INFO_LIST.getEndpoint() + " returned 304 Not Modified" );

			etag.setLastModified( date );
			mEtagDaoHelper.save( this, locationProfile, etag );
		}
			
		Log.v( TAG, "download : exit" );
		return null;
	}

}
