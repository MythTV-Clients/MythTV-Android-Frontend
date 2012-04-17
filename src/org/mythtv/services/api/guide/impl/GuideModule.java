/**
 * 
 */
package org.mythtv.services.api.guide.impl;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;

/**
 * @author Daniel Frey
 *
 */
public class GuideModule extends SimpleModule {

	public GuideModule() {
		super( "GuideModule", new Version( 1, 4, 0, null ) );
	}

	@Override
	public void setupModule( SetupContext context ) {
	}

}
