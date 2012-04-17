/**
 * 
 */
package org.mythtv.services.api.myth.impl;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;

/**
 * @author Daniel Frey
 *
 */
public class MythModule extends SimpleModule {

	public MythModule() {
		super( "MythModule", new Version( 1, 4, 0, null ) );
	}

	@Override
	public void setupModule( SetupContext context ) {
	}

}
