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
package org.mythtv.service.guide;

import java.util.Date;

import org.mythtv.service.AbstractMythtvProcessor;
import org.mythtv.services.api.guide.ProgramGuide;
import org.springframework.http.ResponseEntity;

import android.content.Context;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class GuideProcessor extends AbstractMythtvProcessor {

	protected static final String TAG = GuideProcessor.class.getSimpleName();

	public interface GuideProcessorCallback {

		void send( int resultCode );

	}

	public GuideProcessor( Context context ) {
		super( context );
		Log.v( TAG, "initialize : enter" );
		
		Log.v( TAG, "initialize : exit" );
	}

	public void getProgramGuide( GuideProcessorCallback callback ) {
		Log.v( TAG, "getGuide : enter" );

		Date start = new Date();
		Date end = new Date();
		
		ResponseEntity<ProgramGuide> entity = application.getMythServicesApi().guideOperations().getProgramGuideResponseEntity( start, end, 1, -1, Boolean.TRUE );
		if( Log.isLoggable( TAG, Log.INFO ) ) {
			Log.i( TAG, "getGuide : entity status code = " + entity.getStatusCode().toString() );
		}
		
		switch( entity.getStatusCode() ) {
			case OK :
				processProgramGuide( entity.getBody() );
				break;
			default :
				break;
		}
		
		callback.send( entity.getStatusCode().value() );
		
		Log.v( TAG, "getGuide : exit" );
	}

	// internal helpers
	
	private void processProgramGuide( ProgramGuide programGuide ) {
		Log.v( TAG, "processProgramGuide : enter" );

		Log.v( TAG, "processProgramGuide : exit" );
	}

}
