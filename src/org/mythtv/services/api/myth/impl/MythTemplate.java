/**
 * 
 */
package org.mythtv.services.api.myth.impl;

import org.mythtv.services.api.myth.MythOperations;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 *
 */
public class MythTemplate extends AbstractMythOperations implements MythOperations {

	private final RestTemplate restTemplate;

	public MythTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		this.restTemplate = restTemplate;
	}
	
}
