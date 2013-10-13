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

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.client.ui.util.MenuItemRefreshAnimated;
import org.mythtv.db.channel.ChannelDaoHelper;
import org.mythtv.db.channel.model.ChannelInfo;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.service.guide.ProgramGuideDownloadService;
import org.mythtv.service.util.DateUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * @author Daniel Frey
 *
 */
public class GuideFragment extends AbstractMythFragment 
	implements 
		GuideChannelFragment.OnChannelScrollListener,
//		GuideTimeslotsFragment.OnTimeslotScrollListener,
		GuideDatePickerFragment.OnDialogResultListener {

	private static final String TAG = GuideFragment.class.getSimpleName();
	
	private FragmentManager mFragmentManager;
	
	private ChannelDaoHelper mChannelDaoHelper = ChannelDaoHelper.getInstance();
	private MenuHelper mMenuHelper = MenuHelper.getInstance();
	
	private ProgramGuideDownloadReceiver programGuideDownloadReceiver = new ProgramGuideDownloadReceiver();

//	private TextView mProgramGuideDate;
	private GuideChannelFragment mGuideChannelFragment;
//	private GuideTimeslotsFragment mGuideTimeslotsFragment;
	private GuideDataFragment mGuideDataFragment;
	
	private List<ChannelInfo> channels = new ArrayList<ChannelInfo>();
	
	private DateTime today;
//	private List<DateTime> dateRange = new ArrayList<DateTime>();
	
	private LocationProfile mLocationProfile;
	
	private int downloadDays;
	private int selectedChannelId;
	private DateTime selectedDate;

	private MenuItemRefreshAnimated mMenuItemRefresh;

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.GuideDatePickerFragment.OnDialogResultListener#onDateChanged(org.joda.time.DateTime)
	 */
	@Override
	public void onDateChanged( DateTime date ) {
		Log.v( TAG, "onDateChanged : enter" );

		Log.v( TAG, "onDateChanged : date=" + date.toString() );

		selectedDate = date.withTimeAtStartOfDay();
		Log.v( TAG, "onDateChanged : selectedDate=" + selectedDate.toString() );
		
		updateView();
		
//		timeslotSelect( date.getHourOfDay() + ":00:00" );
		
		Log.v( TAG, "onDateChanged : enter" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );

		mMenuItemRefresh = new MenuItemRefreshAnimated( getActivity() );

		//inflate fragment layout
		View view = inflater.inflate( R.layout.fragment_dvr_guide, container, false );

		Log.v( TAG, "onCreateView : exit" );
		return view;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );
		
		setHasOptionsMenu( true );

		View view = getView();
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( getActivity() );
		downloadDays = Integer.parseInt( sp.getString( "preference_program_guide_days", "14" ) );

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		
		channels = mChannelDaoHelper.findAll( getActivity(), mLocationProfile );
		selectedChannelId = channels.get( 0 ).getChannelId();
		
		today = new DateTime( DateTimeZone.getDefault() ).withTimeAtStartOfDay();
		selectedDate = today;
		Log.v( TAG, "onActivityCreated : selectedDate=" + selectedDate.toString() );
		
//		dateRange.add( today );
//		for( int i = 1; i < downloadDays; i++ ) {
//			dateRange.add( today.plusDays( i ) );
//		}
		
//		mProgramGuideDate = (TextView) getActivity().findViewById( R.id.program_guide_date );
//		mProgramGuideDate.setText( DateUtils.getDateTimeUsingLocaleFormattingPrettyDateOnly( today, getMainApplication().getDateFormat() ) );
		
		// get child fragment manager
		mFragmentManager = getChildFragmentManager();

		// look for program guide channels list placeholder frame layout
		FrameLayout channelsLayout = (FrameLayout) view.findViewById( R.id.frame_layout_program_guide_channels );
		if( null != channelsLayout ) {
			Log.v( TAG, "onActivityCreated : loading channels fragment" );
			
			mGuideChannelFragment = (GuideChannelFragment) mFragmentManager.findFragmentByTag( GuideChannelFragment.class.getName() );
			if( null == mGuideChannelFragment ) {
				mGuideChannelFragment = (GuideChannelFragment) GuideChannelFragment.instantiate( getActivity(), GuideChannelFragment.class.getName() );
				mGuideChannelFragment.setOnChannelScrollListener( this );
			}

			mFragmentManager.beginTransaction()
				.replace( R.id.frame_layout_program_guide_channels, mGuideChannelFragment, GuideChannelFragment.class.getName() )
				.commit();
		
		}

/*		FrameLayout timeslotsLayout = (FrameLayout) view.findViewById( R.id.frame_layout_program_guide_timeslots );
		if( null != timeslotsLayout ) {
			Log.v( TAG, "onActivityCreated : loading timeslots fragment" );
			
			mGuideTimeslotsFragment = (GuideTimeslotsFragment) mFragmentManager.findFragmentByTag( GuideTimeslotsFragment.class.getName() );
			if( null == mGuideTimeslotsFragment ) {
				mGuideTimeslotsFragment = (GuideTimeslotsFragment) GuideTimeslotsFragment.instantiate( getActivity(), GuideTimeslotsFragment.class.getName() );
				mGuideTimeslotsFragment.setOnTimeslotScrollListener( this );
			}

			mFragmentManager.beginTransaction()
				.replace( R.id.frame_layout_program_guide_timeslots, mGuideTimeslotsFragment, GuideTimeslotsFragment.class.getName() )
				.commit();
		
		}
*/
		FrameLayout dataLayout = (FrameLayout) view.findViewById( R.id.frame_layout_program_guide_data );
		if( null != dataLayout ) {
			Log.v( TAG, "onActivityCreated : loading data fragment" );
			
			mGuideDataFragment = (GuideDataFragment) mFragmentManager.findFragmentByTag( GuideDataFragment.class.getName() );
			if( null == mGuideDataFragment ) {
				mGuideDataFragment = (GuideDataFragment) GuideDataFragment.instantiate( getActivity(), GuideDataFragment.class.getName() );
				//mGuideTimeslotsFragment.setOnDataScrollListener( this );

				Bundle args = new Bundle();
				args.putInt( ProgramConstants.FIELD_CHANNEL_ID, selectedChannelId );
				args.putLong( ProgramConstants.FIELD_END_TIME, selectedDate.getMillis() );
				mGuideDataFragment.setArguments( args );

			}
			
			mFragmentManager.beginTransaction()
				.replace( R.id.frame_layout_program_guide_data, mGuideDataFragment, GuideDataFragment.class.getName() )
				.commit();
		
		}

		Log.v( TAG, "onActivityCreated : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		Log.v( TAG, "onStart : enter" );
		super.onStart();

		IntentFilter programGuideDownloadFilter = new IntentFilter( ProgramGuideDownloadService.ACTION_DOWNLOAD );
		programGuideDownloadFilter.addAction( ProgramGuideDownloadService.ACTION_PROGRESS );
		programGuideDownloadFilter.addAction( ProgramGuideDownloadService.ACTION_COMPLETE );
        getActivity().registerReceiver( programGuideDownloadReceiver, programGuideDownloadFilter );

		Log.v( TAG, "onStart : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();

//		timeslotSelect( new DateTime().getHourOfDay() + ":00:00" );

		updateView();
		
		Log.v( TAG, "onResume : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		Log.v( TAG, "onStop : enter" );
		super.onStop();
		
		if( null != programGuideDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( programGuideDownloadReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
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

		if( downloadDays > 1 ) {
			mMenuHelper.guideDayMenuItem( getActivity(), menu );
		}
		
		mMenuHelper.refreshMenuItem( getActivity(), menu, mMenuItemRefresh );
		if( mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.guide.ProgramGuideDownloadService" ) ) {
			mMenuItemRefresh.startRefreshAnimation();
		}

		Log.v( TAG, "onCreateOptionsMenu : exit" );
	}


	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.AbstractRecordingsActivity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );
		
		switch( item.getItemId() ) {
			case MenuHelper.GUIDE_ID:
				Log.d( TAG, "onOptionsItemSelected : guide day selected" );

				showDatePickerFragment();
			
				return true;
			
			case MenuHelper.REFRESH_ID:
				Log.d( TAG, "onOptionsItemSelected : refresh selected" );
				
				if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.dvr.ProgramGuideDownloadService" ) ) {
				
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( getActivity() );
					int downloadDays = Integer.parseInt( sp.getString( "preference_program_guide_days", "14" ) );

					for( int i = 0; i < ( ( downloadDays * 24 ) / 3 ); i++ ) {
						
						EtagInfoDelegate etag = mEtagDaoHelper.findByEndpointAndDataId( getActivity(), mLocationProfile, "GetProgramGuide", String.valueOf( i ) );
						if( null != etag ) {
							mEtagDaoHelper.delete( getActivity(), mLocationProfile, etag );
						}
						
					}
					
					getActivity().startService( new Intent( ProgramGuideDownloadService.ACTION_DOWNLOAD ) );
					
					mMenuItemRefresh.startRefreshAnimation();
				}
				
				return true;
		}
		
		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	// GuideChannelFragment interface
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.GuideChannelFragment.OnChannelScrollListener#channelScroll(int, int, int)
	 */
	@Override
	public void channelScroll( int first, int last, int screenCount, int totalCount ) {
		Log.v( TAG, "channelScroll : enter" );
		
		Log.v( TAG, "channelScroll : first=" + first + ", last=" + last + ", screenCount=" + screenCount + ", totalCount=" + totalCount );

		ChannelInfo start = mGuideChannelFragment.getChannel( first );
		Log.v( TAG, "channelScroll : start=" + start.toString() );

		ChannelInfo end = mGuideChannelFragment.getChannel( last );
		Log.v( TAG, "channelScroll : end=" + end.toString() );
		
		Log.v( TAG, "channelScroll : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.GuideChannelFragment.OnChannelScrollListener#channelSelect(int)
	 */
	@Override
	public void channelSelect( int channelId ) {
		Log.v( TAG, "channelSelect : enter" );
		
		Log.v( TAG, "channelSelect : channelId=" + channelId );
		selectedChannelId = channelId;
		
		updateView();
//		timeslotSelect( new DateTime().getHourOfDay() + ":00:00" );

		Log.v( TAG, "channelSelect : exit" );
	}

	// GuideTimeslotsFragment interface
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.GuideTimeslotsFragment.OnTimeslotScrollListener#timeslotSelect(java.lang.String)
	 */
/*	@Override
	public void timeslotSelect( String time ) {
		Log.v( TAG, "timeslotSelect : enter" );
		
		Log.v( TAG, "timeslotSelect : time=" + time );
		
		String[] values = time.split( ":" );
		DateTime scrollDate = selectedDate.withTime( Integer.parseInt( values[ 0 ] ), Integer.parseInt( values[ 1 ] ), Integer.parseInt( values[ 2 ] ), 0 );
		Log.v( TAG, "timeslotSelect : scrollDate=" + scrollDate );
		
		mGuideDataFragment.scroll( selectedChannelId, scrollDate );
		
		Log.v( TAG, "timeslotSelect : exit" );
	}
*/
	// internal helpers
	
	private void updateView() {
		Log.v( TAG, "updateView : enter" );
	
		getActivity().getActionBar().setTitle( DateUtils.getDateTimeUsingLocaleFormattingPrettyDateOnly( selectedDate, getMainApplication().getDateFormat() ) );
		
		mGuideDataFragment.updateView( selectedChannelId, selectedDate );
//		mProgramGuideDate.setText( DateUtils.getDateTimeUsingLocaleFormattingPrettyDateOnly( selectedDate, getMainApplication().getDateFormat() ) );
		
		Log.v( TAG, "updateView : exit" );
	}
	
	private void showDatePickerFragment() {
		Log.v( TAG, "showDatePickerFragment : enter" );

		Bundle args = new Bundle();
		args.putLong( "selectedDate", selectedDate.getMillis() );
		args.putInt( "downloadDays", downloadDays );
		
		GuideDatePickerFragment datePickerFragment = new GuideDatePickerFragment();
		datePickerFragment.setOnDialogResultListener( this );
		datePickerFragment.setArguments( args );
		
		datePickerFragment.show( getChildFragmentManager(), "datePickerFragment" );

		Log.v( TAG, "showDatePickerFragment : exit" );
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
	        	
	        	mMenuItemRefresh.stopRefreshAnimation();
	        }
	        
	        updateView();
	        
        	Log.v( TAG, "ProgramGuideDownloadReceiver.onReceive : exit" );
		}
		
	}

}
