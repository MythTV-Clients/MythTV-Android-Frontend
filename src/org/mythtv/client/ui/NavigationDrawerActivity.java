/**
 * 
 */
package org.mythtv.client.ui;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.navigationDrawer.ActionRow;
import org.mythtv.client.ui.navigationDrawer.ActionsHeaderRow;
import org.mythtv.client.ui.navigationDrawer.DvrActionRow;
import org.mythtv.client.ui.navigationDrawer.FrontendsHeaderRow;
import org.mythtv.client.ui.navigationDrawer.FrontendsRow;
import org.mythtv.client.ui.navigationDrawer.ManageProfilesActionRow;
import org.mythtv.client.ui.navigationDrawer.MultimediaActionRow;
import org.mythtv.client.ui.navigationDrawer.ProfileRow;
import org.mythtv.client.ui.navigationDrawer.ProfileRow.ProfileChangedListener;
import org.mythtv.client.ui.navigationDrawer.Row;
import org.mythtv.client.ui.navigationDrawer.SetupActionRow;
import org.mythtv.client.ui.navigationDrawer.TopLevelRowType;
import org.mythtv.client.ui.navigationDrawer.VersionRow;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;
import org.mythtv.client.ui.preferences.MythtvPreferenceActivity;
import org.mythtv.client.ui.preferences.MythtvPreferenceActivityHC;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
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
@TargetApi( Build.VERSION_CODES.ICE_CREAM_SANDWICH )
public class NavigationDrawerActivity extends AbstractMythtvFragmentActivity {

	private static final String TAG = NavigationDrawerActivity.class.getSimpleName();

	private NavigationDrawerAdapter mAdapter;
	private ActionBarDrawerToggle drawerToggle = null;
	private DrawerLayout drawer = null;
	private ListView navList = null;

	private ProfileChangedListener mProfileChangedListener = new ProfileChangedListener() {
		
		/* (non-Javadoc)
		 * @see org.mythtv.client.ui.navigationDrawer.ProfileRow.ProfileChangedListener#onProfileChanged()
		 */
		@Override
		public void onProfileChanged() {
			Log.v( TAG, "onProfileChanged : enter" );

			LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile( NavigationDrawerActivity.this );
			mAdapter.resetConnectedLocationProfile( locationProfile );

			drawer.invalidate();
			drawer.closeDrawer( navList );
			
			Log.v( TAG, "onProfileChanged : exit" );
		}
		
	};
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		Log.v( TAG, "onCreate : enter" );

		setContentView( R.layout.activity_navigation_drawer );

		mAdapter = new NavigationDrawerAdapter( getActionBar().getThemedContext() );

		drawer = (DrawerLayout) findViewById( R.id.drawer_layout );

		navList = (ListView) findViewById( R.id.drawer );
		navList.setAdapter( mAdapter );
		
		drawerToggle = new ActionBarDrawerToggle( this, drawer, R.drawable.ic_drawer, R.string.open, R.string.close );
		drawer.setDrawerListener( drawerToggle );

		navList.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick( AdapterView<?> parent, View view, final int position, long id ) {
				Log.v( TAG, "onItemClick : enter" );
				
				Log.v( TAG, "onItemClick : position=" + position + ", id=" + id );
				
				Row row = (Row) mAdapter.getItem( position );

				if( row instanceof VersionRow ) {
					// NO-OP, nothing to see here
				}
				
				if( row instanceof ProfileRow ) {
					// NO-OP, nothing to see here
				}
				
				if( row instanceof FrontendsHeaderRow ) {
					// NO-OP, nothing to see here
				}
				
				if( row instanceof FrontendsRow ) {
					// NO-OP, nothing to see here
				}
				
				if( row instanceof ActionsHeaderRow ) {
					// NO-OP, nothing to see here
				}
				
				if( row instanceof ActionRow ) {
					
					drawer.closeDrawer( navList );

					if( row instanceof ManageProfilesActionRow ) {
						Log.v( TAG, "onCreate : starting preferences activity" );
						
						if( Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ) {
							startActivity( new Intent( NavigationDrawerActivity.this, MythtvPreferenceActivity.class ) );
					    } else {
					    	startActivity( new Intent( NavigationDrawerActivity.this, MythtvPreferenceActivityHC.class ) );
					    }
						
					}

					if( row instanceof DvrActionRow ) {
						Log.v( TAG, "onCreate : starting dvr activity" );
												
					}

					if( row instanceof MultimediaActionRow ) {
						Log.v( TAG, "onCreate : starting multimedia activity" );
						
						Toast.makeText( NavigationDrawerActivity.this, "Multimedia - Coming Soon!", Toast.LENGTH_SHORT ).show();
					}

					if( row instanceof SetupActionRow ) {
						Log.v( TAG, "onCreate : starting setup activity" );
						
						Toast.makeText( NavigationDrawerActivity.this, "Setup - Coming Soon!", Toast.LENGTH_SHORT ).show();
					}

				}
				
				Log.v( TAG, "onItemClick : exit" );
			}

		});

		getActionBar().setDisplayHomeAsUpEnabled( true );
		getActionBar().setHomeButtonEnabled( true );

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

	private class NavigationDrawerAdapter extends BaseAdapter {

		private Context mContext;
		
        private LocationProfile mLocationProfile;

        private List<Row> rows = new ArrayList<Row>();
        
		public NavigationDrawerAdapter( Context context ) { 
			Log.v( TAG, "NavigationDrawerAdapter : enter" );
			
			this.mContext = context;
			
			this.mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( mContext );
			
			setupRowsList();
			
			Log.v( TAG, "NavigationDrawerAdapter : exit" );
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

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			return rows.get( position ).getView( convertView );
		}
		
		public void resetConnectedLocationProfile( LocationProfile locationProfile ) {
			this.mLocationProfile = locationProfile;
			
			setupRowsList();
			
			notifyDataSetChanged();
			navList.invalidateViews();
		}
		
		// internal helpers
		
		private void setupRowsList() {

			rows = new ArrayList<Row>();
			
			rows.add( new VersionRow( mContext, "MAF", "x" ) );
			rows.add( new ProfileRow( mContext, mProfileChangedListener ) );
			rows.add( new ManageProfilesActionRow( mContext, "Manage Profiles" ) );
			
			if( null != mLocationProfile && mLocationProfile.getType().equals( LocationType.HOME ) ) {
				rows.add( new FrontendsHeaderRow( mContext, "Frontends" ) );

				// TODO: Added frontends dropdown here
			}

			if( null != mLocationProfile ) {
				rows.add( new ActionsHeaderRow( mContext, "Actions" ) );

				rows.add( new DvrActionRow( mContext, "Dvr" ) );
				rows.add( new MultimediaActionRow( mContext, "Multimedia" ) );
				rows.add( new SetupActionRow( mContext, "Setup" ) );
			}

		}
		
	}

}
