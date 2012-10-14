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
package org.mythtv.service.guide;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.service.MythtvService;

import android.content.Intent;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGuideCleanupService extends MythtvService {

	private static final String TAG = ProgramGuideCleanupService.class.getSimpleName();

    public static final String ACTION_CLEANUP = "org.mythtv.background.programGuideCleanup.ACTION_CLEANUP";
    public static final String ACTION_COMPLETE = "org.mythtv.background.programGuideCleanup.ACTION_COMPLETE";

    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_COUNT = "COMPLETE_COUNT";

	public ProgramGuideCleanupService() {
		super( "ProgamGuideDownloadService" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );

        if ( intent.getAction().equals( ACTION_CLEANUP ) ) {
    		Log.i( TAG, "onHandleIntent : CLEANUP action selected" );

    		cleanup();
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private void cleanup() {
		Log.v( TAG, "cleanup : enter" );
		
		int count = 0;
		
		DateTime today = new DateTime();
		today = today.minusDays( 1 );
		today = today.withTime( 23, 59, 59, 999 );
		Log.v( TAG, "cleanup : today=" + today.toString() );
		
		File programGuideCache = mFileHelper.getProgramGuideDataDirectory();
		if( null != programGuideCache && programGuideCache.exists() ) {
			Log.v( TAG, "cleanup : found program guide cache" );
			
			List<String> filenames = Arrays.asList( programGuideCache.list() );
			Collections.sort( filenames );
			for( String filename : filenames ) {
				DateTime file = new DateTime( filename.substring( 0, filename.indexOf( 'T' ) ) );
				
				if( file.isBefore( today ) ) {
					File deleted = new File( programGuideCache, filename );
					
					if( deleted.delete() ) {
						Log.v( TAG, "cleanup : deleted filename '" + filename + "'" );
						
						count++;
					}
				}
			}
		}

		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE, "Program Guide Cleanup Service Finished" );
		completeIntent.putExtra( EXTRA_COMPLETE_COUNT, count );
		sendBroadcast( completeIntent );
		
		Log.v( TAG, "cleanup : exit" );
	}
	
}
