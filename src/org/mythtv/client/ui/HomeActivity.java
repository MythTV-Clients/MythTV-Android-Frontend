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
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */
package org.mythtv.client.ui;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.dvr.DvrDashboardFragment;
import org.mythtv.client.ui.frontends.FrontendsDashboardFragment;
import org.mythtv.client.ui.media.MediaDashboardFragment;
import org.mythtv.client.ui.preferences.MythtvPreferences;
import org.mythtv.client.ui.preferences.MythtvPreferencesHC;
import org.mythtv.client.ui.setup.SetupActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * @author Daniel Frey
 * 
 */
public class HomeActivity extends AbstractMythActivity {

	private final static String TAG = HomeActivity.class.getSimpleName();

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.d( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_home );

		List<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add( Fragment.instantiate( this, FrontendsDashboardFragment.class.getName() ) );
		fragments.add( Fragment.instantiate( this, DvrDashboardFragment.class.getName() ) );
		fragments.add( Fragment.instantiate( this, MediaDashboardFragment.class.getName() ) );

		MythtvHomePagerAdapter mAdapter = new MythtvHomePagerAdapter( getSupportFragmentManager(), fragments );
		ViewPager mPager = (ViewPager) findViewById( R.id.home_pager );
		mPager.setAdapter( mAdapter );
		mPager.setCurrentItem( 1 );

		Log.d( TAG, "onCreate : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mythtv.client.ui.BaseActivity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.d( TAG, "onCreateOptionsMenu : enter" );

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.main_menu, menu );

		Log.d( TAG, "onCreateOptionsMenu : exit" );
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mythtv.client.ui.BaseActivity#onOptionsItemSelected(android.view.
	 * MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.d( TAG, "onOptionsItemSelected : enter" );

		switch( item.getItemId() ) {
		case R.id.menu_prefs:
			Log.d( TAG, "onOptionsItemSelected : preferences selected" );

	        if( Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ) {
				Log.d( TAG, "onOptionsItemSelected : pre-honeycomb prefs selected" );

				startActivity( new Intent( this, MythtvPreferences.class ) );
	        } else {
				Log.d( TAG, "onOptionsItemSelected : honeycomb+ prefs selected" );

				startActivity( new Intent( this, MythtvPreferencesHC.class ) );
	        }

	        return true;
		case R.id.menu_setup:
			Log.d( TAG, "onOptionsItemSelected : setup selected" );

			startActivity( new Intent( this, SetupActivity.class ) );
			return true;
		}

		Log.d( TAG, "onOptionsItemSelected : exit" );
		return false;
	}

	private class MythtvHomePagerAdapter extends FragmentStatePagerAdapter {

		private final String TAG = MythtvHomePagerAdapter.class.getSimpleName();
		
		private List<Fragment> fragments;
		
		public MythtvHomePagerAdapter( FragmentManager fm, List<Fragment> fragments ) {
			super( fm );
			Log.v( TAG, "MythtvAwayPagerAdapter : enter" );
			
			this.fragments = fragments;
			
			Log.v( TAG, "MythtvAwayPagerAdapter : exit" );
		}

		@Override
		public Fragment getItem( int position ) {
			Log.v( TAG, "getItem : enter" );
			Log.v( TAG, "getItem : exit" );
			return fragments.get( position );
		}

		public int getCount() {
			Log.v( TAG, "getCount : enter" );
			Log.v( TAG, "getCount : exit" );
			return fragments.size();
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
		 */
		@Override
		public CharSequence getPageTitle( int position ) {
			Log.v( TAG, "getPageTitle : enter" );

			switch( position ) {
			case 0:
				Log.v( TAG, "getPageTitle : frontend page" );

				return resources.getString( R.string.tab_frontends );
			case 1:
				Log.v( TAG, "getPageTitle : dvr page" );

				return resources.getString( R.string.tab_dvr );
			case 2:
				Log.v( TAG, "getPageTitle : media page" );

				return resources.getString( R.string.tab_multimedia );
			}

			Log.v( TAG, "getPageTitle : exit" );
			return super.getPageTitle( position );
		}
		
	}

}
