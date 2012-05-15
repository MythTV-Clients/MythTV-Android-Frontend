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
package org.mythtv.services.api.channel;

import java.util.List;

/**
 * @author Daniel Frey
 *
 */
public interface ChannelOperations {

	/** 
	 * - POST
	 * 
	 * @param multiplexId
	 * @param sourceId
	 * @param channelId
	 * @param callSign
	 * @param channelName
	 * @param channelNumber
	 * @param serviceId
	 * @param atscMajorChannel
	 * @param atscMinorChannel
	 * @param userEIT
	 * @param visible
	 * @param frequencyId
	 * @param icon
	 * @param format
	 * @param xmltvId
	 * @param defaultAuthority
	 * @return
	 */
	boolean addDBChannel( int multiplexId, int sourceId, int channelId, String callSign, String channelName, String channelNumber, int serviceId, int atscMajorChannel, int atscMinorChannel, boolean userEIT, boolean visible, String frequencyId, String icon, String format, String xmltvId, String defaultAuthority );
	
	/**
	 * - POST
	 * 
	 * @param sourceName
	 * @param grabber
	 * @param userId
	 * @param frequencyTable
	 * @param lineupId
	 * @param password
	 * @param useEit
	 * @param configPath
	 * @param nitId
	 * @return
	 */
	int addVideoSource( String sourceName, String grabber, String userId, String frequencyTable, String lineupId, String password, boolean useEit, String configPath, int nitId );
	
	/**
	 * - GET
	 * 
	 * @param sourceId
	 * @param cardId
	 * @param waitForFinish
	 * @return
	 */
	int fetchChannelsFromSource( int sourceId, int cardId, boolean waitForFinish );
	
	/**
	 * - GET
	 * 
	 * @param channelId
	 * @return
	 */
	ChannelInfo getChannelInfo( int channelId );
	
	/**
	 * - GET
	 * 
	 * @param sourceId
	 * @param startIndex
	 * @param count
	 * @return
	 */
	List<ChannelInfo> getChannelInfoList( int sourceId, int startIndex, int count );
	
	/**
	 * - GET
	 * 
	 * @param source
	 * @param userId
	 * @param password
	 * @return
	 */
	List<LineupList> getDDLineupList( String source, String userId, String password );
	
	/**
	 * - GET
	 * 
	 * @param multiplexId
	 * @return
	 */
	VideoMultiplex getVideoMultiplex( int multiplexId );
	
	/**
	 * - GET
	 * 
	 * @param sourceId
	 * @param startIndex
	 * @param count
	 * @return
	 */
	List<VideoMultiplex> getVideoMultiplexList( int sourceId, int startIndex, int count );
	
	/**
	 * - GET
	 * 
	 * @param sourceId
	 * @return
	 */
	VideoSource getVideoSource( int sourceId );
	
	/**
	 * - GET
	 * 
	 * @return
	 */
	List<VideoSource> getVideoSourceList();
	
	/**
	 * - GET
	 * 
	 * @param sourceId
	 * @return
	 */
	List<String> getXmltvIdList( int sourceId );
	
	/**
	 * - POST
	 * 
	 * @param channelId
	 * @return
	 */
	boolean reomveDBChannel( int channelId );
	
	/**
	 * - POST
	 * 
	 * @param sourceId
	 * @return
	 */
	boolean removeVideoSource( int sourceId );

	/**
	 * - POST
	 * 
	 * @param multiplexId
	 * @param sourceId
	 * @param channelId
	 * @param callSign
	 * @param channelName
	 * @param channelNumber
	 * @param serviceId
	 * @param atscMajorChannel
	 * @param atscMinorChannel
	 * @param useEIT
	 * @param visible
	 * @param frequencyId
	 * @param icon
	 * @param format
	 * @param xmltvId
	 * @param defaultAuthority
	 * @return
	 */
	boolean updateDBChannel( int multiplexId, int sourceId, int channelId, String callSign, String channelName, String channelNumber, int serviceId, int atscMajorChannel, int atscMinorChannel, boolean useEIT, boolean visible, String frequencyId, String icon, String format, String xmltvId, String defaultAuthority );
	
	/**
	 * - POST
	 * 
	 * @param sourceId
	 * @param sourceName
	 * @param grabber
	 * @param userId
	 * @param frequencyTable
	 * @param lineupId
	 * @param password
	 * @param userEIT
	 * @param configPath
	 * @param nitId
	 * @return
	 */
	boolean updateVideoSource( int sourceId, String sourceName, String grabber, String userId, String frequencyTable, String lineupId, String password, boolean userEIT, String configPath, int nitId );
	
}
