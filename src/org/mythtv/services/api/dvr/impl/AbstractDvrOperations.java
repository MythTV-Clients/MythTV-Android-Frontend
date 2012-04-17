package org.mythtv.services.api.dvr.impl;

import org.mythtv.services.api.AbstractOperations;

/**
 * @author Daniel Frey
 * 
 */
class AbstractDvrOperations extends AbstractOperations {
	
	public AbstractDvrOperations( String apiUrlBase ) {
		super( apiUrlBase );
	}
	
	protected String getApiUrlBase() {
		return super.getApiUrlBase() + "Dvr/";
	}

}