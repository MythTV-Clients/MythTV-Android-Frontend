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
package org.mythtv.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.mythtv.db.MythtvDatabaseManager;

import android.content.Intent;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class UpgradeCleanupService extends MythtvService {

	private static final String TAG = UpgradeCleanupService.class.getSimpleName();

    public static final String ACTION_PROGRAM_GUIDE_CLEANUP = "org.mythtv.background.upgradeCleanup.PROGRAM_GUIDE_CLEANUP";
    public static final String ACTION_PROGRAMS_CLEANUP = "org.mythtv.background.upgradeCleanup.PROGRAMS_CLEANUP";
    public static final String ACTION_PROGRESS = "org.mythtv.background.upgradeCleanup.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.upgradeCleanup.ACTION_COMPLETE";

    public static final String EXTRA_PROGESS = "PROGESS";
    public static final String EXTRA_COMPLETE = "COMPLETE";

    private MythtvDatabaseManager db = null;
    
	public UpgradeCleanupService() {
		super( "UpgradeCleanupService" );

		db = new MythtvDatabaseManager( this );
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );

		String result = "";
		if ( intent.getAction().equals( ACTION_PROGRAM_GUIDE_CLEANUP ) ) {
    		Log.i( TAG, "onHandleIntent : PROGRAM GUIDE CLEANUP action selected" );

    		try {
    			boolean performCleanup = db.fetchCleanupProgramGuide();
    			if( performCleanup ) {
    				result = cleanupProgramGuide();
    			}
    		} catch( IOException e ) {
    			Log.e( TAG, "onHandleIntent : error PROGRAM GUIDE CLEANUP", e );
    			
    			result = e.getLocalizedMessage();
    		}
    		
        }
		
        if ( intent.getAction().equals( ACTION_PROGRAMS_CLEANUP ) ) {
    		Log.i( TAG, "onHandleIntent : PROGRAMS CLEANUP action selected" );

    		try {
        		boolean performCleanup = db.fetchCleanupPrograms();
        		if( performCleanup ) {
        			result = cleanupPrograms();
        		}
    		} catch( IOException e ) {
    			Log.e( TAG, "onHandleIntent : error PROGRAMS CLEANUP", e );
    			
    			result = e.getLocalizedMessage();
    		}
    		
        }

		Intent completeIntent = new Intent( ACTION_COMPLETE );
		completeIntent.putExtra( EXTRA_COMPLETE, result );
		sendBroadcast( completeIntent );

		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private String cleanupProgramGuide() throws IOException {
		Log.v( TAG, "cleanupProgramGuide : enter" );

		Intent progressIntent = new Intent( ACTION_PROGRESS );
		progressIntent.putExtra( EXTRA_PROGESS, "Upgrade Program Guide Cleanup Service Started" );
		sendBroadcast( progressIntent );

		File programGuideCache = mFileHelper.getProgramGuideDataDirectory();
		if( null != programGuideCache && programGuideCache.exists() ) {
			Log.v( TAG, "cleanupProgramGuide : found program guide cache" );

			FileUtils.cleanDirectory( programGuideCache );
		}

		db.updateCleanup( "CLEANUP_PROGRAM_GUIDE" );
		
		Log.v( TAG, "cleanupProgramGuide : exit" );
		return "Upgrade Program Guide Cleanup Service Finished";
	}

	private String cleanupPrograms() throws IOException {
		Log.v( TAG, "cleanupPrograms : enter" );

		Intent progressIntent = new Intent( ACTION_PROGRESS );
		progressIntent.putExtra( EXTRA_PROGESS, "Upgrade Program Cleanup Service Started" );
		sendBroadcast( progressIntent );

		File programCache = mFileHelper.getProgramDataDirectory();
		if( null != programCache && programCache.exists() ) {
			Log.v( TAG, "cleanupPrograms : found programs cache" );

			FileUtils.cleanDirectory( programCache );
		}

		db.updateCleanup( "CLEANUP_PROGRAMS" );
		
		Log.v( TAG, "cleanupPrograms : exit" );
		return "Upgrade Programs Cleanup Service Finished";
	}
	
}
