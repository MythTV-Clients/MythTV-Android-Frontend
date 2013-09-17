/**
 * 
 */
package org.mythtv.client.ui.media;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythtvFragmentActivity;
import org.mythtv.client.ui.media.navigationDrawer.MediaActionRow;
import org.mythtv.client.ui.media.navigationDrawer.MediaActionsHeaderRow;
import org.mythtv.client.ui.media.navigationDrawer.MediaPicturesActionRow;
import org.mythtv.client.ui.media.navigationDrawer.MediaRowType;
import org.mythtv.client.ui.media.navigationDrawer.MediaVersionRow;
import org.mythtv.client.ui.navigationDrawer.Row;
import org.mythtv.client.ui.util.MenuHelper;

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

/**
 * @author dmfrey
 *
 */
public class MediaNavigationDrawerActivity extends AbstractMythtvFragmentActivity {

	private static final String TAG = MediaNavigationDrawerActivity.class.getSimpleName();
	
	private static final String SELECTION_ID = "SELECTION";
	
	private static WeakReference<MediaNavigationDrawerActivity> wrActivity = null; 

	private DrawerLayout drawer = null;
	private ActionBarDrawerToggle drawerToggle = null;
	private ListView navList = null;

	private MediaNavigationDrawerAdapter mAdapter;
	
	private int selection = 2, oldSelection = -1;
	private Row selectedRow = null;

    private static final String OPENED_MEDIA_KEY = "OPENED_MEDIA_KEY";
    private SharedPreferences prefs = null;
    private Boolean opened = null;

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		Log.v( TAG, "onCreate : enter" );
		
		wrActivity = new WeakReference<MediaNavigationDrawerActivity>( this );
		
		// Read saved state if available
		if( null != savedInstanceState){
			
			if( savedInstanceState.containsKey( SELECTION_ID ) ) {
				selection = savedInstanceState.getInt( SELECTION_ID );
			}
			
		}

		setContentView( R.layout.activity_navigation_drawer );

        mAdapter = new MediaNavigationDrawerAdapter( wrActivity.get().getActionBar().getThemedContext() );

		drawer = (DrawerLayout) wrActivity.get().findViewById( R.id.drawer_layout );

		navList = (ListView) wrActivity.get().findViewById( R.id.drawer );
		navList.setAdapter( mAdapter );
		
		drawerToggle = new ActionBarDrawerToggle( wrActivity.get(), drawer, R.drawable.ic_drawer, R.string.open, R.string.close ) {
	        
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
                        editor.putBoolean( OPENED_MEDIA_KEY, true );
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
	            
	            wrActivity.get().getActionBar().setTitle( R.string.tab_multimedia );
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
		wrActivity.get().getActionBar().setDisplayHomeAsUpEnabled( true );
		wrActivity.get().getActionBar().setHomeButtonEnabled( true );

		new Thread( new Runnable() {

			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				
				prefs = wrActivity.get().getPreferences( MODE_PRIVATE );
				opened = prefs.getBoolean( OPENED_MEDIA_KEY, false );
				
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
		if( row instanceof MediaActionRow ) {
			Log.v( TAG, "updateContent : row is MediaActionRow" );
			
			if( row.isImplemented() ) {
				Log.v( TAG, "updateContent : row is implemented" );

				wrActivity.get().getActionBar().setTitle( row.getTitle() );

				if( selection != oldSelection ) {
					Log.v( TAG, "updateContent : selection is not last selected" );

					if( null != wrActivity.get() && wrActivity.get().isFinishing() != true ) {
						
						FragmentTransaction tx = wrActivity.get().getSupportFragmentManager().beginTransaction();
						tx.replace( R.id.main, Fragment.instantiate( wrActivity.get(), row.getFragment() ) );
						tx.commit();

					}
				
					oldSelection = selection;
				}

				selectedRow = row;

				drawer.closeDrawer( navList );
				invalidateOptionsMenu();
				mAdapter.resetNavigationDrawer();
            
			}

		}
		
		Log.v( TAG, "updateContent : exit" );
	}
	
	private class MediaNavigationDrawerAdapter extends BaseAdapter {

		private Context mContext;

		private List<Row> rows = new ArrayList<Row>();

        public MediaNavigationDrawerAdapter( Context context ) {
			mContext = context;
			
			if( null == selectedRow ) {
				selectedRow = new MediaPicturesActionRow( mContext, "Pictures" );
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
            return MediaRowType.values().length;
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
			
			rows.add( new MediaVersionRow( mContext, "MAF", "x" ) );

			rows.add( new MediaActionsHeaderRow( mContext, "Media Actions" ) );
			
			rows.add( new MediaPicturesActionRow( mContext, "Pictures" ) );
			//rows.add( new MediaVideosActionRow( mContext, "Videos" ) );
			//rows.add( new MediaMusicActionRow( mContext, "Music" ) );
			
		}
		
	}
	
}
