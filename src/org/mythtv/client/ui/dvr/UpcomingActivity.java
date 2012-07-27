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
import java.util.Date;
import java.util.List;

import org.mythtv.R;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.service.dvr.DvrServiceHelper;
import org.mythtv.service.util.DateUtils;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author Daniel Frey
 *
 */
public class UpcomingActivity extends AbstractDvrActivity {

	private static final String TAG = UpcomingActivity.class.getSimpleName();
	private static final int REFRESH_ID = Menu.FIRST + 2;

	private UpcomingReceiver upcomingReceiver;

	private DvrServiceHelper mDvrServiceHelper;

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_dvr_upcoming );

		setupActionBar();

		List<String> fragmentHeadings = new ArrayList<String>();
		List<Fragment> fragments = new ArrayList<Fragment>();

		Date day = DateUtils.getToday();

		String formattedDay = null;
		Bundle bundle = null;
		for( int i = 0; i < 13; i++ ) {
			formattedDay = DateUtils.dateFormatter.format( day );
			fragmentHeadings.add( formattedDay );
			
			bundle = new Bundle();
			bundle.putString( "START_DATE", formattedDay );
			
			fragments.add( Fragment.instantiate( this, UpcomingFragment.class.getName(), bundle ) );
			
			day = DateUtils.getNextDay( day );
		}
		
		MythtvUpcomingPagerAdapter mAdapter = new MythtvUpcomingPagerAdapter( getSupportFragmentManager(), fragmentHeadings, fragments );
		ViewPager mPager = (ViewPager) findViewById( R.id.dvr_upcoming_pager );
		mPager.setAdapter( mAdapter );
		mPager.setCurrentItem( 0 );

		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	public void onPause() {
		Log.v( TAG, "onPause : enter" );
		super.onPause();

		// Unregister for broadcast
		if( null != upcomingReceiver ) {
			try {
				unregisterReceiver( upcomingReceiver );
				upcomingReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}
		
		Log.v( TAG, "onPause : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();
	    
		mDvrServiceHelper = DvrServiceHelper.getInstance( this );

		IntentFilter upcomingFilter = new IntentFilter( DvrServiceHelper.UPCOMING_RESULT );
		upcomingFilter.setPriority( IntentFilter.SYSTEM_LOW_PRIORITY );
        upcomingReceiver = new UpcomingReceiver();
        registerReceiver( upcomingReceiver, upcomingFilter );

		Cursor upcomingCursor = getContentResolver().query( ProgramConstants.CONTENT_URI, new String[] { ProgramConstants._ID }, ProgramConstants.FIELD_PROGRAM_TYPE + " = ?", new String[] { ProgramConstants.ProgramType.UPCOMING.name() }, null );
		Log.v( TAG, "onResume : upcoming count=" + upcomingCursor.getCount() );
		if( upcomingCursor.getCount() == 0 ) {
			loadData();
		}
        upcomingCursor.close();
        
		Log.v( TAG, "onResume : exit" );
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.AbstractMythtvFragmentActivity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	@TargetApi( 11 )
	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.v( TAG, "onCreateOptionsMenu : enter" );

		MenuItem refresh = menu.add( Menu.NONE, REFRESH_ID, Menu.NONE, "Refresh" );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	refresh.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
	    }

		Log.v( TAG, "onCreateOptionsMenu : exit" );
	    return super.onCreateOptionsMenu( menu );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.AbstractDvrActivity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );
		
		switch( item.getItemId() ) {
		case REFRESH_ID:
			Log.d( TAG, "onOptionsItemSelected : refresh selected" );

			loadData();
		    
	        return true;
		}
		
		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	// internal helpers
	
	private void loadData() {
		Log.v( TAG, "loadData : enter" );
		
		mDvrServiceHelper.getUpcomingList();
		
		Log.v( TAG, "loadData : exit" );
	}
	
	private class MythtvUpcomingPagerAdapter extends FragmentStatePagerAdapter {

		private List<String> fragmentHeadings;
		private List<Fragment> fragments;
		
		public MythtvUpcomingPagerAdapter( FragmentManager fm, List<String> fragmentHeadings, List<Fragment> fragments ) {
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

			switch( position ) {
				case 0:
					return resources.getString( R.string.upcoming_today );
				case 1:
					return resources.getString( R.string.upcoming_tomorrow );
				default:
					return fragmentHeadings.get( position );
			}

		}
		
	}

	private class UpcomingReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			Log.v( TAG, "UpcomingReceiver.onReceive : enter" );
			
			Log.v( TAG, "UpcomingReceiver.onReceive : exit" );
		}
		
	}

}
