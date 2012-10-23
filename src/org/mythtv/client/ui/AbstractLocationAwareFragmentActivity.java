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
package org.mythtv.client.ui;

import java.io.File;

import org.joda.time.DateTime;
import org.mythtv.service.dvr.RecordedDownloadService;
import org.mythtv.service.dvr.UpcomingDownloadService;
import org.mythtv.service.guide.ProgramGuideCleanupService;
import org.mythtv.service.guide.ProgramGuideDownloadService;
import org.mythtv.service.util.FileHelper;
import org.mythtv.service.util.RunningServiceHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
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
	private RunningServiceHelper mRunningServiceHelper;
	
	private ProgramGuideDownloadReceiver programGuideDownloadReceiver = new ProgramGuideDownloadReceiver();
	private ProgramGuideCleanupReceiver programGuideCleanupReceiver = new ProgramGuideCleanupReceiver();
	private RecordedDownloadReceiver recordedDownloadReceiver = new RecordedDownloadReceiver();
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
		mRunningServiceHelper = new RunningServiceHelper( this );
		
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

		IntentFilter programGuideCleanupFilter = new IntentFilter();
		programGuideCleanupFilter.addAction( ProgramGuideCleanupService.ACTION_COMPLETE );
	    registerReceiver( programGuideCleanupReceiver, programGuideCleanupFilter );
	    
		IntentFilter programGuideDownloadFilter = new IntentFilter();
		programGuideDownloadFilter.addAction( ProgramGuideDownloadService.ACTION_PROGRESS );
		programGuideDownloadFilter.addAction( ProgramGuideDownloadService.ACTION_COMPLETE );
	    registerReceiver( programGuideDownloadReceiver, programGuideDownloadFilter );
	    
		IntentFilter recordedDownloadFilter = new IntentFilter( RecordedDownloadService.ACTION_DOWNLOAD );
		recordedDownloadFilter.addAction( RecordedDownloadService.ACTION_PROGRESS );
		recordedDownloadFilter.addAction( RecordedDownloadService.ACTION_COMPLETE );
        registerReceiver( recordedDownloadReceiver, recordedDownloadFilter );

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

		new CheckMythtvBackendConnectionTask().execute();
		
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
		if( null != programGuideCleanupReceiver ) {
			try {
				unregisterReceiver( programGuideCleanupReceiver );
				programGuideCleanupReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		if( null != programGuideDownloadReceiver ) {
			try {
				unregisterReceiver( programGuideDownloadReceiver );
				programGuideDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		if( null != recordedDownloadReceiver ) {
			try {
				unregisterReceiver( recordedDownloadReceiver );
				recordedDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
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

	private void startServices() {
		Log.v( TAG, "startServices : enter" );

		startService( new Intent( ProgramGuideCleanupService.ACTION_CLEANUP ) );
		
		File upcoming = new File( mFileHelper.getProgramUpcomingDataDirectory(), UpcomingDownloadService.UPCOMING_FILE );
		if( upcoming.exists() ) {

			DateTime today = new DateTime().withTime( 0, 0, 0, 0 );
			DateTime lastModified = new DateTime( upcoming.lastModified() );
				
			if( lastModified.isBefore( today ) ) {
				if( !mRunningServiceHelper.isServiceRunning( "org.mythtv.service.dvr.UpcomingDownloadService" ) ) {
					startService( new Intent( UpcomingDownloadService.ACTION_DOWNLOAD ) );
				}
			} else {
				Log.i( TAG, "onResume : not time to update 'upcoming' episodes" );
			}
		} else {
			if( !mRunningServiceHelper.isServiceRunning( "org.mythtv.service.dvr.UpcomingDownloadService" ) ) {
				startService( new Intent( UpcomingDownloadService.ACTION_DOWNLOAD ) );
			}
		}
			
		File recorded = new File( mFileHelper.getProgramRecordedDataDirectory(), RecordedDownloadService.RECORDED_FILE );
		if( recorded.exists() ) {

			DateTime lastHour = new DateTime().minusHours( 1 );
			DateTime lastModified = new DateTime( recorded.lastModified() );
				
			if( lastModified.isBefore( lastHour ) ) {
				if( !mRunningServiceHelper.isServiceRunning( "org.mythtv.service.dvr.RecordedDownloadService" ) ) {
					startService( new Intent( RecordedDownloadService.ACTION_DOWNLOAD ) );
				}
			} else {
				Log.i( TAG, "onResume : not time to update 'recorded' episodes" );
			}
		} else {
			if( !mRunningServiceHelper.isServiceRunning( "org.mythtv.service.dvr.RecordedDownloadService" ) ) {
				startService( new Intent( RecordedDownloadService.ACTION_DOWNLOAD ) );
			}
		}

		Log.v( TAG, "startServices : exit" );
	}
	
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

	        	File programGuideCache = mFileHelper.getProgramGuideDataDirectory();
	    		if( null != programGuideCache && programGuideCache.exists() ) {
		        	Log.i( TAG, "ProgramGuideCleanupReceiver.onReceive : programGuide count=" + programGuideCache.list().length );
	    			
	    			if( programGuideCache.list().length < ProgramGuideDownloadService.MAX_HOURS ) {
	    				if( !mRunningServiceHelper.isServiceRunning( "org.mythtv.service.guide.ProgramGuideDownloadService" ) ) {
		    				startService( new Intent( ProgramGuideDownloadService.ACTION_DOWNLOAD ) );
	    				}
	    			}
	    			
	    		}
	    		
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
	        	
	        	if( intent.getExtras().containsKey( UpcomingDownloadService.EXTRA_COMPLETE_UPTODATE ) ) {
	        		Toast.makeText( AbstractLocationAwareFragmentActivity.this, "Upcoming Programs are up to date!", Toast.LENGTH_SHORT ).show();
	        	} else {
	        		Toast.makeText( AbstractLocationAwareFragmentActivity.this, "Upcoming Programs updated!", Toast.LENGTH_SHORT ).show();
	        	}

	        }
	        
		}
		
	}

	private class RecordedDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : enter" );
			
	        if ( intent.getAction().equals( RecordedDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : progress=" + intent.getStringExtra( RecordedDownloadService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( RecordedDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : complete=" + intent.getStringExtra( RecordedDownloadService.EXTRA_COMPLETE ) );
	        	
	        	if( intent.getExtras().containsKey( RecordedDownloadService.EXTRA_COMPLETE_UPTODATE ) ) {
	        		Toast.makeText( AbstractLocationAwareFragmentActivity.this, "Recorded Program are up to date!", Toast.LENGTH_SHORT ).show();
	        	} else {
	        		Toast.makeText( AbstractLocationAwareFragmentActivity.this, "Recorded Programs updated!", Toast.LENGTH_SHORT ).show();
	        	}
	        }

        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : exit" );
		}
		
	}

	private class CheckMythtvBackendConnectionTask extends AsyncTask<Void, Void, ResponseEntity<String>> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected ResponseEntity<String> doInBackground( Void... args ) {
			Log.v( TAG, "CheckMythtvBackendConnectionTask.doInBackground : enter" );
			
			try {
				Log.v( TAG, "CheckMythtvBackendConnectionTask.doInBackground : exit" );
				return getMainApplication().getMythServicesApi().mythOperations().getHostName();
			} catch( Exception e ) {
				Log.w( TAG, "CheckMythtvBackendConnectionTask.doInBackground : error connecting to backend", e );
				
				return null;
			}
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute( ResponseEntity<String> result ) {
			Log.v( TAG, "CheckMythtvBackendConnectionTask.onPostExecute : enter" );

			if( null != result ) {
				
				if( result.getStatusCode().equals( HttpStatus.OK ) ) {
					
					String hostname = result.getBody();
					if( null != hostname && !"".equals( hostname ) ) {

						startServices();
						
					}
			
				}
				
			}
			
			Log.v( TAG, "CheckMythtvBackendConnectionTask.onPostExecute : exit" );
		}
		
	}

}
