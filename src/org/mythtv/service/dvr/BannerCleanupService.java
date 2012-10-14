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

    private File programGroupsDirectory = null;

    public BannerCleanupService() {
		super( "BannerCleanupService" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );

		programGroupsDirectory = mFileHelper.getProgramGroupsDataDirectory();
		if( null == programGroupsDirectory || !programGroupsDirectory.exists() ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Program Groups location can not be found" );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, programGroupsDirectory does not exist" );
			return;
		}

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
		for( String programGroup : programGroupsDirectory.list() ) {
			File programGroupDirectory = mFileHelper.getProgramGroupDirectory( programGroup );
			if( programGroupDirectory.exists() ) {

				File deleted = new File( programGroupDirectory, BannerDownloadService.BANNER_FILE_NA );
				if( deleted.delete() ) {
					Log.v( TAG, "cleanup : deleted '" + BannerDownloadService.BANNER_FILE_NA + "' in program group '" + programGroup + "'" );
					
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
