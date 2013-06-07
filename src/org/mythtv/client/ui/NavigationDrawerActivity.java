/**
 * 
 */
package org.mythtv.client.ui;

import org.mythtv.R;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author dmfrey
 *
 */
@TargetApi( Build.VERSION_CODES.ICE_CREAM_SANDWICH )
public class NavigationDrawerActivity extends FragmentActivity {

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
		ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActionBar().getThemedContext(), android.R.layout.simple_list_item_1, names );

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

}
