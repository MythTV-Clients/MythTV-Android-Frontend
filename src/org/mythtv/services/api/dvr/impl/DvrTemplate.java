/**
 * 
 */
package org.mythtv.services.api.dvr.impl;

import org.mythtv.services.api.dvr.DvrOperations;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 *
 */
public class DvrTemplate extends AbstractDvrOperations implements DvrOperations {

	private final RestTemplate restTemplate;

	public DvrTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		this.restTemplate = restTemplate;
	}
	
}
