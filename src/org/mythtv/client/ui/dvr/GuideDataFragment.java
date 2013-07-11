/**
 * 
 */
package org.mythtv.client.ui.dvr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.dvr.Program;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * @author dmfrey
 *
 */
public class GuideDataFragment extends MythtvListFragment {

	private static final String TAG = GuideDataFragment.class.getSimpleName();
	
	private ProgramHelper mProgramHelper = ProgramHelper.getInstance();
	
	private ProgramGuideChannelAdapter mAdapter;
	
	private static final Map<Integer, Integer> hourTimeslots = new HashMap<Integer, Integer>();
	
	static {
		hourTimeslots.put( 0, 0 );
		hourTimeslots.put( 1, 2 );
		hourTimeslots.put( 2, 4 );
		hourTimeslots.put( 3, 6 );
		hourTimeslots.put( 4, 8 );
		hourTimeslots.put( 5, 10 );
		hourTimeslots.put( 6, 12 );
		hourTimeslots.put( 7, 14 );
		hourTimeslots.put( 8, 16 );
		hourTimeslots.put( 9, 18 );
		hourTimeslots.put( 10, 20 );
		hourTimeslots.put( 11, 22 );
		hourTimeslots.put( 12, 24 );
		hourTimeslots.put( 13, 26 );
		hourTimeslots.put( 14, 28 );
		hourTimeslots.put( 15, 30 );
		hourTimeslots.put( 16, 32 );
		hourTimeslots.put( 17, 34 );
		hourTimeslots.put( 18, 36 );
		hourTimeslots.put( 19, 38 );
		hourTimeslots.put( 20, 40 );
		hourTimeslots.put( 21, 42 );
		hourTimeslots.put( 22, 44 );
		hourTimeslots.put( 23, 46 );
	}

	/**
	 * 
	 */
	public GuideDataFragment() { }

	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
//	@Override
//	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
//
//		View view = inflater.inflate( R.layout.program_guide_data, null );
//		
//		return view;
//	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		mAdapter = new ProgramGuideChannelAdapter( getActivity() );
	    setListAdapter( mAdapter );

		Log.v( TAG, "onActivityCreated : exit" );
	}

	public void changeChannels( List<ChannelInfo> channels ) {
		Log.v( TAG, "changeChannels : enter" );

		mAdapter.setChannels( channels );

		Log.v( TAG, "changeChannels : exit" );
	}
	
	// internal helpers
	
	private class ProgramGuideChannelAdapter extends BaseAdapter {
	
		private static final float DEFAULT_TIMESLOT_WIDTH = 400;
		private static final float DEFAULT_TIMESLOT_DURATION = 30;
		private final float MAX_TIMESLOT_WIDTH = ( hourTimeslots.size() * 2 * DEFAULT_TIMESLOT_WIDTH );
		
		private Context mContext;
		private LayoutInflater mInflater;

        private MainApplication mMainApplication;
        private String mClockFormat;
        private int textColor;
        
		private List<ChannelInfo> channels = new ArrayList<ChannelInfo>();
		
		public ProgramGuideChannelAdapter( Context context ) {
			
			mContext = context;
			mInflater = LayoutInflater.from( context );

			mMainApplication = getMainApplication();
            
			mClockFormat = "hh:mm";
            if( mMainApplication.getClockType() != null && mMainApplication.getClockType().equals( "24" ) ) {
                mClockFormat = "HH:mm";
            }
            
            textColor = getResources().getColor( R.color.body_text_1 );
		}

		public void setChannels( List<ChannelInfo> channels ) {
			this.channels = channels;
			
			notifyDataSetChanged();
		}
		
		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return channels.size();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public ChannelInfo getItem( int position ) {
			if( null != channels && !channels.isEmpty() ) {
				return channels.get( position );
			}
			
			return null;
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

			ChannelViewHolder refHolder = null;

			View view = convertView;
			if( null == view ) {
				
		        view = mInflater.inflate( R.layout.program_guide_data_row, parent, false );
				
		        refHolder = new ChannelViewHolder();
				refHolder.scroll = (HorizontalScrollView) view.findViewById( R.id.program_guide_data_scroll );
				refHolder.row = (LinearLayout) view.findViewById( R.id.program_guide_data_row );
				
				view.setTag( refHolder );

			} else {
				refHolder = (ChannelViewHolder) view.getTag();
			}
			
			ChannelInfo channel = getItem( position );
			if( null != channel ) {
				
				if( null != channel.getPrograms() && !channel.getPrograms().isEmpty() ) {
					
					float extraWidth = 0;
					float totalWidth = 0;
					
					for( Program program : channel.getPrograms() ) {

						float timeslotWidth = ( DEFAULT_TIMESLOT_WIDTH - extraWidth );
						
						DateTime start = program.getStartTime();
						DateTime end = program.getEndTime();
						
						float duration = ( end.getMillis() - start.getMillis() ) / 60000;
						if( DEFAULT_TIMESLOT_DURATION > duration ) {
							
							float percentInTimeslot = ( duration / DEFAULT_TIMESLOT_DURATION );
							float amountInTimeslot = ( DEFAULT_TIMESLOT_WIDTH * percentInTimeslot );
//							Log.v( TAG, "duration < min timeslot, duration=" + duration + ", percentInTimeslot=" + percentInTimeslot + ", amountInTimeslot=" + amountInTimeslot );
							
							extraWidth = 0;							
							timeslotWidth = amountInTimeslot;
						} else {
							float timeslots = duration / DEFAULT_TIMESLOT_DURATION;
							timeslotWidth = DEFAULT_TIMESLOT_WIDTH * ( timeslots % 10 );
							
							extraWidth = ( timeslotWidth % DEFAULT_TIMESLOT_DURATION );
						}
						
						if( timeslotWidth > ( MAX_TIMESLOT_WIDTH - totalWidth ) ) {
							timeslotWidth = ( MAX_TIMESLOT_WIDTH - totalWidth );
							extraWidth = 0;
						}
						
						Log.v( TAG, "duration=" + duration + ", timeslotWidth=" + timeslotWidth + "(" + extraWidth + ")" );
						totalWidth += timeslotWidth;

						LinearLayout timeslot = (LinearLayout) new LinearLayout( mContext ); 
						LayoutParams lParams = new LayoutParams( (int) timeslotWidth, LayoutParams.MATCH_PARENT );
						lParams.gravity = Gravity.CENTER_HORIZONTAL;
						timeslot.setLayoutParams( lParams ); 
						timeslot.setPadding( 2, 2, 2, 2 );
						timeslot.setBackgroundResource( R.drawable.program_guide_timeslot_header_back );
						timeslot.setOrientation( LinearLayout.HORIZONTAL );

						View category = (View) new View( mContext );
						category.setLayoutParams( new LayoutParams( 10, LayoutParams.MATCH_PARENT ) ); 
						category.setBackgroundColor( mProgramHelper.getCategoryColor( program.getCategory() ) );
						timeslot.addView( category );

						LinearLayout recStatus = new LinearLayout( mContext );
						recStatus.setLayoutParams( new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT ) );
						recStatus.setPadding( 2, 2, 2, 2 );
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
						details.setBackgroundColor( getResources().getColor( R.color.background_1 ) );
						recStatus.addView( details );

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
						textViewTime.setText( program.getStartTime().toString( mClockFormat ) + " - " + program.getEndTime().toString( mClockFormat ) );
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

						refHolder.row.addView( timeslot );

						scrollTimeslot( refHolder.scroll );
					}

				}

			}

			return view;
		}

		private void scrollTimeslot( final HorizontalScrollView hsv ) {
			
			hsv.post( new Runnable() {

		        /* (non-Javadoc)
		         * @see java.lang.Runnable#run()
		         */
		        @Override
		        public void run() {

		        	DateTime now = new DateTime();
		        	int timeslot = hourTimeslots.get( now.getHourOfDay() );
		        	
		    		if( now.getMinuteOfHour() > 30 ) {
		    			timeslot++;
		    		}

	                Log.v( TAG, "scroll to timeslot " + timeslot + " at postion '" + ( timeslot * DEFAULT_TIMESLOT_WIDTH ) + "'" );
	                hsv.scrollTo( (int) ( timeslot * DEFAULT_TIMESLOT_WIDTH ), 0 );
		        }

		    });

		}
	}
	
	private static class ChannelViewHolder {
		
		HorizontalScrollView scroll;
		LinearLayout row;
		
	}

}
