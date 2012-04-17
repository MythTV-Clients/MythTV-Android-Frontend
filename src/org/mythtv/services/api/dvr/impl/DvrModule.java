/**
 * 
 */
package org.mythtv.services.api.dvr.impl;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;

/**
 * @author Daniel Frey
 *
 */
public class DvrModule extends SimpleModule {

	public DvrModule() {
		super( "DvrModule", new Version( 1, 4, 0, null ) );
	}

	@Override
	public void setupModule( SetupContext context ) {
	}

}
