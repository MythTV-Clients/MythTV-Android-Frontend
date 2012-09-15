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
import java.io.IOException;

import org.joda.time.DateTime;
import org.mythtv.service.MythtvService;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.dvr.ProgramList;

import android.content.Intent;
import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Daniel Frey
 *
 */
public class RecordedDownloadService extends MythtvService {

	private static final String TAG = RecordedDownloadService.class.getSimpleName();

	public static final String RECORDED_FILE = "recorded.json";
	
    public static final String ACTION_DOWNLOAD = "org.mythtv.background.recordedDownload.ACTION_DOWNLOAD";
    public static final String ACTION_PROGRESS = "org.mythtv.background.recordedDownload.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.recordedDownload.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATE = "PROGRESS_DATE";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";

	public RecordedDownloadService() {
		super( "RecordedDownloadService" );
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
		
		DateTime start = new DateTime();
		start = start.withTime( 0, 0, 0, 0 );
//		Log.d( TAG, "download : start="+ dateTimeFormatter.print( start ) );
		
		File programCache = mFileHelper.getProgramDataDirectory();
		if( programCache.exists() ) {
			
			Intent progressIntent = new Intent( ACTION_PROGRESS );

			ETagInfo etag = ETagInfo.createEmptyETag();
			ProgramList programList = mMainApplication.getMythServicesApi().dvrOperations().getRecordedList( etag );
			
			File existing = new File( programCache, RECORDED_FILE );
			if( existing.exists() ) {
				existing.delete();
			}

			try {
				mObjectMapper.writeValue( new File( programCache, RECORDED_FILE ), programList.getPrograms() );

				newDataDownloaded = true;

			} catch( JsonGenerationException e ) {
				Log.e( TAG, "download : JsonGenerationException - error downloading file for 'recorded'", e );

				progressIntent.putExtra( EXTRA_PROGRESS_ERROR, "error downloading file for 'recorded': " + e.getLocalizedMessage() );
				sendBroadcast( progressIntent );
			} catch( JsonMappingException e ) {
				Log.e( TAG, "download : JsonGenerationException - error downloading file for 'recorded'", e );

				progressIntent.putExtra( EXTRA_PROGRESS_ERROR, "error downloading file for 'recorded': " + e.getLocalizedMessage() );
				sendBroadcast( progressIntent );
			} catch( IOException e ) {
				Log.e( TAG, "download : JsonGenerationException - error downloading file for 'recorded'", e );

				progressIntent.putExtra( EXTRA_PROGRESS_ERROR, "IOException - error downloading file for 'recorded': " + e.getLocalizedMessage() );
				sendBroadcast( progressIntent );
			}

		}
		
		if( newDataDownloaded ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Recorded Programs Download Service Finished" );
			sendBroadcast( completeIntent );
		}
		
//		Log.v( TAG, "download : exit" );
	}
	
}
