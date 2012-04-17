/**
 * 
 */
package org.mythtv.services.api.capture.impl;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;
import org.mythtv.services.api.capture.CaptureCard;
import org.mythtv.services.api.capture.CaptureCardList;
import org.mythtv.services.api.capture.CaptureCards;

/**
 * @author Daniel Frey
 *
 */
public class CaptureModule extends SimpleModule {

	public CaptureModule() {
		super( "CaptureModule", new Version( 1, 4, 0, null ) );
	}

	@Override
	public void setupModule( SetupContext context ) {
		context.setMixInAnnotations( CaptureCardList.class, CaptureCardListMixin.class );
		context.setMixInAnnotations( CaptureCards.class, CaptureCardsMixin.class );
		context.setMixInAnnotations( CaptureCard.class, CaptureCardMixin.class );
	}

}
