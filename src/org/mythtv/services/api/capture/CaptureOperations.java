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
	
}
