/**
 * 
 */
package org.mythtv.client.ui;

import org.mythtv.R;
import org.mythtv.client.ui.setup.SetupActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * @author Daniel Frey
 * 
 */
public class HomeActivity extends BaseActivity {

	private final static String TAG = HomeActivity.class.getSimpleName();

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.d( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_home );
		getActivityHelper().setupActionBar( null, 0 );

		Log.d( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.BaseActivity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.d( TAG, "onCreateOptionsMenu : enter" );

		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.main_menu, menu );

		Log.d( TAG, "onCreateOptionsMenu : exit" );
		return true;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.BaseActivity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.d( TAG, "onOptionsItemSelected : enter" );
		
		switch( item.getItemId() ) {
		case R.id.menu_setup:
			Log.d( TAG, "onOptionsItemSelected : setup selected" );

			Intent intent = new Intent( this, SetupActivity.class );
            startActivity( intent );
			break;
		default:
			Log.d( TAG, "onOptionsItemSelected : nothing selected" );

			break;
		}

		Log.d( TAG, "onOptionsItemSelected : exit" );
		return true;
	}

}
