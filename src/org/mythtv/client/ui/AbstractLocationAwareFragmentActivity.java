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
package org.mythtv.client.ui;

import java.io.File;

import org.joda.time.DateTime;
import org.mythtv.service.dvr.RecordedCleanupService;
import org.mythtv.service.dvr.UpcomingDownloadService;
import org.mythtv.service.guide.ProgramGuideCleanupService;
import org.mythtv.service.guide.ProgramGuideDownloadService;
import org.mythtv.service.util.FileHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * @author Daniel Frey
 * 
 */
public abstract class AbstractLocationAwareFragmentActivity extends AbstractMythtvFragmentActivity {

	protected static final String TAG = AbstractLocationAwareFragmentActivity.class.getSimpleName();

	private FileHelper mFileHelper;

	private ProgramGuideDownloadReceiver programGuideDownloadReceiver = new ProgramGuideDownloadReceiver();
	private ProgramGuideCleanupReceiver programGuideCleanupReceiver = new ProgramGuideCleanupReceiver();
	private RecordedCleanupReceiver recordedCleanupReceiver = new RecordedCleanupReceiver();
	private UpcomingDownloadReceiver upcomingDownloadReceiver = new UpcomingDownloadReceiver();
	
	// ***************************************
	// FragmentActivity methods
	// ***************************************
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );
		
		mFileHelper = new FileHelper( this );
		
		resources = getResources();

		setupActionBar();

		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		Log.v( TAG, "onStart : enter" );
		super.onStart();

		IntentFilter programGuideDownloadFilter = new IntentFilter();
		programGuideDownloadFilter.addAction( ProgramGuideDownloadService.ACTION_PROGRESS );
		programGuideDownloadFilter.addAction( ProgramGuideDownloadService.ACTION_COMPLETE );
	    registerReceiver( programGuideDownloadReceiver, programGuideDownloadFilter );
	    
		IntentFilter programGuideCleanupFilter = new IntentFilter();
		programGuideCleanupFilter.addAction( ProgramGuideCleanupService.ACTION_COMPLETE );
	    registerReceiver( programGuideCleanupReceiver, programGuideCleanupFilter );
	    
		IntentFilter recordedCleanupFilter = new IntentFilter();
		recordedCleanupFilter.addAction( RecordedCleanupService.ACTION_COMPLETE );
	    registerReceiver( recordedCleanupReceiver, recordedCleanupFilter );
	    
		IntentFilter upcomingDownloadFilter = new IntentFilter();
		upcomingDownloadFilter.addAction( UpcomingDownloadService.ACTION_PROGRESS );
		upcomingDownloadFilter.addAction( UpcomingDownloadService.ACTION_COMPLETE );
	    registerReceiver( upcomingDownloadReceiver, upcomingDownloadFilter );

	    Log.v( TAG, "onStart : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();

		startService( new Intent( ProgramGuideCleanupService.ACTION_CLEANUP ) );
		startService( new Intent( RecordedCleanupService.ACTION_CLEANUP ) );
		startService( new Intent( ProgramGuideDownloadService.ACTION_DOWNLOAD ) );
		
		File programCache = mFileHelper.getProgramDataDirectory();
		if( programCache.exists() ) {
			
			DateTime today = new DateTime().withTime( 0, 0, 0, 0 );

			File upcoming = new File( programCache, UpcomingDownloadService.UPCOMING_FILE );
			if( upcoming.exists() ) {

				DateTime lastModified = new DateTime( upcoming.lastModified() );
				
				if( lastModified.isBefore( today ) ) {
					startService( new Intent( UpcomingDownloadService.ACTION_DOWNLOAD ) );
				} else {
					Log.i( TAG, "onResume : not time to update 'upcoming' episodes" );
				}
			} else {
				startService( new Intent( UpcomingDownloadService.ACTION_DOWNLOAD ) );
			}
			
		}
		
		Log.v( TAG, "onResume : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStop()
	 */
	@Override
	protected void onStop() {
		Log.v( TAG, "onStop : enter" );
		super.onStop();

		// Unregister for broadcast
		if( null != programGuideDownloadReceiver ) {
			try {
				unregisterReceiver( programGuideDownloadReceiver );
				programGuideDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		if( null != programGuideCleanupReceiver ) {
			try {
				unregisterReceiver( programGuideCleanupReceiver );
				programGuideCleanupReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		if( null != recordedCleanupReceiver ) {
			try {
				unregisterReceiver( recordedCleanupReceiver );
				recordedCleanupReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		if( null != upcomingDownloadReceiver ) {
			try {
				unregisterReceiver( upcomingDownloadReceiver );
				upcomingDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		Log.v( TAG, "onStop : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );

		switch( item.getItemId() ) {
			case android.R.id.home:
				// app icon in action bar clicked; go home
				Intent intent = new Intent( this, LocationActivity.class );
				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
				startActivity( intent );
				return true;
		}

		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	// internal helpers

	private class ProgramGuideDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( ProgramGuideDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiver.onReceive : progress=" + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( ProgramGuideDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiver.onReceive : " + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_COMPLETE ) );
	        	
	        	if( intent.getBooleanExtra( ProgramGuideDownloadService.EXTRA_COMPLETE_DOWNLOADED, Boolean.FALSE ) ) {
	        		Toast.makeText( AbstractLocationAwareFragmentActivity.this, "Program Guide updated!", Toast.LENGTH_SHORT ).show();
	        	}
	        }

		}
		
	}

	private class ProgramGuideCleanupReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( ProgramGuideCleanupService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "ProgramGuideCleanupReceiver.onReceive : " + intent.getStringExtra( ProgramGuideCleanupService.EXTRA_COMPLETE ) );
	        	Log.i( TAG, "ProgramGuideCleanupReceiver.onReceive : " + intent.getIntExtra( ProgramGuideCleanupService.EXTRA_COMPLETE_COUNT, 0 ) + " files cleaned up" );
	        }
	        
		}
		
	}

	private class RecordedCleanupReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( RecordedCleanupService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "RecordedCleanupReceiver.onReceive : " + intent.getStringExtra( RecordedCleanupService.EXTRA_COMPLETE ) );
	        	Log.i( TAG, "RecordedCleanupReceiver.onReceive : " + intent.getIntExtra( RecordedCleanupService.EXTRA_COMPLETE_COUNT, 0 ) + " files cleaned up" );
	        }
	        
		}
		
	}

	private class UpcomingDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( UpcomingDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "UpcomingDownloadReceiver.onReceive : " + intent.getStringExtra( UpcomingDownloadService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( UpcomingDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "UpcomingDownloadReceiver.onReceive : " + intent.getStringExtra( UpcomingDownloadService.EXTRA_COMPLETE ) );
	        }
	        
		}
		
	}

}
