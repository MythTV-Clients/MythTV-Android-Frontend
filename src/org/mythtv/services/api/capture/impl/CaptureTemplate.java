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
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept( Collections.singletonList( MediaType.APPLICATION_JSON ) );

		HttpEntity<?> requestEntity = new HttpEntity<Object>( requestHeaders );

		ResponseEntity<CaptureCard> responseEntity = restTemplate.exchange( buildUri( "GetCaptureCard", "CardId", new String( "" + cardId ) ), HttpMethod.GET, requestEntity, CaptureCard.class );
		CaptureCard captureCard = responseEntity.getBody();

		return captureCard;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.capture.CaptureOperations#addCaptureCard(org.mythtv.services.api.capture.CaptureCard)
	 */
	@Override
	public int addCaptureCard( CaptureCard captureCard ) {
		return restTemplate.postForObject( buildUri( "AddCaptureCard" ), convertCaptureCardToParameters( captureCard ), Integer.class );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.capture.CaptureOperations#updateCaptureCard(int, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean updateCaptureCard( int cardId, String setting, String value ) {
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "CardId", new String( "" + cardId ) );
		parameters.add( setting, value );
		
		return restTemplate.postForObject( buildUri( "UpdateCaptureCard" ), parameters, Boolean.class );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.capture.CaptureOperations#removeCaptureCard(int)
	 */
	@Override
	public boolean removeCaptureCard( int cardId ) {
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "CardId", new String( "" + cardId ) );

		return restTemplate.postForObject( buildUri( "RemoveCaptureCard" ), parameters, Boolean.class );
	}

	// internal helpers
	
	private LinkedMultiValueMap<String, String> convertCaptureCardToParameters( CaptureCard captureCard ) {
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		
		parameters.add( "VideoDevice", captureCard.getVbiDevice() );
		parameters.add( "CardType", captureCard.getCardType() );
		parameters.add( "DefaultInput", "" );
		parameters.add( "HostName", captureCard.getHostName() );
		parameters.add( "AudioDevice", captureCard.getAudioDevice() );
		parameters.add( "VBIDevice", captureCard.getVbiDevice() );
		parameters.add( "AudioRateLimit", captureCard.getAudioRateLimit().toString() );
		parameters.add( "SkipBTAudio", captureCard.getSkipBtAudio().toString() );
		parameters.add( "DVBSWFilter", captureCard.getDvbSwFilter().toString() );
		parameters.add( "DVBSatType", captureCard.getDvbSatType().toString() );
		parameters.add( "DVBWaitForSeqStart", captureCard.getDvbWaitForSeqStart().toString() );
		parameters.add( "DVBOnDemand", captureCard.getDvbOnDemand().toString() );
		parameters.add( "DVBDiSEqCType", captureCard.getDvbDiSEqCType().toString() );
		parameters.add( "FirewireModel", captureCard.getFirewireModel() );
		parameters.add( "FirewireSpeed", captureCard.getFirewireSpeed().toString() );
		parameters.add( "FirewireConnection", captureCard.getFirewireConnection().toString() );
		parameters.add( "SignalTimeout", captureCard.getSignalTimeout().toString() );
		parameters.add( "ChannelTimeout", captureCard.getChannelTimeout().toString() );
		parameters.add( "DVBTuningDelay", captureCard.getDvbTuningDelay().toString() );
		parameters.add( "Contrast", captureCard.getContrast().toString() );
		parameters.add( "Brightness", captureCard.getBrightness().toString() );
		parameters.add( "Colour", captureCard.getColour().toString() );
		parameters.add( "Hue", captureCard.getHue().toString() );
		parameters.add( "DiSEqCId", captureCard.getDiSEqCId().toString() );
		parameters.add( "DVBEITScan", captureCard.getDvbEitScan().toString() );

		return parameters;
	}
	
}
