/**
 * 
 */
package org.mythtv.client.ui.dvr;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythtvFragmentActivity;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrActionRow;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrActionsHeaderRow;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrGuideActionRow;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrGuideLastUpdateActionRow;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrRecordingRulesActionRow;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrRecordingRulesLastUpdateActionRow;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrRecordingsActionRow;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrRecordingsLastUpdateActionRow;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrUpcomingActionRow;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrUpcomingLastUpdateActionRow;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrVersionRow;
import org.mythtv.client.ui.navigationDrawer.Row;
import org.mythtv.client.ui.navigationDrawer.TopLevelRowType;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.services.api.dvr.impl.DvrTemplate;
import org.mythtv.services.api.guide.impl.GuideTemplate;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * @author dmfrey
 *
 */
@TargetApi( Build.VERSION_CODES.ICE_CREAM_SANDWICH )
public class DvrNavigationDrawerActivity extends AbstractMythtvFragmentActivity {

	private static final String TAG = DvrNavigationDrawerActivity.class.getSimpleName();
	
	private DrawerLayout drawer = null;
	private ListView navList = null;

	private DvrNavigationDrawerAdapter mAdapter;
	
	private int selection = 2, oldSelection = -1;
//	private Row selectedRow = null;

    private static final String OPENED_DVR_KEY = "OPENED_DVR_KEY";
    private SharedPreferences prefs = null;
    private Boolean opened = null;

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		Log.v( TAG, "onCreate : enter" );

		setContentView( R.layout.activity_navigation_drawer );

        mAdapter = new DvrNavigationDrawerAdapter( getActionBar().getThemedContext() );

		drawer = (DrawerLayout) findViewById( R.id.drawer_layout );

		navList = (ListView) findViewById( R.id.drawer );
		navList.setAdapter( mAdapter );
		
		navList.setOnItemClickListener( new OnItemClickListener() {

			/* (non-Javadoc)
			 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
			 */
			@Override
			public void onItemClick( AdapterView<?> parent, View view, final int position, long id ) {
				Log.v( TAG, "onItemClick : enter" );
				
				Log.v( TAG, "onItemClick : position=" + position + ", id=" + id + ", oldSelection=" + oldSelection );
				
				selection = position;
				
				updateContent(); 
				
				Log.v( TAG, "onItemClick : exit" );
			}

		});

		updateContent(); 
		getActionBar().setDisplayHomeAsUpEnabled( true );
		getActionBar().setHomeButtonEnabled( true );

		new Thread( new Runnable() {

			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				
				prefs = getPreferences( MODE_PRIVATE );
				opened = prefs.getBoolean( OPENED_DVR_KEY, false );
				
				if( opened == false ) {
					
					drawer.openDrawer( navList );
					
				}
				
			}

		}).start();

        Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate( Bundle savedInstanceState ) {
		super.onPostCreate( savedInstanceState );
		Log.v( TAG, "onPostCreate : enter" );
		
		Log.v( TAG, "onPostCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );

	    // Handle item selection
	    switch( item.getItemId() ) {
	        case android.R.id.home:
	    		Log.v( TAG, "onOptionsItemSelected : exit, home pressed" );

	    		onBackPressed();
	            return true;
	            
	        default:
	    		Log.v( TAG, "onOptionsItemSelected : exit" );

	    		return super.onOptionsItemSelected( item );
	    }

	}

    /* (non-Javadoc)
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu( Menu menu ) {
		Log.v( TAG, "onPrepareOptionsMenu : enter" );

    	if( null != drawer && null != navList ) {
    		
/*    		MenuItem item = menu.findItem( R.id.add );
    		if( item != null ) {
    			item.setVisible( !drawer.isDrawerOpen( navList ) );
    		}
*/    		
    	}
    	
		Log.v( TAG, "onPrepareOptionsMenu : exit" );
    	return super.onPrepareOptionsMenu( menu );
    }

    // internal helpers
	
	private void updateContent() {
		Log.v( TAG, "updateContent : enter" );
		
		Row row = mAdapter.getItem( selection );
		if( row instanceof DvrActionRow ) {
		
			getActionBar().setTitle( row.getTitle() );

			if( selection != oldSelection ) {
				FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
				tx.replace( R.id.main, Fragment.instantiate( DvrNavigationDrawerActivity.this, row.getFragment() ) );
				tx.commit();
			
				oldSelection = selection;
			}

//            selectedRow = row;

			drawer.closeDrawer( navList );
			invalidateOptionsMenu();
//			mAdapter.resetConnectedLocationProfile();
            
		}
		
		Log.v( TAG, "updateContent : exit" );
	}
	
	private class DvrNavigationDrawerAdapter extends BaseAdapter {

		private Context mContext;

        private LocationProfile mLocationProfile;

		private List<Row> rows = new ArrayList<Row>();

        public DvrNavigationDrawerAdapter( Context context ) {
			this.mContext = context;
			
			this.mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( mContext );

//			if( null == selectedRow ) {
//				selectedRow = new DvrRecordingsActionRow( mContext, "Recordings" );
//			}
			
			setupRowsList();
        }

        /* (non-Javadoc)
         * @see android.widget.BaseAdapter#getItemViewType(int)
         */
        @Override
        public int getItemViewType( int position ) {
            return rows.get( position ).getViewType();
        }

        /* (non-Javadoc)
         * @see android.widget.BaseAdapter#getViewTypeCount()
         */
        @Override
        public int getViewTypeCount() {
            return TopLevelRowType.values().length;
        }

        /* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return rows.size();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Row getItem( int position ) {
			return rows.get( position );
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId( int position ) {
			return position;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			return rows.get( position ).getView( convertView );
		}
		
		// internal helpers
		
		private void setupRowsList() {

			rows = new ArrayList<Row>();
			
			rows.add( new DvrVersionRow( mContext, "MAF", "x" ) );

			rows.add( new DvrActionsHeaderRow( mContext, "Actions" ) );
			
			rows.add( new DvrRecordingsActionRow( mContext, "Recordings" ) );
//			if( null != selectedRow && selectedRow instanceof DvrRecordingsActionRow ) {
				EtagInfoDelegate recordingsEtag = mEtagDaoHelper.findByEndpointAndDataId( mContext, mLocationProfile, DvrTemplate.Endpoint.GET_RECORDED_LIST.name(), "" );
//				if( null != recordingsEtag && recordingsEtag.getId() > 0 ) {
					rows.add( new DvrRecordingsLastUpdateActionRow( mContext, recordingsEtag ) );
//				}
//			}
			
			rows.add( new DvrUpcomingActionRow( mContext, "Upcoming" ) );
//			if( null != selectedRow && selectedRow instanceof DvrUpcomingActionRow ) {
				EtagInfoDelegate upcomingEtag = mEtagDaoHelper.findByEndpointAndDataId( mContext, mLocationProfile, DvrTemplate.Endpoint.GET_UPCOMING_LIST.name(), "" );
//				if( null != upcomingEtag && upcomingEtag.getId() > 0 ) {
					rows.add( new DvrUpcomingLastUpdateActionRow( mContext, upcomingEtag ) );
//				}
//			}
			
			rows.add( new DvrGuideActionRow( mContext, "Guide" ) );
//			if( null != selectedRow && selectedRow instanceof DvrGuideActionRow ) {
				EtagInfoDelegate guideEtag = mEtagDaoHelper.findByEndpointAndDataId( mContext, mLocationProfile, GuideTemplate.Endpoint.GET_PROGRAM_GUIDE.name(), "" );
//				if( null != guideEtag && guideEtag.getId() > 0 ) {
					rows.add( new DvrGuideLastUpdateActionRow( mContext, guideEtag ) );
//				}
//			}
			
			rows.add( new DvrRecordingRulesActionRow( mContext, "Recording Rules" ) );
//			if( null != selectedRow && selectedRow instanceof DvrRecordingRulesActionRow ) {
				EtagInfoDelegate recordingRulesEtag = mEtagDaoHelper.findByEndpointAndDataId( mContext, mLocationProfile, DvrTemplate.Endpoint.GET_RECORD_SCHEDULE_LIST.name(), "" );
//				if( null != recordingRulesEtag && recordingRulesEtag.getId() > 0 ) {
					rows.add( new DvrRecordingRulesLastUpdateActionRow( mContext, recordingRulesEtag ) );
//				}
//			}
			
		}
		
	}
	
}
