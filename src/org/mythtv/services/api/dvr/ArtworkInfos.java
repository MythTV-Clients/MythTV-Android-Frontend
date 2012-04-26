/**
 * 
 */
package org.mythtv.services.api.dvr;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Daniel Frey
 *
 */
public class ArtworkInfos {

	@JsonProperty( "ArtworkInfos" )
	private List<ArtworkInfo> artworkInfos;
	
	public ArtworkInfos() { }

	/**
	 * @return the artworkInfos
	 */
	public List<ArtworkInfo> getArtworkInfos() {
		return artworkInfos;
	}

	/**
	 * @param artworkInfos the artworkInfos to set
	 */
	public void setArtworkInfos( List<ArtworkInfo> artworkInfos ) {
		this.artworkInfos = artworkInfos;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "ArtworkInfos [" );
		
		if( artworkInfos != null ) {
			builder.append( "artworkInfos=" );
			builder.append( artworkInfos );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
