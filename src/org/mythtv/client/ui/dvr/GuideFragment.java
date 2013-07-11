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

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.dvr.ProgramGuideDaoHelper;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.guide.ProgramGuide;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @author Daniel Frey
 *
 */
public class GuideFragment extends AbstractMythFragment implements GuideChannelFragment.OnChannelScrollListener {

	private static final String TAG = GuideFragment.class.getSimpleName();
	
	private FragmentManager mFragmentManager;
	
	private ProgramGuideDaoHelper mProgramGuideDaoHelper = ProgramGuideDaoHelper.getInstance();
	
	private TextView mProgramGuideDate;
	private GuideChannelFragment mGuideChannelFragment;
	private GuideTimeslotsFragment mGuideTimeslotsFragment;
	private GuideDataFragment mGuideDataFragment;
	
	private int downloadDays;
	private DateTime today;
	private Map<DateTime, ProgramGuide> dateRange = new HashMap<DateTime, ProgramGuide>();
	
	private LocationProfile mLocationProfile;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );

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
		
		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		
		View view = getView();
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( getActivity() );
		downloadDays = Integer.parseInt( sp.getString( "preference_program_guide_days", "14" ) );

		today = new DateTime().withTimeAtStartOfDay();
		
		new ProgramGuideTask().execute();
		
		mProgramGuideDate = (TextView) getActivity().findViewById( R.id.program_guide_date );
		mProgramGuideDate.setText( DateUtils.getDateTimeUsingLocaleFormattingPrettyDateOnly( today, getMainApplication().getDateFormat() ) );
		
		// get child fragment manager
		mFragmentManager = getFragmentManager();

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

		FrameLayout timeslotsLayout = (FrameLayout) view.findViewById( R.id.frame_layout_program_guide_timeslots );
		if( null != timeslotsLayout ) {
			Log.v( TAG, "onActivityCreated : loading timeslots fragment" );
			
			mGuideTimeslotsFragment = (GuideTimeslotsFragment) mFragmentManager.findFragmentByTag( GuideTimeslotsFragment.class.getName() );
			if( null == mGuideTimeslotsFragment ) {
				mGuideTimeslotsFragment = (GuideTimeslotsFragment) GuideTimeslotsFragment.instantiate( getActivity(), GuideTimeslotsFragment.class.getName() );
				//mGuideTimeslotsFragment.setOnTimeslotScrollListener( this );
			}

			mFragmentManager.beginTransaction()
				.replace( R.id.frame_layout_program_guide_timeslots, mGuideTimeslotsFragment, GuideTimeslotsFragment.class.getName() )
				.commit();
		
			//mGuideTimeslotsFragment.updateTimeslot( today );
			
		}

		FrameLayout dataLayout = (FrameLayout) view.findViewById( R.id.frame_layout_program_guide_data );
		if( null != dataLayout ) {
			Log.v( TAG, "onActivityCreated : loading data fragment" );
			
			mGuideDataFragment = (GuideDataFragment) mFragmentManager.findFragmentByTag( GuideDataFragment.class.getName() );
			if( null == mGuideDataFragment ) {
				mGuideDataFragment = (GuideDataFragment) GuideDataFragment.instantiate( getActivity(), GuideDataFragment.class.getName() );
				//mGuideTimeslotsFragment.setOnDataScrollListener( this );
			}

			mFragmentManager.beginTransaction()
				.replace( R.id.frame_layout_program_guide_data, mGuideDataFragment, GuideDataFragment.class.getName() )
				.commit();
		
		}

		Log.v( TAG, "onActivityCreated : exit" );
	}

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

	// internal helpers
	
	private class ProgramGuideTask extends AsyncTask<Void, Void, Void> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground( Void...voids ) {
			
			DateTime add = today.withTimeAtStartOfDay();
			
			dateRange.put( add, mProgramGuideDaoHelper.getProgramGuideForDate( getActivity(), mLocationProfile, add ) );
			for( int i = 1; i < downloadDays; i++ ) {
				dateRange.put( add.plusDays( i ), mProgramGuideDaoHelper.getProgramGuideForDate( getActivity(), mLocationProfile, add.plusDays( i ) ) );
			}

			return null;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result) {
			
			mGuideChannelFragment.changeChannels( ( dateRange.get( today ) ).getChannels() );
			mGuideDataFragment.changeChannels( ( dateRange.get( today ) ).getChannels() );
			
			mGuideTimeslotsFragment.updateTimeslot( new DateTime() );
			
		}
		
	}
	
}
