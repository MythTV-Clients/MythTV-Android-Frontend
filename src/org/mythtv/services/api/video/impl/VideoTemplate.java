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
package org.mythtv.services.api.video.impl;

import java.util.List;

import org.mythtv.services.api.video.BlurayInfo;
import org.mythtv.services.api.video.VideoLookup;
import org.mythtv.services.api.video.VideoLookupList;
import org.mythtv.services.api.video.VideoMetadataInfo;
import org.mythtv.services.api.video.VideoMetadataInfoList;
import org.mythtv.services.api.video.VideoOperations;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 *
 */
public class VideoTemplate extends AbstractVideoOperations implements VideoOperations {

	private final RestTemplate restTemplate;

	public VideoTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		this.restTemplate = restTemplate;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.video.VideoOperations#addVideo(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean addVideo( String filename, String hostname ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "FileName", filename );
		parameters.add( "HostName", hostname );

		ResponseEntity<Bool> responseEntity = restTemplate.exchange( buildUri( "AddVideo", parameters ), HttpMethod.GET, getRequestEntity(), Bool.class );
		Bool bool = responseEntity.getBody();

		return bool.getBool();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.video.VideoOperations#getBluray(java.lang.String)
	 */
	@Override
	public BlurayInfo getBluray( String path ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "Path", path );

		ResponseEntity<BlurayInfo> responseEntity = restTemplate.exchange( buildUri( "GetBluray", parameters ), HttpMethod.GET, getRequestEntity(), BlurayInfo.class );
		BlurayInfo blurayInfo = responseEntity.getBody();

		return blurayInfo;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.video.VideoOperations#getVideo(int)
	 */
	@Override
	public VideoMetadataInfo getVideo( int id ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "Id", "" + id );

		ResponseEntity<VideoMetadataInfo> responseEntity = restTemplate.exchange( buildUri( "GetVideoList", parameters ), HttpMethod.GET, getRequestEntity(), VideoMetadataInfo.class );
		VideoMetadataInfo videoMetadataInfo = responseEntity.getBody();

		return videoMetadataInfo;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.video.VideoOperations#getVideByFilename(java.lang.String)
	 */
	@Override
	public VideoMetadataInfo getVideByFilename( String filename ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "FileName", filename );

		ResponseEntity<VideoMetadataInfo> responseEntity = restTemplate.exchange( buildUri( "GetVideoByFileName", parameters ), HttpMethod.GET, getRequestEntity(), VideoMetadataInfo.class );
		VideoMetadataInfo videoMetadataInfo = responseEntity.getBody();

		return videoMetadataInfo;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.video.VideoOperations#getVideoList(boolean, int, int)
	 */
	@Override
	public List<VideoMetadataInfo> getVideoList( boolean descending, int startIndex, int count ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "Descending", Boolean.toString( descending ) );
		
		if( startIndex > 0 ) {
			parameters.add( "StartIndex", "" + startIndex );
		}

		if( count > 0 ) {
			parameters.add( "Count", "" + count );
		}

		ResponseEntity<VideoMetadataInfoList> responseEntity = restTemplate.exchange( buildUri( "GetVideoList", parameters ), HttpMethod.GET, getRequestEntity(), VideoMetadataInfoList.class );
		VideoMetadataInfoList videoMetadataInfoList = responseEntity.getBody();

		return videoMetadataInfoList.getVideoMetadataInfos().getVideoMetadataInfos();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.video.VideoOperations#lookupVideo(java.lang.String, java.lang.String, java.lang.String, int, int, java.lang.String, boolean)
	 */
	@Override
	public List<VideoLookup> lookupVideo( String title, String subtitle, String inetRef, int season, int episode, String grabberType, boolean allowGeneric ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "Title", title );
		
		if( null != subtitle && !"".equals( subtitle ) ) {
			parameters.add( "Subtitle", subtitle );
		}

		if( null != inetRef && !"".equals( inetRef ) ) {
			parameters.add( "Inetref", inetRef );
		}

		if( season > 0 ) {
			parameters.add( "Season", "" + season );
		}

		if( episode > 0 ) {
			parameters.add( "Episode", "" + episode );
		}

		ResponseEntity<VideoLookupList> responseEntity = restTemplate.exchange( buildUri( "LookupVideo", parameters ), HttpMethod.GET, getRequestEntity(), VideoLookupList.class );
		VideoLookupList videoLookupList = responseEntity.getBody();

		return videoLookupList.getVideoLookups().getVideoLookups();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.video.VideoOperations#removeVideoFromDatabase(int)
	 */
	@Override
	public boolean removeVideoFromDatabase( int id ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "Id", "" + id );
		
		ResponseEntity<Bool> responseEntity = restTemplate.exchange( buildUri( "RemoveVideoFromDB", parameters ), HttpMethod.GET, getRequestEntity(), Bool.class );
		Bool bool = responseEntity.getBody();

		return bool.getBool();
	}
	
}
