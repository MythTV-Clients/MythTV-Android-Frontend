/**
 * 
 */
package org.mythtv.client.ui.dvr;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.service.dvr.UpcomingDownloadService;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.RunningServiceHelper;
import org.mythtv.services.api.dvr.impl.DvrTemplate.Endpoint;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * @author Daniel Frey
 *
 */
public class UpcomingPagerFragment extends AbstractMythFragment {

	private static final String TAG = UpcomingPagerFragment.class.getSimpleName();

	private UpcomingDownloadReceiver upcomingDownloadReceiver = new UpcomingDownloadReceiver();

	private EtagDaoHelper mEtagDaoHelper = EtagDaoHelper.getInstance();
	private MenuHelper mMenuHelper = MenuHelper.getInstance();
	private RunningServiceHelper mRunningServiceHelper = RunningServiceHelper.getInstance();

	private LocationProfile mLocationProfile;
	
	private View mView;
	private ViewPager mViewPager;
	private MythtvUpcomingPagerAdapter mAdapter;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );
		
		mView = inflater.inflate( R.layout.activity_dvr_upcoming, container, false );
		mViewPager = (ViewPager) mView.findViewById( R.id.dvr_upcoming_pager );
		
		mAdapter = new MythtvUpcomingPagerAdapter( getChildFragmentManager() );
		mViewPager.setAdapter( mAdapter );
		mViewPager.setCurrentItem( 0 );

		Log.v( TAG, "onCreateView : exit" );
		return mView;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		setHasOptionsMenu( true );

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		
		DateTime etag = mEtagDaoHelper.findDateByEndpointAndDataId( getActivity(), mLocationProfile, Endpoint.GET_UPCOMING_LIST.name(), "" );
		if( null != etag ) {
			
			DateTime now = new DateTime();
			if( now.getMillis() - etag.getMillis() > ( 2 * 3600000 ) ) {
				loadData();
			}
		} else {
			loadData();
		}

		Log.v( TAG, "onActivityCreated : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		Log.v( TAG, "onStart : enter" );
		super.onStart();

		IntentFilter upcomingDownloadFilter = new IntentFilter();
		upcomingDownloadFilter.addAction( UpcomingDownloadService.ACTION_PROGRESS );
		upcomingDownloadFilter.addAction( UpcomingDownloadService.ACTION_COMPLETE );
	    getActivity().registerReceiver( upcomingDownloadReceiver, upcomingDownloadFilter );

	    Log.v( TAG, "onStart : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		Log.v( TAG, "onStop : enter" );
		super.onStop();

		if( null != upcomingDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( upcomingDownloadReceiver );
				upcomingDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, "onStop : error", e );
			}
		}

		Log.v( TAG, "onStop : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		Log.v( TAG, "onCreateOptionsMenu : enter" );

		mMenuHelper.refreshMenuItem( getActivity(), menu );

		Log.v( TAG, "onCreateOptionsMenu : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
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

	// internal helpers 
	
	private void loadData() {
		Log.v( TAG, "loadData : enter" );
		
		if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.dvr.UpcomingDownloadService" ) ) {
			getActivity().startService( new Intent( UpcomingDownloadService.ACTION_DOWNLOAD ) );
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
	        	
	        	if( intent.getExtras().containsKey( UpcomingDownloadService.EXTRA_COMPLETE_UPTODATE ) ) {
	        		Toast.makeText( getActivity(), "Upcoming Programs are up to date!", Toast.LENGTH_SHORT ).show();
	        	} else if( intent.getExtras().containsKey( UpcomingDownloadService.EXTRA_COMPLETE_OFFLINE ) ) {
	        		Toast.makeText( getActivity(), "Upcoming Programs Update failed because Master Backend is not connected!", Toast.LENGTH_SHORT ).show();
	        	} else {
	        		Toast.makeText( getActivity(), "Upcoming Programs updated!", Toast.LENGTH_SHORT ).show();

	        		mAdapter.notifyDataSetChanged();
	        	}
	        }
	        
		}
		
	}

}
