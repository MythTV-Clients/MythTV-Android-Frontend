package org.mythtv.services.api.capture.impl;

import org.mythtv.services.api.AbstractOperations;

/**
 * @author Daniel Frey
 * 
 */
class AbstractCaptureOperations extends AbstractOperations {
	
	public AbstractCaptureOperations( String apiUrlBase ) {
		super( apiUrlBase );
	}
	
	protected String getApiUrlBase() {
		return super.getApiUrlBase() + "Capture/";
	}

}