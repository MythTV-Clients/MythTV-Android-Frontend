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
package org.mythtv.services.api;

import java.net.URI;
import java.util.Collections;

import org.springframework.http.ContentCodingType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.social.support.URIBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import android.util.Log;

/**
 * @author Daniel Frey
 * 
 */
public abstract class AbstractOperations {
	
	private static final String TAG = AbstractOperations.class.getSimpleName();
	
	private final String apiUrlBase;

	private HttpEntity<?> requestEntity;
	
	/**
	 * @param apiUrlBase
	 */
	public AbstractOperations( String apiUrlBase ) {
		this.apiUrlBase = apiUrlBase;
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept( Collections.singletonList( MediaType.APPLICATION_JSON ) );
		requestHeaders.setAcceptEncoding( Collections.singletonList( ContentCodingType.GZIP ) );
		
		requestEntity = new HttpEntity<Object>( requestHeaders );
	}

	/**
	 * @param path
	 * @return
	 */
	protected URI buildUri( String path ) {
		Log.i( TAG, buildUri( path, EMPTY_PARAMETERS ).toString() );
		
		return buildUri( path, EMPTY_PARAMETERS );
	}

	/**
	 * @param path
	 * @param parameterName
	 * @param parameterValue
	 * @return
	 */
	protected URI buildUri( String path, String parameterName, String parameterValue ) {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.set( parameterName, parameterValue );

		return buildUri( path, parameters );
	}

	/**
	 * @param path
	 * @param parameters
	 * @return
	 */
	protected URI buildUri( String path, MultiValueMap<String, String> parameters ) {
		Log.v( TAG, "URI : " + URIBuilder.fromUri( getApiUrlBase() + path ).queryParams( parameters ).build() );
		
		return URIBuilder.fromUri( getApiUrlBase() + path ).queryParams( parameters ).build();
	}

	/**
	 * @return
	 */
	protected String getApiUrlBase() {
		return apiUrlBase;
	}

	/**
	 * @return the requestEntity
	 */
	protected HttpEntity<?> getRequestEntity() {
		return requestEntity;
	}

	private static final LinkedMultiValueMap<String, String> EMPTY_PARAMETERS = new LinkedMultiValueMap<String, String>();

}