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
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.guide.ProgramGuideWrapper;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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
	    getListView().setFastScrollEnabled( true );
	    
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
			Log.v( TAG, "GuideRowAdapter.getView : position=" + position );
			
			convertView = mInflater.inflate( R.layout.guide_row, parent, false );
			ViewHolder mHolder = new ViewHolder();
			
			mHolder.timeSlotContainer = (LinearLayout) convertView.findViewById( R.id.guide_row );
			mHolder.channel = (TextView) convertView.findViewById( R.id.guide_channel );
			
			int textColor = getResources().getColor( R.color.body_text_1 );
			
			ChannelInfo channel = getItem( position );
			if( null != channel && !channel.getPrograms().isEmpty() ) {
				Log.v( TAG, "GuideRowAdapter.getView : channel retrieved - " + channel.getChannelNumber() );

				mHolder.channel.setText( channel.getChannelNumber() );
				for( Program program : channel.getPrograms() ) {
					Log.v( TAG, "GuideRowAdapter.getView : program iteration" );

					StringBuilder ally = new StringBuilder();
					ally.append( DateUtils.timeFormatter.print( program.getStartTime() ) ).append( " " );
					ally.append( "Title: " ).append( program.getTitle() ).append( " " );
					ally.append( "Description: " ).append( program.getSubTitle() );

					LinearLayout timeslot = (LinearLayout) new LinearLayout( mContext ); 
					LayoutParams lParams = new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
					lParams.weight = 0.5f;
					timeslot.setLayoutParams(lParams); 
					timeslot.setOrientation( LinearLayout.HORIZONTAL );

					View category = (View) new View( mContext );
					category.setLayoutParams( new LayoutParams( 10, LayoutParams.MATCH_PARENT ) ); 
					category.setBackgroundColor( mProgramHelper.getCategoryColor( program.getCategory() ) );
					timeslot.addView( category );

					LinearLayout details = (LinearLayout) new LinearLayout( mContext ); 
					details.setLayoutParams( new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT ) );
					details.setOrientation( LinearLayout.VERTICAL );

					TextView title = (TextView) new TextView( mContext );
					title.setLayoutParams( new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ) ); 
					title.setText( program.getTitle() );
					title.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 12.0f );
					title.setTextColor( textColor );
					title.setPadding( 8, 4, 8, 1 );
					title.setEllipsize( TruncateAt.END );
					title.setSingleLine( true );
					title.setHorizontallyScrolling( true );
					title.setContentDescription( ally );
					details.addView( title );

					TextView description = (TextView)  new TextView( mContext );
					description.setLayoutParams( new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ) ); 
					description.setText( program.getSubTitle() );
					description.setTextColor( textColor );
					description.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 9.0f );
					description.setPadding( 8, 2, 8, 4 );
					description.setEllipsize( TruncateAt.END );
					description.setSingleLine( true );
					description.setHorizontallyScrolling( true );
					details.addView( description );

					timeslot.addView( details );

					mHolder.timeSlotContainer.addView( timeslot );
				}
			}
			
			Log.v( TAG, "GuideRowAdapter.getView : exit" );
			return convertView;
		}

		private class ViewHolder {
			
			LinearLayout timeSlotContainer;
			TextView channel;
			
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
