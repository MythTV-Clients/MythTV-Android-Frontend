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
package org.mythtv.services.api.dvr.impl;

import java.util.List;

import org.mythtv.services.api.dvr.DvrOperations;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.ProgramList;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 *
 */
public class DvrTemplate extends AbstractDvrOperations implements DvrOperations {

	private final RestTemplate restTemplate;
	
	/**
	 * @param restTemplate
	 * @param apiUrlBase
	 */
	public DvrTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		this.restTemplate = restTemplate;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getRecordedList()
	 */
	@Override
	public List<Program> getRecordedList() {
		
		ResponseEntity<ProgramList> responseEntity = restTemplate.exchange( buildUri( "GetRecordedList" ), HttpMethod.GET, getRequestEntity(), ProgramList.class );
		ProgramList programList = responseEntity.getBody();
		
		return programList.getPrograms().getPrograms();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.dvr.DvrOperations#getRecordedList(int, int, boolean)
	 */
	@Override
	public List<Program> getRecordedList( int startIndex, int count, boolean descending ) {
		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		
		if( startIndex > 0 ) {
			parameters.add( "StartIndex", "" + startIndex );
		}
		
		if( count > 0 ) {
			parameters.add( "Count", "" + count );
		}

		if( descending ) {
			parameters.add( "Descending", "true" );
		}

		ResponseEntity<ProgramList> responseEntity = restTemplate.exchange( buildUri( "GetRecordedList", parameters ), HttpMethod.GET, getRequestEntity(), ProgramList.class );
		ProgramList programList = responseEntity.getBody();
		
		return programList.getPrograms().getPrograms();
	}
	
}
