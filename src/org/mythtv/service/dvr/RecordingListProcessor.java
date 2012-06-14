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

package org.mythtv.service.dvr;

import java.util.List;

import org.mythtv.client.MainApplication;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.ProgramList;
import org.springframework.http.ResponseEntity;

import android.content.Context;
import android.util.Log;


/**
 * @author Daniel Frey
 *
 */
public class RecordingListProcessor {

	protected static final String TAG = RecordingListProcessor.class.getSimpleName();

	private MainApplication application;
	private Context mContext;

	public interface RecordingListProcessorCallback {

		void send( int resultCode );

	}

	public RecordingListProcessor( Context context ) {
		Log.v( TAG, "initialize : enter" );
		
		mContext = context;
		application = (MainApplication) context.getApplicationContext();
		
		Log.v( TAG, "initialize : exit" );
	}

	public void getRecordedList( RecordingListProcessorCallback callback ) {
		Log.d( TAG, "getRecordedList : enter" );

		ResponseEntity<ProgramList> entity = application.getMythServicesApi().dvrOperations().getRecordedListResponseEntity();
		Log.d( TAG, "getRecordedList : entity status code = " + entity.getStatusCode().toString() );
		
		updateContentProvider( entity.getBody() );
		
		callback.send( entity.getStatusCode().value() );
		
		Log.d( TAG, "getRecordedList : exit" );
	}

	// internal helpers
	
	private void updateContentProvider( ProgramList programList ) {
		Log.v( TAG, "updateContentProvider : enter" );

		for( Program program : programList.getPrograms().getPrograms() ) {
			Log.v( TAG, "updateContentProvider : program=" + program.toString() );
		}
		
		Log.v( TAG, "updateContentProvider : exit" );
	}

}
