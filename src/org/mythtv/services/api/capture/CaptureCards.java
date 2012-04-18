/**
 * 
 */
package org.mythtv.services.api.capture;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Daniel Frey
 *
 */
public class CaptureCards {

	@JsonProperty( "CaptureCards" )
	private List<CaptureCard> captureCards;
	
	public CaptureCards() { }
	
	public CaptureCards( List<CaptureCard> captureCards ) {
		this.captureCards = captureCards;
	}

	/**
	 * @return the captureCards
	 */
	public List<CaptureCard> getCaptureCards() {
		return captureCards;
	}

	/**
	 * @param captureCards the captureCards to set
	 */
	public void setCaptureCards( List<CaptureCard> captureCards ) {
		this.captureCards = captureCards;
	}
	
}
