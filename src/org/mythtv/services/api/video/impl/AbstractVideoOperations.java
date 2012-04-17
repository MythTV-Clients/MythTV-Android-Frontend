package org.mythtv.services.api.video.impl;

import org.mythtv.services.api.AbstractOperations;

/**
 * @author Daniel Frey
 * 
 */
class AbstractVideoOperations extends AbstractOperations {
	
	public AbstractVideoOperations( String apiUrlBase ) {
		super( apiUrlBase );
	}
	
	protected String getApiUrlBase() {
		return super.getApiUrlBase() + "Video/";
	}

}