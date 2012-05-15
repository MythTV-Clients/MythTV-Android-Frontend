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
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */
package org.mythtv.services.api.guide.impl;

import java.util.Date;

import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.guide.GuideOperations;
import org.mythtv.services.api.guide.ProgramGuide;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 *
 */
public class GuideTemplate extends AbstractGuideOperations implements GuideOperations {

	private final RestTemplate restTemplate;

	public GuideTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		this.restTemplate = restTemplate;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.guide.GuideOperations#getChannelIcon(int, int, int)
	 */
	@Override
	public String getChannelIcon( int channelId, int width, int height ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.guide.GuideOperations#getProgramDetails(int, java.util.Date)
	 */
	@Override
	public Program getProgramDetails( int channelId, Date startTime ) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.guide.GuideOperations#getProgramGuide(java.util.Date, java.util.Date, int, int, boolean)
	 */
	@Override
	public ProgramGuide getProgramGuide( Date start, Date end, int startChannelId, int numberOfChannels, boolean details ) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
