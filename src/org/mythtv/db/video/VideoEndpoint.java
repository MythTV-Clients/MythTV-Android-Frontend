/**
 * 
 */
package org.mythtv.db.video;

/**
 * @author Daniel Frey
 *
 */
public enum VideoEndpoint {
	ADD_VIDEO( "AddVideo" ),
	GET_BLURAY( "GetBluray" ),
	GET_VIDEO( "GetVideo" ),
	GET_VIDEO_BY_FILENAME( "GetVideoByFileName" ),
	GET_VIDEO_LIST( "GetVideoList" ),
	LOOKUP_VIDEO( "LookupVideo"),
	REMOVE_VIDEO_FROM_DB( "RemoveVideoFromDB" );
	
	private String endpoint;
	
	private VideoEndpoint( String endpoint ) {
		this.endpoint = endpoint;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

}
