/**
 * 
 */
package org.mythtv.services.api.frontend.impl;

import org.mythtv.services.api.frontend.FrontendOperations;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 *
 */
public class FrontendTemplate extends AbstractFrontendOperations implements FrontendOperations {

	private final RestTemplate restTemplate;

	public FrontendTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		this.restTemplate = restTemplate;
	}
	
}
