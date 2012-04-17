/**
 * 
 */
package org.mythtv.services.api.content.impl;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;

/**
 * @author Daniel Frey
 *
 */
public class ContentModule extends SimpleModule {

	public ContentModule() {
		super( "ContentModule", new Version( 1, 4, 0, null ) );
	}

	@Override
	public void setupModule( SetupContext context ) {
	}

}
