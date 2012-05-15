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
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */
package org.mythtv.services.api.content.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.mythtv.services.api.content.ArtworkInfo;
import org.mythtv.services.api.content.ContentOperations;
import org.mythtv.services.api.content.LiveStreamInfo;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 *
 */
public class ContentTemplate extends AbstractContentOperations implements ContentOperations {

	private static final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
	
	private final RestTemplate restTemplate;
	
	public ContentTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		this.restTemplate = restTemplate;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#addLiveStream(java.lang.String, java.lang.String, java.lang.String, int, int, int, int, int, int)
	 */
	@Override
	public LiveStreamInfo addLiveStream( String storageGroup, String filename, String hostname, int maxSegments, int width, int height, int bitrate, int audioBitrate, int sampleRate ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#addRecordingLiveStream(int, java.util.Date, int, int, int, int, int, int)
	 */
	@Override
	public LiveStreamInfo addRecordingLiveStream( int channelId, Date startTime, int maxSegments, int width, int height, int bitrate, int audioBitrate, int sampleRate ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "ChanId", "" + channelId );
		parameters.add( "StartTime", sdf.format( startTime ) );
		
		if( maxSegments > 0 ) {
			parameters.add( "MaxSegments", "" + maxSegments );
		}
		
		if( width > 0 ) {
			parameters.add( "Width", "" + width );
		}

		if( height > 0 ) {
			parameters.add( "Height", "" + height );
		}

		if( bitrate > 0 ) {
			parameters.add( "Bitrate", "" + bitrate );
		}

		if( audioBitrate > 0 ) {
			parameters.add( "AudioBitrate", "" + audioBitrate );
		}

		if( sampleRate > 0 ) {
			parameters.add( "SampleRate", "" + sampleRate );
		}

		ResponseEntity<LiveStreamInfo> responseEntity = restTemplate.exchange( buildUri( "AddRecordingLiveStream", parameters ), HttpMethod.GET, getRequestEntity(), LiveStreamInfo.class );
		LiveStreamInfo liveStreamInfo = responseEntity.getBody();

		return liveStreamInfo;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#addVideoLiveStream(int, int, int, int, int, int, int)
	 */
	@Override
	public LiveStreamInfo addVideoLiveStream( int id, int maxSegments, int width, int height, int bitrate, int audioBitrate, int sampleRate ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#downloadFile(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean downloadFile( String url, String storageGroup ) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getAlbumArt(int, int, int)
	 */
	@Override
	public String getAlbumArt( int id, int width, int height ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getFile(java.lang.String, java.lang.String)
	 */
	@Override
	public String getFile( String storageGroup, String filename ) {
		
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "StorageGroup", storageGroup );
		
		if( null != filename && !"".equals( filename ) ) {
			parameters.add( "FileName", filename );
		}

		ResponseEntity<String> responseEntity = restTemplate.exchange( buildUri( "GetFile", parameters ), HttpMethod.GET, getRequestEntity(), String.class );
		String url = responseEntity.getBody();

		return url;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getFileList(java.lang.String)
	 */
	@Override
	public List<String> getFileList( String storageGroup ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getFilteredLiveStreamList(java.lang.String)
	 */
	@Override
	public List<LiveStreamInfo> getFilteredLiveStreamList( String filename ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getHash(java.lang.String, java.lang.String)
	 */
	@Override
	public String getHash( String storageGroup, String filename ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getImageFile(java.lang.String, java.lang.String, int, int)
	 */
	@Override
	public String getImageFile( String storageGroup, String filename, int width, int height ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getLiveStream(int)
	 */
	@Override
	public LiveStreamInfo getLiveStream( int id ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getLiveStreamList()
	 */
	@Override
	public List<LiveStreamInfo> getLiveStreamList() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getMusic(int)
	 */
	@Override
	public String getMusic( int id ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getPreviewImage(int, java.util.Date, int, int, int)
	 */
	@Override
	public String getPreviewImage( int channelId, Date startTime, int width, int height, int secondsIn ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getProgramArtworkList(java.lang.String, int)
	 */
	@Override
	public List<ArtworkInfo> getProgramArtworkList( String inetRef, int season ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getRecording(int, java.util.Date)
	 */
	@Override
	public String getRecording( int channelId, Date startTime ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getRecordingArtwork(java.lang.String, java.lang.String, int, int, int)
	 */
	@Override
	public String getRecordingArtwork( String type, String inetRef, int season, int width, int height ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getRecordingArtworkList(int, java.util.Date)
	 */
	@Override
	public List<ArtworkInfo> getRecordingArtworkList( int channelId, Date startTime ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getVideo(int)
	 */
	@Override
	public String getVideo( int id ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getVideoArtwork(java.lang.String, int, int, int)
	 */
	@Override
	public String getVideoArtwork( String type, int id, int width, int height ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#removeLiveStream(int)
	 */
	@Override
	public boolean removeLiveStream( int id ) {
		
		ResponseEntity<Boolean> responseEntity = restTemplate.exchange( buildUri( "RemoveLiveStream", "Id", "" + id ), HttpMethod.GET, getRequestEntity(), Boolean.class );
		Boolean removed = responseEntity.getBody();

		return removed;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#stopLiveStream(int)
	 */
	@Override
	public LiveStreamInfo stopLiveStream( int id ) {

		ResponseEntity<LiveStreamInfo> responseEntity = restTemplate.exchange( buildUri( "StopLiveStream", "Id", "" + id ), HttpMethod.GET, getRequestEntity(), LiveStreamInfo.class );
		LiveStreamInfo liveStreamInfo = responseEntity.getBody();

		return liveStreamInfo;
	}
	
}
