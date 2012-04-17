/**
 * 
 */
package org.mythtv.services.api.channel.impl;

import org.mythtv.services.api.channel.ChannelOperations;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 *
 */
public class ChannelTemplate extends AbstractChannelOperations implements ChannelOperations {

	private final RestTemplate restTemplate;

	public ChannelTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		this.restTemplate = restTemplate;
	}
	
}
