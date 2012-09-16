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
package org.mythtv.client.ui.dvr;

	
// Enable this code to use vitamio: http://vov.io/vitamio/
// Section 1 of 4
//import io.vov.vitamio.widget.MediaController;
//import io.vov.vitamio.widget.VideoView;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.preferences.PlaybackProfile;
import org.mythtv.service.dvr.cache.ProgramGroupLruMemoryCache;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.content.LiveStreamInfo;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.Programs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * @author John Baab
 * 
 */
public class VideoActivity extends AbstractDvrActivity {

	private static final String TAG = VideoActivity.class.getSimpleName();

	public static final String EXTRA_PROGRAM_CHANNEL_ID = "org.mythtv.client.ui.dvr.programGroup.EXTRA_PROGRAM_CHANNEL_ID";
	public static final String EXTRA_PROGRAM_START_TIME = "org.mythtv.client.ui.dvr.programGroup.EXTRA_PROGRAM_START_TIME";
	public static final String EXTRA_PROGRAM_CLEANED_TITLE = "org.mythtv.client.ui.dvr.programGroup.EXTRA_PROGRAM_CLEANED_TITLE";
	
	private LiveStreamInfo info = null;
	private ProgressDialog progressDialog;
	private Boolean firstrun = true;
	private PlaybackProfile selectedPlaybackProfile;

	private ProgramGroupLruMemoryCache cache;

	// ***************************************
	// Activity methods
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

	    setContentView( R.layout.activity_video );
	    
	    setupActionBar();
	    
		cache = new ProgramGroupLruMemoryCache( this );

		progressDialog = ProgressDialog.show( this, "Please wait...", "Retrieving video...", true, true );

	    String channelId = getIntent().getStringExtra( EXTRA_PROGRAM_CHANNEL_ID);
	    String startTime = getIntent().getStringExtra( EXTRA_PROGRAM_START_TIME );
	    String cleaned = getIntent().getStringExtra( EXTRA_PROGRAM_CLEANED_TITLE );
	    
		Log.v( TAG, "onCreate : channelId=" + channelId + ", startTime=" + startTime + ", cleaned=" + cleaned );
	    
	    Program selected = null;
		Programs programs = cache.get( cleaned );
	    for( Program program : programs.getPrograms() ) {
			Log.v( TAG, "onCreate : program iteration" );

			Log.v( TAG, "onCreate : program.channelId=" + program.getChannelInfo().getChannelId() + ", startTime=" + program.getStartTime() );
			if( channelId.equals( program.getChannelInfo().getChannelId() ) && startTime.equals( DateUtils.dateTimeFormatter.print( program.getStartTime() ) ) ) {
	    		selected = program;
	    		
				Log.v( TAG, "onCreate : program selected" );
	    		break;
	    	}
	    }
	    
	    if( null != selected ) {
			Log.v( TAG, "onCreate : filename=" + selected.getFilename() + ", hostname=" + selected.getHostname() );

	    	new CreateStreamTask().execute( selected.getFilename(), selected.getHostname() );
	    }
	    
		Log.v( TAG, "onCreate : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();
		
//		if( !firstrun ) {
//			Log.v( TAG, "onResume : resuming after video playback started" );
//			
//			finish();
//		} else {
//			firstrun = false;
//		}
		
		Log.v( TAG, "onResume : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		Log.v( TAG, "onDestroy : enter" );
        super.onDestroy();

        new RemoveStreamTask().execute();
        
        Log.v( TAG, "onDestroy : exit" );
    }

	// internal helpers
	
	private void startVideo() {
		Log.v( TAG, "Starting Video" );
		
		String temp = getMainApplication().getMasterBackend();
		temp = temp.replaceAll( "/$", "" );
		String url = temp + info.getRelativeUrl();
	    Log.v( TAG, "URL: " + url );
	    
	    // Enable this code to use vitamio: http://vov.io/vitamio/
	    // Section 2 of 4
	    /*VideoView mVideoView = (VideoView) findViewById(R.id.surface_view);
	    mVideoView.setVideoURI(Uri.parse(url));
	    mVideoView.setMediaController(new MediaController(this));*/
	    
	    if( progressDialog!=null ) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	    
	    // Disable this code to use vitamio: http://vov.io/vitamio/
	    // Section 3 of 4
	    Intent tostart = new Intent( Intent.ACTION_VIEW );
	    tostart.setDataAndType( Uri.parse(url), "application/x-mpegurl" );
	    startActivity( tostart );
	    
	    
	    // Enable this code to use vitamio: http://vov.io/vitamio/
	    // Section 4 of 4
	    /*mVideoView.requestFocus();
	    mVideoView.start();*/
	    
	    Log.v( TAG, "Done Starting Video" );
	}
	
	private void exceptionDialolg( Throwable t ) {
		AlertDialog.Builder builder = new AlertDialog.Builder( this );

		builder
			.setTitle( R.string.exception )
			.setMessage( t.toString() )
			.setPositiveButton( R.string.close, null )
				.show();
	}
	
	public void setLiveStreamInfo( LiveStreamInfo info ) {
		Log.v( TAG, "setLiveStreamInfo : enter" );

		this.info = info;
		
		checkLiveStreamInfo( info );

		Log.v( TAG, "setLiveStreamInfo : exit" );
	}
	
	public void checkLiveStreamInfo( LiveStreamInfo info ){
		Log.v( TAG, "checkLiveStreamInfo : enter" );

		//if(info.getStatusInt() < 2 || info.getPercentComplete() < 1){
		if( info.getStatusInt() < 2 || info.getCurrentSegment() <= 2 ) {
			new UpdateStreamInfoTask().execute();
		} else {
			startVideo();
		}

		Log.v( TAG, "checkLiveStreamInfo : exit" );
	}
	
	
	private class CreateStreamTask extends AsyncTask<String, Void, LiveStreamInfo> {

		private Exception e = null;

		@Override
		protected LiveStreamInfo doInBackground( String... params ) {
			Log.v( TAG, "CreateStreamTask : enter" );

			LiveStreamInfo lookup = null;

			try {
				Log.v( TAG, "CreateStreamTask : api" );
				
				String location = getMainApplication().getLocation();
				
				if (location == "HOME"){
					selectedPlaybackProfile = getMainApplication().getSelectedHomePlaybackProfile();
				}
				else if (location == "AWAY"){
					selectedPlaybackProfile = getMainApplication().getSelectedAwayPlaybackProfile();
				}
				else{
					Log.e( TAG, "Unknown Location!" );
				}
				
				
				//lookup = getApplicationContext().getMythServicesApi().contentOperations().
				//	addRecordingLiveStream(Integer.valueOf(program.getChannelInfo().getChannelId()), program.getStartTime(), 
				//			-1, -1, -1, -1, -1, -1);
				
				lookup = getMainApplication().getMythServicesApi().contentOperations().
						addLiveStream(null, params[ 0 ], params[ 1 ], -1, -1,
								selectedPlaybackProfile.getHeight(), selectedPlaybackProfile.getVideoBitrate(), 
								selectedPlaybackProfile.getAudioBitrate(), selectedPlaybackProfile.getAudioSampleRate());
			} catch( Exception e ) {
				Log.v( TAG, "CreateStreamTask : error" );

				this.e = e;
			}

			Log.v( TAG, "CreateStreamTask : exit" );
			return lookup;
		}

		@Override
		protected void onPostExecute( LiveStreamInfo result ) {
			Log.v( TAG, "CreateStreamTask onPostExecute : enter" );

			if( null == e ) {
				setLiveStreamInfo( result );
			} else {
				Log.e( TAG, "error creating live stream", e );
				exceptionDialolg( e );
			}

			Log.v( TAG, "CreateStreamTask onPostExecute : exit" );
		}
	}
	
	private class UpdateStreamInfoTask extends AsyncTask<Void, Void, LiveStreamInfo> {

		private Exception e = null;

		@Override
		protected LiveStreamInfo doInBackground( Void... params ) {
			Log.v( TAG, "UpdateStreamInfoTask : enter" );

			LiveStreamInfo lookup = null;

			try {
				Log.v( TAG, "UpdateStreamInfoTask : api" );
				lookup = info;
				
				//while (lookup.getStatusInt() < 2 || lookup.getPercentComplete() < 1){
				while (lookup.getStatusInt() < 2 || lookup.getCurrentSegment() <= 2){
					Thread.sleep(5000);
					ETagInfo eTag = ETagInfo.createEmptyETag();
					lookup = getMainApplication().getMythServicesApi().contentOperations().getLiveStream( info.getId(), eTag );
				}
			} catch( Exception e ) {
				Log.v( TAG, "UpdateStreamInfoTask : error" );

				this.e = e;
			}

			Log.v( TAG, "UpdateStreamInfoTask : exit" );
			return lookup;
		}

		@Override
		protected void onPostExecute( LiveStreamInfo result ) {
			Log.v( TAG, "UpdateStreamInfoTask onPostExecute : enter" );

			if( null == e ) {
				setLiveStreamInfo( result );
			} else {
				Log.e( TAG, "error updating live stream", e );
				exceptionDialolg( e );
			}

			Log.v( TAG, "UpdateStreamInfoTask onPostExecute : exit" );
		}
	}
	
	private class RemoveStreamTask extends AsyncTask<Void, Void, LiveStreamInfo> {

		private Exception e = null;

		@Override
		protected LiveStreamInfo doInBackground( Void... params ) {
			Log.v( TAG, "RemoveStreamTask : enter" );

			LiveStreamInfo lookup = null;

			try {
				Log.v( TAG, "RemoveStreamTask : api" );
				getMainApplication().getMythServicesApi().contentOperations().removeLiveStream(info.getId());
			} catch( Exception e ) {
				Log.v( TAG, "RemoveStreamTask : error" );

				this.e = e;
			}

			Log.v( TAG, "RemoveStreamTask : exit" );
			return lookup;
		}

		@Override
		protected void onPostExecute( LiveStreamInfo result ) {
			Log.v( TAG, "RemoveStreamTask onPostExecute : enter" );

			if( null != e ) {
				Log.e( TAG, "error removing live stream", e );
				exceptionDialolg( e );
			}

			Log.v( TAG, "RemoveStreamTask onPostExecute : exit" );
		}
	}
	

}
