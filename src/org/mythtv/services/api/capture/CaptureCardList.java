/**
 * 
 */
package org.mythtv.services.api.capture;


/**
 * @author Daniel Frey
 *
 */
public class CaptureCardList {

	private CaptureCards captureCards;
	
	public CaptureCardList() { }
	
	public CaptureCardList( CaptureCards captureCards ) {
		this.captureCards = captureCards;
	}

	/**
	 * @return the captureCards
	 */
	public CaptureCards getCaptureCards() {
		return captureCards;
	}

	/**
	 * @param captureCards the captureCards to set
	 */
	public void setCaptureCards( CaptureCards captureCards ) {
		this.captureCards = captureCards;
	}
	
}
