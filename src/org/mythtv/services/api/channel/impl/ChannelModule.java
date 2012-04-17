/**
 * 
 */
package org.mythtv.services.api.channel.impl;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;

/**
 * @author Daniel Frey
 *
 */
public class ChannelModule extends SimpleModule {

	public ChannelModule() {
		super( "ChannelModule", new Version( 1, 4, 0, null ) );
	}

	@Override
	public void setupModule( SetupContext context ) {
	}

}
