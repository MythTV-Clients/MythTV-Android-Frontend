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

import org.mythtv.R;
import org.mythtv.client.ui.preferences.MythtvPreferences;
import org.mythtv.client.ui.preferences.MythtvPreferencesHC;
import org.mythtv.client.ui.setup.SetupActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

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

		MythtvHomePagerAdapter mAdapter = new MythtvHomePagerAdapter();
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

	private class MythtvHomePagerAdapter extends PagerAdapter {

		private final String TAG = MythtvHomePagerAdapter.class.getSimpleName();
		
		public int getCount() {
			Log.v( TAG, "getCount : enter" );
			Log.v( TAG, "getCount : exit" );
			return 3;
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#instantiateItem(android.view.View, int)
		 */
		public Object instantiateItem( View collection, int position ) {
			Log.v( TAG, "instantiateItem : enter" );

			LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );

			int resId = 0;
			switch( position ) {
			case 0:
				Log.v( TAG, "instantiateItem : frontend page" );

				resId = R.layout.activity_frontend_dashboard;
				break;
			case 1:
				Log.v( TAG, "instantiateItem : dvr page" );

				resId = R.layout.activity_dvr_dashboard;
				break;
			case 2:
				Log.v( TAG, "instantiateItem : media page" );

				resId = R.layout.activity_media_dashboard;
				break;
			}

			View view = inflater.inflate( resId, null );

			( (ViewPager) collection ).addView( view, 0 );

			Log.v( TAG, "instantiateItem : exit" );
			return view;
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#destroyItem(android.view.View, int, java.lang.Object)
		 */
		@Override
		public void destroyItem( View view, int position, Object next ) {
			Log.v( TAG, "destroyItem : enter" );

			( (ViewPager) view ).removeView( (View) next );

			Log.v( TAG, "destroyItem : exit" );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#finishUpdate(android.view.View)
		 */
		@Override
		public void finishUpdate( View view ) {
			Log.v( TAG, "finishUpdate : enter" );

			// TODO Auto-generated method stub

			Log.v( TAG, "finishUpdate : exit" );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#isViewFromObject(android.view.View, java.lang.Object)
		 */
		@Override
		public boolean isViewFromObject( View view, Object next ) {
			Log.v( TAG, "isViewFromObject : enter" );
			Log.v( TAG, "isViewFromObject : exit" );
			return view == ( (View) next );

		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#restoreState(android.os.Parcelable, java.lang.ClassLoader)
		 */
		@Override
		public void restoreState( Parcelable parcel, ClassLoader cl ) {
			Log.v( TAG, "restoreState : enter" );

			// TODO Auto-generated method stub

			Log.v( TAG, "restoreState : exit" );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#saveState()
		 */
		@Override
		public Parcelable saveState() {
			Log.v( TAG, "saveState : enter" );

			// TODO Auto-generated method stub
			
			Log.v( TAG, "saveState : exit" );
			return null;
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#startUpdate(android.view.View)
		 */
		@Override
		public void startUpdate( View view ) {
			Log.v( TAG, "startUpdate : enter" );

			// TODO Auto-generated method stub

			Log.v( TAG, "startUpdate : exit" );
		}

	}
}
