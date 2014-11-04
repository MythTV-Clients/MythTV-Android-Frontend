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

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;
import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.db.content.LiveStreamDaoHelper;
import org.mythtv.db.content.model.LiveStreamInfo;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.dvr.model.Program;
import org.mythtv.db.dvr.programGroup.ProgramGroup;
import org.mythtv.db.dvr.programGroup.ProgramGroupDaoHelper;
import org.mythtv.service.content.GetLiveStreamTask;
import org.mythtv.service.content.LiveStreamService;
import org.mythtv.service.dvr.RecordedService;
import org.mythtv.service.util.DateUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class EpisodeFragment extends AbstractMythFragment implements GetLiveStreamTask.TaskFinishedListener {

	private static final String TAG = EpisodeFragment.class.getSimpleName();
	private static final String DISMISS_ADD = "org.mythtv.episodeFragment.dismissAddDialog";
	
	private OnEpisodeActionListener listener = null;

	private LiveStreamReceiver liveStreamReceiver = new LiveStreamReceiver();
	private RecordedRemovedReceiver recordedRemovedReceiver = new RecordedRemovedReceiver();

	private LiveStreamDaoHelper mLiveStreamDaoHelper = LiveStreamDaoHelper.getInstance();
	private MenuHelper mMenuHelper = MenuHelper.getInstance();
	private ProgramGroupDaoHelper mProgramGroupDaoHelper = ProgramGroupDaoHelper.getInstance(); 
	private RecordedDaoHelper mRecordedDaoHelper = RecordedDaoHelper.getInstance();

	private Program program;
	private LocationProfile mLocationProfile;
	
	private TextView hlsView;
	
	private MenuItem addHlsMenuItem = null, clearHlsMenuItem;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );

		View root = inflater.inflate( R.layout.fragment_dvr_episode, container, false );
		
		/*
		 * TODO Add the following to enable an image where there is none
		 * on the backend. Change the coverart to something nicer.
		 * .showImageForEmptyUri( R.drawable.coverart_missing )
		 * .showImageOnFail( R.drawable.coverart_missing )
		 */
		
		options = new DisplayImageOptions.Builder()
			.cacheInMemory( true )
			.cacheOnDisk( true 	)
			.build();

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

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );

		

		setHasOptionsMenu( true );

        hlsView = (TextView) getActivity().findViewById( R.id.textView_episode_hls );

        Log.v( TAG, "onActivityCreated : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();

        hlsView = (TextView) getActivity().findViewById( R.id.textView_episode_hls );

        Log.v( TAG, "onResume : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		Log.v( TAG, "onStart : enter" );
		super.onStart();

		IntentFilter recordedRemovedFilter = new IntentFilter( RecordedService.ACTION_REMOVE );
		recordedRemovedFilter.addAction( RecordedService.ACTION_COMPLETE );
        getActivity().registerReceiver( recordedRemovedReceiver, recordedRemovedFilter );

		IntentFilter liveStreamFilter = new IntentFilter( LiveStreamService.ACTION_COMPLETE );
		liveStreamFilter.addAction( LiveStreamService.ACTION_PROGRESS );
		liveStreamFilter.addAction( LiveStreamService.ACTION_CREATE );
		liveStreamFilter.addAction( LiveStreamService.ACTION_PLAY );
		liveStreamFilter.addAction( LiveStreamService.ACTION_LOAD );
		liveStreamFilter.addAction( LiveStreamService.ACTION_REMOVE );
        getActivity().registerReceiver( liveStreamReceiver, liveStreamFilter );

        Log.v( TAG, "onStart : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		Log.v( TAG, "onStop : enter" );
		super.onStop();

		// Unregister for broadcast
		if( null != recordedRemovedReceiver ) {
			try {
				getActivity().unregisterReceiver( recordedRemovedReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}

		if( null != liveStreamReceiver ) {
			try {
				getActivity().unregisterReceiver( liveStreamReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}

		Log.v( TAG, "onStop : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		Log.v( TAG, "onCreateOptionsMenu : enter" );
		super.onCreateOptionsMenu( menu, inflater );

		mMenuHelper.watchMenuItem( getActivity(), menu );
		mMenuHelper.watchOnFrontendMenuItem( getActivity(), menu );
		addHlsMenuItem = mMenuHelper.addMenuItem( getActivity(), menu );
		clearHlsMenuItem = mMenuHelper.clearMenuItem( getActivity(), menu );
		mMenuHelper.deleteMenuItem( getActivity(), menu );
		
		if( null != addHlsMenuItem ) {
			addHlsMenuItem.setTitle( getString( R.string.menu_add ) + " HLS Stream" );
		}
		
		if( null != clearHlsMenuItem ) {
			clearHlsMenuItem.setTitle( getString( R.string.menu_clear ) + " HLS Stream" );
			clearHlsMenuItem.setVisible( false );
		}
		
		updateHlsMenuButtons();
		
		Log.v( TAG, "onCreateOptionsMenu : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.AbstractRecordingsActivity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );
		
		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

		switch( item.getItemId() ) {
		case MenuHelper.WATCH_ID:
			Log.d( TAG, "onOptionsItemSelected : watch selected" );

			if( mLocationProfile.getType().equals( LocationType.HOME ) ) {

				builder
				.setTitle( R.string.episode_watch_title )
				.setMessage( R.string.episode_watch_message )
				.setPositiveButton( R.string.episode_watch_button_hls, new DialogInterface.OnClickListener() {

					@Override
					public void onClick( DialogInterface dialog, int which ) {

						LiveStreamInfo liveStreamInfo = mLiveStreamDaoHelper.findByProgram( getActivity(), mLocationProfile, program );
						if( null == liveStreamInfo ) {
							Log.v( TAG, "onOptionsItemSelected : WATCH - starting stream service" );
							
							startCreateStreamService();

						} else {
							Log.v( TAG, "onOptionsItemSelected : WATCH - starting play service" );

							if( liveStreamInfo.getPercentComplete() > 2 ) {
								startPlayStreamService();
							} else {
								Toast.makeText( getActivity(), "Please wait, you can start watching HLS after your program is 2% processed", Toast.LENGTH_SHORT ).show();
							}

						}

					}

				})
				.setNegativeButton( R.string.episode_watch_button_raw, new DialogInterface.OnClickListener() {

					@Override
					public void onClick( DialogInterface dialog, int which ) {

						startPlayer( true );

					}

				})
				.show();

			} else {

				LiveStreamInfo liveStreamInfo = mLiveStreamDaoHelper.findByProgram( getActivity(), mLocationProfile, program );
				if( null == liveStreamInfo ) {

					startCreateStreamService();

				} else {

					if( liveStreamInfo.getPercentComplete() > 2 ) {
						startPlayStreamService();
					} else {
						Toast.makeText( getActivity(), "Please wait, you can start watching HLS after your program is 2% processed", Toast.LENGTH_SHORT ).show();
					}

				}

			}

			return true;
		case MenuHelper.WATCH_ON_TV_ID:
			//TODO: Show list of zeroconf frontends and send to selection
			//PlayRecordingOnFrontEndTask playTask = new PlayRecordingOnFrontEndTask( mLocationProfile, program );
			//playTask.execute( "http://192.168.10.200:6547" );
			
			Toast.makeText( getActivity(), "Watch on TV - Coming Soon!", Toast.LENGTH_SHORT ).show();

			return true;
		case MenuHelper.ADD_ID:
			Log.d( TAG, "onOptionsItemSelected : add selected" );

			if( !preferences.getBoolean( DISMISS_ADD, false ) ) {

				View dismissView = View.inflate( getActivity(), R.layout.dismiss_checkbox, null );
				CheckBox dismiss = (CheckBox) dismissView.findViewById( R.id.dismiss );
				dismiss.setOnCheckedChangeListener( new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {

						setDismissAddPreference( isChecked );

					}

				});
				dismiss.setText( getString( R.string.episode_add_doNotDisplay ) );

				builder
				.setTitle( R.string.episode_add_title )
				.setMessage( R.string.episode_add_message )
				.setView( dismissView )
				.setPositiveButton( R.string.episode_add_button_play, new DialogInterface.OnClickListener() {

					@Override
					public void onClick( DialogInterface dialog, int which ) {

						startCreateStreamService();

						Toast.makeText( getActivity(), "Episode will be available for future viewing when processing is complete.", Toast.LENGTH_SHORT ).show();

					}
				} )
				.setNegativeButton( R.string.episode_add_button_cancel, null )
				.show();

			} else {

				startCreateStreamService();

				Toast.makeText( getActivity(), "Episode will be available for future viewing when processing is complete.", Toast.LENGTH_SHORT ).show();

				updateHlsMenuButtons();

			}
				
			return true;
		case MenuHelper.CLEAR_ID :
			Log.d( TAG, "onOptionsItemSelected : clear selected" );
			
			builder
			.setTitle( R.string.episode_alert_remove_title )
			.setMessage( R.string.episode_alert_remove_message )
			.setPositiveButton( R.string.episode_alert_remove_button_delete, new DialogInterface.OnClickListener() {

				@Override
				public void onClick( DialogInterface dialog, int which ) {

					startRemoveStreamService();

				}
			} )
			.setNegativeButton( R.string.episode_alert_remove_button_cancel, null )
			.show();

			updateHlsMenuButtons();

			return true;
		case MenuHelper.DELETE_ID:
			Log.d( TAG, "onOptionsItemSelected : delete selected" );

			builder
			.setTitle( R.string.episode_alert_delete_title )
			.setMessage( R.string.episode_alert_delete_message )
			.setPositiveButton( R.string.episode_alert_delete_button_delete, new DialogInterface.OnClickListener() {

				@Override
				public void onClick( DialogInterface dialog, int which ) {

					startRemoveProgramService();

				}
			} )
			.setNegativeButton( R.string.episode_alert_delete_button_cancel, null )
			.show();
			
			return true;
		}
		
		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	public void loadEpisode( int channelId, DateTime startTime ) {
		Log.v( TAG, "loadEpisode : enter" );

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );

		Log.v( TAG, "loadEpisode : channelId=" + channelId + ", startTime=" + DateUtils.dateTimeFormatterPretty.print( startTime ) );
        program = mRecordedDaoHelper.findOne( getActivity(), mLocationProfile, channelId, startTime );
		if( null != program ) {
			Log.v( TAG, "loadEpisode : program found" );
			
			LiveStreamInfo liveStreamInfo = mLiveStreamDaoHelper.findByProgram( getActivity(), mLocationProfile, program );
			if( null != liveStreamInfo ) {
				Log.v( TAG, "loadEpisode : program has livestream" );
				
				if( liveStreamInfo.getPercentComplete() < 100 ) {
					Log.v( TAG, "loadEpisode : livestream is not complete, start update" );
					
					startUpdateStreamService();
				}
				
			}
			
			// get activity to grab views from
			Activity activity = this.getActivity();

			// coverart
			final ImageView iView = (ImageView) activity.findViewById( R.id.imageView_episode_coverart );
			iView.setImageDrawable( null );
			
			if( null != program.getInetref() && !"".equals( program.getInetref() ) ) {

				String imageUri = mLocationProfile.getUrl() + "Content/GetRecordingArtwork?Type=Coverart&Inetref=" + program.getInetref();
				imageLoader.displayImage( imageUri, iView, options, new SimpleImageLoadingListener() {

					/* (non-Javadoc)
					 * @see com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener#onLoadingComplete(android.graphics.Bitmap)
					 */
					@Override
					public void onLoadingComplete( String imageUri, View view, Bitmap loadedImage ) {
				        iView.setVisibility( View.VISIBLE );
					}

					/* (non-Javadoc)
					 * @see com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener#onLoadingFailed(com.nostra13.universalimageloader.core.assist.FailReason)
					 */
					@Override
					public void onLoadingFailed( String imageUri, View view, FailReason failReason ) {
						if ( options.shouldShowImageOnFail() ) {
							// TODO Fix this with 1.9.2 UniversalImageLoader
							//iView.setImageResource( options.getImageOnFail() );
							iView.setVisibility( View.VISIBLE );
						}
						else {
							iView.setVisibility( View.GONE );
						}
					}

				});

			}
			else {

				if ( options.shouldShowImageOnFail() ) {
					// TODO see above
					//iView.setImageResource( options.getImageOnFail() );
					iView.setVisibility( View.VISIBLE );
				}
				else {
					iView.setVisibility( View.GONE );
				}

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
            tView.setText( DateUtils.getDateTimeUsingLocaleFormattingPretty( program.getStartTime(), getMainApplication().getDateFormat(), getMainApplication().getClockType() ) );

            if( null == hlsView ) {
            	hlsView = (TextView) activity.findViewById( R.id.textView_episode_hls );
            }
            
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
		void onEpisodeDeleted( ProgramGroup programGroup );
	}

	// internal helpers
	
	private void startPlayer( boolean raw ) {
		Log.i( TAG, "startPlayer : enter" );
		
		if( null != program ) {
			Log.v( TAG, "startPlayer : channelId=" + program.getChannelInfo().getChannelId() + ", startTime=" + program.getStartTime().getMillis() );
		}

		Intent playerIntent = new Intent( getActivity(), VideoActivity.class );
		playerIntent.putExtra( VideoActivity.EXTRA_CHANNEL_ID, program.getChannelInfo().getChannelId() );
		playerIntent.putExtra( VideoActivity.EXTRA_START_TIME, program.getStartTime().getMillis() );
		playerIntent.putExtra( VideoActivity.EXTRA_RAW, raw );
		startActivity( playerIntent );

		Log.i( TAG, "startPlayer : exit" );
	}
	
	private void startPlayStreamService() {
		Log.i( TAG, "startPlayStreamService : enter" );
		
		if( null != program ) {
			Log.v( TAG, "startPlayStreamService : channelId=" + program.getChannelInfo().getChannelId() + ", startTime=" + program.getStartTime().getMillis() );
		}

//		Intent intent = new Intent( LiveStreamService.ACTION_PLAY );
//		intent.putExtra( LiveStreamService.KEY_CHANNEL_ID, program.getChannelInfo().getChannelId() );
//		intent.putExtra( LiveStreamService.KEY_START_TIMESTAMP, program.getStartTime().getMillis() );
//		getActivity().startService( intent );

		startPlayer( false );
		
		Log.i( TAG, "startPlayStreamService : exit" );
	}

	private void startCreateStreamService() {
		Log.i( TAG, "startCreateStreamService : enter" );
		
		if( null != program ) {
			Log.v( TAG, "startCreateStreamService : channelId=" + program.getChannelInfo().getChannelId() + ", startTime=" + program.getStartTime().getMillis() );
		}
		
		Intent intent = new Intent( LiveStreamService.ACTION_CREATE );
		intent.putExtra( LiveStreamService.KEY_CHANNEL_ID, program.getChannelInfo().getChannelId() );
		intent.putExtra( LiveStreamService.KEY_START_TIMESTAMP, program.getStartTime().getMillis() );
		getActivity().startService( intent );

		Log.i( TAG, "startCreateStreamService : exit" );
	}
	
	private void startUpdateStreamService() {
		Log.i( TAG, "startUpdateStreamService : enter" );
		
		if( null != program ) {
			Log.v( TAG, "startUpdateStreamService : channelId=" + program.getChannelInfo().getChannelId() + ", startTime=" + program.getStartTime().getMillis() );

			LiveStreamInfo liveStreamInfo = mLiveStreamDaoHelper.findByProgram( getActivity(), mLocationProfile, program );
			if( null != liveStreamInfo ) {
			
				if( liveStreamInfo.getPercentComplete() < 100 ) {

					Intent intent = new Intent( LiveStreamService.ACTION_LOAD );
					intent.putExtra( LiveStreamService.KEY_CHANNEL_ID, program.getChannelInfo().getChannelId() );
					intent.putExtra( LiveStreamService.KEY_START_TIMESTAMP, program.getStartTime().getMillis() );
					getActivity().startService( intent );
					
				}
			
			}
			
		}

		Log.i( TAG, "startUpdateStreamService : exit" );
	}
	
	private void startRemoveStreamService() {
		Log.i( TAG, "startRemoveStreamService : enter" );
		
		if( null != program ) {
			Log.v( TAG, "startRemoveStreamService : channelId=" + program.getChannelInfo().getChannelId() + ", startTime=" + program.getStartTime().getMillis() );
		}

		getActivity().stopService( new Intent( getActivity(), LiveStreamService.class ) );
		
		Intent intent = new Intent( LiveStreamService.ACTION_REMOVE );
		intent.putExtra( LiveStreamService.KEY_CHANNEL_ID, program.getChannelInfo().getChannelId() );
		intent.putExtra( LiveStreamService.KEY_START_TIMESTAMP, program.getStartTime().getMillis() );
		getActivity().startService( intent );

		Log.i( TAG, "startRemoveStreamService : exit" );
	}
	
	private void startRemoveProgramService() {
		Log.i( TAG, "startRemoveProgramService : enter" );
		
		if( null != program ) {
			Log.v( TAG, "startRemoveProgramService : channelId=" + program.getChannelInfo().getChannelId() + ", startTime=" + program.getStartTime().getMillis() );
		}

		startRemoveStreamService();
		
		Intent intent = new Intent( RecordedService.ACTION_REMOVE );
		intent.putExtra( RecordedService.KEY_CHANNEL_ID, program.getChannelInfo().getChannelId() );
		intent.putExtra( RecordedService.KEY_START_TIMESTAMP, program.getStartTime().getMillis() );
		intent.putExtra( RecordedService.KEY_RECORD_ID, program.getRecording().getRecordId() );
		getActivity().startService( intent );

		Log.i( TAG, "startRemoveProgramService : exit" );
	}
	
	private void setDismissAddPreference( boolean isChecked ) {
		
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean( DISMISS_ADD, isChecked );
		editor.commit();
		
	}
	
	private void updateHlsDetails() {
		Log.v( TAG, "updateHlsDetails : enter" );
		
		if( null != hlsView ) {
		
			LiveStreamInfo liveStreamInfo = mLiveStreamDaoHelper.findByProgram( getActivity(), mLocationProfile, program );
			if( null != liveStreamInfo ) {
				Log.v( TAG, "updateHlsDetails : live stream found, liveStreamInfo=" + liveStreamInfo.toString() );

				if( liveStreamInfo.getPercentComplete() == 100 ) {
					hlsView.setText( "WATCH IT NOW!" );
					
				} else if( liveStreamInfo.getPercentComplete() >= 0 ) {
					hlsView.setText( "Processing " + liveStreamInfo.getPercentComplete() + "%" );

				} else {
					hlsView.setText( "" );
				}

			} else {
				hlsView.setText( "" );
			}
		
		} else {
			
	        hlsView = (TextView) getActivity().findViewById( R.id.textView_episode_hls );
			hlsView.setText( "" );

		}
		
        updateHlsMenuButtons();
        
		Log.v( TAG, "updateHlsDetails : exit" );
	}
	
	private void updateHlsMenuButtons() {
		Log.v( TAG, "updateHlsMenuButtons : enter" );
		
        LiveStreamInfo liveStreamInfo = mLiveStreamDaoHelper.findByProgram( getActivity(), mLocationProfile, program );
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

	private class RecordedRemovedReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.i( TAG, "RecordedRemovedReceiver.onReceive : enter" );
			
	        if ( intent.getAction().equals( RecordedService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "RecordedRemovedReceiver.onReceive : complete=" + intent.getStringExtra( RecordedService.EXTRA_COMPLETE ) );
	        	
	        	if( intent.getExtras().containsKey( RecordedService.EXTRA_COMPLETE_OFFLINE ) ) {
	        		notConnectedNotify();
	        	} else {
	        		
	        		if( null != intent.getStringExtra( RecordedService.EXTRA_COMPLETE ) && intent.getStringExtra( RecordedService.EXTRA_COMPLETE ).startsWith( "Episode" ) && intent.getStringExtra( RecordedService.EXTRA_COMPLETE ).endsWith( "deleted!" ) ) {
	        		
	        			if( null != program ) {
	        				ProgramGroup programGroup = mProgramGroupDaoHelper.findByTitle( getActivity(), mLocationProfile, program.getTitle() );
	        				listener.onEpisodeDeleted( programGroup );
	        			} else {
	        				listener.onEpisodeDeleted( null );
	        			}

	        		} else {
	        			
	        			listener.onEpisodeDeleted( null );
	        			
	        		}
	        		
	        	}
	        	
	        }

        	Log.i( TAG, "RecordedRemovedReceiver.onReceive : exit" );
		}
		
	}

	private class LiveStreamReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.i( TAG, "LiveStreamReceiver.onReceive : enter" );
			
	        if ( intent.getAction().equals( LiveStreamService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "LiveStreamReceiver.onReceive : progress=" + intent.getIntExtra( LiveStreamService.EXTRA_PROGRESS_ID, -1 ) + ":" + intent.getIntExtra( LiveStreamService.EXTRA_PROGRESS_DATA, -1 ) );
	        	
	        }
	        
	        if ( intent.getAction().equals( LiveStreamService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "LiveStreamReceiver.onReceive : complete=" + intent.getStringExtra( LiveStreamService.EXTRA_COMPLETE ) );
	        	
	        	if( intent.getExtras().containsKey( LiveStreamService.EXTRA_COMPLETE_OFFLINE ) ) {
	        		notConnectedNotify();
	        	}
	        	
	        	if( intent.getExtras().containsKey( LiveStreamService.EXTRA_COMPLETE_ID ) ) {
	        		startUpdateStreamService();
	        	}
	        	
	        	if( intent.getExtras().containsKey( LiveStreamService.EXTRA_COMPLETE_ERROR ) ) {
//	        		Toast.makeText( getActivity(), intent.getStringExtra( LiveStreamService.EXTRA_COMPLETE_ERROR ), Toast.LENGTH_SHORT ).show();
	        	} 
	        	
	        	if( intent.getExtras().containsKey( LiveStreamService.EXTRA_COMPLETE_PLAY ) ) {
	        		
        			startPlayer( intent.getExtras().getBoolean( LiveStreamService.EXTRA_COMPLETE_RAW ) );
	        		
	        	}
	        	
	        	if( intent.getExtras().containsKey( LiveStreamService.EXTRA_COMPLETE_REMOVE ) ) {

	        		Integer programCount = mProgramGroupDaoHelper.countProgramsByTitle( getActivity(), mLocationProfile, program.getTitle() );
	        		if( null != programCount && programCount.intValue() > 0 ) {
	        			ProgramGroup programGroup = mProgramGroupDaoHelper.findByTitle( getActivity(), mLocationProfile, program.getTitle() );
	        			listener.onEpisodeDeleted( programGroup );
	        		} else {
	        			listener.onEpisodeDeleted( null );
	        		}
       			
	        	}
	        	
	        }

        	updateHlsDetails();
    		
        	Log.i( TAG, "LiveStreamReceiver.onReceive : exit" );
		}
		
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.content.GetLiveStreamTask.TaskFinishedListener#onGetLiveStreamTaskStarted()
	 */
	@Override
	public void onGetLiveStreamTaskStarted() {
		Log.i( TAG, "onGetLiveStreamTaskStarted : enter" );

		Log.i( TAG, "onGetLiveStreamTaskStarted : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.content.GetLiveStreamTask.TaskFinishedListener#onGetLiveStreamTaskFinished(org.mythtv.db.content.model.LiveStreamInfo)
	 */
	@Override
	public void onGetLiveStreamTaskFinished( LiveStreamInfo result ) {
		Log.i( TAG, "onGetLiveStreamTaskFinished : enter" );

		if( null != result ) {
			updateHlsDetails();
		}
		
		Log.i( TAG, "onGetLiveStreamTaskFinished : exit" );
	}

}
