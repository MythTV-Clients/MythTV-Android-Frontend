package org.mythtv.client.ui;

import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.util.ActivityHelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

/**
 * A base activity that defers common functionality across app activities to an
 * {@link ActivityHelper}. This class shouldn't be used directly; instead,
 * activities should inherit from {@link BaseSinglePaneActivity} or
 * {@link BaseMultiPaneActivity}.
 */
public abstract class BaseActivity extends FragmentActivity implements MythActivity {
	
	private final static String TAG = BaseActivity.class.getSimpleName();

	final ActivityHelper mActivityHelper = ActivityHelper.createInstance( this );

	// ***************************************
	// MythActivity methods
	// ***************************************
	public MainApplication getApplicationContext() {
		return (MainApplication) super.getApplicationContext();
	}

	@Override
	protected void onPostCreate( Bundle savedInstanceState ) {
		super.onPostCreate( savedInstanceState );
		mActivityHelper.onPostCreate(savedInstanceState);
	}

	@Override
	public boolean onKeyLongPress( int keyCode, KeyEvent event ) {
		return
				mActivityHelper.onKeyLongPress(keyCode, event) ||
				super.onKeyLongPress( keyCode, event );
	}

	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event ) {
		return
				mActivityHelper.onKeyDown(keyCode, event) ||
				super.onKeyDown( keyCode, event );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		return
				mActivityHelper.onCreateOptionsMenu(menu) ||
				super.onCreateOptionsMenu( menu );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		return
				mActivityHelper.onOptionsItemSelected(item) ||
				super.onOptionsItemSelected( item );
	}

	/**
	 * Returns the {@link ActivityHelper} object associated with this activity.
	 */
	protected ActivityHelper getActivityHelper() {
		return mActivityHelper;
	}

	/**
	 * Takes a given intent and either starts a new activity to handle it (the
	 * default behavior), or creates/updates a fragment (in the case of a
	 * multi-pane activity) that can handle the intent.
	 * 
	 * Must be called from the main (UI) thread.
	 */
	public void openActivityOrFragment( Intent intent ) {
		// Default implementation simply calls startActivity
		startActivity( intent );
	}

	/**
	 * Converts an intent into a {@link Bundle} suitable for use as fragment
	 * arguments.
	 */
	public static Bundle intentToFragmentArguments( Intent intent ) {
		Bundle arguments = new Bundle();
		if( intent == null ) {
			return arguments;
		}

		final Uri data = intent.getData();
		if( data != null ) {
			arguments.putParcelable( "_uri", data );
		}

		final Bundle extras = intent.getExtras();
		if( extras != null ) {
			arguments.putAll( intent.getExtras() );
		}

		return arguments;
	}

	/**
	 * Converts a fragment arguments bundle into an intent.
	 */
	public static Intent fragmentArgumentsToIntent( Bundle arguments ) {
		Intent intent = new Intent();
		if( arguments == null ) {
			return intent;
		}

		final Uri data = arguments.getParcelable( "_uri" );
		if( data != null ) {
			intent.setData( data );
		}

		intent.putExtras( arguments );
		intent.removeExtra( "_uri" );
		
		return intent;
	}
}
