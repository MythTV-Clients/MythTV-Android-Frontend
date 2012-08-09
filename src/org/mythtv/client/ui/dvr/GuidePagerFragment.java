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

import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.guide.ProgramGuideWrapper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Daniel Frey
 *
 */
public class GuidePagerFragment extends MythtvListFragment {

	private static final String TAG = GuidePagerFragment.class.getSimpleName();
	
	private GuideRowAdapter adapter;

	private ProgramHelper mProgramHelper;

	public static GuidePagerFragment newInstance( String startDate, String timeslot ) {
		Log.v( TAG, "newInstance : enter" );
		GuidePagerFragment fragment = new GuidePagerFragment();
		
		Log.v( TAG, "newInstance : startDate=" + startDate + ", timeslot=" + timeslot );
		
		Bundle args = new Bundle();
		args.putString( "START_DATE", startDate );
		args.putString( "TIMESLOT", timeslot );
		fragment.setArguments( args );

		Log.v( TAG, "newInstance : exit" );
		return fragment;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		mProgramHelper = ProgramHelper.createInstance( getActivity() );
		
		setHasOptionsMenu( true );
		
		setRetainInstance( true );

		Bundle args = getArguments();
		String startDate = args.getString( "START_DATE" );
		String timeslot = args.getString( "TIMESLOT" );
		
	    adapter = new GuideRowAdapter( getActivity().getApplicationContext(), startDate, timeslot );
	    
	    setListAdapter( adapter );
		
		Log.v( TAG, "onActivityCreated : exit" );
	}

	// internal helpers
	
	private class GuideRowAdapter extends BaseAdapter {

		private Context mContext;
		private LayoutInflater mInflater;

		private List<ChannelInfo> channels;
		
		public GuideRowAdapter( Context context, String startDate, String timeslot ) {
			Log.v( TAG, "GuideRowAdapter : enter" );

			Log.i( TAG, "GuideRowAdapter : startDate=" + startDate + ", timeslot=" + timeslot );

			mContext = context;
			mInflater = LayoutInflater.from( context );

			int hour = Integer.parseInt( timeslot );
			
			DateTime startTime = new DateTime( startDate );
			startTime = startTime.withHourOfDay( hour ).withMinuteOfHour( 0 ).withSecondOfMinute( 01 );

			DateTime endTime = new DateTime( startDate );
			endTime = endTime.withHourOfDay( hour ).withMinuteOfHour( 59 ).withSecondOfMinute( 59 );
		
			Log.i( TAG, "Loading Guide between " + DateUtils.dateTimeFormatter.print( startTime ) + " and " + DateUtils.dateTimeFormatter.print( endTime ) );
			
			if( null == channels ) {
				new DownloadProgramGuideTask().execute( startTime, endTime );
			}
			
			Log.v( TAG, "GuideRowAdapter : exit" );
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			if( null == channels ) {
				return 0;
			}
			
			return channels.size();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public ChannelInfo getItem( int position ) {
			if( null == channels ) {
				return null;
			}
			
			return channels.get( position );
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId( int position ) {
			return position;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			Log.v( TAG, "GuideRowAdapter.getView : enter" );

			ViewHolder mHolder;
			
			if( convertView == null ) {
				Log.v( TAG, "GuideRowAdapter.getView : creating new view" );

				convertView = mInflater.inflate( R.layout.guide_row, parent, false );
				
				mHolder = new ViewHolder();
				
				mHolder.channel = (TextView) convertView.findViewById( R.id.guide_channel );
				mHolder.timeSlotContainer1 = (LinearLayout) convertView.findViewById(R.id.guide_container_slot_1);
				mHolder.category1 = (View) convertView.findViewById( R.id.guide_slot_1_category );
				mHolder.timeSlot1 = (TextView) convertView.findViewById( R.id.guide_slot_1 );
				mHolder.timeSlotDescription1 = (TextView) convertView.findViewById( R.id.guide_slot_1_description );
				mHolder.timeSlotContainer2 = (LinearLayout) convertView.findViewById(R.id.guide_container_slot_2);
				mHolder.category2 = (View) convertView.findViewById( R.id.guide_slot_2_category );
				mHolder.timeSlot2 = (TextView) convertView.findViewById( R.id.guide_slot_2 );
				mHolder.timeSlotDescription2 = (TextView) convertView.findViewById( R.id.guide_slot_2_description );
				
				convertView.setTag( mHolder );
			} else { 
				Log.v( TAG, "GuideRowAdapter.getView : retrieving cached view" );

				mHolder = (ViewHolder) convertView.getTag();
			}

			ChannelInfo channel = getItem( position );
			if( null != channel ) {
				Log.v( TAG, "GuideRowAdapter.getView : channel retrieved" );

				mHolder.channel.setText( channel.getChannelNumber() );
				if( !channel.getPrograms().isEmpty() ) {
					Log.v( TAG, "GuideRowAdapter.getView : setting programs" );

					try {
						StringBuilder ally = new StringBuilder();
						ally.append( DateUtils.timeFormatter.print( channel.getPrograms().get( 0 ).getStartTime() ) ).append( " " );
						ally.append( "Title: " ).append( channel.getPrograms().get( 0 ).getTitle() ).append( " " );
						ally.append( "Description: " ).append( channel.getPrograms().get( 0 ).getSubTitle() );
						
						mHolder.category1.setBackgroundColor( mProgramHelper.getCategoryColor( channel.getPrograms().get( 0 ).getCategory() ) );
						mHolder.timeSlot1.setText( channel.getPrograms().get( 0 ).getTitle() );
						mHolder.timeSlot1.setContentDescription( ally );
						mHolder.timeSlotDescription1.setText( channel.getPrograms().get( 0 ).getSubTitle() );
					} catch( IndexOutOfBoundsException e ) {
						mHolder.timeSlot1.setText( "empty" );
						mHolder.timeSlotContainer1.setVisibility( View.GONE );
					}

					try {
						StringBuilder ally = new StringBuilder();
						ally.append( DateUtils.timeFormatter.print( channel.getPrograms().get( 0 ).getStartTime() ) ).append( " " );
						ally.append( "Title: " ).append( channel.getPrograms().get( 0 ).getTitle() ).append( " " );
						mHolder.timeSlot2.setContentDescription( ally );
						ally.append( "Description: " ).append( channel.getPrograms().get( 0 ).getSubTitle() );

						mHolder.category2.setBackgroundColor( mProgramHelper.getCategoryColor( channel.getPrograms().get( 1 ).getCategory() ) );
						mHolder.timeSlot2.setText( channel.getPrograms().get( 1 ).getTitle() );
						mHolder.timeSlotDescription2.setText( channel.getPrograms().get( 1 ).getSubTitle() );
					} catch( IndexOutOfBoundsException e ) {
						mHolder.timeSlot2.setText( "empty" );
						mHolder.timeSlotContainer2.setVisibility( View.GONE );
					}
				}
			}
			
			Log.v( TAG, "GuideRowAdapter.getView : exit" );
			return convertView;
		}

		private class ViewHolder {
			
			TextView channel;

			LinearLayout timeSlotContainer1;
			View category1;
			TextView timeSlot1;
			TextView timeSlotDescription1;

			LinearLayout timeSlotContainer2;
			View category2;
			TextView timeSlot2;
			TextView timeSlotDescription2;
			
			ViewHolder() { }

		}
		
		private class DownloadProgramGuideTask extends AsyncTask<DateTime, Void, ProgramGuideWrapper> {

			@Override
			protected ProgramGuideWrapper doInBackground( DateTime... params ) {
				Log.v( TAG, "GuideRowAdapter.DownloadProgramGuideTask.doInBackground : enter" );

				DateTime startTime = params[ 0 ];
				DateTime endTime = params[ 1 ];
				Log.v( TAG, "GuideRowAdapter.DownloadProgramGuideTask.doInBackground : downloading program guide between " + DateUtils.dateTimeFormatter.print( startTime ) + " and "  + DateUtils.dateTimeFormatter.print( endTime ) );
				
				Log.v( TAG, "GuideRowAdapter.DownloadProgramGuideTask.doInBackground : exit" );
				return ( (MainApplication) mContext.getApplicationContext() ).getMythServicesApi().guideOperations().getProgramGuide( startTime, endTime, 1, -1, Boolean.FALSE );
			}

			/* (non-Javadoc)
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute( ProgramGuideWrapper result ) {
				Log.v( TAG, "GuideRowAdapter.DownloadProgramGuideTask.onPostExecute : enter" );

				if( null != result && null != result.getProgramGuide() && !result.getProgramGuide().getChannels().isEmpty() ) {
					Log.v( TAG, "GuideRowAdapter.DownloadProgramGuideTask.onPostExecute : channels retrieved, updating adapter" );

					List<ChannelInfo> channelInfos = result.getProgramGuide().getChannels();
					Collections.sort( channelInfos );
					channels = channelInfos;
					
					notifyDataSetChanged();
				}
			
				Log.v( TAG, "GuideRowAdapter.DownloadProgramGuideTask.onPostExecute : exit" );
			}
			
		}

	}
	
}
