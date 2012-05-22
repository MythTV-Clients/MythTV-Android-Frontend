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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.mythtv.services.api.content.ArtworkInfo;
import org.mythtv.services.api.content.ArtworkInfos;
import org.mythtv.services.api.content.ContentOperations;
import org.mythtv.services.api.content.LiveStreamInfo;
import org.mythtv.services.api.content.LiveStreamInfoWrapper;
import org.mythtv.services.api.content.LiveStreamInfos;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 * @author John Baab
 *
 */
public class ContentTemplate extends AbstractContentOperations implements ContentOperations {

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
		
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "FileName", "" + filename );
		
		if( storageGroup != null ) {
			parameters.add( "StorageGroup", "" + storageGroup );
		}
		
		if( hostname != null ) {
			parameters.add( "HostName", "" + hostname );
		}
		
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

		ResponseEntity<LiveStreamInfoWrapper> responseEntity = restTemplate.exchange( buildUri( "AddLiveStream", parameters ), HttpMethod.GET, getRequestEntity(), LiveStreamInfoWrapper.class );
		LiveStreamInfoWrapper wrapper = responseEntity.getBody();

		return wrapper.getLiveStreamInfo();
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

		ResponseEntity<LiveStreamInfoWrapper> responseEntity = restTemplate.exchange( buildUri( "AddRecordingLiveStream", parameters ), HttpMethod.GET, getRequestEntity(), LiveStreamInfoWrapper.class );
		LiveStreamInfoWrapper wrapper = responseEntity.getBody();

		return wrapper.getLiveStreamInfo();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#addVideoLiveStream(int, int, int, int, int, int, int)
	 */
	@Override
	public LiveStreamInfo addVideoLiveStream( int id, int maxSegments, int width, int height, int bitrate, int audioBitrate, int sampleRate ) {
		
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "Id", "" + id );
		
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

		ResponseEntity<LiveStreamInfoWrapper> responseEntity = restTemplate.exchange( buildUri( "AddVideoLiveStream", parameters ), HttpMethod.GET, getRequestEntity(), LiveStreamInfoWrapper.class );
		LiveStreamInfoWrapper wrapper = responseEntity.getBody();

		return wrapper.getLiveStreamInfo();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#downloadFile(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean downloadFile( String url, String storageGroup ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "StorageGroup", storageGroup );
		parameters.add( "URL", url );

		ResponseEntity<Boolean> responseEntity = restTemplate.exchange( buildUri( "DownloadFile", parameters ), HttpMethod.GET, getRequestEntity(), Boolean.class );
		boolean downloaded = responseEntity.getBody();

		return downloaded;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getAlbumArt(int, int, int)
	 */
	@Override
	public String getAlbumArt( int id, int width, int height ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "Id", "" + id );
		
		if( width > 0 ) {
			parameters.add( "Width", "" + width );
		}

		if( height > 0 ) {
			parameters.add( "Height", "" + height );
		}

		ResponseEntity<String> responseEntity = restTemplate.exchange( buildUri( "GetAlbumArt", parameters ), HttpMethod.GET, getRequestEntity(), String.class );
		String albumArt = responseEntity.getBody();

		return albumArt;
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

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "StorageGroup", storageGroup );
		
		ResponseEntity<String[]> responseEntity = restTemplate.exchange( buildUri( "GetFileList", parameters ), HttpMethod.GET, getRequestEntity(), String[].class );
		List<String> urls = Arrays.asList( responseEntity.getBody() );

		return urls;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getFilteredLiveStreamList(java.lang.String)
	 */
	@Override
	public List<LiveStreamInfo> getFilteredLiveStreamList( String filename ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "FileName", filename );
		
		ResponseEntity<LiveStreamInfos> responseEntity = restTemplate.exchange( buildUri( "GetFilteredLiveStreamList", parameters ), HttpMethod.GET, getRequestEntity(), LiveStreamInfos.class );
		LiveStreamInfos liveStreamInfos = responseEntity.getBody();

		return liveStreamInfos.getLiveStreamInfos();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getHash(java.lang.String, java.lang.String)
	 */
	@Override
 	public String getHash( String storageGroup, String filename ) {
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "StorageGroup", storageGroup );
		parameters.add( "FileName", filename );
		
		ResponseEntity<String> responseEntity = restTemplate.exchange( buildUri( "GetHash", parameters ), HttpMethod.GET, getRequestEntity(), String.class );
		String hash = responseEntity.getBody();

		return hash;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getImageFile(java.lang.String, java.lang.String, int, int)
	 */
	@Override
	public String getImageFile( String storageGroup, String filename, int width, int height ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "StorageGroup", storageGroup );
		parameters.add( "FileName", filename );
		
		if( width > 0 ) {
			parameters.add( "Width", "" + width );
		}

		if( height > 0 ) {
			parameters.add( "Height", "" + height );
		}

		ResponseEntity<String> responseEntity = restTemplate.exchange( buildUri( "GetImageFile", parameters ), HttpMethod.GET, getRequestEntity(), String.class );
		String file = responseEntity.getBody();

		return file;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getLiveStream(int)
	 */
	@Override
	public LiveStreamInfo getLiveStream( int id ) {
	
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "Id", "" + id );

		ResponseEntity<LiveStreamInfoWrapper> responseEntity = restTemplate.exchange( buildUri( "GetLiveStream", parameters ), HttpMethod.GET, getRequestEntity(), LiveStreamInfoWrapper.class );
		LiveStreamInfoWrapper wrapper = responseEntity.getBody();

		return wrapper.getLiveStreamInfo();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getLiveStreamList()
	 */
	@Override
	public List<LiveStreamInfo> getLiveStreamList() {

		ResponseEntity<LiveStreamInfos> responseEntity = restTemplate.exchange( buildUri( "GetLiveStreamList" ), HttpMethod.GET, getRequestEntity(), LiveStreamInfos.class );
		LiveStreamInfos liveStreamInfos = responseEntity.getBody();

		return liveStreamInfos.getLiveStreamInfos();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getMusic(int)
	 */
	@Override
	public String getMusic( int id ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "Id", "" + id );

		ResponseEntity<String> responseEntity = restTemplate.exchange( buildUri( "GetMusic", parameters ), HttpMethod.GET, getRequestEntity(), String.class );
		String music = responseEntity.getBody();

		return music;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getPreviewImage(int, java.util.Date, int, int, int)
	 */
	@Override
	public String getPreviewImage( int channelId, Date startTime, int width, int height, int secondsIn ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "ChanId", "" + channelId );
		parameters.add( "StartTime", sdf.format( startTime ) );
		
		if( width > 0 ) {
			parameters.add( "Width", "" + width );
		}

		if( height > 0 ) {
			parameters.add( "Height", "" + height );
		}

		if( secondsIn > 0 ) {
			parameters.add( "SecsIn", "" + secondsIn );
		}

		ResponseEntity<String> responseEntity = restTemplate.exchange( buildUri( "GetPreviewImage", parameters ), HttpMethod.GET, getRequestEntity(), String.class );
		String previewImage = responseEntity.getBody();

		return previewImage;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getProgramArtworkList(java.lang.String, int)
	 */
	@Override
	public List<ArtworkInfo> getProgramArtworkList( String inetRef, int season ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "Inetref", inetRef );
		
		if( season > 0 ) {
			parameters.add( "Season", "" + season );
		}

		ResponseEntity<ArtworkInfos> responseEntity = restTemplate.exchange( buildUri( "GetProgramArtwork", parameters ), HttpMethod.GET, getRequestEntity(), ArtworkInfos.class );
		ArtworkInfos artworkInfos = responseEntity.getBody();

		return artworkInfos.getArtworkInfos();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getRecording(int, java.util.Date)
	 */
	@Override
	public String getRecording( int channelId, Date startTime ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "ChanId", "" + channelId );
		parameters.add( "StartTime", sdf.format( startTime ) );

		ResponseEntity<String> responseEntity = restTemplate.exchange( buildUri( "GetRecording", parameters ), HttpMethod.GET, getRequestEntity(), String.class );
		String recording = responseEntity.getBody();

		return recording;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getRecordingArtwork(java.lang.String, java.lang.String, int, int, int)
	 */
	@Override
	public String getRecordingArtwork( String type, String inetRef, int season, int width, int height ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "Inetref", inetRef );
		
		if( null != type && !"".equals( type ) ) {
			parameters.add( "Type", type );
		}

		if( season > 0 ) {
			parameters.add( "Season", "" + season );
		}

		if( width > 0 ) {
			parameters.add( "Width", "" + width );
		}

		if( height > 0 ) {
			parameters.add( "Height", "" + height );
		}

		ResponseEntity<String> responseEntity = restTemplate.exchange( buildUri( "GetRecordingArtwork", parameters ), HttpMethod.GET, getRequestEntity(), String.class );
		String artworkInfo = responseEntity.getBody();

		return artworkInfo;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getRecordingArtworkList(int, java.util.Date)
	 */
	@Override
	public List<ArtworkInfo> getRecordingArtworkList( int channelId, Date startTime ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "ChanId", "" + channelId );
		parameters.add( "StartTime", sdf.format( startTime ) );
		
		ResponseEntity<ArtworkInfos> responseEntity = restTemplate.exchange( buildUri( "GetRecordingArtworkList", parameters ), HttpMethod.GET, getRequestEntity(), ArtworkInfos.class );
		ArtworkInfos artworkInfos = responseEntity.getBody();

		return artworkInfos.getArtworkInfos();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getVideo(int)
	 */
	@Override
	public String getVideo( int id ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "Id", "" + id );
		
		ResponseEntity<String> responseEntity = restTemplate.exchange( buildUri( "GetVideo", parameters ), HttpMethod.GET, getRequestEntity(), String.class );
		String video = responseEntity.getBody();

		return video;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.content.ContentOperations#getVideoArtwork(java.lang.String, int, int, int)
	 */
	@Override
	public String getVideoArtwork( String type, int id, int width, int height ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "Id", "" + id );
		
		if( null != type && !"".equals( type ) ) {
			parameters.add( "Type", type );
		}

		if( width > 0 ) {
			parameters.add( "Width", "" + width );
		}

		if( height > 0 ) {
			parameters.add( "Height", "" + height );
		}

		ResponseEntity<String> responseEntity = restTemplate.exchange( buildUri( "GetVideoArtwork", parameters ), HttpMethod.GET, getRequestEntity(), String.class );
		String artworkInfo = responseEntity.getBody();

		return artworkInfo;
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
