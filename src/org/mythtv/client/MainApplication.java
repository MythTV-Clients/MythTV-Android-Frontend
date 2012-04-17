/**
 * 
 */
package org.mythtv.client;

import org.mythtv.R;
import org.mythtv.services.api.MythServices;
import org.mythtv.services.connect.MythServicesServiceProvider;

import android.app.Application;

/**
 * @author Daniel Frey
 *
 */
public class MainApplication extends Application {

	private MythServicesServiceProvider provider;
		
	//***************************************
    // Application methods
    //***************************************

	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		
		provider = new MythServicesServiceProvider( getApiUrlBase() );
	}

	//***************************************
    // Private methods
    //***************************************
	private String getApiUrlBase() {
		return getString( R.string.base_url );
	}

	//***************************************
    // Public methods
    //***************************************
	public MythServices getMythServicesApi() {
		return provider.getApi();
	}

}
