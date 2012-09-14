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

import java.io.File;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mythtv.service.util.FileHelper;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGuideCleanupService extends IntentService {

	private static final String TAG = ProgramGuideCleanupService.class.getSimpleName();

	public static final DateTimeFormatter fileDateTimeFormatter = DateTimeFormat.forPattern( "yyyy-MM-dd'T'HH-mm-ss" );
    public static final String ACTION_CLEANUP = "org.mythtv.background.programGuideCleanup.ACTION_CLEANUP";
    public static final String ACTION_COMPLETE = "org.mythtv.background.programGuideCleanup.ACTION_COMPLETE";

    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_COUNT = "COMPLETE_COUNT";

    private static ObjectMapper mapper;
    
	private FileHelper mFileHelper;
	
	public ProgramGuideCleanupService() {
		super( "ProgamGuideDownloadService" );
		
		mFileHelper = new FileHelper( this );
		
		mapper = new ObjectMapper();
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

    		cleanup();
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private void cleanup() {
//		Log.v( TAG, "cleanup : enter" );
		
		int count = 0;
		
		DateTime today = new DateTime();
		today = today.minusDays( 1 );
		today = today.withTime( 23, 59, 59, 999 );

		File programGuideCache = mFileHelper.getProgramGuideDataDirectory();
		if( programGuideCache.exists() ) {

			for( String filename : programGuideCache.list() ) {
				Log.v( TAG, "cleanup : filename=" + filename );
				DateTime file = new DateTime( filename.subSequence( 0, filename.indexOf( 'T' ) ) );
				
				if( file.isBefore( today ) ) {
					File deleted = new File( programGuideCache, filename );
					
					if( deleted.delete() ) {
						count++;
					}
				} else {
					break;
				}
			}
		}

		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE, "Program Guide Cleanup Service Finished" );
		completeIntent.putExtra( EXTRA_COMPLETE_COUNT, count );
		sendBroadcast( completeIntent );
		
//		Log.v( TAG, "cleanup : exit" );
	}
	
}
