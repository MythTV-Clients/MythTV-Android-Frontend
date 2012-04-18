/**
 * 
 */
package org.mythtv.services.api.capture;

import java.util.List;

/**
 * @author Daniel Frey
 *
 */
public interface CaptureOperations {

	List<CaptureCard> getCaptureCardList();
	
	List<CaptureCard> getCaptureCardList( String hostName, String cardType );

	CaptureCard getCaptureCard( int cardId );
	
	int addCaptureCard( CaptureCard captureCard );
	
	boolean updateCaptureCard( int cardId, String setting, String value );
	
	boolean removeCaptureCard( int cardId );
		
}
