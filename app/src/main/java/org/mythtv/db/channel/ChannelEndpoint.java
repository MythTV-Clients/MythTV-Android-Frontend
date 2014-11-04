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
/**
 * 
 */
package org.mythtv.db.channel;

/**
 * @author Daniel Frey
 *
 */
public enum ChannelEndpoint {
	ADD_DB_CHANNEL( "AddDBChannel" ),
	ADD_VIDEO_SOURCE( "AddVideoSource" ),
	FETCH_CHANNELS_FROM_SOURCE( "FetchChannelsFromSource" ),
	GET_CHANNEL_INFO( "GetChannelInfo" ),
	GET_CHANNEL_INFO_LIST( "GetChannelInfoList" ),
	GET_DD_LINEUP_LIST( "GetDDLineupList"),
	GET_VIDEO_MULTIPLEX( "GetVideoMultiplex" ),
	GET_VIDEO_MULTIPLEX_LIST( "GetVideoMultiplexList" ),
	GET_VIDEO_SOURCE( "GetVideoSource" ),
	GET_VIDEO_SOURCE_LIST( "GetVideoSourceList" ),
	GET_XMLTVID_LIST( "GetXMLTVIdList" ),
	REMOVE_DB_CHANNEL( "RemoveDBChannel" ),
	REMOVE_VIDEO_SOURCE( "RemoveVideoSource" ),
	UPDATE_DB_CHANNEL( "UpdateDBChannel" ),
	UPDATE_VIDEO_SOURCE( "UpdateVideoSource" );
	
	private String endpoint;
	
	private ChannelEndpoint( String endpoint ) {
		this.endpoint = endpoint;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

}
