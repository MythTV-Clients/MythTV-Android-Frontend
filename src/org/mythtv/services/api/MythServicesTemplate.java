/**
 *  This file is part of MythTV for Android
 * 
 *  MythTV for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MythTV for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MythTV for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * This software can be found at <https://github.com/MythTV-Android/mythtv-for-android/>
 *
 */
package org.mythtv.services.api;

import org.mythtv.services.api.capture.CaptureOperations;
import org.mythtv.services.api.capture.impl.CaptureTemplate;
import org.mythtv.services.api.channel.ChannelOperations;
import org.mythtv.services.api.channel.impl.ChannelTemplate;
import org.mythtv.services.api.content.ContentOperations;
import org.mythtv.services.api.content.impl.ContentTemplate;
import org.mythtv.services.api.dvr.DvrOperations;
import org.mythtv.services.api.dvr.impl.DvrTemplate;
import org.mythtv.services.api.frontend.FrontendOperations;
import org.mythtv.services.api.frontend.impl.FrontendTemplate;
import org.mythtv.services.api.guide.GuideOperations;
import org.mythtv.services.api.guide.impl.GuideTemplate;
import org.mythtv.services.api.myth.MythOperations;
import org.mythtv.services.api.myth.impl.MythTemplate;
import org.mythtv.services.api.video.VideoOperations;
import org.mythtv.services.api.video.impl.VideoTemplate;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 * 
 */
public class MythServicesTemplate implements MythServices {

	private final String apiUrlBase;
	private final RestTemplate restTemplate;

	private CaptureOperations captureOperations;
	private ChannelOperations channelOperations;
	private ContentOperations contentOperations;
	private DvrOperations dvrOperations;
	private FrontendOperations frontendOperations;
	private GuideOperations guideOperations;
	private MythOperations mythOperations;
	private VideoOperations videoOperations;

	public MythServicesTemplate( String apiUrlBase ) {
		this.apiUrlBase = apiUrlBase;

		restTemplate = new RestTemplate( true, ClientHttpRequestFactorySelector.getRequestFactory() );
		//restTemplate.setMessageConverters( getMessageConverters() );

		getRestTemplate().setErrorHandler( new MythServicesErrorHandler() );
		initSubApis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mythtv.services.api.MythServices#captureOperations()
	 */
	@Override
	public CaptureOperations captureOperations() {
		return captureOperations;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.MythServices#channelOperations()
	 */
	@Override
	public ChannelOperations channelOperations() {
		return channelOperations;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.MythServices#contentOperations()
	 */
	@Override
	public ContentOperations contentOperations() {
		return contentOperations;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.MythServices#dvrOperations()
	 */
	@Override
	public DvrOperations dvrOperations() {
		return dvrOperations;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.MythServices#frontendOperations()
	 */
	@Override
	public FrontendOperations frontendOperations() {
		return frontendOperations;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.MythServices#guideOperations()
	 */
	@Override
	public GuideOperations guideOperations() {
		return guideOperations;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.MythServices#mythOperations()
	 */
	@Override
	public MythOperations mythOperations() {
		return mythOperations;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.MythServices#videoOperations()
	 */
	@Override
	public VideoOperations videoOperations() {
		return videoOperations;
	}

	// private helper

	private String getApiUrlBase() {
		return apiUrlBase;
	}

	private RestTemplate getRestTemplate() {
		return restTemplate;
	}

	private void initSubApis() {
		this.captureOperations = new CaptureTemplate( getRestTemplate(), getApiUrlBase() );
		this.channelOperations = new ChannelTemplate( getRestTemplate(), getApiUrlBase() );
		this.contentOperations = new ContentTemplate( getRestTemplate(), getApiUrlBase() );
		this.dvrOperations = new DvrTemplate( getRestTemplate(), getApiUrlBase() );
		this.frontendOperations = new FrontendTemplate( getRestTemplate(), getApiUrlBase() );
		this.guideOperations = new GuideTemplate( getRestTemplate(), getApiUrlBase() );
		this.mythOperations = new MythTemplate( getRestTemplate(), getApiUrlBase() );
		this.videoOperations = new VideoTemplate( getRestTemplate(), getApiUrlBase() );
	}

}
