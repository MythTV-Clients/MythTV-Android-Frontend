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
import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mythtv.client.MainApplication;
import org.mythtv.service.util.FileHelper;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.guide.ProgramGuideWrapper;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGuideDownloadService extends IntentService {

	private static final String TAG = ProgramGuideDownloadService.class.getSimpleName();

	private static final Integer MAX_DAYS = 12;
	private static final Integer MAX_HOURS = 24;
	
	public static final String FILENAME_EXT = ".json";
	public static final DateTimeFormatter validDateTimeFormatter = DateTimeFormat.forPattern( "yyyy-MM-dd'T'HH:mm:ss" );
	public static final DateTimeFormatter fileDateTimeFormatter = DateTimeFormat.forPattern( "yyyy-MM-dd'T'HH-mm-ss" );
    public static final String ACTION_DOWNLOAD = "org.mythtv.background.programGuideDownload.ACTION_DOWNLOAD";
    public static final String ACTION_PROGRESS = "org.mythtv.background.programGuideDownload.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.programGuideDownload.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATE = "PROGRESS_DATE";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";

    private static ObjectMapper mapper;
    
    private MainApplication mMainApplication;
	private FileHelper mFileHelper;
	
	public ProgramGuideDownloadService() {
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

        if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		download();
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private void download() {
//		Log.v( TAG, "download : enter" );
		
		boolean newDateDownloaded = false;
		
		mMainApplication = (MainApplication) ProgramGuideDownloadService.this.getApplicationContext();

		DateTime start = new DateTime();
		start = start.withTime( 0, 0, 0, 0 );
//		Log.d( TAG, "download : start="+ dateTimeFormatter.print( start ) );
		
		int currentHour = 0;
		int currentDay = 0;
		
		File programGuideCache = mFileHelper.getProgramGuideDataDirectory();
		if( programGuideCache.exists() ) {

			for( currentDay = 0; currentDay < MAX_DAYS; currentDay++ ) {

				for( currentHour = 0; currentHour < MAX_HOURS; currentHour++ ) {

					Intent progressIntent = new Intent( ACTION_PROGRESS );

					String sStart = fileDateTimeFormatter.print( start );
					String filename = sStart + FILENAME_EXT;
					File file = new File( programGuideCache, filename );
					if( !file.exists() ) {
						DateTime end = start.withTime( start.getHourOfDay(), 59, 59, 999 );
						Log.i( TAG, "download : starting download for " + validDateTimeFormatter.print( start ) + ", end time=" + validDateTimeFormatter.print( end ) );

						ETagInfo etag = ETagInfo.createEmptyETag();
						ProgramGuideWrapper programGuide = mMainApplication.getMythServicesApi().guideOperations().getProgramGuide( start, end, 1, -1, false, etag );
						if( null != programGuide ) {

							try {
								mapper.writeValue( file, programGuide.getProgramGuide() );

								newDateDownloaded = true;

								progressIntent.putExtra( EXTRA_PROGRESS, "Completed downloading file for " + sStart );
								progressIntent.putExtra( EXTRA_PROGRESS_DATE, validDateTimeFormatter.print( start ) );
							} catch( JsonGenerationException e ) {
								Log.e( TAG, "download : JsonGenerationException - error downloading file for " + sStart, e );

								progressIntent.putExtra( EXTRA_PROGRESS_ERROR, "error downloading file for " + sStart + ": " + e.getLocalizedMessage() );
							} catch( JsonMappingException e ) {
								Log.e( TAG, "download : JsonMappingException - error downloading file for " + sStart, e );

								progressIntent.putExtra( EXTRA_PROGRESS_ERROR, "error downloading file for " + sStart + ": " + e.getLocalizedMessage() );
							} catch( IOException e ) {
								Log.e( TAG, "download : IOException - error downloading file for " + sStart, e );

								progressIntent.putExtra( EXTRA_PROGRESS_ERROR, "IOException - error downloading file for " + sStart + ": " + e.getLocalizedMessage() );
							}

							sendBroadcast( progressIntent );

						}

					}

				}

				if( start.getHourOfDay() < MAX_HOURS - 1 ) {
					start = start.plusHours( 1 );
				}
			}

			start = start.withTime( 0, 0, 0, 0 );
			start = start.plusDays( 1 );
		}
		
		if( newDateDownloaded ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Program Guide Download Service Finished" );
			sendBroadcast( completeIntent );
		}
		
//		Log.v( TAG, "download : exit" );
	}
	
}
