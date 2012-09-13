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

import org.joda.time.DateTime;
import org.mythtv.client.MainApplication;
import org.mythtv.service.guide.GuideService;
import org.mythtv.service.guide.GuideServiceHelper;
import org.mythtv.service.guide.ProgramGuideDownloadService;
import org.mythtv.service.util.DateUtils;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

	private SharedPreferences mythtvPreferences;
	
	private ProgramGuideDownloadReceiver programGuideDownloaderReceiver = new ProgramGuideDownloadReceiver();
	private GuideReceiver guideReceiver;
	private NotifyReceiver notifyReceiver;
	
	private GuideServiceHelper mGuideServiceHelper;
	
	private boolean isGuideDataLoaded;
	
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
		
		resources = getResources();

		mythtvPreferences = getSharedPreferences( "MythtvPreferences", Context.MODE_PRIVATE );
		
		setupActionBar();

//		isGuideDataLoaded = mythtvPreferences.getBoolean( MainApplication.GUIDE_DATA_LOADED, false );
//
//		if( !isGuideDataLoaded ) {
//			Log.v( TAG, "onCreate : guide data not loaded, checking database" );
//
//			Cursor cursor = getContentResolver().query( ProgramConstants.CONTENT_URI, new String[] { ProgramConstants._ID }, ProgramConstants.FIELD_PROGRAM_TYPE + " = ?", new String[] { ProgramConstants.ProgramType.GUIDE.name() }, null );
//			if( cursor.getCount() == 0 ) {
//				Log.v( TAG, "onCreate : guide data not loaded" );
//			
//				isGuideDataLoaded = false;
//			} else {
//				Log.v( TAG, "onCreate : guide data loaded" );
//
//				isGuideDataLoaded = true;
//
//				SharedPreferences.Editor editor = mythtvPreferences.edit();
//				editor.putBoolean( MainApplication.GUIDE_DATA_LOADED, isGuideDataLoaded );
//				editor.commit();
//			}
//			cursor.close();
//		}
		
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
		programGuideDownloadFilter.setPriority( IntentFilter.SYSTEM_LOW_PRIORITY );
	    registerReceiver( programGuideDownloaderReceiver, programGuideDownloadFilter );
	    
		Log.v( TAG, "onStart : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();

//		notifyReceiver = new NotifyReceiver();
//		registerReceiver( notifyReceiver, new IntentFilter( GuideService.BROADCAST_ACTION ) );
//
//		mGuideServiceHelper = GuideServiceHelper.getInstance( this );
//
//		IntentFilter guideFilter = new IntentFilter( GuideServiceHelper.GUIDE_RESULT );
//		guideFilter.setPriority( IntentFilter.SYSTEM_LOW_PRIORITY );
//      guideReceiver = new GuideReceiver();
//      registerReceiver( guideReceiver, guideFilter );
        
//		isGuideDataLoaded = mythtvPreferences.getBoolean( MainApplication.GUIDE_DATA_LOADED, false );
//
//		if( !isGuideDataLoaded ) {
//			showGuideAlertDialog();
//        } else {
//        	if( !getMainApplication().isDatabaseLoading() ) {
//        		Log.v( TAG, "onResume : data is not currently loading" );
//        		
//        		long storedNext = mythtvPreferences.getLong( MainApplication.NEXT_GUIDE_DATA_LOAD, DateUtils.getYesterday().getMillis() );
//        		
//        		DateTime next = new DateTime( storedNext );
//        		
//        		DateTime now = new DateTime();
//        		
//        		if( now.isAfter( next ) ) {
//        			Log.v( TAG, "onResume : next program guide load date is passed" );
//
//        			mGuideServiceHelper.getGuideList();
//        		} else {
//        			Log.v( TAG, "onResume : next scheduled date is " + DateUtils.dateTimeFormatter.print( next ) );
//        		}
//        	}
//        }
        
		startService( new Intent( ProgramGuideDownloadService.ACTION_DOWNLOAD ) );
		
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
		if( null != programGuideDownloaderReceiver ) {
			try {
				unregisterReceiver( programGuideDownloaderReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		Log.v( TAG, "onStop : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.v( TAG, "onPause : enter" );
		super.onPause();

		// Unregister for broadcast
//		if( null != notifyReceiver ) {
//			try {
//				unregisterReceiver( notifyReceiver );
//			} catch( IllegalArgumentException e ) {
//				Log.e( TAG, e.getLocalizedMessage(), e );
//			}
//		}
//		
//		if( null != guideReceiver ) {
//			try {
//				unregisterReceiver( guideReceiver );
//			} catch( IllegalArgumentException e ) {
//				Log.e( TAG, e.getLocalizedMessage(), e );
//			}
//		}

		Log.v( TAG, "onPause : exit" );
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

	private void showGuideAlertDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( this );

		// set title
		alertDialogBuilder.setTitle( "Mythtv for Android" );

		// set dialog message
		alertDialogBuilder
		.setMessage( "Since this is the first time you have run Mythtv for Android, I need to download your program guide.  This process will take a long time as I need to download data for the next two weeks.  Subsequent updates will occur in the background, but for now, please be patient while I process data from your Mythtv backend. Your actions will be limited while downloading takes place." )
		.setCancelable( true )
		.setPositiveButton( "Close", new DialogInterface.OnClickListener() {
			public void onClick( DialogInterface dialog,int id ) {
				dialog.cancel();
				
	        	mGuideServiceHelper.getGuideList();
	        	
				SharedPreferences.Editor editor = mythtvPreferences.edit();
				editor.putBoolean( MainApplication.GUIDE_DATA_LOADED, true );
				editor.commit();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();

		alertDialog.show();
	}
	
	private class GuideReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			Log.v( TAG, "GuideReceiver.onReceive : enter" );
			
			Log.v( TAG, "GuideReceiver.onReceive : exit" );
		}
		
	}
	
    private class NotifyReceiver extends BroadcastReceiver {
    	
        @Override
        public void onReceive( Context context, Intent intent ) {
    		Log.d( TAG, "onReceive : enter" );

    		String message = intent.getExtras().getString( GuideService.BROADCAST_ACTION );
    		
    		Toast toast = Toast.makeText( context, message, Toast.LENGTH_LONG );
    		toast.show();
    		
    		Log.d( TAG, "onReceive : exit" );
        }
        
    };

	private class ProgramGuideDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			Log.v( TAG, "ProgramGuideDownloadReceiver.onReceive : enter" );
			
	        if ( intent.getAction().equals( ProgramGuideDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiver.onReceive : progress=" + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( ProgramGuideDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiver.onReceive : complete=" + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_COMPLETE ) );
	        }

	        Log.v( TAG, "ProgramGuideDownloadReceiver.onReceive : exit" );
		}
		
	}

}
