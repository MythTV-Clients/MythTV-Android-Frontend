/**
 * 
 */
package org.mythtv.services.api.frontend.impl;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;

/**
 * @author Daniel Frey
 *
 */
public class FrontendModule extends SimpleModule {

	public FrontendModule() {
		super( "FrontendModule", new Version( 1, 4, 0, null ) );
	}

	@Override
	public void setupModule( SetupContext context ) {
	}

}
