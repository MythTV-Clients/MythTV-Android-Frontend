/**
 * 
 */
package org.mythtv.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mythtv.R;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.preferences.LocationProfile.LocationType;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author dmfrey
 *
 */
@TargetApi( Build.VERSION_CODES.ICE_CREAM_SANDWICH )
public class NavigationDrawerActivity extends AbstractMythtvFragmentActivity {

	private static final String TAG = NavigationDrawerActivity.class.getSimpleName();

	private int selection = 0;
	private int oldSelection = -1;

	private String[] names = null;
	private String[] classes = null;

	private ActionBarDrawerToggle drawerToggle = null;
	private DrawerLayout drawer = null;
	private ListView navList = null;

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		Log.v( TAG, "onCreate : enter" );

		setContentView( R.layout.activity_navigation_drawer );

		names = getResources().getStringArray( R.array.nav_names );
		classes = getResources().getStringArray( R.array.nav_classes );
		NavigationDrawerAdapter adapter = new NavigationDrawerAdapter( getActionBar().getThemedContext(), names );

		drawer = (DrawerLayout) findViewById( R.id.drawer_layout );

		navList = (ListView) findViewById( R.id.drawer );
		navList.setAdapter( adapter );
		
		drawerToggle = new ActionBarDrawerToggle( this, drawer, R.drawable.ic_drawer, R.string.open, R.string.close );
		drawer.setDrawerListener( drawerToggle );

		navList.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick( AdapterView<?> parent, View view, final int pos, long id ) {
				Log.v( TAG, "onItemClick : enter" );
				
				Log.v( TAG, "onItemClick : pos=" + pos + ", id=" + id );
				selection = pos;
				drawer.closeDrawer( navList );

				Log.v( TAG, "onItemClick : exit" );
			}

		});

		updateContent();
		
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

	private void updateContent() {
		Log.v( TAG, "updateContent : enter" );

		Log.v( TAG, "updateContent : selection=" + selection );
		getActionBar().setTitle( names[ selection ] );
		
		if( selection != oldSelection ) {
			Log.v( TAG, "updateContent : new drawer item selected" );

			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.replace( R.id.main, Fragment.instantiate( NavigationDrawerActivity.this, classes[ selection ] ) );
			tx.commit();
			
			oldSelection = selection;
		}

		Log.v( TAG, "updateContent : exit" );
	}

	private class NavigationDrawerAdapter extends BaseAdapter {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

        private Context mContext;
		private LayoutInflater mLayoutInflater;
		
		private List<String> mItems = new ArrayList<String>();
        private Set<Integer> mSeparatorsSet = new TreeSet<Integer>();
		
        private LocationProfile mLocationProfile;
        
		public NavigationDrawerAdapter( Context context, String[] items ) { 
			Log.v( TAG, "NavigationDrawerAdapter : enter" );
			
			this.mContext = context;
			
			mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( mContext );
			
			this.mLayoutInflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );

			this.mSeparatorsSet.add( mItems.size() - 1 );
			this.mItems.add( "MAF version" );
			// TODO: add toggle switch here
			
			this.mItems.add( mLocationProfile.getHostname() );
			
			
			if( mLocationProfile.getType().equals( LocationType.HOME ) ) {
				this.mSeparatorsSet.add( mItems.size() - 1 );
				this.mItems.add( "Frontends" );
				// TODO: add auto discovered frontends here
			
			}
			
			
			this.mSeparatorsSet.add( mItems.size() - 1 );
			this.mItems.add( "Actions" );
			this.mItems.addAll( Arrays.asList( items ) );
			
			Log.v( TAG, "NavigationDrawerAdapter : exit" );
		}
		
        /* (non-Javadoc)
         * @see android.widget.BaseAdapter#getItemViewType(int)
         */
        @Override
        public int getItemViewType( int position ) {
			Log.v( TAG, "NavigationDrawerAdapter.getItemViewType : enter" );
			Log.v( TAG, "NavigationDrawerAdapter.getItemViewType : exit" );
            return mSeparatorsSet.contains( position ) ? TYPE_SEPARATOR : TYPE_ITEM;
        }

        /* (non-Javadoc)
         * @see android.widget.BaseAdapter#getViewTypeCount()
         */
        @Override
        public int getViewTypeCount() {
			Log.v( TAG, "NavigationDrawerAdapter.getItemViewTypeCount : enter" );
			Log.v( TAG, "NavigationDrawerAdapter.getItemViewTypeCount : exit" );
            return TYPE_MAX_COUNT;
        }

        /* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			Log.v( TAG, "NavigationDrawerAdapter.getCount : enter" );

			if( null == mItems || mItems.isEmpty() ) {
				Log.v( TAG, "NavigationDrawerAdapter.getCount : exit, no items" );
				return 0;
			}
			
			Log.v( TAG, "NavigationDrawerAdapter.getCount : exit" );
			return mItems.size();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public String getItem( int position ) {
			Log.v( TAG, "NavigationDrawerAdapter.getItem : enter" );

			if( null == mItems || mItems.isEmpty() ) {
				Log.v( TAG, "NavigationDrawerAdapter.getItem : exit, no items" );
				return null;
			}

			Log.v( TAG, "NavigationDrawerAdapter.getCount : exit" );
			return mItems.get( position );
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId( int position ) {
			Log.v( TAG, "NavigationDrawerAdapter.getItemId : enter" );
			Log.v( TAG, "NavigationDrawerAdapter.getItemId : exit" );
			return position;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			Log.v( TAG, "NavigationDrawerAdapter.getView : enter" );

			ViewHolder holder = null;
			int type = getItemViewType( position );
            if( null == convertView ) {
    			Log.v( TAG, "NavigationDrawerAdapter.getView : convertView is null" );

    			holder = new ViewHolder();
                
                switch( type ) {
                	case TYPE_ITEM:
            			Log.v( TAG, "NavigationDrawerAdapter.getView : type = item" );

            			convertView = mLayoutInflater.inflate( android.R.layout.simple_list_item_1, null );
                		holder.textView = (TextView) convertView.findViewById( android.R.id.text1 );
                		break;
                		
                	case TYPE_SEPARATOR:
            			Log.v( TAG, "NavigationDrawerAdapter.getView : type = header" );

                		convertView = mLayoutInflater.inflate( R.layout.connected_profile_toggle, null );
                		holder.textView = (TextView) convertView.findViewById( R.id.connected_profile_toggle_hostname );
                		break;
                }

                convertView.setTag( holder );
            } else {
    			Log.v( TAG, "NavigationDrawerAdapter.getView : loading holder" );

    			holder = (ViewHolder) convertView.getTag();
            }
            
            holder.textView.setText( getItem( position ) );
 			
			Log.v( TAG, "NavigationDrawerAdapter.getView : exit" );
			return convertView;
		}
		
	}
	
	private static class ViewHolder {
	
		TextView textView;
		
	}
	
}
