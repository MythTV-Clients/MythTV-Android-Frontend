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

import java.util.List;

import org.joda.time.DateTimeZone;
import org.mythtv.R;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;
import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.client.ui.util.MenuItemRefreshAnimated;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.db.dvr.model.Program;
import org.mythtv.db.status.model.BackendStatus;
import org.mythtv.db.status.model.Encoder;
import org.mythtv.db.status.model.Job;
import org.mythtv.service.channel.ChannelDownloadService;
import org.mythtv.service.dvr.RecordedService;
import org.mythtv.service.dvr.RecordingRuleService;
import org.mythtv.service.dvr.UpcomingDownloadService;
import org.mythtv.service.frontends.FrontendsDiscoveryService;
import org.mythtv.service.guide.ProgramGuideDownloadService;
import org.mythtv.service.status.BackendStatusService;
import org.mythtv.service.util.DateUtils;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author Thomas G. Kenny Jr
 * 
 */
public class BackendStatusFragment extends AbstractMythFragment {

	private static final String TAG = BackendStatusFragment.class.getSimpleName();
	public static final String BACKEND_STATUS_FRAGMENT_NAME = "org.mythtv.client.ui.BackendStatusFragment";

	private ProgramHelper mProgramHelper = ProgramHelper.getInstance();
	
	private BackendStatusReceiver backendStatusReceiver = new BackendStatusReceiver();
	private ChannelDownloadReceiver channelDownloadReceiver = new ChannelDownloadReceiver();
	private FrontendsDiscoveryReceiver frontendsDiscoveryReceiver = new FrontendsDiscoveryReceiver();
	private ProgramGuideDownloadReceiver programGuideDownloadReceiver = new ProgramGuideDownloadReceiver();
	private RecordedDownloadReceiver recordedDownloadReceiver = new RecordedDownloadReceiver();
	private RecordingRuleDownloadReceiver recordingRuleDownloadReceiver = new RecordingRuleDownloadReceiver();
	private UpcomingDownloadReceiver upcomingDownloadReceiver = new UpcomingDownloadReceiver();

	private int mPanelStartY;
	private View mView;
	private MenuItemRefreshAnimated mMenuItemRefresh;
	private LocationProfile mLocationProfile;
	private LinearLayout mLinearLayoutEncodersList;
	private LinearLayout mLinearLayoutUpcomingRecsList;
	private LinearLayout mLinearLayoutJobQueueList;
	private LinearLayout mLinearLayoutStatusCard;
	private LinearLayout mLinearLayoutEncodersCard;
	private LinearLayout mLinearLayoutUpcomingRecsCard;
	private LinearLayout mLinearLayoutJobQueueCard;
	private TextView mTextViewEncodersEmpty;
	private TextView mTextViewJobQueueEmpty;
	private TextView mTextViewUpcomingRecEmpty;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		Log.v( TAG, "onStart : enter" );
		super.onStart();

		IntentFilter backendStatusFilter = new IntentFilter( BackendStatusService.ACTION_DOWNLOAD );
		backendStatusFilter.addAction( BackendStatusService.ACTION_COMPLETE );
	    getActivity().registerReceiver( backendStatusReceiver, backendStatusFilter );

		IntentFilter channelDownloadFilter = new IntentFilter( ChannelDownloadService.ACTION_DOWNLOAD );
		channelDownloadFilter.addAction( ChannelDownloadService.ACTION_PROGRESS );
		channelDownloadFilter.addAction( ChannelDownloadService.ACTION_COMPLETE );
	    getActivity().registerReceiver( channelDownloadReceiver, channelDownloadFilter );

		IntentFilter programGuideDownloadFilter = new IntentFilter( ProgramGuideDownloadService.ACTION_DOWNLOAD );
		programGuideDownloadFilter.addAction( ProgramGuideDownloadService.ACTION_PROGRESS );
		programGuideDownloadFilter.addAction( ProgramGuideDownloadService.ACTION_COMPLETE );
        getActivity().registerReceiver( programGuideDownloadReceiver, programGuideDownloadFilter );

		IntentFilter recordedDownloadFilter = new IntentFilter( RecordedService.ACTION_DOWNLOAD );
		recordedDownloadFilter.addAction( RecordedService.ACTION_PROGRESS );
		recordedDownloadFilter.addAction( RecordedService.ACTION_COMPLETE );
        getActivity().registerReceiver( recordedDownloadReceiver, recordedDownloadFilter );

		IntentFilter upcomingDownloadFilter = new IntentFilter( UpcomingDownloadService.ACTION_DOWNLOAD );
		upcomingDownloadFilter.addAction( UpcomingDownloadService.ACTION_PROGRESS );
		upcomingDownloadFilter.addAction( UpcomingDownloadService.ACTION_COMPLETE );
	    getActivity().registerReceiver( upcomingDownloadReceiver, upcomingDownloadFilter );

		IntentFilter recordingRuleDownloadFilter = new IntentFilter( RecordingRuleService.ACTION_DOWNLOAD );
		recordingRuleDownloadFilter.addAction( RecordingRuleService.ACTION_PROGRESS );
		recordingRuleDownloadFilter.addAction( RecordingRuleService.ACTION_COMPLETE );
        getActivity().registerReceiver( recordingRuleDownloadReceiver, recordingRuleDownloadFilter );

		IntentFilter frontendsDiscoveryFilter = new IntentFilter( FrontendsDiscoveryService.ACTION_DISCOVER );
		frontendsDiscoveryFilter.addAction( FrontendsDiscoveryService.ACTION_PROGRESS );
		frontendsDiscoveryFilter.addAction( FrontendsDiscoveryService.ACTION_COMPLETE );
	    getActivity().registerReceiver( frontendsDiscoveryReceiver, frontendsDiscoveryFilter );

	    Log.v( TAG, "onStart : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.d( TAG, "onCreateView : enter" );

		setHasOptionsMenu( true );

		mMenuItemRefresh = new MenuItemRefreshAnimated( this.getActivity() );

		mPanelStartY = container.getHeight() + 1;

		mView = inflater.inflate( R.layout.fragment_backend_status, null, false );

		mLinearLayoutEncodersList = (LinearLayout) mView.findViewById( R.id.linearlayout_encoders_list );
		mLinearLayoutUpcomingRecsList = (LinearLayout) mView.findViewById( R.id.linearlayout_upcoming_recordings_list );
		mLinearLayoutJobQueueList = (LinearLayout) mView.findViewById( R.id.linearlayout_job_queue );

		mLinearLayoutStatusCard = (LinearLayout) mView.findViewById( R.id.linearlayout_backendstatus_status_card );
		mLinearLayoutStatusCard.setAlpha( 0 );
		mLinearLayoutStatusCard.setTranslationY( mPanelStartY );
		mLinearLayoutEncodersCard = (LinearLayout) mView.findViewById( R.id.linearlayout_backendstatus_encoders_card );
		mLinearLayoutEncodersCard.setAlpha( 0 );
		mLinearLayoutEncodersCard.setTranslationY( mPanelStartY );
		mLinearLayoutUpcomingRecsCard = (LinearLayout) mView.findViewById( R.id.linearlayout_backendstatus_upcoming_recordings_card );
		mLinearLayoutUpcomingRecsCard.setAlpha( 0 );
		mLinearLayoutUpcomingRecsCard.setTranslationY( mPanelStartY );
		mLinearLayoutJobQueueCard = (LinearLayout) mView.findViewById( R.id.linearlayout_backendstatus_job_queue_card );
		mLinearLayoutJobQueueCard.setAlpha( 0 );
		mLinearLayoutJobQueueCard.setTranslationY( mPanelStartY );

		mTextViewEncodersEmpty = (TextView) mView.findViewById( R.id.textview_encoders_list_empty );
		mTextViewJobQueueEmpty = (TextView) mView.findViewById( R.id.textview_job_queue_empty );
		mTextViewUpcomingRecEmpty = (TextView) mView.findViewById( R.id.textview_upcoming_rec_empty );

		Log.d( TAG, "onCreateView : exit" );
		return mView;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		Log.v( TAG, "onStop : enter" );
		super.onStop();

		// Unregister for broadcast
		if( null != backendStatusReceiver ) {
			try {
				getActivity().unregisterReceiver( backendStatusReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		// Unregister for broadcast
		if( null != channelDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( channelDownloadReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onDestroy : error", e );
			}
		}

		if( null != programGuideDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( programGuideDownloadReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onDestroy : error", e );
			}
		}

		if( null != recordedDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( recordedDownloadReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onDestroy : error", e );
			}
		}

		if( null != recordingRuleDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( recordingRuleDownloadReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onDestroy : error", e );
			}
		}
		
		if( null != upcomingDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( upcomingDownloadReceiver );
				upcomingDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onDestroy : error", e );
			}
		}

		if( null != frontendsDiscoveryReceiver ) {
			try {
				getActivity().unregisterReceiver( frontendsDiscoveryReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onDestroy : error", e );
			}
		}

		Log.v( TAG, "onStop : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu,
	 * android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		super.onCreateOptionsMenu( menu, inflater );

		MenuHelper.getInstance().refreshMenuItem( getActivity(), menu, mMenuItemRefresh );

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem
	 * )
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {

		switch( item.getItemId() ) {
			case MenuHelper.REFRESH_ID:
			
				getStatus();
			
				return true;

		}
		
		return super.onOptionsItemSelected( item );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.d( TAG, "onResume : enter" );
		super.onResume();

		getStatus();

		Log.d( TAG, "onResume : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged( Configuration newConfig ) {

		// re- attach the fragment forcing the view to be recreated.
		Fragment currentFragment = getFragmentManager().findFragmentByTag( BACKEND_STATUS_FRAGMENT_NAME );
		if( null != currentFragment ) {
			FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
			fragTransaction.detach( currentFragment );
			fragTransaction.attach( currentFragment );
			fragTransaction.commit();
		}

		super.onConfigurationChanged( newConfig );
	}

	// internal helpers

	private void animateCardLinearLayout( final LinearLayout linearLayout, long startDelay ) {
		linearLayout.setAlpha( 1 );

		// animator that translates linearlayout
		AnimatorUpdateListener translationAnimatorListener = new AnimatorUpdateListener() {
			
			/* (non-Javadoc)
			 * @see android.animation.ValueAnimator.AnimatorUpdateListener#onAnimationUpdate(android.animation.ValueAnimator)
			 */
			@Override
			public void onAnimationUpdate( ValueAnimator animation ) {
				Float w = (Float) animation.getAnimatedValue();
				linearLayout.setTranslationY( w );
			}
			
		};

		ValueAnimator scaleAnimator = ValueAnimator.ofFloat( linearLayout.getTranslationY(), 0f );
		scaleAnimator.setDuration( 500 );
		scaleAnimator.setRepeatCount( 0 );
		scaleAnimator.setStartDelay( startDelay );
		scaleAnimator.addUpdateListener( translationAnimatorListener );

		scaleAnimator.start();
	}

	private void resetStatusUi() {
		mLinearLayoutStatusCard.setAlpha( 0 );
		mLinearLayoutStatusCard.setTranslationY( mPanelStartY );
		mLinearLayoutEncodersCard.setAlpha( 0 );
		mLinearLayoutEncodersCard.setTranslationY( mPanelStartY );
		mLinearLayoutUpcomingRecsCard.setAlpha( 0 );
		mLinearLayoutUpcomingRecsCard.setTranslationY( mPanelStartY );
		mLinearLayoutJobQueueCard.setAlpha( 0 );
		mLinearLayoutJobQueueCard.setTranslationY( mPanelStartY );
	}

	/**
	 * 
	 */
	private void getStatus() {

		resetStatusUi();

		if( null != mView ) {

			TextView tView = (TextView) mView.findViewById( R.id.textview_status );
			if( null != tView ) {
				tView.setText( getStatusText() );
			}
		}

		if( null != mLinearLayoutStatusCard ) {
			animateCardLinearLayout( mLinearLayoutStatusCard, 0 );
		}
	}

	/**
	 * Returns the current backend connection status text and starts a
	 * BackendStatusTask to get more details.
	 * 
	 * @return
	 */
	private String getStatusText() {
		Log.v( TAG, "getStatusText : enter" );

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		
		if( null == mLocationProfile ) {
			Log.v( TAG, "getStatusText : exit, no connected profiles found" );

			return "The selected Backend profile's host isn't responding";
		}

		Log.v( TAG, "getStatusText : mLocationProfile=" + mLocationProfile.toString() );

		checkBackendStatusService();

		mMenuItemRefresh.startRefreshAnimation();

		Log.v( TAG, "getStatusText : exit" );
		return ( mLocationProfile.isConnected() ? "Connected to " : "NOT Connected to " ) + mLocationProfile.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Called at the end of BackendStatusTask.onPostExecute() when result is not
	 * null.
	 */
	private void onBackendStatusUpdated( BackendStatus result ) {

		this.mMenuItemRefresh.stopRefreshAnimation();

		LayoutInflater inflater = LayoutInflater.from( this.getActivity() );

		// clear lists
		mLinearLayoutEncodersList.removeAllViews();
		mLinearLayoutUpcomingRecsList.removeAllViews();
		mLinearLayoutJobQueueList.removeAllViews();

		// Set encoder list
		List<Encoder> encoders = result.getEncoders().getEncoders();
		if( null != encoders ) {
			mLinearLayoutEncodersList.setVisibility( View.VISIBLE );
			mTextViewEncodersEmpty.setVisibility( View.GONE );

			for( int i = 0; i < encoders.size(); i++ ) {
				mLinearLayoutEncodersList.addView( this.getEncoderView( inflater, encoders.get( i ) ) );
			}
		} else {
			mLinearLayoutEncodersList.setVisibility( View.GONE );
			mTextViewEncodersEmpty.setVisibility( View.VISIBLE );
		}

		if( null != mLinearLayoutEncodersCard ) {
			animateCardLinearLayout( mLinearLayoutEncodersCard, 0 );
		}

		List<Job> jobs = result.getJobQueue().getJobs();
		if( null != jobs ) {
			mLinearLayoutJobQueueList.setVisibility( View.VISIBLE );
			mTextViewJobQueueEmpty.setVisibility( View.GONE );

			for( int i = 0; i < jobs.size(); i++ ) {
				mLinearLayoutJobQueueList.addView( this.getJobView( inflater, jobs.get( i ) ) );
			}
		} else {
			mLinearLayoutJobQueueList.setVisibility( View.GONE );
			mTextViewJobQueueEmpty.setVisibility( View.VISIBLE );
		}

		if( null != mLinearLayoutJobQueueCard ) {
			animateCardLinearLayout( mLinearLayoutJobQueueCard, 250 );
		}

		// Set Upcoming recordings list
		List<Program> programs = result.getScheduled().getPrograms();
		if( null != programs && !programs.isEmpty() ) {
			mLinearLayoutUpcomingRecsList.setVisibility( View.VISIBLE );
			mTextViewUpcomingRecEmpty.setVisibility( View.GONE );

			for( int i = 0; i < programs.size(); i++ ) {
				mLinearLayoutUpcomingRecsList.addView( this.getUpcomingRecView( inflater, programs.get( i ) ) );
			}
		} else {
			mLinearLayoutUpcomingRecsList.setVisibility( View.GONE );
			mTextViewUpcomingRecEmpty.setVisibility( View.VISIBLE );
		}

		if( null != mLinearLayoutUpcomingRecsCard ) {
			animateCardLinearLayout( mLinearLayoutUpcomingRecsCard, 500 );
		}
	}

	private View getEncoderView( LayoutInflater inflater, Encoder encoder ) {

		View view = (View) inflater.inflate( R.layout.backend_status_encoder_list_item, null, false );

		// set device label
		TextView tView = (TextView) view.findViewById( R.id.textView_encoder_devicelabel );
		if( null != tView ) {
			tView.setText( Integer.toString( encoder.getId() ) + " - " + encoder.getDeviceLabel() );
		}

		// set device host
		tView = (TextView) view.findViewById( R.id.textView_encoder_host );
		if( null != tView ) {
			tView.setText( encoder.getHostname() );
		}

		// set device recording status
		tView = (TextView) view.findViewById( R.id.textView_encoder_rec_status );
		if( null != tView ) {
			Program rec = encoder.getRecording();
			if( null != rec ) {
				tView.setText( rec.getTitle() + " on " + rec.getChannelInfo().getChannelName() );
				// + rec.getEndTime().toString("hh:mm") );
			} else {
				tView.setText( "Inactive" );
			}
		}

		return view;
	}

	private View getUpcomingRecView( LayoutInflater inflater, Program program ) {

		View view = (View) inflater.inflate( R.layout.backend_status_upcoming_list_item, null, false );

		// set category color
		View category = view.findViewById( R.id.upcoming_category );
		if( null != category )
			category.setBackgroundColor( mProgramHelper.getCategoryColor( program.getCategory() ) );

		// set upcoming_title
		TextView tView = (TextView) view.findViewById( R.id.upcoming_title );
		if( null != tView ) {
			tView.setText( program.getTitle() );
		}

		// set upcoming_sub_title
		tView = (TextView) view.findViewById( R.id.upcoming_sub_title );
		if( null != tView ) {
			tView.setText( program.getSubTitle() );
		}

		// set upcoming_channel
		tView = (TextView) view.findViewById( R.id.upcoming_channel );
		if( null != tView ) {
			tView.setText( program.getChannelInfo().getCallSign() );
		}

		// set upcoming_start_time
		tView = (TextView) view.findViewById( R.id.upcoming_start_time );
		if( null != tView ) {
			tView.setText( DateUtils.getDayTimeWithLocaleFormatting(
					program.getStartTime().withZone( DateTimeZone.getDefault() ), getMainApplication().getClockType() ) );
		}

		// set upcoming_duration
		tView = (TextView) view.findViewById( R.id.upcoming_duration );
		if( null != tView ) {
			tView.setText( Long.toString( program.getDurationInMinutes() ) );
		}

		return view;

	}

	private View getJobView( LayoutInflater inflater, Job job ) {

		View view = (View) inflater.inflate( R.layout.backend_status_job_list_item, null, false );

		// set title
		TextView tView = (TextView) view.findViewById( R.id.textView_job_program_title );
		if( null != tView ) {
			tView.setText( job.getProgram() != null ? job.getProgram().getTitle() : "" );
		}

		// set type
		tView = (TextView) view.findViewById( R.id.textView_job_type );
		if( null != tView ) {
			tView.setText( job.getType() != null ? job.getType().name() : "" );
		}

		// set status
		tView = (TextView) view.findViewById( R.id.textView_job_status );
		if( null != tView ) {
			tView.setText( getJobStatusStr( job.getStatus() ) );
		}

		return view;

	}

	private String getJobStatusStr( Job.Status status ) {
		switch( status ) {
		case ABORTED:
			return getString( R.string.job_queue_status_aborted );
		case ABORTING:
			return getString( R.string.job_queue_status_aborting );
		case CANCELLED:
			return getString( R.string.job_queue_status_cancelled );
		case DONE:
			return getString( R.string.job_queue_status_done );
		case ERRORED:
			return getString( R.string.job_queue_status_errored );
		case ERRORING:
			return getString( R.string.job_queue_status_erroring );
		case FINISHED:
			return getString( R.string.job_queue_status_finished );
		case NO_FLAGS:
			return getString( R.string.job_queue_status_no_flags );
		case PAUSED:
			return getString( R.string.job_queue_status_paused );
		case PENDING:
			return getString( R.string.job_queue_status_pending );
		case QUEUED:
			return getString( R.string.job_queue_status_queued );
		case RETRY:
			return getString( R.string.job_queue_status_retry );
		case RUNNING:
			return getString( R.string.job_queue_status_running );
		case STARTING:
			return getString( R.string.job_queue_status_starting );
		case STOPPING:
			return getString( R.string.job_queue_status_stopping );
		default:
			return getString( R.string.job_queue_status_unknown );
		}
	}

    protected void checkBackendStatusService() {

    	startBackendStatusService();

    }
    
    private void startBackendStatusService() {
    	
		if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.status.BackendStatusService" ) ) {
			getActivity().startService( new Intent( BackendStatusService.ACTION_DOWNLOAD ) );
		}

    }
    
    private void checkFrontendDiscoveryService() {
      	 
    	startFrontendDiscoveryService();

    }
    
    private void startFrontendDiscoveryService() {

		if( mLocationProfile.getType().equals( LocationType.HOME ) ) {
			
			if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.frontends.FrontendsDiscoveryService" ) ) {
				getActivity().startService( new Intent( FrontendsDiscoveryService.ACTION_DISCOVER ) );
			}
			
		}
   	
    }
    
    private void checkChannelDownloadService() {
    	 
		startChannelDownloadService();

    }
    
    private void startChannelDownloadService() {

    	if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.channel.ChannelDownloadService" ) ) {
    		getActivity().startService( new Intent( ChannelDownloadService.ACTION_DOWNLOAD ) );
		}
   	
    }
    
    private void checkProgramGuideDownloadService() {

		startProgramGuideDownloadService();

    }
    
    private void startProgramGuideDownloadService() {
    	
		if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.guide.ProgramGuideDownloadService" ) ) {
			getActivity().startService( new Intent( ProgramGuideDownloadService.ACTION_DOWNLOAD ) );
		}

    }
    
    private void checkRecordedDownloadService() {

		startRecordedDownloadService();
		
    }
    
    private void startRecordedDownloadService() {
    	
		if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.dvr.RecordedDownloadService" ) ) {
			getActivity().startService( new Intent( RecordedService.ACTION_DOWNLOAD ) );
		}

    }
    
    private void checkRecordingRulesDownloadService() {

		startRecordingRulesDownloadService();
		
    }
    
    private void startRecordingRulesDownloadService() {
    	
		if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.dvr.RecordingRuleService" ) ) {
			getActivity().startService( new Intent( RecordingRuleService.ACTION_DOWNLOAD ) );
		}

    }
    
    private void checkUpcomingDownloadService() {

    	startUpcomingDownloadService();
		
    }
    
    private void startUpcomingDownloadService() {
    	
		if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.dvr.UpcomingDownloadService" ) ) {
			getActivity().startService( new Intent( UpcomingDownloadService.ACTION_DOWNLOAD ) );
		}

    }
    
	private class BackendStatusReceiver extends BroadcastReceiver {

		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.v( TAG, "BackendStatusReceiver.onReceive : enter" );
			
	        if( intent.getAction().equals( BackendStatusService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "BackendStatusReceiver.onReceive : " + intent.getStringExtra( BackendStatusService.EXTRA_COMPLETE ) );

	        	if( !intent.getBooleanExtra( BackendStatusService.EXTRA_COMPLETE_OFFLINE, false ) ) {
	        		
	        		if( null != intent.getSerializableExtra( BackendStatusService.EXTRA_COMPLETE_DATA ) ) {
	        			mStatus = (BackendStatus) intent.getSerializableExtra( BackendStatusService.EXTRA_COMPLETE_DATA );
	        		
	        			onBackendStatusUpdated( mStatus );
	        			
	        		}
	        		
        			checkFrontendDiscoveryService();
        			checkChannelDownloadService();
	        	}
	        	
	        }

        	Log.v( TAG, "BackendStatusReceiver.onReceive : exit" );
		}
		
	}

	private class ChannelDownloadReceiver extends BroadcastReceiver {

		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.v( TAG, "ChannelDownloadReceiver.onReceive : enter" );

	        if( intent.getAction().equals( ChannelDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "ChannelDownloadReceiver.onReceive : progress=" + intent.getStringExtra( ChannelDownloadService.EXTRA_PROGRESS ) );
	        }
	        
	        if( intent.getAction().equals( ChannelDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "ChannelDownloadReceiver.onReceive : " + intent.getStringExtra( ChannelDownloadService.EXTRA_COMPLETE ) );
	        	
        		checkRecordedDownloadService();
        		
	        }

        	Log.v( TAG, "ChannelDownloadReceiver.onReceive : exit" );
		}
		
	}

	private class RecordedDownloadReceiver extends BroadcastReceiver {

		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.v( TAG, "RecordedDownloadReceiver.onReceive : enter" );
			
	        if( intent.getAction().equals( RecordedService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : progress=" + intent.getStringExtra( RecordedService.EXTRA_PROGRESS ) );
	        }
	        
	        if( intent.getAction().equals( RecordedService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : complete=" + intent.getStringExtra( RecordedService.EXTRA_COMPLETE ) );
	        	
	        	checkUpcomingDownloadService();

	        }

        	Log.v( TAG, "RecordedDownloadReceiver.onReceive : exit" );
		}
		
	}

	private class RecordingRuleDownloadReceiver extends BroadcastReceiver {

		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.v( TAG, "RecordingRuleDownloadReceiver.onReceive : enter" );
			
	        if ( intent.getAction().equals( RecordingRuleService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "RecordingRuleDownloadReceiver.onReceive : progress=" + intent.getStringExtra( RecordingRuleService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( RecordingRuleService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "RecordingRuleDownloadReceiver.onReceive : complete=" + intent.getStringExtra( RecordingRuleService.EXTRA_COMPLETE ) );
	        	
	        	checkProgramGuideDownloadService();

	        }

        	Log.v( TAG, "RecordingRuleDownloadReceiver.onReceive : exit" );
		}
		
	}

	private class UpcomingDownloadReceiver extends BroadcastReceiver {

		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.v( TAG, "UpcomingDownloadReceiver.onReceive : enter" );

	        if ( intent.getAction().equals( UpcomingDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "UpcomingDownloadReceiver.onReceive : " + intent.getStringExtra( UpcomingDownloadService.EXTRA_PROGRESS ) );
	        	
	        	String filename = intent.getStringExtra( UpcomingDownloadService.EXTRA_PROGRESS_FILENAME );
	        	if( null != filename && !"".equals( filename ) ) {
	        		Log.d( TAG, "UpcomingDownloadReceiver.onReceive : removing from cache" + filename );
	        	}
	        }
	        
	        if ( intent.getAction().equals( UpcomingDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "UpcomingDownloadReceiver.onReceive : " + intent.getStringExtra( UpcomingDownloadService.EXTRA_COMPLETE ) );
	        	
	        	checkRecordingRulesDownloadService();

	        }
	        
        	Log.v( TAG, "UpcomingDownloadReceiver.onReceive : exit" );
		}
		
	}

	private class ProgramGuideDownloadReceiver extends BroadcastReceiver {

		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.v( TAG, "ProgramGuideDownloadReceiver.onReceive : enter" );

	        if ( intent.getAction().equals( ProgramGuideDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiver.onReceive : " + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_PROGRESS ) );
	        	
	        }
	        
	        if ( intent.getAction().equals( ProgramGuideDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "ProgramGuideDownloadReceiver.onReceive : " + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_COMPLETE ) );
	        	
	        }
	        
        	Log.v( TAG, "ProgramGuideDownloadReceiver.onReceive : exit" );
		}
		
	}

	private class FrontendsDiscoveryReceiver extends BroadcastReceiver {

		/* (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.v( TAG, "FrontendsDiscoveryReceiver.onReceive : enter" );

	        if ( intent.getAction().equals( FrontendsDiscoveryService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "FrontendsDiscoveryReceiver.onReceive : progress=" + intent.getStringExtra( FrontendsDiscoveryService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( ChannelDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "FrontendsDiscoveryReceiver.onReceive : " + intent.getStringExtra( FrontendsDiscoveryService.EXTRA_COMPLETE ) );
	        }

        	Log.v( TAG, "FrontendsDiscoveryReceiver.onReceive : exit" );
		}
		
	}

}
