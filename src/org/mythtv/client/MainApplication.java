/**
 * 
 */
package org.mythtv.client;

import org.mythtv.services.api.MythServices;
import org.mythtv.services.connect.MythServicesServiceProvider;

import android.app.Application;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class MainApplication extends Application {

	private static final String TAG = MainApplication.class.getSimpleName();

	private MythServicesServiceProvider provider;
	
	private String masterBackend;
	
	//***************************************
    // Application methods
    //***************************************

	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		Log.v( TAG, "onCreate : enter" );

		super.onCreate();
		
		Log.v( TAG, "onCreate : exit" );
	}

	//***************************************
    // Private methods
    //***************************************
//	private String getApiUrlBase() {
//		return getString( R.string.base_url );
//	}

	//***************************************
    // Public methods
    //***************************************
	public MythServices getMythServicesApi() {
		return provider.getApi();
	}

	/**
	 * @return the masterBackend
	 */
	public String getMasterBackend() {
		return masterBackend;
	}

	/**
	 * @param masterBackend the masterBackend to set
	 */
	public void setMasterBackend( String masterBackend ) {
		this.masterBackend = masterBackend;

		provider = new MythServicesServiceProvider( this.masterBackend );
	}

}
