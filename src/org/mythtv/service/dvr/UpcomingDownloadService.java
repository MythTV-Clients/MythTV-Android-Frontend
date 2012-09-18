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
package org.mythtv.service.dvr;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.mythtv.service.MythtvService;
import org.mythtv.service.dvr.cache.UpcomingLruMemoryCache;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.ProgramList;
import org.mythtv.services.api.dvr.Programs;

import android.content.Intent;
import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Daniel Frey
 *
 */
public class UpcomingDownloadService extends MythtvService {

	private static final String TAG = UpcomingDownloadService.class.getSimpleName();

	public static final String UPCOMING_FILE = "upcoming.json";
	public static final String UPCOMING_FILE_EXT = "-upcoming.json";
	
    public static final String ACTION_DOWNLOAD = "org.mythtv.background.upcomingDownload.ACTION_DOWNLOAD";
    public static final String ACTION_PROGRESS = "org.mythtv.background.upcomingDownload.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.upcomingDownload.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATE = "PROGRESS_DATE";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";

	private UpcomingLruMemoryCache cache;

	public UpcomingDownloadService() {
		super( "UpcomingDownloadService" );

		cache = new UpcomingLruMemoryCache( this );
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );
		super.onHandleIntent( intent );
		
        if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		download();
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private void download() {
//		Log.v( TAG, "download : enter" );
		
		boolean newDataDownloaded = false;
		
		File programCache = mFileHelper.getProgramDataDirectory();
		if( programCache.exists() ) {
			
			Intent progressIntent = new Intent( ACTION_PROGRESS );

			ETagInfo etag = ETagInfo.createEmptyETag();
			ProgramList programList = mMainApplication.getMythServicesApi().dvrOperations().getUpcomingList( -1, -1, false, etag );
			
			File existing = new File( programCache, UPCOMING_FILE );
			if( existing.exists() ) {
				existing.delete();
				
				FilenameFilter filter = new FilenameFilter() {

					@Override
					public boolean accept( File dir, String filename ) {
						return filename.endsWith( UPCOMING_FILE_EXT );
					}
					
				};
				for( String filename : programCache.list( filter ) ) {
//					Log.v( TAG, "download : filename=" + filename );
					
					File deleted = new File( programCache, filename );
					if( deleted.delete() ) {
//						Log.v( TAG, "download : deleted filename=" + filename );
					}
				}
			}

			try {
				mObjectMapper.writeValue( new File( programCache, UPCOMING_FILE ), programList.getPrograms() );

				Map<DateTime, Programs> upcomingDates = new TreeMap<DateTime, Programs>();
				for( Program program : programList.getPrograms().getPrograms() ) {
//					Log.v( TAG, "download : upcoming program iteration" );
					
					program.setStartTime( program.getStartTime().withZone( zone ) );
					program.setEndTime( program.getEndTime().withZone( zone ) );
					
					DateTime date = program.getStartTime().withTime( 0, 0, 0, 0 ).withZone( zone );
					if( upcomingDates.containsKey( date ) ) {
//						Log.v( TAG, "download : adding program to EXISTING " + DateUtils.dateFormatter.print( date ) );
												
						upcomingDates.get( date ).getPrograms().add( program );
					} else {
//						Log.v( TAG, "download : adding program to NEW " + DateUtils.dateFormatter.print( date ) );
						Programs datePrograms = new Programs();
						
						List<Program> dateProgramList = new ArrayList<Program>();
						dateProgramList.add( program );
						datePrograms.setPrograms( dateProgramList );
						
						upcomingDates.put( date, datePrograms );
					}
					
				}
				
				for( DateTime date : upcomingDates.keySet() ) {
					Programs upcomingPrograms = upcomingDates.get( date );
					
					String key = DateUtils.dateFormatter.print( date );
					
//					Log.v( TAG, "download : writing file " + key + UPCOMING_FILE_EXT );
					mObjectMapper.writeValue( new File( programCache, key + UPCOMING_FILE_EXT ), upcomingPrograms );

					synchronized( cache ) {
						cache.remove( key );
						cache.put( key, upcomingPrograms );
					}
					
				}
				
				newDataDownloaded = true;

			} catch( JsonGenerationException e ) {
				Log.e( TAG, "download : JsonGenerationException - error downloading file for 'upcoming'", e );

				progressIntent.putExtra( EXTRA_PROGRESS_ERROR, "error downloading file for 'upcoming': " + e.getLocalizedMessage() );
				sendBroadcast( progressIntent );
			} catch( JsonMappingException e ) {
				Log.e( TAG, "download : JsonGenerationException - error downloading file for 'upcoming'", e );

				progressIntent.putExtra( EXTRA_PROGRESS_ERROR, "error downloading file for 'upcoming': " + e.getLocalizedMessage() );
				sendBroadcast( progressIntent );
			} catch( IOException e ) {
				Log.e( TAG, "download : JsonGenerationException - error downloading file for 'upcoming'", e );

				progressIntent.putExtra( EXTRA_PROGRESS_ERROR, "IOException - error downloading file for 'upcoming': " + e.getLocalizedMessage() );
				sendBroadcast( progressIntent );
			}

		}
		
		if( newDataDownloaded ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Upcoming Programs Download Service Finished" );
			sendBroadcast( completeIntent );
		}
		
//		Log.v( TAG, "download : exit" );
	}
	
}
