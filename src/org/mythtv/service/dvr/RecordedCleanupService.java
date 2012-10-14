/**
 * This file is part of MythTV Android Frontend
 *
 * MythTV Android Frontend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MythTV Android Frontend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MythTV Android Frontend.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This software can be found at <https://github.com/MythTV-Clients/MythTV-Android-Frontend/>
 */
package org.mythtv.service.dvr;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.mythtv.service.MythtvService;

import android.content.Intent;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * @author Daniel Frey
 *
 */
public class RecordedCleanupService extends MythtvService {

	private static final String TAG = RecordedCleanupService.class.getSimpleName();

    public static final String ACTION_CLEANUP = "org.mythtv.background.recordedCleanup.ACTION_CLEANUP";
    public static final String ACTION_COMPLETE = "org.mythtv.background.recordedCleanup.ACTION_COMPLETE";

    public static final String EXTRA_COMPLETE = "COMPLETE";

    private final ObjectMapper mapper = new ObjectMapper();

    public RecordedCleanupService() {
		super( "RecordedCleanupService" );
		
		mapper.registerModule( new JodaModule() );

	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );

        if ( intent.getAction().equals( ACTION_CLEANUP ) ) {
    		Log.i( TAG, "onHandleIntent : CLEANUP action selected" );

    		try {
				cleanup();
			} catch( IOException e ) {
				Log.e( TAG, "onHandleIntent : error", e );
			}
        }
		
		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE, "Program Cleanup Service Finished" );
		sendBroadcast( completeIntent );
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private void cleanup() throws IOException {
		Log.v( TAG, "cleanup : enter" );
		
		File recordedDirectory = mFileHelper.getProgramRecordedDataDirectory();
		if( null != recordedDirectory && recordedDirectory.exists() ) {

			FileUtils.cleanDirectory( recordedDirectory );

		}
		
		Log.v( TAG, "cleanup : exit" );
	}
	
}
