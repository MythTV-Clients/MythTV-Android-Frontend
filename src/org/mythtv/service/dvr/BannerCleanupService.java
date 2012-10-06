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

import org.mythtv.service.MythtvService;

import android.content.Intent;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class BannerCleanupService extends MythtvService {

	private static final String TAG = BannerCleanupService.class.getSimpleName();

    public static final String ACTION_CLEANUP = "org.mythtv.background.bannerCleanup.ACTION_CLEANUP";
    public static final String ACTION_COMPLETE = "org.mythtv.background.bannerCleanup.ACTION_COMPLETE";

    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_COUNT = "COMPLETE_COUNT";

    public BannerCleanupService() {
		super( "BannerCleanupService" );
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
		
		File imageCache = mFileHelper.getProgramImagesDataDirectory();
		if( imageCache.exists() ) {

			FilenameFilter filter = new FilenameFilter() {

				@Override
				public boolean accept( File dir, String filename ) {
					return filename.endsWith( BannerDownloadService.BANNER_FILE_NA_EXT );
				}

			};
			for( String filename : imageCache.list( filter ) ) {
				Log.v( TAG, "cleanup : filename=" + filename );

				File deleted = new File( imageCache, filename );
				if( deleted.delete() ) {
					count++;

				}
		
			}

		}

		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE, "Banner Cleanup Service Finished" );
		completeIntent.putExtra( EXTRA_COMPLETE_COUNT, count );
		sendBroadcast( completeIntent );
		
		Log.v( TAG, "cleanup : exit" );
	}
	
}
