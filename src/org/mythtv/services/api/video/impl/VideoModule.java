/**
 * 
 */
package org.mythtv.services.api.video.impl;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;

/**
 * @author Daniel Frey
 *
 */
public class VideoModule extends SimpleModule {

	public VideoModule() {
		super( "VideoModule", new Version( 1, 4, 0, null ) );
	}

	@Override
	public void setupModule( SetupContext context ) {
	}

}
