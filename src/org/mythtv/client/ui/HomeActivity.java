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
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.setup.SetupActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * @author Daniel Frey
 * 
 */
public class HomeActivity extends FragmentActivity {

	private final static String TAG = HomeActivity.class.getSimpleName();

//	private ViewPager mViewPager;
//	private TabsAdapter mTabsAdapter;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.d( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_home );
		
//		mViewPager = new ViewPager( this );
//		mViewPager.setId( R.id.dashboard_pager );
//		setContentView( mViewPager );
//		// getActivityHelper().setupActionBar( null, 0 );
//
//		final ActionBar bar = getActionBar();
//		bar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );
//		bar.setDisplayOptions( 0, ActionBar.DISPLAY_SHOW_TITLE );
//
//		mTabsAdapter = new TabsAdapter( this, mViewPager );
//		mTabsAdapter.addTab( bar.newTab().setText( "Dvr" ), DvrDashboardFragment.class, null );
//		mTabsAdapter.addTab( bar.newTab().setText( "Multimeida" ), MediaDashboardFragment.class, null );
//
//		if( savedInstanceState != null ) {
//			bar.setSelectedNavigationItem( savedInstanceState.getInt( "tab", 0 ) );
//		}

		Log.d( TAG, "onCreate : exit" );
	}

//	@Override
//	protected void onSaveInstanceState( Bundle outState ) {
//		Log.d( TAG, "onSaveInstanceState : enter" );
//
//		super.onSaveInstanceState( outState );
//		outState.putInt( "tab", getActionBar().getSelectedNavigationIndex() );
//
//		Log.d( TAG, "onSaveInstanceState : exit" );
//	}

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
		
		Intent intent = null;
		
		switch( item.getItemId() ) {
		case R.id.menu_setup:
			Log.d( TAG, "onOptionsItemSelected : setup selected" );

			intent = new Intent( this, SetupActivity.class );
			startActivity( intent );
			return true;
		case R.id.menu_clear:
			Log.d( TAG, "onOptionsItemSelected : clear selected" );

			( (MainApplication) getApplicationContext() ).clearMasterBackend();
			
			intent = new Intent( this, MythtvMasterBackendActivity.class );
			startActivity( intent );
			return true;
		}

		Log.d( TAG, "onOptionsItemSelected : exit" );
		return false;
	}

	/**
	 * This is a helper class that implements the management of tabs and all
	 * details of connecting a ViewPager with associated TabHost. It relies on a
	 * trick. Normally a tab host has a simple API for supplying a View or
	 * Intent that each tab will show. This is not sufficient for switching
	 * between pages. So instead we make the content part of the tab host 0dp
	 * high (it is not shown) and the TabsAdapter supplies its own dummy view to
	 * show as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct paged in the ViewPager whenever the selected tab
	 * changes.
	 */
//	public static class TabsAdapter extends FragmentPagerAdapter implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
//		private final Context mContext;
//		private final ActionBar mActionBar;
//		private final ViewPager mViewPager;
//		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
//
//		static final class TabInfo {
//			private final Class<?> clss;
//			private final Bundle args;
//
//			TabInfo( Class<?> _class, Bundle _args ) {
//				clss = _class;
//				args = _args;
//			}
//		}
//
//		public TabsAdapter( FragmentActivity activity, ViewPager pager ) {
//			super( activity.getSupportFragmentManager() );
//			mContext = activity;
//			mActionBar = activity.getActionBar();
//			mViewPager = pager;
//			mViewPager.setAdapter( this );
//			mViewPager.setOnPageChangeListener( this );
//		}
//
//		public void addTab( ActionBar.Tab tab, Class<?> clss, Bundle args ) {
//			TabInfo info = new TabInfo( clss, args );
//			tab.setTag( info );
//			tab.setTabListener( this );
//			mTabs.add( info );
//			mActionBar.addTab( tab );
//			notifyDataSetChanged();
//		}
//
//		@Override
//		public int getCount() {
//			return mTabs.size();
//		}
//
//		@Override
//		public Fragment getItem( int position ) {
//			TabInfo info = mTabs.get( position );
//			return Fragment.instantiate( mContext, info.clss.getName(), info.args );
//		}
//
//		@Override
//		public void onPageScrolled( int position, float positionOffset, int positionOffsetPixels ) {
//		}
//
//		@Override
//		public void onPageSelected( int position ) {
//			mActionBar.setSelectedNavigationItem( position );
//		}
//
//		@Override
//		public void onPageScrollStateChanged( int state ) {
//		}
//
//		@Override
//		public void onTabSelected( Tab tab, FragmentTransaction ft ) {
//			Object tag = tab.getTag();
//			for( int i = 0; i < mTabs.size(); i++ ) {
//				if( mTabs.get( i ) == tag ) {
//					mViewPager.setCurrentItem( i );
//				}
//			}
//		}
//
//		@Override
//		public void onTabUnselected( Tab tab, FragmentTransaction ft ) {
//		}
//
//		@Override
//		public void onTabReselected( Tab tab, FragmentTransaction ft ) {
//		}
//	}

}
