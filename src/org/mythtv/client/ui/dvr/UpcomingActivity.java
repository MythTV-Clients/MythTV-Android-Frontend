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
package org.mythtv.client.ui.dvr;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythtvFragmentActivity;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.client.ui.util.MenuItemRefreshAnimated;
import org.mythtv.db.dvr.DvrEndpoint;
import org.mythtv.db.dvr.UpcomingDaoHelper;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.service.dvr.UpcomingDownloadService;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.RunningServiceHelper;

import android.annotation.TargetApi;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * @author Daniel Frey
 * @author Thomas G. Kenny Jr
 *
 */
public class UpcomingActivity extends AbstractMythtvFragmentActivity {

	private static final String TAG = UpcomingActivity.class.getSimpleName();


	private UpcomingDownloadReceiver upcomingDownloadReceiver = new UpcomingDownloadReceiver();

	private EtagDaoHelper mEtagDaoHelper = EtagDaoHelper.getInstance();
	private RunningServiceHelper mRunningServiceHelper = RunningServiceHelper.getInstance();
	private UpcomingDaoHelper mUpcomingDaoHelper = UpcomingDaoHelper.getInstance();
			
	private MythtvUpcomingPagerAdapter mAdapter;
	
	private LocationProfile mLocationProfile;
	private MenuItemRefreshAnimated mMenuItemRefresh;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_dvr_upcoming );
		
		mMenuItemRefresh = new MenuItemRefreshAnimated(this);

		setupActionBar();

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( this );
		
		mAdapter = new MythtvUpcomingPagerAdapter( getSupportFragmentManager() );
		ViewPager mPager = (ViewPager) findViewById( R.id.dvr_upcoming_pager );
		mPager.setAdapter( mAdapter );
		mPager.setCurrentItem( 0 );

		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		Log.v( TAG, "onStart : enter" );
		super.onStart();

		IntentFilter upcomingDownloadFilter = new IntentFilter();
		upcomingDownloadFilter.addAction( UpcomingDownloadService.ACTION_PROGRESS );
		upcomingDownloadFilter.addAction( UpcomingDownloadService.ACTION_COMPLETE );
	    registerReceiver( upcomingDownloadReceiver, upcomingDownloadFilter );

	    Log.v( TAG, "onStart : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();

		DateTime etag = mEtagDaoHelper.findDateByEndpointAndDataId( this, mLocationProfile, DvrEndpoint.GET_UPCOMING_LIST.name(), "" );
		if( null != etag ) {
			
			DateTime now = DateUtils.convertUtc( new DateTime( System.currentTimeMillis() ) );
			if( now.getMillis() - etag.getMillis() > ( 2 * 3600000 ) ) {
				loadData();
			}
		} else {
			loadData();
		}

		Log.v( TAG, "onResume : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onStop()
	 */
	@Override
	protected void onStop() {
		Log.v( TAG, "onStop : enter" );
		super.onStop();

		if( null != upcomingDownloadReceiver ) {
			try {
				unregisterReceiver( upcomingDownloadReceiver );
				upcomingDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		Log.v( TAG, "onStop : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.AbstractMythtvFragmentActivity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	@TargetApi( 11 )
	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.v( TAG, "onCreateOptionsMenu : enter" );

		mMenuHelper.refreshMenuItem( this, menu, this.mMenuItemRefresh );

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
		case MenuHelper.REFRESH_ID:
			Log.d( TAG, "onOptionsItemSelected : refresh selected" );

			loadData();
		    
	        return true;
		}
		
		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	/**
	 * @return
	 */
	public UpcomingDaoHelper getUpcomingDaoHelper() {
		return mUpcomingDaoHelper;
	}
	
	// internal helpers
	
	private void loadData() {
		Log.v( TAG, "loadData : enter" );
		
		if( !mRunningServiceHelper.isServiceRunning( this, "org.mythtv.service.dvr.UpcomingDownloadService" ) ) {
			startService( new Intent( UpcomingDownloadService.ACTION_DOWNLOAD ) );
			this.mMenuItemRefresh.startRefreshAnimation();
		}
		
		Log.v( TAG, "loadData : exit" );
	}
	
	private class MythtvUpcomingPagerAdapter extends FragmentStatePagerAdapter {

		private List<String> fragmentHeadings;
		
		public MythtvUpcomingPagerAdapter( FragmentManager fm ) {
			super( fm );
			
			fragmentHeadings = new ArrayList<String>();

			DateTime day = DateUtils.getToday();
			
			String formattedDay = null;
			for( int i = 0; i < 13; i++ ) {
				formattedDay = DateUtils.dateFormatter.print( day );
				fragmentHeadings.add( formattedDay );
				
				day = DateUtils.getNextDay( day );
			}

		}

		/* (non-Javadoc)
		 * @see android.support.v4.app.FragmentStatePagerAdapter#getItem(int)
		 */
		@Override
		public Fragment getItem( int position ) {
			Log.v( TAG, "getItem : enter" );
			
			Log.d( TAG, "getItem : fragmentHeading=" + fragmentHeadings.get( position ) );
			UpcomingFragment upcomingFragment = UpcomingFragment.newInstance( fragmentHeadings.get( position ) ); 
			
			Log.v( TAG, "getItem : exit" );
			return upcomingFragment;
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#getItemPosition(java.lang.Object)
		 */
		@Override
		public int getItemPosition( Object object ) {
			return POSITION_NONE;
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

			switch( position ) {
				case 0:
					return getMainApplication().getResources().getString( R.string.upcoming_today );
				case 1:
					return getMainApplication().getResources().getString( R.string.upcoming_tomorrow );
				default:
                    return DateUtils.getDateWithLocaleFormatting(fragmentHeadings.get(position), getMainApplication().getDateFormat());
			}

		}
		
	}

	private class UpcomingDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( UpcomingDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "UpcomingDownloadReceiver.onReceive : " + intent.getStringExtra( UpcomingDownloadService.EXTRA_PROGRESS ) );
	        	
	        	String filename = intent.getStringExtra( UpcomingDownloadService.EXTRA_PROGRESS_FILENAME );
	        	if( null != filename && !"".equals( filename ) ) {
	        		Log.d( TAG, "UpcomingDownloadReceiver.onReceive : removing from cache" + filename );
	        	}
	        }
	        
	        if ( intent.getAction().equals( UpcomingDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "UpcomingDownloadReceiver.onReceive : " + intent.getStringExtra( UpcomingDownloadService.EXTRA_COMPLETE ) );
	        	
	        	mMenuItemRefresh.stopRefreshAnimation();
	        	
	        	if( intent.getExtras().containsKey( UpcomingDownloadService.EXTRA_COMPLETE_UPTODATE ) ) {
	        		Toast.makeText( UpcomingActivity.this, "Upcoming Programs are up to date!", Toast.LENGTH_SHORT ).show();
	        	} else if( intent.getExtras().containsKey( UpcomingDownloadService.EXTRA_COMPLETE_OFFLINE ) ) {
	        		Toast.makeText( UpcomingActivity.this, "Upcoming Programs Update failed because Master Backend is not connected!", Toast.LENGTH_SHORT ).show();
	        	} else {
	        		Toast.makeText( UpcomingActivity.this, "Upcoming Programs updated!", Toast.LENGTH_SHORT ).show();

	        		mAdapter.notifyDataSetChanged();
	        	}
	        }
	        
		}
		
	}

}
