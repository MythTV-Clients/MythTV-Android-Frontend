/**
 * 
 */
package org.mythtv.client;

import org.mythtv.services.api.MythServices;
import org.mythtv.services.connect.MythServicesServiceProvider;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class MainApplication extends Application {

	private static final String TAG = MainApplication.class.getSimpleName();
	private static final String MASTER_BACKEND = "MASTER_BACKEND";
	
	private SharedPreferences mythtvPreferences;

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
		
		mythtvPreferences = getSharedPreferences( "MythtvPreferences", Context.MODE_PRIVATE );
		
		Log.v( TAG, "onCreate : exit" );
	}

	
	//***************************************
    // Private methods
    //***************************************

	
	//***************************************
    // Public methods
    //***************************************
	public MythServices getMythServicesApi() {
		Log.v( TAG, "getMythServicesApi : enter" );

		if( null == provider ) {
			Log.v( TAG, "getMythServicesApi : initializing MythServicesServiceProvider" );

			provider = new MythServicesServiceProvider( getMasterBackend() );
		}
		
		Log.v( TAG, "getMythServicesApi : exit" );
		return provider.getApi();
	}

	/**
	 * @return the masterBackend
	 */
	public String getMasterBackend() {
		Log.v( TAG, "getMasterBackend : enter" );

		if( null == masterBackend || "".equals( masterBackend ) ) {
			Log.v( TAG, "getMasterBackend : masterbackend not set, checking SharedPreferences" );

			masterBackend = mythtvPreferences.getString( MASTER_BACKEND, null );
		}
		Log.v( TAG, "getMasterBackend : masterBackend=" + masterBackend );
		
		Log.v( TAG, "getMasterBackend : exit" );
		return masterBackend;
	}

	/**
	 * @param masterBackend the masterBackend to set
	 */
	public void setMasterBackend( String masterBackend ) {
		Log.v( TAG, "setMasterBackend : enter" );

		this.masterBackend = masterBackend;

		Log.v( TAG, "setMasterBackend : storing masterbackend in SharedPreferences [" + masterBackend + "]" );
		SharedPreferences.Editor editor = mythtvPreferences.edit();
		editor.putString( MASTER_BACKEND, masterBackend );
		editor.commit();

		Log.v( TAG, "setMasterBackend : enter" );
	}

	public void clearMasterBackend() {
		Log.v( TAG, "clearMasterBackend : enter" );

		Log.v( TAG, "clearMasterBackend : removing masterbackend in SharedPreferences" );
		SharedPreferences.Editor editor = mythtvPreferences.edit();
		editor.remove( MASTER_BACKEND );
		editor.commit();

		masterBackend = null;
		
		Log.v( TAG, "clearMasterBackend : enter" );
	}

}
