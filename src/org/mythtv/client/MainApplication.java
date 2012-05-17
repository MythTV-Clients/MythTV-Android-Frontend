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
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */
package org.mythtv.client;

import java.util.List;
import java.util.Map;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.services.api.MythServices;
import org.mythtv.services.api.capture.CaptureCard;
import org.mythtv.services.api.dvr.Program;
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

	private static final String TAG = MainApplication.class.getSimpleName();
	private static final String MASTER_BACKEND = "MASTER_BACKEND";
	
	private SharedPreferences mythtvPreferences;

	private MythServicesServiceProvider provider;
	
	private LocationProfile selectedHomeLocationProfile;
	private LocationProfile selectedAwayLocationProfile;
	
	private String masterBackend;
	
	private Program currentProgram;
	private List<Program> currentRecordingsInProgramGroup;
	
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

	/**
	 * @return the currentRecordingsInProgramGroup
	 */
	public List<Program> getCurrentRecordingsInProgramGroup() {
		return currentRecordingsInProgramGroup;
	}

	/**
	 * @param currentRecordingsInProgramGroup the currentRecordingsInProgramGroup to set
	 */
	public void setCurrentRecordingsInProgramGroup( List<Program> currentRecordingsInProgramGroup ) {
		this.currentRecordingsInProgramGroup = currentRecordingsInProgramGroup;
	}
	
	/**
	 * @return the currentProgram
	 */
	public Program getCurrentProgram() {
		return currentProgram;
	}

	/**
	 * @param currentProgram the currentProgram to set
	 */
	public void setCurrentProgram( Program currentProgram) {
		this.currentProgram = currentProgram;
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

}
