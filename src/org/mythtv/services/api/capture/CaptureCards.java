/**
 * 
 */
package org.mythtv.services.api.capture;

import java.util.List;

/**
 * @author Daniel Frey
 *
 */
public class CaptureCards {

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
