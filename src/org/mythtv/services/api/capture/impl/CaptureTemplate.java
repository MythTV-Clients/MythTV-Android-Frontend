/**
 * 
 */
package org.mythtv.services.api.capture.impl;

import java.util.List;

import org.mythtv.services.api.capture.CaptureCard;
import org.mythtv.services.api.capture.CaptureCardList;
import org.mythtv.services.api.capture.CaptureOperations;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 *
 */
public class CaptureTemplate extends AbstractCaptureOperations implements CaptureOperations {

	private final RestTemplate restTemplate;

	public CaptureTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		this.restTemplate = restTemplate;
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.services.api.capture.CaptureOperations#getCaptureCardList()
	 */
	@Override
	public List<CaptureCard> getCaptureCardList() {
		CaptureCardList captureCardList = restTemplate.getForObject( buildUri( "GetCaptureCardList" ), CaptureCardList.class );
		return captureCardList.getCaptureCards().getCaptureCards();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.capture.CaptureOperations#getCaptureCardList(java.lang.String, java.lang.String)
	 */
	@Override
	public List<CaptureCard> getCaptureCardList( String hostName, String cardType ) {
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		
		if( null != hostName && !"".equals( hostName ) ) {
			parameters.add( "HostName", hostName );
		}
		
		if( null != cardType && !"".equals( cardType ) ) {
			parameters.add( "CardType", cardType );
		}
		
		CaptureCardList captureCardList = restTemplate.getForObject( buildUri( "GetCaptureCardList", parameters ), CaptureCardList.class );
		return captureCardList.getCaptureCards().getCaptureCards();
	}

}
