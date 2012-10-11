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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.mythtv.service.MythtvService;
import org.mythtv.service.util.UrlUtils;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.Programs;

import android.content.Intent;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
    public static final String EXTRA_COMPLETE_COUNT = "COMPLETE_COUNT";

    private final ObjectMapper mapper = new ObjectMapper();

//    private RecordedLruMemoryCache cache;

    public RecordedCleanupService() {
		super( "ProgamGuideDownloadService" );
		
//		cache = new RecordedLruMemoryCache( this );
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
		Log.v( TAG, "cleanup : enter" );
		
		int count = 0;
		
		File programCache = mFileHelper.getProgramDataDirectory();
		if( null != programCache && programCache.exists() ) {

			File file = new File( programCache, RecordedDownloadService.RECORDED_FILE );
			if( file.exists() ) {
				try {
					InputStream is = new BufferedInputStream( new FileInputStream( file ), 8192 );
					Programs programs = mapper.readValue( is, Programs.class );

					List<String> programGroups = new ArrayList<String>();
					if( null != programs ) {
						for( Program program : programs.getPrograms() ) {

							String title = UrlUtils.encodeUrl( program.getTitle() );

							if( !programGroups.contains( title ) ) {
								programGroups.add( title );
							}
						}

						FilenameFilter filter = new FilenameFilter() {

							@Override
							public boolean accept( File dir, String filename ) {
								return filename.endsWith( ProgramGroupRecordedDownloadService.RECORDED_FILE );
							}

						};
						for( String filename : programCache.list( filter ) ) {

							File deleted = new File( programCache, filename );
							String programGroup = filename.substring( 0, filename.indexOf( ProgramGroupRecordedDownloadService.RECORDED_FILE ) );
							if( !programGroups.contains( programGroup ) ) {

								if( deleted.delete() ) {
									count++;

									Log.v( TAG, "cleanup : deleted filename=" + filename );
								}
							}
						}
					}

				} catch( JsonParseException e ) {
					Log.e( TAG, "create : JsonParseException - error opening file 'recorded'", e );
				} catch( JsonMappingException e ) {
					Log.e( TAG, "create : JsonMappingException - error opening file 'recorded'", e );
				} catch( IOException e ) {
					Log.e( TAG, "create : IOException - error opening file 'recorded'", e );
				}
				
			}
			
		}

		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE, "Program Cleanup Service Finished" );
		completeIntent.putExtra( EXTRA_COMPLETE_COUNT, count );
		sendBroadcast( completeIntent );
		
		Log.v( TAG, "cleanup : exit" );
	}
	
}
