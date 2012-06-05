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
 * @author John Baab <rhpot1991@ubuntu.com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */
package org.mythtv.client.ui.dvr;

	
// Enable this code to use vitamio: http://vov.io/vitamio/
// Section 1 of 4
//import io.vov.vitamio.widget.MediaController;
//import io.vov.vitamio.widget.VideoView;

import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.preferences.PlaybackProfile;
import org.mythtv.services.api.content.LiveStreamInfo;
import org.mythtv.services.api.dvr.Program;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * @author John Baab
 * 
 */
public class VideoActivity extends FragmentActivity {

	private static final String TAG = VideoActivity.class.getSimpleName();

	public static final String EXTRA_PROGRAM_GROUP_KEY = "org.mythtv.client.ui.dvr.programGroup.EXTRA_PROGRAM_GROUP_KEY";
	
	private SharedPreferences mPreferences;
	
	private LiveStreamInfo info = null;
	private ProgressDialog progressDialog;
	private Boolean firstrun = true;
	private PlaybackProfile selectedPlaybackProfile;
	
	public MainApplication getApplicationContext() {
		return (MainApplication) super.getApplicationContext();
	}

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
	    
	    mPreferences = getSharedPreferences( "org.mythtv.dvr.videoActivity", MODE_PRIVATE );
	    firstrun = mPreferences.getBoolean( "FIRSTRUN", Boolean.TRUE );
	    
	    progressDialog = ProgressDialog.show( this, "Please wait...", "Retrieving video...", true, true );
	    
	    new CreateStreamTask().execute();
	    
		Log.v( TAG, "onCreate : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		Log.v( TAG, "onRestart : enter" );
		
		super.onRestart();

		Log.v( TAG, "onRestart : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		Log.v( TAG, "onStart : enter" );
		
		super.onRestart();

		Log.v( TAG, "onStart : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();
		
		if( !firstrun ) {
			Log.v( TAG, "onResume : resuming after video playback started" );
			
			finish();
		}
		
		Log.v( TAG, "onResume : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.v( TAG, "onPause : enter" );
		super.onPause();

        SharedPreferences.Editor ed = mPreferences.edit();
        ed.putBoolean( "FIRSTRUN", firstrun );
        ed.commit();

        Log.v( TAG, "onPause : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		Log.v( TAG, "onStop : enter" );
		
		super.onStop();

		Log.v( TAG, "onStop : exit" );
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
		
		String temp = getApplicationContext().getMasterBackend();
		temp = temp.replaceAll( "/$", "" );
		String url = temp + info.getRelativeUrl();
	    Log.v( TAG, "URL: " + url );
	    
	    // Enable this code to use vitamio: http://vov.io/vitamio/
	    // Section 2 of 4
	    /*VideoView mVideoView = (VideoView) findViewById(R.id.surface_view);
	    mVideoView.setVideoURI(Uri.parse(url));
	    mVideoView.setMediaController(new MediaController(this));*/
	    
	    if (progressDialog!=null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	    
	    firstrun = false;

	    // Disable this code to use vitamio: http://vov.io/vitamio/
	    // Section 3 of 4
	    Intent tostart = new Intent( Intent.ACTION_VIEW );
	    tostart.setDataAndType( Uri.parse(url), "video/*" );
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
	
	
	private class CreateStreamTask extends AsyncTask<Void, Void, LiveStreamInfo> {

		private Exception e = null;

		@Override
		protected LiveStreamInfo doInBackground( Void... params ) {
			Log.v( TAG, "CreateStreamTask : enter" );

			LiveStreamInfo lookup = null;

			try {
				Log.v( TAG, "CreateStreamTask : api" );
				Program program = getApplicationContext().getCurrentProgram();
				
				String location = getApplicationContext().getLocation();
				
				if (location == "HOME"){
					selectedPlaybackProfile = getApplicationContext().getSelectedHomePlaybackProfile();
				}
				else if (location == "AWAY"){
					selectedPlaybackProfile = getApplicationContext().getSelectedAwayPlaybackProfile();
				}
				else{
					Log.e( TAG, "Unknown Location!" );
				}
				
				
				//lookup = getApplicationContext().getMythServicesApi().contentOperations().
				//	addRecordingLiveStream(Integer.valueOf(program.getChannelInfo().getChannelId()), program.getStartTime(), 
				//			-1, -1, -1, -1, -1, -1);
				
				lookup = getApplicationContext().getMythServicesApi().contentOperations().
						addLiveStream(null, program.getFilename(), null, -1, selectedPlaybackProfile.getWidth(),
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
					lookup = getApplicationContext().getMythServicesApi().contentOperations().getLiveStream(info.getId());
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
				getApplicationContext().getMythServicesApi().contentOperations().removeLiveStream(info.getId());
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
