package org.mythtv.services.api.content.impl;

import org.mythtv.services.api.AbstractOperations;

/**
 * @author Daniel Frey
 * 
 */
class AbstractContentOperations extends AbstractOperations {
	
	public AbstractContentOperations( String apiUrlBase ) {
		super( apiUrlBase );
	}
	
	protected String getApiUrlBase() {
		return super.getApiUrlBase() + "Content/";
	}

}