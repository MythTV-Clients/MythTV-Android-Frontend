/**
 * 
 */
package org.mythtv.services.api.guide.impl;

import org.mythtv.services.api.guide.GuideOperations;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 *
 */
public class GuideTemplate extends AbstractGuideOperations implements GuideOperations {

	private final RestTemplate restTemplate;

	public GuideTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		this.restTemplate = restTemplate;
	}
	
}
