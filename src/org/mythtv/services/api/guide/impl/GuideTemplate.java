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
package org.mythtv.services.api.guide.impl;

import java.util.Date;

import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.guide.GuideOperations;
import org.mythtv.services.api.guide.ProgramGuide;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
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

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "ChanId", "" + channelId );
		
		if( width > 0 ) {
			parameters.add( "Width", "" + width );
		}
		
		if( height > 0 ) {
			parameters.add( "Height", "" + height );
		}
		
		ResponseEntity<String> responseEntity = restTemplate.exchange( buildUri( "GetChannelIcon", parameters ), HttpMethod.GET, getRequestEntity(), String.class );
		String icon = responseEntity.getBody();

		return icon;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.guide.GuideOperations#getProgramDetails(int, java.util.Date)
	 */
	@Override
	public Program getProgramDetails( int channelId, Date startTime ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "ChanId", "" + channelId );
		
		if( null != startTime ) {
			parameters.add( "StartTime", sdf.format( startTime ) );
		}

		ResponseEntity<Program> responseEntity = restTemplate.exchange( buildUri( "GetProgramDetails", parameters ), HttpMethod.GET, getRequestEntity(), Program.class );
		Program program = responseEntity.getBody();

		return program;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.guide.GuideOperations#getProgramGuide(java.util.Date, java.util.Date, int, int, boolean)
	 */
	@Override
	public ProgramGuide getProgramGuide( Date start, Date end, int startChannelId, int numberOfChannels, boolean details ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "StartTime", sdf.format( start ) );
		parameters.add( "EndTime", sdf.format( end ) );
		
		if( startChannelId > 0 ) {
			parameters.add( "StartChanId", "" + startChannelId );
		}

		if( numberOfChannels > 0 ) {
			parameters.add( "NumChannels", "" + numberOfChannels );
		}

		if( details ) {
			parameters.add( "Details", Boolean.toString( details ) );
		}

		ResponseEntity<ProgramGuide> responseEntity = restTemplate.exchange( buildUri( "GetProgramGuide", parameters ), HttpMethod.GET, getRequestEntity(), ProgramGuide.class );
		ProgramGuide programGuide = responseEntity.getBody();

		return programGuide;
	}
	
}
