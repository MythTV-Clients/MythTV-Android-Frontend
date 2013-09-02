/**
 * 
 */
package org.mythtv.db.captureCard.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Sebastien Astie
 *
 */
public class CaptureCardWrapper {

	@JsonProperty( "CaptureCard" )
	private CaptureCard captureCard;
	
	public CaptureCardWrapper() { }

	/**
	 * @return the captureCard
	 */
	public CaptureCard getCaptureCard() {
		return captureCard;
	}

	/**
	 * @param captureCard the captureCard to set
	 */
	public void setCaptureCard( CaptureCard captureCard ) {
		this.captureCard = captureCard;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append( "CaptureCardWrapper [" );
		
		if( captureCard != null ) {
			builder.append( "captureCard=" );
			builder.append( captureCard );
		}
		
		builder.append( "]" );
	
		return builder.toString();
	}
	
}
