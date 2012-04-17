package org.mythtv.services.api.channel.impl;

import org.mythtv.services.api.AbstractOperations;

/**
 * @author Daniel Frey
 * 
 */
class AbstractChannelOperations extends AbstractOperations {
	
	public AbstractChannelOperations( String apiUrlBase ) {
		super( apiUrlBase );
	}
	
	protected String getApiUrlBase() {
		return super.getApiUrlBase() + "Channel/";
	}

}