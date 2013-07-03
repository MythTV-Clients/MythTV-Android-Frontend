/**
 * 
 */
package org.mythtv.client.ui.dvr;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author dmfrey
 *
 */
public class GuideTimeslotsFragment extends AbstractMythFragment {

	private static final String TAG = GuideTimeslotsFragment.class.getSimpleName();
	
	private static final Map<Integer, Integer> hourTimeslots = new HashMap<Integer, Integer>();
	
	private TextView 
		timeslot_00_00 = null, timeslot_00_30 = null,
		timeslot_01_00 = null, timeslot_01_30 = null,
		timeslot_02_00 = null, timeslot_02_30 = null,
		timeslot_03_00 = null, timeslot_03_30 = null,
		timeslot_04_00 = null, timeslot_04_30 = null,
		timeslot_05_00 = null, timeslot_05_30 = null,
		timeslot_06_00 = null, timeslot_06_30 = null,
		timeslot_07_00 = null, timeslot_07_30 = null,
		timeslot_08_00 = null, timeslot_08_30 = null,
		timeslot_09_00 = null, timeslot_09_30 = null,
		timeslot_10_00 = null, timeslot_10_30 = null,
		timeslot_11_00 = null, timeslot_11_30 = null,
		timeslot_12_00 = null, timeslot_12_30 = null,
		timeslot_13_00 = null, timeslot_13_30 = null,
		timeslot_14_00 = null, timeslot_14_30 = null,
		timeslot_15_00 = null, timeslot_15_30 = null,
		timeslot_16_00 = null, timeslot_16_30 = null,
		timeslot_17_00 = null, timeslot_17_30 = null,
		timeslot_18_00 = null, timeslot_18_30 = null,
		timeslot_19_00 = null, timeslot_19_30 = null,
		timeslot_20_00 = null, timeslot_20_30 = null,
		timeslot_21_00 = null, timeslot_21_30 = null,
		timeslot_22_00 = null, timeslot_22_30 = null,
		timeslot_23_00 = null, timeslot_23_30 = null;
	
	private int startingTimeslot = 0;
	
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
	public GuideTimeslotsFragment() {
		Log.v( TAG, "initialize : enter" );
		
		DateTime now = new DateTime();
		startingTimeslot = hourTimeslots.get( now.getHourOfDay() );
		
		if( now.getMinuteOfHour() > 30 ) {
			startingTimeslot++;
		}
		Log.v( TAG, "initialize : startingTimeslot=" + startingTimeslot );
		
		Log.v( TAG, "initialize : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );

		//inflate fragment layout
		View view = inflater.inflate( R.layout.program_guide_timeslots, container, false );

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

		instantiateControls();
		
		final HorizontalScrollView hsv = (HorizontalScrollView) getActivity().findViewById( R.id.program_guide_timeslots_scrollview );
		hsv.post( new Runnable() {

	        /* (non-Javadoc)
	         * @see java.lang.Runnable#run()
	         */
	        @Override
	        public void run() {
	            
                final View child = ((LinearLayout) hsv.getChildAt( 0 ) ).getChildAt( startingTimeslot );                 

                Log.v( TAG, "onActivityCreated : scroll to timeslot(" + child.getWidth() + ") " + startingTimeslot + " at postion '" + ( startingTimeslot * ( child.getWidth() ) ) + "'" );
                hsv.scrollTo( ( startingTimeslot * ( child.getWidth() ) ), 0 );
	        }

	    });
		
		Log.v( TAG, "onActivityCreated : exit" );
	}

	// internal helpers
	
	private void instantiateControls() {
		
		timeslot_00_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_00_00 );
		timeslot_00_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_00_30 );
		timeslot_01_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_01_00 );
		timeslot_01_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_01_30 );
		timeslot_02_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_02_00 );
		timeslot_02_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_02_30 );
		timeslot_03_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_03_00 );
		timeslot_03_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_03_30 );
		timeslot_04_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_04_00 );
		timeslot_04_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_04_30 );
		timeslot_05_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_05_00 );
		timeslot_05_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_05_30 );
		timeslot_06_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_06_00 );
		timeslot_06_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_06_30 );
		timeslot_07_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_07_00 );
		timeslot_07_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_07_30 );
		timeslot_08_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_08_00 );
		timeslot_08_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_08_30 );
		timeslot_09_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_09_00 );
		timeslot_09_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_09_30 );
		timeslot_10_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_10_00 );
		timeslot_10_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_10_30 );
		timeslot_11_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_11_00 );
		timeslot_11_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_11_30 );
		timeslot_12_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_12_00 );
		timeslot_12_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_12_30 );
		timeslot_13_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_13_00 );
		timeslot_13_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_13_30 );
		timeslot_14_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_14_00 );
		timeslot_14_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_14_30 );
		timeslot_15_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_15_00 );
		timeslot_15_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_15_30 );
		timeslot_16_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_16_00 );
		timeslot_16_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_16_30 );
		timeslot_17_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_17_00 );
		timeslot_17_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_17_30 );
		timeslot_18_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_18_00 );
		timeslot_18_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_18_30 );
		timeslot_19_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_19_00 );
		timeslot_19_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_19_30 );
		timeslot_20_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_20_00 );
		timeslot_20_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_20_30 );
		timeslot_21_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_21_00 );
		timeslot_21_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_21_30 );
		timeslot_22_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_22_00 );
		timeslot_22_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_22_30 );
		timeslot_23_00 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_23_00 );
		timeslot_23_30 = (TextView) getActivity().findViewById( R.id.program_guide_timeslots_23_30 );

		if( getMainApplication().getClockType() != null && getMainApplication().getClockType().equals( "24" ) ) {
			update24HourLabels();
		}
	}

	private void update24HourLabels() {
		
		timeslot_00_00.setText( getActivity().getString( R.string.program_guide_timeslots_00_00_24 ) );
		timeslot_00_30.setText( getActivity().getString( R.string.program_guide_timeslots_00_30_24 ) );
		timeslot_01_00.setText( getActivity().getString( R.string.program_guide_timeslots_01_00_24 ) );
		timeslot_01_30.setText( getActivity().getString( R.string.program_guide_timeslots_01_30_24 ) );
		timeslot_02_00.setText( getActivity().getString( R.string.program_guide_timeslots_02_00_24 ) );
		timeslot_02_30.setText( getActivity().getString( R.string.program_guide_timeslots_02_30_24 ) );
		timeslot_03_00.setText( getActivity().getString( R.string.program_guide_timeslots_03_00_24 ) );
		timeslot_03_30.setText( getActivity().getString( R.string.program_guide_timeslots_03_30_24 ) );
		timeslot_04_00.setText( getActivity().getString( R.string.program_guide_timeslots_04_00_24 ) );
		timeslot_04_30.setText( getActivity().getString( R.string.program_guide_timeslots_04_30_24 ) );
		timeslot_05_00.setText( getActivity().getString( R.string.program_guide_timeslots_05_00_24 ) );
		timeslot_05_30.setText( getActivity().getString( R.string.program_guide_timeslots_05_30_24 ) );
		timeslot_06_00.setText( getActivity().getString( R.string.program_guide_timeslots_06_00_24 ) );
		timeslot_06_30.setText( getActivity().getString( R.string.program_guide_timeslots_06_30_24 ) );
		timeslot_07_00.setText( getActivity().getString( R.string.program_guide_timeslots_07_00_24 ) );
		timeslot_07_30.setText( getActivity().getString( R.string.program_guide_timeslots_07_30_24 ) );
		timeslot_08_00.setText( getActivity().getString( R.string.program_guide_timeslots_08_00_24 ) );
		timeslot_08_30.setText( getActivity().getString( R.string.program_guide_timeslots_08_30_24 ) );
		timeslot_09_00.setText( getActivity().getString( R.string.program_guide_timeslots_09_00_24 ) );
		timeslot_09_30.setText( getActivity().getString( R.string.program_guide_timeslots_09_30_24 ) );
		timeslot_10_00.setText( getActivity().getString( R.string.program_guide_timeslots_10_00_24 ) );
		timeslot_10_30.setText( getActivity().getString( R.string.program_guide_timeslots_10_30_24 ) );
		timeslot_11_00.setText( getActivity().getString( R.string.program_guide_timeslots_11_00_24 ) );
		timeslot_11_30.setText( getActivity().getString( R.string.program_guide_timeslots_11_30_24 ) );
		timeslot_12_00.setText( getActivity().getString( R.string.program_guide_timeslots_12_00_24 ) );
		timeslot_12_30.setText( getActivity().getString( R.string.program_guide_timeslots_12_30_24 ) );
		timeslot_13_00.setText( getActivity().getString( R.string.program_guide_timeslots_13_00_24 ) );
		timeslot_13_30.setText( getActivity().getString( R.string.program_guide_timeslots_13_30_24 ) );
		timeslot_14_00.setText( getActivity().getString( R.string.program_guide_timeslots_14_00_24 ) );
		timeslot_14_30.setText( getActivity().getString( R.string.program_guide_timeslots_14_30_24 ) );
		timeslot_15_00.setText( getActivity().getString( R.string.program_guide_timeslots_15_00_24 ) );
		timeslot_15_30.setText( getActivity().getString( R.string.program_guide_timeslots_15_30_24 ) );
		timeslot_16_00.setText( getActivity().getString( R.string.program_guide_timeslots_16_00_24 ) );
		timeslot_16_30.setText( getActivity().getString( R.string.program_guide_timeslots_16_30_24 ) );
		timeslot_17_00.setText( getActivity().getString( R.string.program_guide_timeslots_17_00_24 ) );
		timeslot_17_30.setText( getActivity().getString( R.string.program_guide_timeslots_17_30_24 ) );
		timeslot_18_00.setText( getActivity().getString( R.string.program_guide_timeslots_18_00_24 ) );
		timeslot_18_30.setText( getActivity().getString( R.string.program_guide_timeslots_18_30_24 ) );
		timeslot_19_00.setText( getActivity().getString( R.string.program_guide_timeslots_19_00_24 ) );
		timeslot_19_30.setText( getActivity().getString( R.string.program_guide_timeslots_19_30_24 ) );
		timeslot_20_00.setText( getActivity().getString( R.string.program_guide_timeslots_20_00_24 ) );
		timeslot_20_30.setText( getActivity().getString( R.string.program_guide_timeslots_20_30_24 ) );
		timeslot_21_00.setText( getActivity().getString( R.string.program_guide_timeslots_21_00_24 ) );
		timeslot_21_30.setText( getActivity().getString( R.string.program_guide_timeslots_21_30_24 ) );
		timeslot_22_00.setText( getActivity().getString( R.string.program_guide_timeslots_22_00_24 ) );
		timeslot_22_30.setText( getActivity().getString( R.string.program_guide_timeslots_22_30_24 ) );
		timeslot_23_00.setText( getActivity().getString( R.string.program_guide_timeslots_23_00_24 ) );
		timeslot_23_30.setText( getActivity().getString( R.string.program_guide_timeslots_23_30_24 ) );
		
	}
	
}
