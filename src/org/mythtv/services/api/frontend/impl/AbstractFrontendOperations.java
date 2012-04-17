package org.mythtv.services.api.frontend.impl;

import org.mythtv.services.api.AbstractOperations;

/**
 * @author Daniel Frey
 * 
 */
class AbstractFrontendOperations extends AbstractOperations {
	
	public AbstractFrontendOperations( String apiUrlBase ) {
		super( apiUrlBase );
	}
	
	protected String getApiUrlBase() {
		return super.getApiUrlBase() + "Frontend/";
	}

}