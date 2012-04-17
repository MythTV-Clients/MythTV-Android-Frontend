/**
 * 
 */
package org.mythtv.services.connect;

import org.mythtv.services.api.MythServices;
import org.mythtv.services.api.MythServicesTemplate;

/**
 * @author Daniel Frey
 *
 */
public class MythServicesServiceProvider {

	private final String apiUrlBase;
	
	public MythServicesServiceProvider( String apiUrlBase ) {
		this.apiUrlBase = apiUrlBase;
	}

	public MythServices getApi() {
		return new MythServicesTemplate( getApiUrlBase() );
	}
	
	// internal helpers
	
	private String getApiUrlBase() {
		return apiUrlBase;
	}

}
