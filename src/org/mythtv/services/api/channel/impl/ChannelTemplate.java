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
 * This software can be found at <https://github.com/MythTV-Android/mythtv-for-android/>
 *
 */
package org.mythtv.services.api.channel.impl;

import java.util.List;

import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.channel.ChannelInfoList;
import org.mythtv.services.api.channel.ChannelOperations;
import org.mythtv.services.api.channel.LineupList;
import org.mythtv.services.api.channel.VideoMultiplex;
import org.mythtv.services.api.channel.VideoSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 *
 */
public class ChannelTemplate extends AbstractChannelOperations implements ChannelOperations {

	private final RestTemplate restTemplate;

	public ChannelTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		this.restTemplate = restTemplate;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#addDBChannel(int, int, int, java.lang.String, java.lang.String, java.lang.String, int, int, int, boolean, boolean, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean addDBChannel( int multiplexId, int sourceId, int channelId, String callSign, String channelName, String channelNumber, int serviceId, int atscMajorChannel, int atscMinorChannel, boolean userEIT, boolean visible, String frequencyId, String icon, String format, String xmltvId, String defaultAuthority ) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#addVideoSource(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String, int)
	 */
	@Override
	public int addVideoSource( String sourceName, String grabber, String userId, String frequencyTable, String lineupId, String password, boolean useEit, String configPath, int nitId ) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#fetchChannelsFromSource(int, int, boolean)
	 */
	@Override
	public int fetchChannelsFromSource( int sourceId, int cardId, boolean waitForFinish ) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#getChannelInfo(int)
	 */
	@Override
	public ChannelInfo getChannelInfo( int channelId ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#getChannelInfoList(int, int, int)
	 */
	@Override
	public List<ChannelInfo> getChannelInfoList( int sourceId, int startIndex, int count ) {
		
		ResponseEntity<ChannelInfoList> responseEntity = getChannelInfoListResponseEntity( sourceId, startIndex, count );
		ChannelInfoList channelInfoList = responseEntity.getBody();
		
		return channelInfoList.getChannelInfos().getChannelInfos();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#getChannelInfoListResponseEntity(int, int, int)
	 */
	@Override
	public ResponseEntity<ChannelInfoList> getChannelInfoListResponseEntity( int sourceId, int startIndex, int count ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		
		if( sourceId > 0 ) {
			parameters.add( "SourceId", "" + sourceId );
		}
		
		if( startIndex > 0 ) {
			parameters.add( "StartIndex", "" + startIndex );
		}

		if( count > 0 ) {
			parameters.add( "Count", "" + count );
		}

		return restTemplate.exchange( buildUri( "GetChannelInfoList", parameters ), HttpMethod.GET, getRequestEntity(), ChannelInfoList.class );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#getChannelInfoList()
	 */
	@Override
	public List<ChannelInfo> getChannelInfoList() {

		ResponseEntity<ChannelInfoList> responseEntity = getChannelInfoListResponseEntity();
		ChannelInfoList channelInfoList = responseEntity.getBody();
		
		return channelInfoList.getChannelInfos().getChannelInfos();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#getChannelInfoListResponseEntity()
	 */
	@Override
	public ResponseEntity<ChannelInfoList> getChannelInfoListResponseEntity() {
		return restTemplate.exchange( buildUri( "GetChannelInfoList" ), HttpMethod.GET, getRequestEntity(), ChannelInfoList.class );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#getDDLineupList(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<LineupList> getDDLineupList( String source, String userId, String password ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#getVideoMultiplex(int)
	 */
	@Override
	public VideoMultiplex getVideoMultiplex( int multiplexId ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#getVideoMultiplexList(int, int, int)
	 */
	@Override
	public List<VideoMultiplex> getVideoMultiplexList( int sourceId, int startIndex, int count ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#getVideoSource(int)
	 */
	@Override
	public VideoSource getVideoSource( int sourceId ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#getVideoSourceList()
	 */
	@Override
	public List<VideoSource> getVideoSourceList() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#getXmltvIdList(int)
	 */
	@Override
	public List<String> getXmltvIdList( int sourceId ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#reomveDBChannel(int)
	 */
	@Override
	public boolean reomveDBChannel( int channelId ) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#removeVideoSource(int)
	 */
	@Override
	public boolean removeVideoSource( int sourceId ) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#updateDBChannel(int, int, int, java.lang.String, java.lang.String, java.lang.String, int, int, int, boolean, boolean, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean updateDBChannel( int multiplexId, int sourceId, int channelId, String callSign, String channelName, String channelNumber, int serviceId, int atscMajorChannel, int atscMinorChannel, boolean useEIT, boolean visible, String frequencyId, String icon, String format, String xmltvId, String defaultAuthority ) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.channel.ChannelOperations#updateVideoSource(int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, java.lang.String, int)
	 */
	@Override
	public boolean updateVideoSource( int sourceId, String sourceName, String grabber, String userId, String frequencyTable, String lineupId, String password, boolean userEIT, String configPath, int nitId ) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
