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
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.service.MythtvService;
import org.mythtv.service.dvr.cache.RecordedLruMemoryCache;
import org.mythtv.service.util.UrlUtils;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.ProgramList;
import org.mythtv.services.api.dvr.Programs;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Intent;
import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGroupRecordedDownloadService extends MythtvService {

	private static final String TAG = ProgramGroupRecordedDownloadService.class.getSimpleName();

	public static final String RECORDED_FILE = "-recorded.json";
	
    public static final String ACTION_DOWNLOAD = "org.mythtv.background.programGroupRecordedDownload.ACTION_DOWNLOAD";
    public static final String ACTION_COMPLETE = "org.mythtv.background.programGroupRecordedDownload.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_TITLE = "PROGRESS_TITLE";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";

    private RecordedLruMemoryCache cache;
    private File programCache = null;
    
	public ProgramGroupRecordedDownloadService() {
		super( "ProgramGroupRecordedDownloadService" );
		
		cache = new RecordedLruMemoryCache( this );
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );
		super.onHandleIntent( intent );
		
		programCache = mFileHelper.getProgramDataDirectory();
		if( null == programCache || !programCache.exists() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Program Cache location can not be found" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, programCache does not exist" );
			return;
		}

		ResponseEntity<String> hostname = mMainApplication.getMythServicesApi().mythOperations().getHostName();
		if( null == hostname || "".equals( hostname ) ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend unreachable" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, Master Backend unreachable" );
			return;
		}

		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		try {
    			List<String> programGroups = parseProgramGroups();
    			download( programGroups );
			} catch( JsonGenerationException e ) {
				Log.e( TAG, "onHandleIntent : error generating json", e );
			} catch( JsonMappingException e ) {
				Log.e( TAG, "onHandleIntent : error mapping json", e );
			} catch( IOException e ) {
				Log.e( TAG, "onHandleIntent : error handling files", e );
			} finally {
    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Program Group Recorded Download Service Finished" );
    			sendBroadcast( completeIntent );
    		}

		}
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers

	private List<String> parseProgramGroups() {
		Log.v( TAG, "parseProgramGroups : enter" );
		
		List<String> programGroups = new ArrayList<String>();
		Programs programs = cache.get( RecordedDownloadService.RECORDED_FILE );
		if( null != programs ) {
			for( Program program : programs.getPrograms() ) {
				if( !programGroups.contains( program.getTitle() ) ) {
					programGroups.add( program.getTitle() );
				}
			}
		}
		
		Log.v( TAG, "parseProgramGroups : exit" );
		return programGroups;
	}
	
	private void download( List<String> programGroups ) throws JsonGenerationException, JsonMappingException, IOException {
		Log.v( TAG, "download : enter" );
		
		DateTime start = new DateTime();
		start = start.withTime( 0, 0, 0, 0 );
		
		for( String title : programGroups ) {
			title = UrlUtils.encodeUrl( title );
					
			ETagInfo etag = ETagInfo.createEmptyETag();
			ResponseEntity<ProgramList> responseEntity = mMainApplication.getMythServicesApi().dvrOperations().getFiltererRecordedList( true, 1, 999, title, null, null, etag );
			if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
						
				File existing = new File( programCache, title + RECORDED_FILE );
				if( existing.exists() ) {
					existing.delete();
				}

				ProgramList programList = responseEntity.getBody();
				mMainApplication.getObjectMapper().writeValue( new File( programCache, title + RECORDED_FILE ), programList.getPrograms() );

			}
				
		}
		
		Log.v( TAG, "download : exit" );
	}
	
}
