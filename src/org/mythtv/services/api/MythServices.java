/**
 * 
 */
package org.mythtv.services.api;

import org.mythtv.services.api.capture.CaptureOperations;
import org.mythtv.services.api.channel.ChannelOperations;
import org.mythtv.services.api.content.ContentOperations;
import org.mythtv.services.api.dvr.DvrOperations;
import org.mythtv.services.api.frontend.FrontendOperations;
import org.mythtv.services.api.guide.GuideOperations;
import org.mythtv.services.api.myth.MythOperations;
import org.mythtv.services.api.video.VideoOperations;

/**
 * @author Daniel Frey
 *
 */
public interface MythServices {

	CaptureOperations captureOperations();
	
	ChannelOperations channelOperations();
	
	ContentOperations contentOperations();
	
	DvrOperations dvrOperations();
	
	FrontendOperations frontendOperations();
	
	GuideOperations guideOperations();
	
	MythOperations mythOperations();
	
	VideoOperations videoOperations();
	
}
