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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * @author dmfrey
 *
 */
public class GuideTimeslotsFragment extends AbstractMythFragment {

	private static final String TAG = GuideTimeslotsFragment.class.getSimpleName();
	
	private static final Map<Integer, Integer> hourTimeslots = new HashMap<Integer, Integer>();
	
	private OnTimeslotScrollListener listener;
	
	private Button 
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
	
	public interface OnTimeslotScrollListener {
		
		void timeslotSelect( String time );
		
	}

	/**
	 * 
	 */
	public GuideTimeslotsFragment() {
		Log.v( TAG, "initialize : enter" );
		
		DateTime now = new DateTime( System.currentTimeMillis() );
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
		
		scrollTimeslot();
		
		Log.v( TAG, "onActivityCreated : exit" );
	}

	public void setOnTimeslotScrollListener( OnTimeslotScrollListener listener ) {
		this.listener = listener;
	}
	
	public void updateTimeslot( DateTime date ) {
		
		startingTimeslot = hourTimeslots.get( date.getHourOfDay() );
		
		if( date.getMinuteOfHour() > 30 ) {
			startingTimeslot++;
		}

		scrollTimeslot();
		
	}
	
	// internal helpers
	
	private void scrollTimeslot() {
		
		final HorizontalScrollView hsv = (HorizontalScrollView) getActivity().findViewById( R.id.program_guide_timeslots_scrollview );
		if( null != hsv ) {

			hsv.post( new Runnable() {

				/* (non-Javadoc)
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run() {

					final View child = ( (LinearLayout) hsv.getChildAt( 0 ) ).getChildAt( startingTimeslot );                 
					
					Log.v( TAG, "onActivityCreated : scroll to timeslot(" + child.getWidth() + ") " + startingTimeslot + " at postion '" + ( startingTimeslot * ( child.getWidth() ) ) + "'" );
					hsv.scrollTo( ( startingTimeslot * ( child.getWidth() ) ), 0 );
				}

			});

		}

	}
	
	private void instantiateControls() {
		
		timeslot_00_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_00_00 );
		timeslot_00_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "00:00:00" );
				
			}
			
		});
		
		timeslot_00_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_00_30 );
		timeslot_00_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "00:30:00" );
				
			}
			
		});
		
		timeslot_01_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_01_00 );
		timeslot_01_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "01:00:00" );
				
			}
			
		});
		
		timeslot_01_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_01_30 );
		timeslot_01_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "01:30:00" );
				
			}
			
		});
		
		timeslot_02_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_02_00 );
		timeslot_02_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "02:00:00" );
				
			}
			
		});
		
		timeslot_02_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_02_30 );
		timeslot_02_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "02:30:00" );
				
			}
			
		});
		
		timeslot_03_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_03_00 );
		timeslot_03_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "03:00:00" );
				
			}
			
		});
		
		timeslot_03_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_03_30 );
		timeslot_03_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "03:30:00" );
				
			}
			
		});
		
		timeslot_04_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_04_00 );
		timeslot_04_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "04:00:00" );
				
			}
			
		});
		
		timeslot_04_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_04_30 );
		timeslot_04_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "04:30:00" );
				
			}
			
		});
		
		timeslot_05_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_05_00 );
		timeslot_05_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "05:00:00" );
				
			}
			
		});
		
		timeslot_05_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_05_30 );
		timeslot_05_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "05:30:00" );
				
			}
			
		});
		
		timeslot_06_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_06_00 );
		timeslot_06_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "06:00:00" );
				
			}
			
		});
		
		timeslot_06_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_06_30 );
		timeslot_06_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "06:30:00" );
				
			}
			
		});
		
		timeslot_07_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_07_00 );
		timeslot_07_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnCli4ckListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "07:00:00" );
				
			}
			
		});
		
		timeslot_07_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_07_30 );
		timeslot_07_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "07:30:00" );
				
			}
			
		});
		
		timeslot_08_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_08_00 );
		timeslot_08_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "08:00:00" );
				
			}
			
		});
		
		timeslot_08_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_08_30 );
		timeslot_08_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "00:00:00" );
				
			}
			
		});
		
		timeslot_09_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_09_00 );
		timeslot_09_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "09:00:00" );
				
			}
			
		});
		
		timeslot_09_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_09_30 );
		timeslot_09_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "09:30:00" );
				
			}
			
		});
		
		timeslot_10_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_10_00 );
		timeslot_10_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "10:00:00" );
				
			}
			
		});
		
		timeslot_10_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_10_30 );
		timeslot_10_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "10:30:00" );
				
			}
			
		});
		
		timeslot_11_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_11_00 );
		timeslot_11_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "11:00:00" );
				
			}
			
		});
		
		timeslot_11_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_11_30 );
		timeslot_11_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "11:30:00" );
				
			}
			
		});
		
		timeslot_12_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_12_00 );
		timeslot_12_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "12:00:00" );
				
			}
			
		});
		
		timeslot_12_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_12_30 );
		timeslot_12_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "12:30:00" );
				
			}
			
		});
		
		timeslot_13_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_13_00 );
		timeslot_13_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "13:00:00" );
				
			}
			
		});
		
		timeslot_13_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_13_30 );
		timeslot_13_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "13:30:00" );
				
			}
			
		});
		
		timeslot_14_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_14_00 );
		timeslot_14_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "14:00:00" );
				
			}
			
		});
		
		timeslot_14_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_14_30 );
		timeslot_14_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "14:30:00" );
				
			}
			
		});
		
		timeslot_15_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_15_00 );
		timeslot_15_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "15:00:00" );
				
			}
			
		});
		
		timeslot_15_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_15_30 );
		timeslot_15_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "15:30:00" );
				
			}
			
		});
		
		timeslot_16_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_16_00 );
		timeslot_16_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "16:00:00" );
				
			}
			
		});
		
		timeslot_16_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_16_30 );
		timeslot_16_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "16:30:00" );
				
			}
			
		});
		
		timeslot_17_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_17_00 );
		timeslot_17_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "17:00:00" );
				
			}
			
		});
		
		timeslot_17_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_17_30 );
		timeslot_17_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "17:30:00" );
				
			}
			
		});
		
		timeslot_18_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_18_00 );
		timeslot_18_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "18:00:00" );
				
			}
			
		});
		
		timeslot_18_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_18_30 );
		timeslot_18_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "18:30:00" );
				
			}
			
		});
		
		timeslot_19_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_19_00 );
		timeslot_19_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "19:00:00" );
				
			}
			
		});
		
		timeslot_19_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_19_30 );
		timeslot_19_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "19:30:00" );
				
			}
			
		});
		
		timeslot_20_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_20_00 );
		timeslot_20_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "20:00:00" );
				
			}
			
		});
		
		timeslot_20_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_20_30 );
		timeslot_20_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "20:30:00" );
				
			}
			
		});
		
		timeslot_21_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_21_00 );
		timeslot_21_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "21:00:00" );
				
			}
			
		});
		
		timeslot_21_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_21_30 );
		timeslot_21_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "21:30:00" );
				
			}
			
		});
		
		timeslot_22_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_22_00 );
		timeslot_22_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "22:00:00" );
				
			}
			
		});
		
		timeslot_22_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_22_30 );
		timeslot_22_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "22:30:00" );
				
			}
			
		});
		
		timeslot_23_00 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_23_00 );
		timeslot_23_00.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "23:00:00" );
				
			}
			
		});
		
		timeslot_23_30 = (Button) getActivity().findViewById( R.id.program_guide_timeslots_23_30 );
		timeslot_23_30.setOnClickListener( new OnClickListener() {
			
			/* (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick( View v ) {
				
				listener.timeslotSelect( "23:30:00" );
				
			}
			
		});
		

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
