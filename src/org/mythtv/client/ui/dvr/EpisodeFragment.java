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
package org.mythtv.client.ui.dvr;

import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;
import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.dvr.programGroup.ProgramGroup;
import org.mythtv.db.dvr.programGroup.ProgramGroupDaoHelper;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.image.ImageFetcher;
import org.mythtv.services.api.Bool;
import org.mythtv.services.api.dvr.Program;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EpisodeFragment extends AbstractMythFragment {

	private static final String TAG = EpisodeFragment.class.getSimpleName();

	private OnEpisodeActionListener listener = null;

	private MenuHelper mMenuHelper;
	private ProgramGroupDaoHelper mProgramGroupDaoHelper; 
	private RecordedDaoHelper mRecordedDaoHelper; 
	private ImageFetcher mImageFetcher;

	private Program program;
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );

		View root = inflater.inflate( R.layout.fragment_dvr_episode, container, false );

		Log.v( TAG, "onCreateView : exit" );
		return root;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		mMenuHelper = new MenuHelper( getActivity() );
		mProgramGroupDaoHelper = new ProgramGroupDaoHelper( getActivity() );
		mRecordedDaoHelper = new RecordedDaoHelper( getActivity() );
		
		setHasOptionsMenu( true );
		setRetainInstance( true );

		Log.v( TAG, "onActivityCreated : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@TargetApi( 11 )
	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		Log.v( TAG, "onCreateOptionsMenu : enter" );
		super.onCreateOptionsMenu( menu, inflater );

		mMenuHelper.watchMenuItem( menu );
		mMenuHelper.watchOnFrontendMenuItem( menu );
		mMenuHelper.addMenuItem( menu );
		mMenuHelper.deleteMenuItem( menu );
		
	    Log.v( TAG, "onCreateOptionsMenu : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.AbstractRecordingsActivity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );
		
		switch( item.getItemId() ) {
		case MenuHelper.WATCH_ID:
			Log.d( TAG, "onOptionsItemSelected : watch selected" );

			if( mNetworkHelper.isNetworkConnected() ) {
				
				Intent playerIntent = new Intent( getActivity(), VideoActivity.class );
				playerIntent.putExtra( VideoActivity.EXTRA_CHANNEL_ID, program.getChannelInfo().getChannelId() );
				playerIntent.putExtra( VideoActivity.EXTRA_START_TIME, program.getStartTime().getMillis() );
				startActivity( playerIntent );
			
			} else {
				notConnectedNotify();
			}
			
			return true;
		case MenuHelper.WATCH_ON_TV_ID:
//TODO: Show list of zeroconf frontends and send to selection
//new PlayRecordingOnFrontEndTask().execute("http://192.168.1.106:6547");
			return true;
		case MenuHelper.ADD_ID:
			Log.d( TAG, "onOptionsItemSelected : add selected" );

			Toast.makeText( getActivity(), "Add HLS to Playlist - Coming Soon!", Toast.LENGTH_SHORT ).show();
			
//			if( mNetworkHelper.isNetworkConnected() ) {
//				
//			
//			} else {
//				notConnectedNotify();
//			}

			return true;
		case MenuHelper.DELETE_ID:
			Log.d( TAG, "onOptionsItemSelected : delete selected" );

			if( mNetworkHelper.isNetworkConnected() ) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

				builder
					.setTitle( R.string.episode_alert_delete_title )
					.setMessage( R.string.episode_alert_delete_message )
					.setPositiveButton( R.string.episode_alert_delete_button_delete, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick( DialogInterface dialog, int which ) {
							
							new RemoveRecordingTask().execute();
							
						}
					} )
					.setNegativeButton( R.string.episode_alert_delete_button_cancel, null )
					.show();
			
			} else {
				notConnectedNotify();
			}

			return true;
		}
		
		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	public void loadEpisode( Long channelId, DateTime startTime ) {
		Log.v( TAG, "loadEpisode : enter" );

		if( null == mRecordedDaoHelper ) {
			mRecordedDaoHelper = new RecordedDaoHelper( getActivity() );
		}
		
        if( RecordingsActivity.class.isInstance( getActivity() ) ) {
            mImageFetcher = ( (RecordingsActivity) getActivity() ).getImageFetcher();
        }

        if( EpisodeActivity.class.isInstance( getActivity() ) ) {
            mImageFetcher = ( (EpisodeActivity) getActivity() ).getImageFetcher();
        }

		Log.v( TAG, "loadEpisode : channelId=" + channelId + ", startTime=" + DateUtils.dateTimeFormatterPretty.print( startTime ) );
        program = mRecordedDaoHelper.findOne( channelId, startTime );
		if( null != program ) {

			// get activity to grab views from
			FragmentActivity activity = this.getActivity();

			// coverart
			ImageView iView = (ImageView) activity.findViewById( R.id.imageView_episode_coverart );
			if( null != program.getInetref() && !"".equals( program.getInetref() ) ) {
				if( null != mImageFetcher ) {
					mImageFetcher.loadImage( program.getInetref(), "Coverart", iView, null );
				} else {
					iView.setImageDrawable( null );
				}
			} else {
				iView.setImageDrawable( null );
			}

			// title
			TextView tView = (TextView) activity.findViewById( R.id.textView_episode_title );
			tView.setText( program.getTitle() );

			// subtitle
			tView = (TextView) activity.findViewById( R.id.textView_episode_subtitle );
			tView.setText( program.getSubTitle() );

			// description
			tView = (TextView) activity.findViewById( R.id.textView_episode_description );
			tView.setText( program.getDescription() );

			// channel number
			tView = (TextView) activity.findViewById( R.id.textView_episode_ch_num );
			tView.setText( program.getChannelInfo().getChannelNumber() );

			// airdate
			tView = (TextView) activity.findViewById( R.id.textView_episode_airdate );
            tView.setText(DateUtils.getDateTimeUsingLocaleFormattingPretty(program.getStartTime(), getMainApplication().getDateFormat(), getMainApplication().getClockType()));

		}

		Log.v( TAG, "loadEpisode : exit" );
	}

	public void setOnEpisodeActionListener( OnEpisodeActionListener listener ) {
		Log.v( TAG, "setOnEpisodeActionListener : enter" );

		this.listener = listener;

		Log.v( TAG, "setOnEpisodeActionListener : exit" );
	}

	public interface OnEpisodeActionListener {
		void onEpisodeDeleted( String programGroup );
	}

	// internal helpers
	
	private void notConnectedNotify() {
		
		Toast.makeText( getActivity(), getResources().getString( R.string.notification_not_connected ), Toast.LENGTH_SHORT ).show();
		
	}

	private void deleteNotify( Boolean deleted ) {
		
		Toast.makeText( getActivity(), ( "Episode" + ( !deleted ? " NOT " : " " ) + "deleted!" ), Toast.LENGTH_SHORT ).show();
		
	}
	
	private void exceptionDialolg( Throwable t ) {
		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

		builder
			.setTitle( R.string.exception )
			.setMessage( t.toString() )
			.setPositiveButton( R.string.close, null )
				.show();
	}

	private class RemoveRecordingTask extends AsyncTask<Void, Void, ResponseEntity<Bool>> {

		private Exception e = null;

		@Override
		protected ResponseEntity<Bool> doInBackground( Void... params ) {
			Log.v( TAG, "CreateStreamTask : enter" );

			if( !mNetworkHelper.isMasterBackendConnected() ) {
				return null;
			}

			ResponseEntity<Bool> removed = null;

			try {
				Log.v( TAG, "RemoveRecordingTask : api" );
				
				removed = getMainApplication().getMythServicesApi().dvrOperations().removeRecorded( program.getChannelInfo().getChannelId(), program.getStartTime() );
			} catch( Exception e ) {
				Log.v( TAG, "CreateStreamTask : error" );

				this.e = e;
			}

			Log.v( TAG, "CreateStreamTask : exit" );
			return removed;
		}

		@Override
		protected void onPostExecute( ResponseEntity<Bool> result ) {
			Log.v( TAG, "RemoveRecordingTask : onPostExecute - enter" );

			if( null == e ) {
				if( null != result ) {
					if( result.getStatusCode().equals( HttpStatus.OK ) ) {
						
						Bool bool = result.getBody();
						if( bool.getBool().equals( Boolean.TRUE ) ) {
						
							String title = program.getTitle();
							ProgramGroup programGroup = mProgramGroupDaoHelper.findByTitle( title );
							
							String programGroupName = programGroup.getProgramGroup();
							
							mRecordedDaoHelper.delete( program );
							
							List<Program> programs = mRecordedDaoHelper.findAllByTitle( title );
							if( null == programs || programs.isEmpty() ) {
								mProgramGroupDaoHelper.delete( programGroup );
								
								List<ProgramGroup> programGroups = mProgramGroupDaoHelper.findAll();
								if( null != programGroups && !programGroups.isEmpty() ) {
									programGroupName = programGroups.get( 0 ).getProgramGroup();
								}
							}
							
							listener.onEpisodeDeleted( programGroupName );
							
						}
						
						deleteNotify( bool.getBool() );
					}
				}
			} else {
				Log.e( TAG, "RemoveRecordingTask : onPostExecute - error removing recording", e );
				exceptionDialolg( e );
			}

			Log.v( TAG, "RemoveRecordingTask : onPostExecute - exit" );
		}
	}
	
	private class PlayRecordingOnFrontEndTask extends AsyncTask<String, Void, ResponseEntity<Bool>> {

		@Override
		protected ResponseEntity<Bool> doInBackground(String... params) {
			return getMainApplication().getMythServicesApi().frontendOperations().playRecording(
					params[0], program.getChannelInfo().getChannelId(), program.getStartTime());
		}
		
	}

}
