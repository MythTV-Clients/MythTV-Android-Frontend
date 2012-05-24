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
package org.mythtv.services.api.frontend.impl;

import org.mythtv.services.api.frontend.FrontendActionList;
import org.mythtv.services.api.frontend.FrontendOperations;
import org.mythtv.services.api.frontend.FrontendStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author Daniel Frey
 *
 */
public class FrontendTemplate extends AbstractFrontendOperations implements FrontendOperations {

	private final RestTemplate restTemplate;

	public FrontendTemplate( RestTemplate restTemplate, String apiUrlBase ) {
		super( apiUrlBase );
		this.restTemplate = restTemplate;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.frontend.FrontendOperations#getStatus(java.lang.String)
	 */
	@Override
	public FrontendStatus getStatus( String frontedApiUrlBase ) {
		
		ResponseEntity<FrontendStatus> responseEntity = restTemplate.exchange( frontedApiUrlBase + "/Frontend/GetStatus", HttpMethod.GET, getRequestEntity(), FrontendStatus.class );
		FrontendStatus frontendStatus = responseEntity.getBody();

		return frontendStatus;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.frontend.FrontendOperations#sendMessage(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean sendMessage( String frontedApiUrlBase, String message ) {

		ResponseEntity<Bool> responseEntity = restTemplate.exchange( frontedApiUrlBase + "/Frontend/SendMessage", HttpMethod.GET, getRequestEntity(), Bool.class );
		Bool bool = responseEntity.getBody();

		return bool.getBool();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.frontend.FrontendOperations#sendAction(java.lang.String, java.lang.String, java.lang.String, int, int)
	 */
	@Override
	public boolean sendAction( String frontedApiUrlBase, String action, String file, int width, int height ) {

		LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.add( "Action", action );
		if(null != file) parameters.add( "File", file );
		if(width > 0 && height > 0){
			parameters.add( "Width", "" + width );
			parameters.add( "Height", "" + height );
		}

		ResponseEntity<Bool> responseEntity = restTemplate.exchange( buildUri( frontedApiUrlBase + "/Frontend/SendAction", parameters ), HttpMethod.GET, getRequestEntity(), Bool.class );
		Bool bool = responseEntity.getBody();

		return bool.getBool();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.services.api.frontend.FrontendOperations#getActionList(java.lang.String)
	 */
	@Override
	public FrontendActionList getActionList( String frontedApiUrlBase ) {
	
		ResponseEntity<FrontendActionList> responseEntity = restTemplate.exchange( buildUri( frontedApiUrlBase + "/Frontend/GetActionList" ), HttpMethod.GET, getRequestEntity(), FrontendActionList.class );
		FrontendActionList frontendActionList = responseEntity.getBody();
		
		return frontendActionList;
	}
	
}
