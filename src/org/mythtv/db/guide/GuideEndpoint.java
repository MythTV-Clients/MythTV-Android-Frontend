/**
 * 
 */
package org.mythtv.db.guide;

/**
 * @author Daniel Frey
 *
 */
public enum GuideEndpoint {
	GET_CHANNEL_ICON( "GetChannelIcon" ),
	GET_PROGRAM_DETAILS( "GetProgramDetails" ),
	GET_PROGRAM_GUIDE( "GetProgramGuide" );
	
	private String endpoint;
	
	private GuideEndpoint( String endpoint ) {
		this.endpoint = endpoint;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

}
