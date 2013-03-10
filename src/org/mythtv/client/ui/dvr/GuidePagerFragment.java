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
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.service.guide.cache.ProgramGuideLruMemoryCache;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.guide.ProgramGuide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.util.TimingLogger;
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
@SuppressLint( "ValidFragment" )
public class GuidePagerFragment extends MythtvListFragment {

	private static final String TAG = GuidePagerFragment.class.getSimpleName();
	
	private GuideRowAdapter adapter;

	private ProgramHelper mProgramHelper;

    private MainApplication mainApplication;

	private String startDate, timeslot;
	private ProgramGuide programGuide;
	
	public static GuidePagerFragment newInstance( String startDate, String timeslot, ProgramGuide programGuide ) {
		Log.v( TAG, "newInstance : enter" );

		Log.d( TAG, "newInstance : startDate=" + startDate + ", timeslot=" + timeslot );
		
		GuidePagerFragment fragment = new GuidePagerFragment( startDate, timeslot, programGuide );
		
		Log.v( TAG, "newInstance : exit" );
		return fragment;
	}

	private GuidePagerFragment( String startDate, String timeslot, ProgramGuide programGuide ) {
		this.startDate = startDate;
		this.timeslot = timeslot;
		this.programGuide = programGuide;
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

        mainApplication = (MainApplication) getActivity().getApplicationContext();
        mProgramHelper = ProgramHelper.getInstance();
		
		setHasOptionsMenu( true );

		//setRetainInstance( true );

	    adapter = new GuideRowAdapter( getActivity().getApplicationContext(), startDate, timeslot, programGuide );
	    
	    setListAdapter( adapter );
	    getListView().setFastScrollEnabled( true );
	    
		Log.v( TAG, "onActivityCreated : exit" );
	}

	// internal helpers
	
	private class GuideRowAdapter extends BaseAdapter {

		private Context mContext;
		private LayoutInflater mInflater;

		private List<ChannelInfo> channels;
		private DateTime startTime;
		private DateTime endTime;
		private long timeSlotLengthMillis;
		
		public GuideRowAdapter( Context context, String startDate, String timeslot, ProgramGuide programGuide ) {
			Log.v( TAG, "GuideRowAdapter : enter" );

			Log.i( TAG, "GuideRowAdapter : startDate=" + startDate + ", timeslot=" + timeslot );

			mContext = context;
			mInflater = LayoutInflater.from( context );

			int hour = Integer.parseInt( timeslot );
			
			startTime = new DateTime( startDate );
			startTime = startTime.withTime( hour, 0, 0, 0 );

			endTime = new DateTime( startDate );
			endTime = endTime.withTime( hour, 59, 59, 999 );
		
			timeSlotLengthMillis = endTime.getMillis() - startTime.getMillis(); 
			
			if( null == channels ) {
				Log.v( TAG, "GuideRowAdapter : channels is null, loading program guide from cache" );
				
				if( null != programGuide ) {
					Log.v( TAG, "GuideRowAdapter : program guide loaded from cache" );

					channels = programGuide.getChannels();
				} else {
					Log.v( TAG, "GuideRowAdapter : program guide NOT loaded from cache" );

					channels = ProgramGuideLruMemoryCache.getDownloadingProgramGuide( startTime ).getChannels();
				}
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
//			Log.v( TAG, "GuideRowAdapter.getView : enter" );
			
			TimingLogger tl = new TimingLogger( TAG, "getView" );
			
			convertView = mInflater.inflate( R.layout.guide_row, parent, false );
			ViewHolder mHolder = new ViewHolder();
			
			mHolder.timeSlotContainer = (LinearLayout) convertView.findViewById( R.id.guide_row );
			mHolder.channel = (TextView) convertView.findViewById( R.id.guide_channel );
			tl.addSplit( "load viewholder" );			
			
			int textColor = getResources().getColor( R.color.body_text_1 );
			
			ChannelInfo channel = getItem( position );
			tl.addSplit( "get channel" );
			if( null != channel && !channel.getPrograms().isEmpty() ) {
//				Log.v( TAG, "GuideRowAdapter.getView : channel retrieved - " + channel.getChannelNumber() );

				mHolder.channel.setText( channel.getChannelNumber() );

                String clockFormat = "hh:mm";
                if (mainApplication.getClockType() != null && mainApplication.getClockType().equals("24")) {
                    clockFormat = "HH:mm";
                }

				int count = 0;
				float weightSum = 0.0f;
				for( Program program : channel.getPrograms() ) {
//					Log.v( TAG, "GuideRowAdapter.getView : program iteration" );

					LinearLayout timeslot = (LinearLayout) new LinearLayout( mContext ); 
					LayoutParams lParams = new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );
					lParams.weight = getProgramDurationLayoutWeight(program);
					weightSum += lParams.weight;
					timeslot.setLayoutParams(lParams); 
					timeslot.setOrientation( LinearLayout.HORIZONTAL );

					View category = (View) new View( mContext );
					category.setLayoutParams( new LayoutParams( 10, LayoutParams.MATCH_PARENT ) ); 
					category.setBackgroundColor( mProgramHelper.getCategoryColor( program.getCategory() ) );
					timeslot.addView( category );
					
					LinearLayout recStatus = new LinearLayout( mContext );
					recStatus.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
					recStatus.setPadding(4,4,4,4);
					if( null != program.getRecording() ) {
						if( 4 == program.getRecording().getStatus() ) {
							recStatus.setBackgroundColor( Color.BLUE );
						}

						if( -1 == program.getRecording().getStatus() ) {
							recStatus.setBackgroundColor( Color.GREEN );
						}

						if( -2 == program.getRecording().getStatus() ) {
							recStatus.setBackgroundColor( Color.RED );
						}
					}

					LinearLayout details = (LinearLayout) new LinearLayout( mContext ); 
					details.setLayoutParams( new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT ) );
					details.setOrientation( LinearLayout.VERTICAL );
					details.setBackgroundColor(getResources().getColor( R.color.background_1 ));
					recStatus.addView(details);

					TextView title = (TextView) new TextView( mContext );
					title.setLayoutParams( new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ) ); 
					title.setText( program.getTitle() );
					title.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 12.0f );
					title.setTypeface(Typeface.DEFAULT_BOLD);
					title.setTextColor( textColor );
					title.setPadding( 8, 4, 8, 1 );
					title.setEllipsize( TruncateAt.END );
					title.setSingleLine( true );
					title.setHorizontallyScrolling( true );
					title.setContentDescription( program.getTitle() );
					details.addView( title );
				
					TextView textViewTime = (TextView)  new TextView( mContext );
					textViewTime.setLayoutParams( new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ) ); 
					textViewTime.setText( this.utcToLocal(program.getStartTime()).toString(clockFormat) + " - " + this.utcToLocal(program.getEndTime()).toString(clockFormat) );
					textViewTime.setTextColor( textColor );
					textViewTime.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 10.0f );
					textViewTime.setTypeface(Typeface.DEFAULT_BOLD);
					textViewTime.setPadding( 8, 2, 8, 4 );
					textViewTime.setEllipsize( TruncateAt.END );
					textViewTime.setSingleLine( true );
					textViewTime.setHorizontallyScrolling( true );
					details.addView( textViewTime );

					TextView description = (TextView)  new TextView( mContext );
					description.setLayoutParams( new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ) ); 
					description.setText( program.getSubTitle() );
					description.setTextColor( textColor );
					description.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 10.0f );
					description.setTypeface(Typeface.DEFAULT_BOLD);
					description.setPadding( 8, 2, 8, 4 );
					description.setEllipsize( TruncateAt.END );
					description.setSingleLine( true );
					description.setHorizontallyScrolling( true );
					details.addView( description );
										
					timeslot.addView( recStatus );

					mHolder.timeSlotContainer.addView( timeslot );
					
					tl.addSplit( "program " + count );			

					count++;
				}
				
				mHolder.timeSlotContainer.setWeightSum(weightSum);
			}
			
			tl.dumpToLog();			

//			Log.v( TAG, "GuideRowAdapter.getView : exit" );
			return convertView;
		}
		
		/**
		 * Calculates the layout weight a program should have for the
		 * current timeslot.
		 * 
		 * @author Thomas G. Kenny Jr
		 * @param program
		 * @return
		 */
		private float getProgramDurationLayoutWeight(Program program) {
			
			//get the program total duration in milliseconds
			long programDurationMillis = program.getEndTime().getMillis() - program.getStartTime().getMillis();
			
			//ignore program duration before current timeslot
			if(program.getStartTime().isBefore(startTime)){ 
				programDurationMillis -= startTime.minus(program.getStartTime().getMillis()).getMillis();
			}
			
			//ignore program duration after current time slot
			if(program.getEndTime().isAfter(endTime)){
				programDurationMillis -= program.getEndTime().minus(endTime.getMillis()).getMillis();
			}
			
			// calculate percentage of timeslot milliseconds.
			// Round to the nearest 100th.
			// protect against divide by 0
			float weight = timeSlotLengthMillis != 0 ? 
					Math.round(((float) programDurationMillis / (float) timeSlotLengthMillis) * 100.00f) / 100.00f
					: 0;
					
			//if we got a weight <= 0 then set it to 1%.
			weight = weight > 0 ? weight : 0.01f;
			
			//invert
			weight = 1.0f - weight;
			
//			//debug
//			Log.d(TAG, "Show Title: " + program.getTitle());
//			Log.d(TAG, "timeslotlength: " + timeSlotLengthMillis);
//			Log.d(TAG, "Show duration in current timeslot: " + programDurationMillis);
//			Log.d(TAG, "P starttime: " + program.getStartTime().getMillis());
//			Log.d(TAG, "P endtime: " + program.getEndTime().getMillis());
//			Log.d(TAG, "P Diff: " + (program.getEndTime().getMillis() - program.getStartTime().getMillis()));
//			Log.d(TAG, "Weight: " + weight);
			
			//done
			return weight;
		}
		
		/**
		 * Converts the given UTC DateTime to Local DateTime.
		 * @param dt
		 * @return
		 */
		private DateTime utcToLocal(DateTime dt){
			return dt.toDateTime(DateTime.now().getZone());
		}

		private class ViewHolder {
			
			LinearLayout timeSlotContainer;
			TextView channel;
			
			ViewHolder() { }

		}
		
	}
	
}
