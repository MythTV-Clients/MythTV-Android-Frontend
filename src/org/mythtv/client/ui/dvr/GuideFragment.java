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

import java.util.Date;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;
import org.mythtv.service.util.DateUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Daniel Frey
 *
 */
public class GuideFragment extends AbstractMythFragment implements OnClickListener {

	private static final String TAG = GuideFragment.class.getSimpleName();

	private Button mPreviousButton, mNextButton;
	private TextView mDateTextView;
	
	private Date date;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );

		//inflate fragment layout
		View view = inflater.inflate( R.layout.fragment_dvr_guide, container, false );

		mPreviousButton = (Button) view.findViewById( R.id.guide_previous );
		mPreviousButton.setOnClickListener( this );
		
		mDateTextView = (TextView) view.findViewById( R.id.guide_date );
		
		mNextButton = (Button) view.findViewById( R.id.guide_next );
		mNextButton.setOnClickListener( this );

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
		
		date = DateUtils.getEndOfDay( new Date() );
		updateDateHeader();
		
		Log.v( TAG, "onActivityCreated : exit" );
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick( View v ) {
		Log.v( TAG, "onClick : enter" );
		
		switch( v.getId() ) {
			case R.id.guide_previous :
				Log.v( TAG, "onClick : selected previous button" );

				date = DateUtils.getPreviousDay( date );
				
				break;
			case R.id.guide_next :
				Log.v( TAG, "onClick : selected next button" );

				date = DateUtils.getNextDay( date );
				
				break;
		}

		updateDateHeader();
		
		Log.v( TAG, "onClick : enter" );
	}

	// internal helpers
	
	private void updateDateHeader() {
		mDateTextView.setText( DateUtils.dateFormatter.format( date ) );
		
		Date today = new Date();
		if( DateUtils.dateFormatter.format( today ).equals( DateUtils.dateFormatter.format( date ) ) ) {
			mPreviousButton.setEnabled( false );
		} else {
			mPreviousButton.setEnabled( true );
		}

		Date end = DateUtils.getDaysFromToday( 12 );
		if( DateUtils.dateFormatter.format( end ).equals( DateUtils.dateFormatter.format( date ) ) ) {
			mNextButton.setEnabled( false );
		} else {
			mNextButton.setEnabled( true );
		}
	}
	
	private class MythtvGuidePagerAdapter extends FragmentStatePagerAdapter {

		private List<String> fragmentHeadings;
		private List<Fragment> fragments;
		
		public MythtvGuidePagerAdapter( FragmentManager fm, List<String> fragmentHeadings, List<Fragment> fragments ) {
			super( fm );
			
			this.fragmentHeadings = fragmentHeadings;
			this.fragments = fragments;
			
		}

		/* (non-Javadoc)
		 * @see android.support.v4.app.FragmentStatePagerAdapter#getItem(int)
		 */
		@Override
		public Fragment getItem( int position ) {
			return fragments.get( position );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#getCount()
		 */
		public int getCount() {
			return fragments.size();
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
		 */
		@Override
		public CharSequence getPageTitle( int position ) {
			return fragmentHeadings.get( position );
		}
		
	}

}
