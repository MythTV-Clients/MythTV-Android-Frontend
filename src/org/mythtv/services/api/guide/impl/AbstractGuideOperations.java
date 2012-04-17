package org.mythtv.services.api.guide.impl;

import org.mythtv.services.api.AbstractOperations;

/**
 * @author Daniel Frey
 * 
 */
class AbstractGuideOperations extends AbstractOperations {
	
	public AbstractGuideOperations( String apiUrlBase ) {
		super( apiUrlBase );
	}
	
	protected String getApiUrlBase() {
		return super.getApiUrlBase() + "Guide/";
	}

}