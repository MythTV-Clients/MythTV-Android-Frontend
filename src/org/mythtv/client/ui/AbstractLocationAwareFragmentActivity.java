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
import java.io.FilenameFilter;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.channel.ChannelDownloadService;
import org.mythtv.service.guide.ProgramGuideCleanupService;
import org.mythtv.service.guide.ProgramGuideDownloadService;
import org.mythtv.service.guide.ProgramGuideDownloadServiceNew;
import org.mythtv.service.util.FileHelper;
import org.mythtv.service.util.RunningServiceHelper;

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

	private LocationProfileDaoHelper mLocationProfileDaoHelper;
	private LocationProfile mLocationProfile;
	private RunningServiceHelper mRunningServiceHelper;
	
	private ChannelDownloadReceiver channelDownloadReceiver = new ChannelDownloadReceiver();
	private ProgramGuideDownloadReceiver programGuideDownloadReceiver = new ProgramGuideDownloadReceiver();
	private ProgramGuideDownloadReceiverNew programGuideDownloadReceiverNew = new ProgramGuideDownloadReceiverNew();
	private ProgramGuideCleanupReceiver programGuideCleanupReceiver = new ProgramGuideCleanupReceiver();
	
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

		mRunningServiceHelper = RunningServiceHelper.getInstance();

		mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
		
		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		Log.v( TAG, "onStart : enter" );
		super.onStart();

		IntentFilter channelDownloadFilter = new IntentFilter();
		channelDownloadFilter.addAction( ChannelDownloadService.ACTION_COMPLETE );
	    registerReceiver( channelDownloadReceiver, channelDownloadFilter );

	    IntentFilter programGuideCleanupFilter = new IntentFilter();
		programGuideCleanupFilter.addAction( ProgramGuideCleanupService.ACTION_COMPLETE );
	    registerReceiver( programGuideCleanupReceiver, programGuideCleanupFilter );
	    
		IntentFilter programGuideDownloadFilter = new IntentFilter();
		programGuideDownloadFilter.addAction( ProgramGuideDownloadService.ACTION_PROGRESS );
		programGuideDownloadFilter.addAction( ProgramGuideDownloadService.ACTION_COMPLETE );
	    registerReceiver( programGuideDownloadReceiver, programGuideDownloadFilter );
	    
		IntentFilter programGuideDownloadFilterNew = new IntentFilter();
		programGuideDownloadFilterNew.addAction( ProgramGuideDownloadServiceNew.ACTION_PROGRESS );
		programGuideDownloadFilterNew.addAction( ProgramGuideDownloadServiceNew.ACTION_COMPLETE );
	    registerReceiver( programGuideDownloadReceiverNew, programGuideDownloadFilterNew );
	    
	    Log.v( TAG, "onStart : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();

		startService( new Intent( ChannelDownloadService.ACTION_DOWNLOAD ) );
		startService( new Intent( ProgramGuideCleanupService.ACTION_CLEANUP ) );

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
		if( null != channelDownloadReceiver ) {
			try {
				unregisterReceiver( channelDownloadReceiver );
				//channelDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		if( null != programGuideCleanupReceiver ) {
			try {
				unregisterReceiver( programGuideCleanupReceiver );
				//programGuideCleanupReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		if( null != programGuideDownloadReceiver ) {
			try {
				unregisterReceiver( programGuideDownloadReceiver );
				//programGuideDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		if( null != programGuideDownloadReceiverNew ) {
			try {
				unregisterReceiver( programGuideDownloadReceiverNew );
				//programGuideDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );

//		switch( item.getItemId() ) {
//			case android.R.id.home:
//				// app icon in action bar clicked; go home
//				Intent intent = new Intent( this, LocationActivity.class );
//				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//				startActivity( intent );
//				return true;
//		}

		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	// internal helpers

	private class ChannelDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( ChannelDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiver.onReceive : progress=" + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( ChannelDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiver.onReceive : " + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_COMPLETE ) );
	        	
 //       		Toast.makeText( AbstractLocationAwareFragmentActivity.this, "Channels Loaded!", Toast.LENGTH_SHORT ).show();

//	        	if( !mRunningServiceHelper.isServiceRunning( "org.mythtv.service.guide.ProgramGuideDownloadServiceNew" ) ) {
//	    			startService( new Intent( ProgramGuideDownloadServiceNew.ACTION_DOWNLOAD ) );
//	    		}

	        }

		}
		
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

	        	if( intent.getExtras().containsKey( ProgramGuideDownloadService.EXTRA_COMPLETE_OFFLINE ) ) {
	        		Toast.makeText( AbstractLocationAwareFragmentActivity.this, "Program Guide Update failed because Master Backend is not connected!", Toast.LENGTH_SHORT ).show();
	        	}
	        }

		}
		
	}

	private class ProgramGuideDownloadReceiverNew extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( ProgramGuideDownloadServiceNew.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiverNew.onReceive : progress=" + intent.getStringExtra( ProgramGuideDownloadServiceNew.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( ProgramGuideDownloadServiceNew.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiverNew.onReceive : " + intent.getStringExtra( ProgramGuideDownloadServiceNew.EXTRA_COMPLETE ) );
	        	
	        	if( intent.getBooleanExtra( ProgramGuideDownloadServiceNew.EXTRA_COMPLETE_DOWNLOADED, Boolean.FALSE ) ) {
	        		Toast.makeText( AbstractLocationAwareFragmentActivity.this, "Program Guide updated!", Toast.LENGTH_SHORT ).show();
	        	}

	        	if( intent.getExtras().containsKey( ProgramGuideDownloadServiceNew.EXTRA_COMPLETE_OFFLINE ) ) {
	        		Toast.makeText( AbstractLocationAwareFragmentActivity.this, "Program Guide Update failed because Master Backend is not connected!", Toast.LENGTH_SHORT ).show();
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

	    		FilenameFilter filter = new FilenameFilter() {
	    		    
	    			public boolean accept( File directory, String fileName ) {
	    	            return fileName.startsWith( mLocationProfile.getHostname() + "_" ) &&
	    	            		fileName.endsWith( ProgramGuideDownloadService.FILENAME_EXT );
	    	        }
	    			
	    	    };
	    		
	        	File programGuideCache = FileHelper.getInstance().getProgramGuideDataDirectory();
	    		if( null != programGuideCache && programGuideCache.exists() ) {
		        	Log.i( TAG, "ProgramGuideCleanupReceiver.onReceive : programGuide count=" + programGuideCache.list( filter ).length );
	    			
	    			if( programGuideCache.list( filter ).length < ProgramGuideDownloadService.MAX_HOURS ) {
	    				if( !mRunningServiceHelper.isServiceRunning( "org.mythtv.service.guide.ProgramGuideDownloadService" ) ) {
		    				startService( new Intent( ProgramGuideDownloadService.ACTION_DOWNLOAD ) );
	    				}
	    			}
	    			
	    		}
	    		
	        }
	        
		}
		
	}

}
