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
import org.mythtv.client.ui.dvr.navigationDrawer.DvrLastUpdateActionRow.OnRefreshListener;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrRecordingRulesActionRow;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrRecordingRulesLastUpdateActionRow;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrRecordingsActionRow;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrRecordingsLastUpdateActionRow;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrRowType;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrUpcomingActionRow;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrUpcomingLastUpdateActionRow;
import org.mythtv.client.ui.dvr.navigationDrawer.DvrVersionRow;
import org.mythtv.client.ui.navigationDrawer.Row;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.db.dvr.DvrEndpoint;
import org.mythtv.db.guide.GuideEndpoint;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
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
import android.widget.Toast;

/**
 * @author dmfrey
 *
 */
public class DvrNavigationDrawerActivity extends AbstractMythtvFragmentActivity implements OnRefreshListener {

	private static final String TAG = DvrNavigationDrawerActivity.class.getSimpleName();
	
	private static final String SELECTION_ID = "SELECTION";
	
	private DrawerLayout drawer = null;
	private ActionBarDrawerToggle drawerToggle = null;
	private ListView navList = null;

	private DvrNavigationDrawerAdapter mAdapter;
	
	private int selection = 2, oldSelection = -1;
	private Row selectedRow = null;

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
		
		// Read saved state if available
		if( null != savedInstanceState ) {
			
			if( savedInstanceState.containsKey( SELECTION_ID ) ) {
				selection = savedInstanceState.getInt( SELECTION_ID );
			}
			
		}

		setContentView( R.layout.activity_navigation_drawer );

        mAdapter = new DvrNavigationDrawerAdapter( getActionBar().getThemedContext() );

		drawer = (DrawerLayout) findViewById( R.id.drawer_layout );

		navList = (ListView) findViewById( R.id.drawer );
		navList.setAdapter( mAdapter );
		
		drawerToggle = new ActionBarDrawerToggle( this, drawer, R.drawable.ic_drawer, R.string.open, R.string.close ) {
	        
			/* (non-Javadoc)
			 * @see android.support.v4.app.ActionBarDrawerToggle#onDrawerClosed(android.view.View)
			 */
			@Override
	        public void onDrawerClosed( View drawerView ) {
				Log.d( TAG, "onDrawerClosed : enter" );
				super.onDrawerClosed( drawerView );
	            
                invalidateOptionsMenu();
                if( null != opened && opened == false ) {
                	
                	opened = true;
                    
                	if( null != prefs ) {
                        Editor editor = prefs.edit();
                        editor.putBoolean( OPENED_DVR_KEY, true );
                        editor.apply();
                    }
                	
                }
                
				Log.d( TAG, "onDrawerClosed : exit" );
	        }
	 
	        /* (non-Javadoc)
	         * @see android.support.v4.app.ActionBarDrawerToggle#onDrawerOpened(android.view.View)
	         */
	        @Override
	        public void onDrawerOpened( View drawerView ) {
				Log.d( TAG, "onDrawerOpened : enter" );
	            super.onDrawerOpened( drawerView );
	            
	            getActionBar().setTitle( R.string.tab_dvr );
	            invalidateOptionsMenu();

	            Log.d( TAG, "onDrawerOpened : exit" );
	        }
	        
	    };
		drawer.setDrawerListener( drawerToggle );

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
		
		drawerToggle.syncState();

		Log.v( TAG, "onPostCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );

		if( drawerToggle.onOptionsItemSelected( item ) ) {
			Log.v( TAG, "onOptionsItemSelected : exit, drawerToggle selected" );

			return true;
		}

   		Log.v( TAG, "onOptionsItemSelected : exit" );
   		return super.onOptionsItemSelected( item );
	}

    /* (non-Javadoc)
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu( Menu menu ) {
		Log.v( TAG, "onPrepareOptionsMenu : enter" );

    	if( null != drawer && null != navList ) {
    		
    		MenuItem about = menu.findItem( MenuHelper.ABOUT_ID );
    		if( null != about ) {
    			about.setVisible( !drawer.isDrawerOpen( navList ) );
    		}

    		MenuItem help = menu.findItem( MenuHelper.HELP_ID );
    		if( null != help ) {
    			help.setVisible( !drawer.isDrawerOpen( navList ) );
    		}

    		MenuItem refresh = menu.findItem( MenuHelper.REFRESH_ID );
    		if( null != refresh ) {
    			refresh.setVisible( !drawer.isDrawerOpen( navList ) );
    		}

    		MenuItem watch = menu.findItem( MenuHelper.WATCH_ID );
    		if( null != watch ) {
    			watch.setVisible( !drawer.isDrawerOpen( navList ) );
    		}

    		MenuItem watchOnTv = menu.findItem( MenuHelper.WATCH_ON_TV_ID );
    		if( null != watchOnTv ) {
    			watchOnTv.setVisible( !drawer.isDrawerOpen( navList ) );
    		}

    		MenuItem add = menu.findItem( MenuHelper.ADD_ID );
    		if( null != add ) {
    			add.setVisible( !drawer.isDrawerOpen( navList ) );
    		}

    		MenuItem edit = menu.findItem( MenuHelper.EDIT_ID );
    		if( null != edit ) {
    			edit.setVisible( !drawer.isDrawerOpen( navList ) );
    		}

    		MenuItem save = menu.findItem( MenuHelper.SAVE_ID );
    		if( null != save ) {
    			save.setVisible( !drawer.isDrawerOpen( navList ) );
    		}


    		MenuItem delete = menu.findItem( MenuHelper.DELETE_ID );
    		if( null != delete ) {
    			delete.setVisible( !drawer.isDrawerOpen( navList ) );
    		}

    		MenuItem guideDay = menu.findItem( MenuHelper.GUIDE_ID );
    		if( null != guideDay ) {
    			guideDay.setVisible( !drawer.isDrawerOpen( navList ) );
    		}
    		
    	}
    	
		Log.v( TAG, "onPrepareOptionsMenu : exit" );
    	return super.onPrepareOptionsMenu( menu );
    }

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState( Bundle outState ) {
		
		outState.putInt( SELECTION_ID, selection );
		
		super.onSaveInstanceState( outState );
	}

    // internal helpers
	
	private void updateContent() {
		Log.v( TAG, "updateContent : enter" );
		
		Row row = mAdapter.getItem( selection );
		if( row instanceof DvrActionRow ) {
		
			if( row.isImplemented() ) {
				getActionBar().setTitle( row.getTitle() );
				
				if( selection != oldSelection ) {
				
					FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
					tx.replace( R.id.main, Fragment.instantiate( DvrNavigationDrawerActivity.this, row.getFragment() ) );
					tx.commit();
					
					oldSelection = selection;
				}

				selectedRow = row;

				drawer.closeDrawer( navList );
				invalidateOptionsMenu();
				mAdapter.resetNavigationDrawer();
            
			} else {
				Toast.makeText( DvrNavigationDrawerActivity.this, row.getTitle() + " comming soon!", Toast.LENGTH_SHORT ).show();
			}
		
		}
		
		Log.v( TAG, "updateContent : exit" );
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.navigationDrawer.DvrLastUpdateActionRow.OnRefreshListener#refresh(org.mythtv.client.ui.navigationDrawer.Row)
	 */
	@Override
	public void refresh( Row row ) {
		Log.v( TAG, "refresh : enter" );
		
		if( null != row ) {
			
			if( row instanceof DvrRecordingsLastUpdateActionRow ) {
				Log.v( TAG, "refresh : refresh recordings" );
								
			}
			
			if( row instanceof DvrUpcomingLastUpdateActionRow ) {
				Log.v( TAG, "refresh : refresh upcoming" );
								
			}
			
			if( row instanceof DvrGuideLastUpdateActionRow ) {
				Log.v( TAG, "refresh : refresh guide" );
								
			}
			
			if( row instanceof DvrRecordingRulesLastUpdateActionRow ) {
				Log.v( TAG, "refresh : refresh recording rules" );
								
			}
			
		}
		
		Log.v( TAG, "refresh : exit" );
	}
	
	private class DvrNavigationDrawerAdapter extends BaseAdapter {

		private Context mContext;

        private LocationProfile mLocationProfile;

		private List<Row> rows = new ArrayList<Row>();

        public DvrNavigationDrawerAdapter( Context context ) {
			this.mContext = context;
			
			this.mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( mContext );

			if( null == selectedRow ) {
				selectedRow = new DvrRecordingsActionRow( mContext, "Recordings" );
			}
			
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
            return DvrRowType.values().length;
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
		
		public void resetNavigationDrawer() {
		
			setupRowsList();
			
			notifyDataSetChanged();
			navList.invalidateViews();

		}
		
		// internal helpers
		
		private void setupRowsList() {

			rows = new ArrayList<Row>();
			
			rows.add( new DvrVersionRow( mContext, "MAF", "x" ) );

			rows.add( new DvrActionsHeaderRow( mContext, "DVR Actions" ) );
			
			rows.add( new DvrRecordingsActionRow( mContext, "Recordings" ) );
			if( null != selectedRow && selectedRow instanceof DvrRecordingsActionRow ) {
				EtagInfoDelegate recordingsEtag = mEtagDaoHelper.findByEndpointAndDataId( mContext, mLocationProfile, DvrEndpoint.GET_RECORDED_LIST.name(), "" );
				if( null != recordingsEtag && recordingsEtag.getId() > 0 ) {
					DvrRecordingsLastUpdateActionRow recordingsLastUpdateRow = new DvrRecordingsLastUpdateActionRow( mContext, recordingsEtag );
					recordingsLastUpdateRow.setOnRefreshListener( DvrNavigationDrawerActivity.this );
					rows.add( recordingsLastUpdateRow );
				}
			}
			
			rows.add( new DvrUpcomingActionRow( mContext, "Upcoming" ) );
			if( null != selectedRow && selectedRow instanceof DvrUpcomingActionRow ) {
				EtagInfoDelegate upcomingEtag = mEtagDaoHelper.findByEndpointAndDataId( mContext, mLocationProfile, DvrEndpoint.GET_UPCOMING_LIST.name(), "" );
				if( null != upcomingEtag && upcomingEtag.getId() > 0 ) {
					DvrUpcomingLastUpdateActionRow upcomingLastUpdateRow = new DvrUpcomingLastUpdateActionRow( mContext, upcomingEtag );
					upcomingLastUpdateRow.setOnRefreshListener( DvrNavigationDrawerActivity.this );
					rows.add( upcomingLastUpdateRow );
				}
			}
			
			rows.add( new DvrGuideActionRow( mContext, "Guide" ) );
			if( null != selectedRow && selectedRow instanceof DvrGuideActionRow ) {
				EtagInfoDelegate guideEtag = mEtagDaoHelper.findByEndpointAndDataId( mContext, mLocationProfile, GuideEndpoint.GET_PROGRAM_GUIDE.name(), "" );
				if( null != guideEtag && guideEtag.getId() > 0 ) {
					DvrGuideLastUpdateActionRow guideLastUpdateRow = new DvrGuideLastUpdateActionRow( mContext, guideEtag );
					guideLastUpdateRow.setOnRefreshListener( DvrNavigationDrawerActivity.this );
					rows.add( guideLastUpdateRow );
				}
			}
			
			rows.add( new DvrRecordingRulesActionRow( mContext, "Recording Rules" ) );
			if( null != selectedRow && selectedRow instanceof DvrRecordingRulesActionRow ) {
				EtagInfoDelegate recordingRulesEtag = mEtagDaoHelper.findByEndpointAndDataId( mContext, mLocationProfile, DvrEndpoint.GET_RECORD_SCHEDULE_LIST.name(), "" );
				if( null != recordingRulesEtag && recordingRulesEtag.getId() > 0 ) {
					DvrRecordingRulesLastUpdateActionRow recordingRulesLastUpdateRow = new DvrRecordingRulesLastUpdateActionRow( mContext, recordingRulesEtag );
					recordingRulesLastUpdateRow.setOnRefreshListener( DvrNavigationDrawerActivity.this );
					rows.add( recordingRulesLastUpdateRow );
				}
			}
			
		}
		
	}
	
}
