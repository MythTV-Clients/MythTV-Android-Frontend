/**
 * 
 */
package org.mythtv.services.api.capture.impl;

import java.util.Collections;
import java.util.List;

import org.mythtv.services.api.capture.CaptureCard;
import org.mythtv.services.api.capture.CaptureCardList;
import org.mythtv.services.api.capture.CaptureOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept( Collections.singletonList( MediaType.APPLICATION_JSON ) );

		HttpEntity<?> requestEntity = new HttpEntity<Object>( requestHeaders );

		ResponseEntity<CaptureCardList> responseEntity = restTemplate.exchange( buildUri( "GetCaptureCardList" ), HttpMethod.GET, requestEntity, CaptureCardList.class );
		CaptureCardList captureCardList = responseEntity.getBody();
		
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
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept( Collections.singletonList( MediaType.APPLICATION_JSON ) );

		HttpEntity<?> requestEntity = new HttpEntity<Object>( requestHeaders );

		ResponseEntity<CaptureCardList> responseEntity = restTemplate.exchange( buildUri( "GetCaptureCardList", parameters ), HttpMethod.GET, requestEntity, CaptureCardList.class );
		CaptureCardList captureCardList = responseEntity.getBody();
		
		return captureCardList.getCaptureCards().getCaptureCards();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.capture.CaptureOperations#getCaptureCard(int)
	 */
	@Override
	public CaptureCard getCaptureCard( int cardId ) {
		return restTemplate.getForObject( buildUri( "GetCaptureCard", "CardId", new String( "" + cardId ) ), CaptureCard.class );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.capture.CaptureOperations#addCaptureCard(org.mythtv.services.api.capture.CaptureCard)
	 */
	@Override
	public int addCaptureCard( CaptureCard captureCard ) {
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.capture.CaptureOperations#updateCaptureCard(int, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean updateCaptureCard( int cardId, String setting, String value ) {
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "CardId", new String( "" + cardId ) );
		parameters.add( setting, value );
		
		return restTemplate.getForObject( buildUri( "UpdateCaptureCard", parameters ), Boolean.class );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.capture.CaptureOperations#removeCaptureCard(int)
	 */
	@Override
	public boolean removeCaptureCard( int cardId ) {
		return restTemplate.getForObject( buildUri( "RemoveCaptureCard", "CardId", new String( "" + cardId ) ), Boolean.class );
	}

}
