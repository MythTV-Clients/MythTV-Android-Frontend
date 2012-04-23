/**
 * 
 */
package org.mythtv.client.ui.setup;

import org.mythtv.R;
import org.mythtv.client.ui.BaseActivity;
import org.mythtv.client.ui.HomeActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * @author Daniel Frey
 * 
 */
public class SetupActivity extends BaseActivity {

	private final static String TAG = SetupActivity.class.getSimpleName();

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.d( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_setup );
		getActivityHelper().setupActionBar( null, 0 );

		final ActionBar actionBar = getActionBar();
		
		actionBar.setDisplayHomeAsUpEnabled( true );
		
		Log.d( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.BaseActivity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.d( TAG, "onCreateOptionsMenu : enter" );

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
        case android.R.id.home:
			Log.d( TAG, "onOptionsItemSelected : home selected" );
			
			// app icon in action bar clicked; go home
            Intent intent = new Intent( this, HomeActivity.class );
            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
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
