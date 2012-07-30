/**
 *  This file is part of MythTV for Android
 * 
 *  MythTV for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MythTV for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MythTV for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * This software can be found at <https://github.com/MythTV-Android/mythtv-for-android/>
 *
 */
package org.mythtv.client;

import java.util.List;
import java.util.Map;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.preferences.PlaybackProfile;
import org.mythtv.services.api.MythServices;
import org.mythtv.services.api.capture.CaptureCard;
import org.mythtv.services.connect.MythServicesServiceProvider;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author Daniel Frey
 * @author John Baab
 *
 */
public class MainApplication extends Application {

	public static final String GUIDE_DATA_LOADED = "GUIDE_DATA_LOADED";

	private static final String TAG = MainApplication.class.getSimpleName();
	private static final String MASTER_BACKEND = "MASTER_BACKEND";
	
	private SharedPreferences mythtvPreferences;

	private MythServicesServiceProvider provider;
	
	private boolean databaseLoading;
	
	private String location;
	
	private LocationProfile selectedHomeLocationProfile;
	private LocationProfile selectedAwayLocationProfile;
	
	private PlaybackProfile selectedHomePlaybackProfile;
	private PlaybackProfile selectedAwayPlaybackProfile;
	
	private String masterBackend;
	
	private List<String> captureCards;
	private Map<String,List<CaptureCard>> currentCaptureCards;
	
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
	 * @return the selectedHomeLocationProfile
	 */
	public LocationProfile getSelectedHomeLocationProfile() {
		Log.v( TAG, "getSelectedHomeLocationProfile : enter" );
		Log.v( TAG, "getSelectedHomeLocationProfile : exit" );
		return selectedHomeLocationProfile;
	}

	/**
	 * @param selectedHomeLocationProfile the selectedHomeLocationProfile to set
	 */
	public void setSelectedHomeLocationProfile( LocationProfile selectedHomeLocationProfile ) {
		Log.v( TAG, "setSelectedHomeLocationProfile : enter" );

		this.selectedHomeLocationProfile = selectedHomeLocationProfile;

		Log.v( TAG, "setSelectedHomeLocationProfile : exit" );
	}

	/**
	 * 
	 */
	public void connectSelectedHomeLocationProfile() {
		Log.v( TAG, "connectSelectedHomeLocationProfile : enter" );

		setMasterBackend( getSelectedHomeLocationProfile().getUrl() );

		Log.v( TAG, "connectSelectedHomeLocationProfile : exit" );
	}
	
	/**
	 * @return the selectedAwayLocationProfile
	 */
	public LocationProfile getSelectedAwayLocationProfile() {
		Log.v( TAG, "getSelectedAwayLocationProfile : enter" );
		Log.v( TAG, "getSelectedAwayLocationProfile : exit" );
		return selectedAwayLocationProfile;
	}

	/**
	 * @param selectedAwayLocationProfile the selectedAwayLocationProfile to set
	 */
	public void setSelectedAwayLocationProfile( LocationProfile selectedAwayLocationProfile ) {
		Log.v( TAG, "setSelectedAwayLocationProfile : enter" );
		
		this.selectedAwayLocationProfile = selectedAwayLocationProfile;

		Log.v( TAG, "setSelectedAwayLocationProfile : exit" );
	}

	/**
	 * 
	 */
	public void connectSelectedAwayLocationProfile() {
		Log.v( TAG, "connectSelectedAwayLocation : enter" );
		
		setMasterBackend( getSelectedAwayLocationProfile().getUrl() );

		Log.v( TAG, "connectSelectedAwayLocation : exit" );
	}
	
	/**
	 * @return the selectedHomePlaybackProfile
	 */
	public PlaybackProfile getSelectedHomePlaybackProfile() {
		return selectedHomePlaybackProfile;
	}


	/**
	 * @param selectedHomePlaybackProfile the selectedHomePlaybackProfile to set
	 */
	public void setSelectedHomePlaybackProfile( PlaybackProfile selectedHomePlaybackProfile ) {
		this.selectedHomePlaybackProfile = selectedHomePlaybackProfile;
	}


	/**
	 * @return the selectedAwayPlaybackProfile
	 */
	public PlaybackProfile getSelectedAwayPlaybackProfile() {
		return selectedAwayPlaybackProfile;
	}


	/**
	 * @param selectedAwayPlaybackProfile the selectedAwayPlaybackProfile to set
	 */
	public void setSelectedAwayPlaybackProfile( PlaybackProfile selectedAwayPlaybackProfile ) {
		this.selectedAwayPlaybackProfile = selectedAwayPlaybackProfile;
	}


	/**
	 * @return the masterBackend
	 */
	public String getMasterBackend() {
		Log.v( TAG, "getMasterBackend : enter" );

		if( null == masterBackend || "".equals( masterBackend ) ) {
//			Log.v( TAG, "getMasterBackend : masterbackend not set, checking SharedPreferences" );

			masterBackend = mythtvPreferences.getString( MASTER_BACKEND, null );
		}
//		Log.v( TAG, "getMasterBackend : masterBackend=" + masterBackend );
		
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

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}


	/**
	 * @param location the location to set
	 */
	public void setLocation( String location ) {
		this.location = location;
	}


	/**
	 * @return the captureCards
	 */
	public List<String> getCaptureCards() {
		return captureCards;
	}

	/**
	 * @param captureCards the captureCards to set
	 */
	public void setCaptureCards( List<String> captureCards ) {
		this.captureCards = captureCards;
	}

	/**
	 * @return the currentCaptureCards
	 */
	public Map<String, List<CaptureCard>> getCurrentCaptureCards() {
		return currentCaptureCards;
	}

	/**
	 * @param currentCaptureCards the currentCaptureCards to set
	 */
	public void setCurrentCaptureCards( Map<String, List<CaptureCard>> currentCaptureCards ) {
		this.currentCaptureCards = currentCaptureCards;
	}


	/**
	 * @return the databaseLoading
	 */
	public boolean isDatabaseLoading() {
		return databaseLoading;
	}


	/**
	 * @param databaseLoading the databaseLoading to set
	 */
	public void setDatabaseLoading( boolean databaseLoading ) {
		this.databaseLoading = databaseLoading;
	}

}
