package org.mythtv.services.api;

import java.net.URI;

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

	public AbstractOperations( String apiUrlBase ) {
		this.apiUrlBase = apiUrlBase;
	}

	protected URI buildUri( String path ) {
		Log.i( TAG, buildUri( path, EMPTY_PARAMETERS ).toString() );
		
		return buildUri( path, EMPTY_PARAMETERS );
	}

	protected URI buildUri( String path, String parameterName, String parameterValue ) {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		parameters.set( parameterName, parameterValue );

		return buildUri( path, parameters );
	}

	protected URI buildUri( String path, MultiValueMap<String, String> parameters ) {
		return URIBuilder.fromUri( getApiUrlBase() + path ).queryParams( parameters ).build();
	}

	protected String getApiUrlBase() {
		return apiUrlBase;
	}

	private static final LinkedMultiValueMap<String, String> EMPTY_PARAMETERS = new LinkedMultiValueMap<String, String>();

}