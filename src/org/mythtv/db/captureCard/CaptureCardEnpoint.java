/**
 * 
 */
package org.mythtv.db.captureCard;

/**
 * @author Daniel Frey
 *
 */
public enum CaptureCardEnpoint {
	ADD_CAPTURE_CARD( "AddCaptureCard" ),
	ADD_CARD_INPUT( "AddCardInput" ),
	GET_CAPTURE_CARD( "GetCaptureCard" ),
	GET_CAPTURE_CARD_LIST( "GetCaptureCardList" ),
	REMOVE_CAPTURE_CARD( "RemoveCaptureCard" ),
	REMOVE_CARD_INPUT( "RemoveCardInput"),
	UPDATE_CAPTURE_CARD( "UpdateCaptureCard" ),
	UPDATE_CARD_INPUT( "UpdateCardInput" );
	
	private String endpoint;
	
	private CaptureCardEnpoint( String endpoint ) {
		this.endpoint = endpoint;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

}
