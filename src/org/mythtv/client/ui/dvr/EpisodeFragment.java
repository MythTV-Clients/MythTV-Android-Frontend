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
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;
import org.mythtv.client.ui.preferences.PlaybackProfile;
import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.db.content.LiveStreamDaoHelper;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.dvr.programGroup.ProgramGroup;
import org.mythtv.db.dvr.programGroup.ProgramGroupDaoHelper;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.service.util.image.ImageFetcher;
import org.mythtv.services.api.Bool;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.content.LiveStreamInfo;
import org.mythtv.services.api.content.LiveStreamInfoWrapper;
import org.mythtv.services.api.dvr.Program;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
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

	private LiveStreamDaoHelper mLiveStreamDaoHelper;
	private MenuHelper mMenuHelper;
	private NetworkHelper mNetworkHelper;
	private ProgramGroupDaoHelper mProgramGroupDaoHelper; 
	private RecordedDaoHelper mRecordedDaoHelper; 
	private ImageFetcher mImageFetcher;

	private Program program;
	private LiveStreamInfo liveStreamInfo;
	
	private TextView hlsView;
	
	private MenuItem addHlsMenuItem = null, clearHlsMenuItem;
	
	private CreateStreamTask createStreamTask = null;
	private UpdateStreamInfoTask updateStreamInfoTask = null;
	
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

		mLiveStreamDaoHelper = ( (AbstractDvrActivity) getActivity() ).getLiveStreamDaoHelper();
		mMenuHelper = ( (AbstractDvrActivity) getActivity() ).getMenuHelper();
		mNetworkHelper = ( (AbstractDvrActivity) getActivity() ).getNetworkHelper();
		mProgramGroupDaoHelper = ( (AbstractDvrActivity) getActivity() ).getProgramGroupDaoHelper();
		mRecordedDaoHelper = ( (AbstractDvrActivity) getActivity() ).getRecordedDaoHelper();
		
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
		addHlsMenuItem = mMenuHelper.addMenuItem( menu );
		clearHlsMenuItem = mMenuHelper.clearMenuItem( menu );
		mMenuHelper.deleteMenuItem( menu );
		
		addHlsMenuItem.setTitle( getString( R.string.menu_add ) + " HLS Stream" );

		clearHlsMenuItem.setTitle( getString( R.string.menu_clear ) + " HLS Stream" );
		clearHlsMenuItem.setVisible( false );

		updateHlsMenuButtons();
		
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
				
				if( null == liveStreamInfo ) {
					createStreamTask = new CreateStreamTask();
					createStreamTask.execute( true );
				} else {

					Intent playerIntent = new Intent( getActivity(), VideoActivity.class );
					playerIntent.putExtra( VideoActivity.EXTRA_CHANNEL_ID, program.getChannelInfo().getChannelId() );
					playerIntent.putExtra( VideoActivity.EXTRA_START_TIME, program.getStartTime().getMillis() );
					startActivity( playerIntent );
				
				}
				
			} else {
				notConnectedNotify();
			}
			
			return true;
		case MenuHelper.WATCH_ON_TV_ID:
			//TODO: Show list of zeroconf frontends and send to selection
			//new PlayRecordingOnFrontEndTask().execute( "http://192.168.10.200:6547" );
			
			Toast.makeText( getActivity(), "Watch on TV - Coming Soon!", Toast.LENGTH_SHORT ).show();

			return true;
		case MenuHelper.ADD_ID:
			Log.d( TAG, "onOptionsItemSelected : add selected" );

			if( mNetworkHelper.isNetworkConnected() ) {
				
				createStreamTask = new CreateStreamTask();
				createStreamTask.execute( false );
			
				Toast.makeText( getActivity(), "Episode added to HLS Playlist", Toast.LENGTH_SHORT ).show();

				updateHlsMenuButtons();
			} else {
				notConnectedNotify();
			}

			return true;
		case MenuHelper.CLEAR_ID :
			Log.d( TAG, "onOptionsItemSelected : clear selected" );
			
			if( mNetworkHelper.isNetworkConnected() ) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

				builder
					.setTitle( R.string.episode_alert_remove_title )
					.setMessage( R.string.episode_alert_remove_message )
					.setPositiveButton( R.string.episode_alert_remove_button_delete, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick( DialogInterface dialog, int which ) {
							
							if( null != createStreamTask && Status.FINISHED != createStreamTask.getStatus() ) {
								Log.d( TAG, "onClick : cancelling create stream task" );

								createStreamTask.cancel( true );
							}
							
							if( null != updateStreamInfoTask && Status.FINISHED != updateStreamInfoTask.getStatus() ) {
								Log.d( TAG, "onClick : cancelling update stream task" );

								updateStreamInfoTask.cancel( true );
							}

							new RemoveStreamTask().execute();
							
						}
					} )
					.setNegativeButton( R.string.episode_alert_remove_button_cancel, null )
					.show();
			
				Toast.makeText( getActivity(), "Episode removed from HLS Playlist", Toast.LENGTH_SHORT ).show();
				
				updateHlsMenuButtons();
			} else {
				notConnectedNotify();
			}

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
							
							if( null != createStreamTask && Status.FINISHED != createStreamTask.getStatus() ) {
								Log.d( TAG, "onClick : cancelling create stream task" );

								createStreamTask.cancel( true );
							}
							
							if( null != updateStreamInfoTask && Status.FINISHED != updateStreamInfoTask.getStatus() ) {
								Log.d( TAG, "onClick : cancelling update stream task" );

								updateStreamInfoTask.cancel( true );
							}
							
							if( null != liveStreamInfo ) {
								new RemoveStreamTask().execute();
							}
							
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

	public void loadEpisode( int channelId, DateTime startTime ) {
		Log.v( TAG, "loadEpisode : enter" );

		program = null;
		liveStreamInfo = null;
		
		if( null == mImageFetcher ) {
            mImageFetcher = ( (AbstractDvrActivity) getActivity() ).getImageFetcher();
		}
		
		if( null == mLiveStreamDaoHelper ) {
			mLiveStreamDaoHelper = ( (AbstractDvrActivity) getActivity() ).getLiveStreamDaoHelper();
		}
		
		if( null == mNetworkHelper ) {
			mNetworkHelper = ( (AbstractDvrActivity) getActivity() ).getNetworkHelper();
		}
		
		if( null == mRecordedDaoHelper ) {
			mRecordedDaoHelper = ( (AbstractDvrActivity) getActivity() ).getRecordedDaoHelper();
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

            hlsView = (TextView) activity.findViewById( R.id.textView_episode_hls );
            updateHlsDetails();
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
	
	private void updateHlsDetails() {
		Log.v( TAG, "updateHlsDetails : enter" );
		
        liveStreamInfo = mLiveStreamDaoHelper.findByProgram( program );
        if( null != liveStreamInfo ) {
    		Log.v( TAG, "updateHlsDetails : live stream found, liveStreamInfo=" + liveStreamInfo.toString() );

        	if( liveStreamInfo.getPercentComplete() == 100 ) {
        		hlsView.setText( "WATCH IT NOW!" );
        	} else if( liveStreamInfo.getPercentComplete() >= 0 ) {
        		hlsView.setText( "Transcode " + liveStreamInfo.getPercentComplete() + "%" );
        		
        		new UpdateStreamInfoTask().execute();
        	} else {
        		hlsView.setText( "" );
        	}
        	
        } else {
    		hlsView.setText( "" );
        }

        updateHlsMenuButtons();
        
		Log.v( TAG, "updateHlsDetails : exit" );
	}
	
	private void updateHlsMenuButtons() {
		Log.v( TAG, "updateHlsMenuButtons : enter" );
		
        if( null != liveStreamInfo ) {
    		Log.v( TAG, "updateHlsMenuButtons : live stream found, liveStreamInfo=" + liveStreamInfo.toString() );

    		if( null != addHlsMenuItem ) {
    			addHlsMenuItem.setVisible( false );
    		}
    		
    		if( null != clearHlsMenuItem ) {
    			clearHlsMenuItem.setVisible( true );
    		}
    		
        } else {
        	
    		if( null != addHlsMenuItem ) {
    			addHlsMenuItem.setVisible( true );
    		}
    		
    		if( null != clearHlsMenuItem ) {
    			clearHlsMenuItem.setVisible( false );
    		}
    		
        }

        Log.v( TAG, "updateHlsMenuButtons : exit" );
	}
	
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

	public void checkLiveStreamInfo( ResponseEntity<LiveStreamInfoWrapper> info ){
		Log.v( TAG, "checkLiveStreamInfo : enter" );

		if( info.getBody().getLiveStreamInfo().getStatusInt() < 2 || info.getBody().getLiveStreamInfo().getCurrentSegment() <= 2 ) {
			new UpdateStreamInfoTask().execute();
		}

		Log.v( TAG, "checkLiveStreamInfo : exit" );
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
		protected ResponseEntity<Bool> doInBackground( String... params ) {
			Log.v( TAG, "PlayRecordingOnFrontEndTask.doInBackground : enter" );
			
			ResponseEntity<Bool> responseEntity = getMainApplication().getMythServicesApi().frontendOperations().playRecording( params[ 0 ], program.getChannelInfo().getChannelId(), program.getStartTime() ); 
			
			Log.v( TAG, "PlayRecordingOnFrontEndTask.doInBackground : exit" );
			return responseEntity;
		}
		
	}

	private class CreateStreamTask extends AsyncTask<Boolean, Void, ResponseEntity<LiveStreamInfoWrapper>> {

		private boolean startVideo;
		
		private Exception e = null;

		private PlaybackProfile selectedPlaybackProfile;

		@Override
		protected ResponseEntity<LiveStreamInfoWrapper> doInBackground( Boolean... params ) {
			Log.v( TAG, "CreateStreamTask : enter" );

			startVideo = params[ 0 ];
			
			try {
				Log.v( TAG, "CreateStreamTask : api" );
				
				LocationType location = getMainApplication().getConnectedLocationProfile().getType();
				
				if( location.equals( LocationType.HOME ) ) {
					selectedPlaybackProfile = getMainApplication().getSelectedHomePlaybackProfile();
				} else if( location.equals( LocationType.AWAY ) ) {
					selectedPlaybackProfile = getMainApplication().getSelectedAwayPlaybackProfile();
				} else {
					Log.e( TAG, "Unknown Location!" );
				}
				
				Log.v( TAG, "CreateStreamTask : exit" );
				return getMainApplication().getMythServicesApi().contentOperations().
						addLiveStream( null, program.getFilename(), program.getHostname(), -1, -1,
								selectedPlaybackProfile.getHeight(), selectedPlaybackProfile.getVideoBitrate(), 
								selectedPlaybackProfile.getAudioBitrate(), selectedPlaybackProfile.getAudioSampleRate() );
			
			} catch( Exception e ) {
				Log.v( TAG, "CreateStreamTask : error" );

				this.e = e;
			}

			Log.v( TAG, "CreateStreamTask : exit, no hls stream created" );
			return null;
		}

		@Override
		protected void onPostExecute( ResponseEntity<LiveStreamInfoWrapper> result ) {
			Log.v( TAG, "CreateStreamTask onPostExecute : enter" );

			if( null == e ) {
				Log.v( TAG, "CreateStreamTask onPostExecute : no exception" );
				
				if( null != result ) {
					Log.v( TAG, "CreateStreamTask onPostExecute : result returned from mythtv services api" );
					
					if( result.getStatusCode().equals( HttpStatus.OK ) ) {
						
						liveStreamInfo = result.getBody().getLiveStreamInfo();
						mLiveStreamDaoHelper.save( liveStreamInfo, program );
						
						new UpdateStreamInfoTask().execute();
						
						updateHlsDetails();
						updateHlsMenuButtons();
						
						if( startVideo ) {
							
							Intent playerIntent = new Intent( getActivity(), VideoActivity.class );
							playerIntent.putExtra( VideoActivity.EXTRA_CHANNEL_ID, program.getChannelInfo().getChannelId() );
							playerIntent.putExtra( VideoActivity.EXTRA_START_TIME, program.getStartTime().getMillis() );
							startActivity( playerIntent );

						}
					}
				}
			} else {
				Log.e( TAG, "error creating live stream", e );
				
				//exceptionDialolg( e );
			}

			Log.v( TAG, "CreateStreamTask onPostExecute : exit" );
		}
	}

	private class UpdateStreamInfoTask extends AsyncTask<Void, Void, ResponseEntity<LiveStreamInfoWrapper>> {

		private Exception e = null;

		@Override
		protected ResponseEntity<LiveStreamInfoWrapper> doInBackground( Void... params ) {
			Log.v( TAG, "UpdateStreamInfoTask : enter" );

			if( !mNetworkHelper.isNetworkConnected() ) {
				Log.v( TAG, "UpdateStreamInfoTask : exit, not connected" );
				
				return null;
			}

			if( null == liveStreamInfo ) {
				Log.v( TAG, "UpdateStreamInfoTask : exit, live stream info is null" );
				
				return null;
			}
			
			try {
				Log.v( TAG, "UpdateStreamInfoTask : api" );
				
				if( null != liveStreamInfo ) {
					if( liveStreamInfo.getPercentComplete() <= 100 ) {
						Thread.sleep( 10000 );
						ETagInfo eTag = ETagInfo.createEmptyETag();
					
						Log.v( TAG, "UpdateStreamInfoTask : exit" );
						return getMainApplication().getMythServicesApi().contentOperations().getLiveStream( liveStreamInfo.getId(), eTag );
					}
				}
			} catch( Exception e ) {
				Log.v( TAG, "UpdateStreamInfoTask : error" );

				this.e = e;
			}

			Log.v( TAG, "UpdateStreamInfoTask : exit, task in error or was cancelled by user" );
			return null;
		}

		@Override
		protected void onPostExecute( ResponseEntity<LiveStreamInfoWrapper> result ) {
			Log.v( TAG, "UpdateStreamInfoTask onPostExecute : enter" );

			if( null == e ) {
				Log.v( TAG, "UpdateStreamInfoTask onPostExecute : no exception occurred" );
				
				if( null != result ) {
					Log.v( TAG, "UpdateStreamInfoTask onPostExecute : result returned from web service" );
					
					if( result.getStatusCode().equals( HttpStatus.OK ) ) {

						// save updated live stream info to database
						liveStreamInfo = result.getBody().getLiveStreamInfo();
						mLiveStreamDaoHelper.save( liveStreamInfo, program );
						
						if( liveStreamInfo.getPercentComplete() <= 100 ) {
							new UpdateStreamInfoTask().execute();
						}
						
						// update display 
						updateHlsDetails();
						updateHlsMenuButtons();
					}
				}
			} else {
				Log.e( TAG, "error updating live stream", e );
				//exceptionDialolg( e );
			}

			Log.v( TAG, "UpdateStreamInfoTask onPostExecute : exit" );
		}
	}

	private class RemoveStreamTask extends AsyncTask<Void, Void, ResponseEntity<Bool>> {

		private Exception e = null;

		@Override
		protected ResponseEntity<Bool> doInBackground( Void... params ) {
			Log.v( TAG, "RemoveStreamTask : enter" );

			try {
				Log.v( TAG, "RemoveStreamTask : api" );
				
				if( null != liveStreamInfo ) {
					return getMainApplication().getMythServicesApi().contentOperations().removeLiveStream( liveStreamInfo.getId() );
				}
				
			} catch( Exception e ) {
				Log.v( TAG, "RemoveStreamTask : error" );

				this.e = e;
			}

			Log.v( TAG, "RemoveStreamTask : exit" );
			return null;
		}

		@Override
		protected void onPostExecute( ResponseEntity<Bool> result ) {
			Log.v( TAG, "RemoveStreamTask onPostExecute : enter" );

			if( null == e ) {
				Log.v( TAG, "RemoveStreamTask onPostExecute : no error occurred" );
				
				if( null != result ) {
					
					if( result.getBody().getBool().booleanValue() ) {
						
						mLiveStreamDaoHelper.delete( liveStreamInfo );
						
						updateHlsDetails();
						
						Log.v( TAG, "RemoveStreamTask onPostExecute : live stream removed" );
					}
					
				}
			} else {
				Log.e( TAG, "error removing live stream", e );

				//exceptionDialolg( e );
			}

			Log.v( TAG, "RemoveStreamTask onPostExecute : exit" );
		}
	}

}
