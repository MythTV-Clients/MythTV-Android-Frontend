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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.util.MythtvListFragment;
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
import android.widget.TextView;

/**
 * @author Daniel Frey
 *
 */
public class GuidePagerFragment extends MythtvListFragment {

	private static final String TAG = GuidePagerFragment.class.getSimpleName();
	
	private GuideRowAdapter adapter;

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

			int year = Integer.parseInt( startDate.substring( 0, startDate.indexOf( '-' ) ) );
			int month = Integer.parseInt( startDate.substring( startDate.indexOf( '-' ) + 1, startDate.lastIndexOf( '-' ) ) ) - 1;
			int day = Integer.parseInt( startDate.substring( startDate.lastIndexOf( '-' ) + 1 ) );
			int hour = Integer.parseInt( timeslot ) - 1;
			
			Calendar startTime = Calendar.getInstance();
			startTime.set( year, month, day, hour, 0, 01 );

			Calendar endTime = Calendar.getInstance();
			endTime.set( startTime.get( Calendar.YEAR ), startTime.get( Calendar.MONTH ), startTime.get( Calendar.DATE ), startTime.get( Calendar.HOUR_OF_DAY ), 59, 59 );
		
			Log.i( TAG, "Loading Guide between " + DateUtils.dateTimeFormatter.format( startTime.getTime() ) + " and " + DateUtils.dateTimeFormatter.format( endTime.getTime() ) );
			
			if( null == channels ) {
				new DownloadProgramGuideTask().execute( startTime.getTime(), endTime.getTime() );
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
				mHolder.timeSlot1 = (TextView) convertView.findViewById( R.id.guide_slot_1 );
				mHolder.timeSlot2 = (TextView) convertView.findViewById( R.id.guide_slot_2 );
				
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
						mHolder.timeSlot1.setText( channel.getPrograms().get( 0 ).getTitle() );
					} catch( IndexOutOfBoundsException e ) {
						mHolder.timeSlot1.setText( "empty" );
					}

					try {
						mHolder.timeSlot2.setText( channel.getPrograms().get( 1 ).getTitle() );
					} catch( IndexOutOfBoundsException e ) {
						mHolder.timeSlot2.setText( "empty" );
					}
				}
			}
			
			Log.v( TAG, "GuideRowAdapter.getView : exit" );
			return convertView;
		}

		private class ViewHolder {
			
			TextView channel;
			TextView timeSlot1;
			TextView timeSlot2;
			
			ViewHolder() { }

		}
		
		private class DownloadProgramGuideTask extends AsyncTask<Date, Void, ProgramGuideWrapper> {

			@Override
			protected ProgramGuideWrapper doInBackground( Date... params ) {
				Log.v( TAG, "GuideRowAdapter.DownloadProgramGuideTask.doInBackground : enter" );

				Date startTime = params[ 0 ];
				Date endTime = params[ 1 ];
				
				Log.v( TAG, "GuideRowAdapter.DownloadProgramGuideTask.doInBackground : startTime" + DateUtils.dateTimeFormatter.format( startTime.getTime() ) + ", endTime=" + DateUtils.dateTimeFormatter.format( endTime.getTime() )  );
				
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
					Log.v( TAG, "GuideRowAdapter.DownloadProgramGuideTask.onPostExecute : " + DateUtils.dateTimeFormatter.format( result.getProgramGuide().getStartTime() ) + " endTime" + DateUtils.dateTimeFormatter.format( result.getProgramGuide().getStartTime() ));
					
					Log.v( TAG, "GuideRowAdapter.DownloadProgramGuideTask.onPostExecute : channels retrieved, updating adapter" );

					channels = result.getProgramGuide().getChannels();
					
					notifyDataSetChanged();
				}
			
				Log.v( TAG, "GuideRowAdapter.DownloadProgramGuideTask.onPostExecute : enter" );
			}
			
		}

	}
	
}
