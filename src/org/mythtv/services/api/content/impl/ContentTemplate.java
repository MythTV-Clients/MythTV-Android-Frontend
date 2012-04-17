/**
 * 
 */
package org.mythtv.services.api.content.impl;

import org.mythtv.services.api.content.ContentOperations;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 *
 */
public class ContentTemplate extends AbstractContentOperations implements ContentOperations {

	private final RestTemplate restTemplate;

	public ContentTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		this.restTemplate = restTemplate;
	}
	
}
