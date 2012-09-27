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

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;
import org.mythtv.service.guide.ProgramGuideDownloadService;
import org.mythtv.service.guide.cache.ProgramGuideLruMemoryCache;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.guide.ProgramGuide;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TimingLogger;
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
	
	private DateTime date;
	private String startDate;
	
	private ProgramGuideDownloadReceiver programGuideDownloaderReceiver = new ProgramGuideDownloadReceiver();

	private ProgramGuideLruMemoryCache cache;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		Log.v( TAG, "onStart : enter" );
		super.onStart();

		IntentFilter programGuideDownloadFilter = new IntentFilter();
		programGuideDownloadFilter.addAction( ProgramGuideDownloadService.ACTION_PROGRESS );
	    getActivity().registerReceiver( programGuideDownloaderReceiver, programGuideDownloadFilter );
	    
		Log.v( TAG, "onStart : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		Log.v( TAG, "onStop : enter" );
		super.onStop();

		// Unregister for broadcast
		if( null != programGuideDownloaderReceiver ) {
			try {
				getActivity().unregisterReceiver( programGuideDownloaderReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		Log.v( TAG, "onStop : exit" );
	}

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
		
		cache = new ProgramGuideLruMemoryCache( getActivity() );

		date = DateUtils.getEndOfDay( new DateTime() );
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
		Log.v( TAG, "updateDateHeader : enter" );
		
		mDateTextView.setText( DateUtils.dateFormatter.print( date ) );
		startDate = DateUtils.dateFormatter.print( date );
		
		DateTime today = new DateTime();
		Log.v( TAG, "updateDateHeader : today=" + DateUtils.dateTimeFormatter.print( today ) );
		if( today.dayOfYear().equals( date.dayOfYear() ) ) {
			mPreviousButton.setEnabled( false );
		} else {
			mPreviousButton.setEnabled( true );
		}

		DateTime end = DateUtils.getDaysFromToday( 12 );
		Log.v( TAG, "updateDateHeader : end=" + DateUtils.dateTimeFormatter.print( end ) );
		if( end.dayOfYear().equals( date.dayOfYear() ) ) {
			mNextButton.setEnabled( false );
		} else {
			mNextButton.setEnabled( true );
		}

		DateTime now = new DateTime();
		MythtvGuidePagerAdapter mAdapter = new MythtvGuidePagerAdapter( getActivity().getSupportFragmentManager() );
		ViewPager mPager = (ViewPager) getActivity().findViewById( R.id.guide_pager );
		mPager.setAdapter( mAdapter );
		mPager.setCurrentItem( now.getHourOfDay() );

		Log.v( TAG, "updateDateHeader : exit" );
	}
	
	private class MythtvGuidePagerAdapter extends FragmentStatePagerAdapter {

		private List<String> fragmentHeadings, fragmentLabels;
		
		public MythtvGuidePagerAdapter( FragmentManager fm ) {
			super( fm );
			Log.v( TAG, "initialize : exit" );
			
			fragmentHeadings = new ArrayList<String>(); fragmentLabels = new ArrayList<String>();
			fragmentHeadings.add( "0" );	fragmentLabels.add( "12 AM" );
			fragmentHeadings.add( "1" );	fragmentLabels.add( "1 AM" );
			fragmentHeadings.add( "2" );	fragmentLabels.add( "2 AM" );
			fragmentHeadings.add( "3" );	fragmentLabels.add( "3 AM" );
			fragmentHeadings.add( "4" );	fragmentLabels.add( "4 AM" );
			fragmentHeadings.add( "5" );	fragmentLabels.add( "5 AM" );
			fragmentHeadings.add( "6" );	fragmentLabels.add( "6 AM" );
			fragmentHeadings.add( "7" );	fragmentLabels.add( "7 AM" );
			fragmentHeadings.add( "8" );	fragmentLabels.add( "8 AM" );
			fragmentHeadings.add( "9" );	fragmentLabels.add( "9 AM" );
			fragmentHeadings.add( "10" );	fragmentLabels.add( "10 AM" );
			fragmentHeadings.add( "11" );	fragmentLabels.add( "11 AM" );
			fragmentHeadings.add( "12" );	fragmentLabels.add( "12 PM" );
			fragmentHeadings.add( "13" );	fragmentLabels.add( "1 PM" );
			fragmentHeadings.add( "14" );	fragmentLabels.add( "2 PM" );
			fragmentHeadings.add( "15" );	fragmentLabels.add( "3 PM" );
			fragmentHeadings.add( "16" );	fragmentLabels.add( "4 PM" );
			fragmentHeadings.add( "17" );	fragmentLabels.add( "5 PM" );
			fragmentHeadings.add( "18" );	fragmentLabels.add( "6 PM" );
			fragmentHeadings.add( "19" );	fragmentLabels.add( "7 PM" );
			fragmentHeadings.add( "20" );	fragmentLabels.add( "8 PM" );
			fragmentHeadings.add( "21" );	fragmentLabels.add( "9 PM" );
			fragmentHeadings.add( "22" );	fragmentLabels.add( "10 PM" );
			fragmentHeadings.add( "23" );	fragmentLabels.add( "11 PM" );
			
			Log.v( TAG, "initialize : exit" );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.app.FragmentStatePagerAdapter#getItem(int)
		 */
		@Override
		public Fragment getItem( int position ) {
			TimingLogger tl = new TimingLogger( TAG, "Load ProgramGuide" );
			
			DateTime programGuideDate = date.withTime( Integer.parseInt( fragmentHeadings.get( position ) ), 0, 0, 0 );
			ProgramGuide programGuide = cache.get( programGuideDate );
			tl.addSplit( "ProgramGuide for " + DateUtils.dateFormatter.print( programGuideDate ) + " loaded" );
			
			return GuidePagerFragment.newInstance( startDate, fragmentHeadings.get( position ), programGuide );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#getCount()
		 */
		public int getCount() {
			return fragmentHeadings.size();
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
		 */
		@Override
		public CharSequence getPageTitle( int position ) {
			return fragmentLabels.get( position );
		}
		
	}

	private class ProgramGuideDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( ProgramGuideDownloadService.ACTION_PROGRESS ) ) {

	        	if( intent.hasExtra( ProgramGuideDownloadService.EXTRA_PROGRESS ) ) {
		        	Log.d( TAG, "ProgramGuideDownloadReceiver.onReceive : progress=" + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_PROGRESS ) );
		        	
		        	DateTime updated = new DateTime( intent.getStringExtra( ProgramGuideDownloadService.EXTRA_PROGRESS_DATE ) );
		        	cache.remove( updated );
	        	}
	        	
	        	if( intent.hasExtra( ProgramGuideDownloadService.EXTRA_PROGRESS_ERROR ) ) {
		        	Log.e( TAG, "ProgramGuideDownloadReceiver.onReceive : progress error=" + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_PROGRESS_ERROR ) );
	        	}
	        	
	        }
	        
		}
		
	}

}
