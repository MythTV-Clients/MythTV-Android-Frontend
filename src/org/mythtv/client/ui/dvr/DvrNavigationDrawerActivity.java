/**
 * 
 */
package org.mythtv.client.ui.dvr;

import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythtvFragmentActivity;

import android.annotation.TargetApi;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
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

	private String[] names = null, classes = null;
	private int selection = 0, oldSelection = -1;

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

		names = getResources().getStringArray( R.array.nav_dvr_names );
        classes = getResources().getStringArray( R.array.nav_dvr_classes );
        
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>( getActionBar().getThemedContext(), android.R.layout.simple_list_item_1, names );

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
		
		getActionBar().setTitle( names[ selection ] );

		if( selection != oldSelection ) {
			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.replace( R.id.main, Fragment.instantiate( DvrNavigationDrawerActivity.this, classes[ selection ]) );
			tx.commit();
			
			oldSelection = selection;
		}
		
		drawer.closeDrawer( navList );

		Log.v( TAG, "updateContent : exit" );
	}
	
}
