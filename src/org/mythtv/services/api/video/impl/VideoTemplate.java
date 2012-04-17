/**
 * 
 */
package org.mythtv.services.api.video.impl;

import org.mythtv.services.api.video.VideoOperations;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 *
 */
public class VideoTemplate extends AbstractVideoOperations implements VideoOperations {

	private final RestTemplate restTemplate;

	public VideoTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		this.restTemplate = restTemplate;
	}
	
}
