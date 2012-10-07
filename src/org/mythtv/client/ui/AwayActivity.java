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
package org.mythtv.client.ui;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.dvr.DvrDashboardFragment;
import org.mythtv.client.ui.media.MediaDashboardFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

/**
 * @author Daniel Frey
 * 
 */
public class AwayActivity extends AbstractLocationAwareFragmentActivity {

	private final static String TAG = AwayActivity.class.getSimpleName();
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.AbstractLocationAwareFragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.d( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		getMainApplication().setLocation( "AWAY" );

		setContentView( R.layout.activity_away );

		List<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add( Fragment.instantiate( this, DvrDashboardFragment.class.getName() ) );
		fragments.add( Fragment.instantiate( this, MediaDashboardFragment.class.getName() ) );

		MythtvAwayPagerAdapter mAdapter = new MythtvAwayPagerAdapter( getSupportFragmentManager(), fragments );
		ViewPager mPager = (ViewPager) findViewById( R.id.away_pager );
		mPager.setAdapter( mAdapter );
		mPager.setCurrentItem( 0 );

		Log.d( TAG, "onCreate : exit" );
	}

	private class MythtvAwayPagerAdapter extends FragmentStatePagerAdapter {

		private final String TAG = MythtvAwayPagerAdapter.class.getSimpleName();
		
		private List<Fragment> fragments;
		
		public MythtvAwayPagerAdapter( FragmentManager fm, List<Fragment> fragments ) {
			super( fm );
			Log.v( TAG, "MythtvAwayPagerAdapter : enter" );
			
			this.fragments = fragments;
			
			Log.v( TAG, "MythtvAwayPagerAdapter : exit" );
		}

		@Override
		public Fragment getItem( int position ) {
			return fragments.get( position );
		}

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

				return resources.getString( R.string.tab_dvr );
			case 1:

				return resources.getString( R.string.tab_multimedia );
			}

			return super.getPageTitle( position );
		}

	}

}
