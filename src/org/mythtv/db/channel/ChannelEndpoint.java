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
