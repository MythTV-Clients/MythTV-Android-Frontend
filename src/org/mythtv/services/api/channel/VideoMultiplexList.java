/**
 * 
 */
package org.mythtv.services.api.channel;

/**
 * @author Daniel Frey
 *
 */
public class VideoMultiplexList {

	private VideoMultiplexes videoMultiplexes;
	
	public VideoMultiplexList() { }

	/**
	 * @return the videoMultiplexes
	 */
	public VideoMultiplexes getVideoMultiplexes() {
		return videoMultiplexes;
	}

	/**
	 * @param videoMultiplexes the videoMultiplexes to set
	 */
	public void setVideoMultiplexes( VideoMultiplexes videoMultiplexes ) {
		this.videoMultiplexes = videoMultiplexes;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append( "VideoMultiplexList [" );
		
		if( videoMultiplexes != null ) {
			builder.append( "videoMultiplexes=" );
			builder.append( videoMultiplexes );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
