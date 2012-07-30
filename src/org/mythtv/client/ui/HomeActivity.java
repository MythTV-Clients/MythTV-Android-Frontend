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
package org.mythtv.client.ui;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.dvr.DvrDashboardFragment;
import org.mythtv.client.ui.frontends.MythmoteActivity;
import org.mythtv.client.ui.media.MediaDashboardFragment;
import org.mythtv.client.ui.setup.SetupActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * @author Daniel Frey
 * 
 */
public class HomeActivity extends AbstractLocationAwareFragmentActivity {

	private final static String TAG = HomeActivity.class.getSimpleName();

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.AbstractLocationAwareFragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.d( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		getMainApplication().setLocation( "HOME" );
		
		setContentView( R.layout.activity_home );

		List<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add( Fragment.instantiate( this, DvrDashboardFragment.class.getName() ) );
		fragments.add( Fragment.instantiate( this, MediaDashboardFragment.class.getName() ) );

		MythtvHomePagerAdapter mAdapter = new MythtvHomePagerAdapter( getSupportFragmentManager(), fragments );
		ViewPager mPager = (ViewPager) findViewById( R.id.home_pager );
		mPager.setAdapter( mAdapter );
		mPager.setCurrentItem( 0 );

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
		inflater.inflate( R.menu.home_main_menu, menu );

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
		case R.id.menu_frontends:
			Log.d( TAG, "onOptionsItemSelected : setup selected" );

			startActivity( new Intent( this, MythmoteActivity.class ) );
			return true;
		case R.id.menu_setup:
			Log.d( TAG, "onOptionsItemSelected : setup selected" );

			startActivity( new Intent( this, SetupActivity.class ) );
			return true;
		case R.id.menu_about:
			Log.d( TAG, "onOptionsItemSelected : about selected" );

		    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		    Fragment prev = getSupportFragmentManager().findFragmentByTag( "aboutDialog" );
		    if( null != prev ) {
		        ft.remove( prev );
		    }
		    ft.addToBackStack( null );

		    DialogFragment newFragment = AboutDialogFragment.newInstance();
		    newFragment.show( ft, "aboutDialog" );
		    
			return true;
		}

		Log.d( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}


	// internal helpers
	
	private class MythtvHomePagerAdapter extends FragmentStatePagerAdapter {

		private List<Fragment> fragments;
		
		public MythtvHomePagerAdapter( FragmentManager fm, List<Fragment> fragments ) {
			super( fm );
			
			this.fragments = fragments;
			
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
