/**
 * 
 */
package org.mythtv.client.ui;

import org.mythtv.client.MainApplication;

import android.app.Activity;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractMythActivity extends Activity implements MythActivity {

	protected static final String TAG = AbstractMythActivity.class.getSimpleName();
	
	//***************************************
    // MythActivity methods
    //***************************************
	public MainApplication getApplicationContext() {
		return (MainApplication) super.getApplicationContext();
	}
	
}
