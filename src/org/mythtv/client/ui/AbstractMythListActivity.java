/**
 * 
 */
package org.mythtv.client.ui;

import org.mythtv.client.MainApplication;

import android.app.ListActivity;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractMythListActivity extends ListActivity implements MythActivity {

	protected static final String TAG = AbstractMythListActivity.class.getSimpleName();
	
	//***************************************
    // MythActivity methods
    //***************************************
	public MainApplication getApplicationContext() {
		return (MainApplication) super.getApplicationContext();
	}
	
}
