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
 *
 */
public class MainApplication extends Application {

	private static final String TAG = MainApplication.class.getSimpleName();
	private static final String MASTER_BACKEND = "MASTER_BACKEND";
	
	private SharedPreferences mythtvPreferences;

	private MythServicesServiceProvider provider;
	
	private String masterBackend;

	private List<String> programGroups;
	private Map<String,List<Program>> currentPrograms;
	private List<Program> currentProgramsInGroup;
	private List<Program> currentRecordings;
	
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
	 * @return the programGroups
	 */
	public List<String> getProgramGroups() {
		return programGroups;
	}

	/**
	 * @param programGroups the programGroups to set
	 */
	public void setProgramGroups( List<String> programGroups ) {
		this.programGroups = programGroups;
	}

	/**
	 * @return the currentPrograms
	 */
	public Map<String, List<Program>> getCurrentPrograms() {
		return currentPrograms;
	}

	/**
	 * @param currentPrograms the currentPrograms to set
	 */
	public void setCurrentPrograms( Map<String, List<Program>> currentPrograms ) {
		this.currentPrograms = currentPrograms;
	}

	/**
	 * @return the currentProgramsInGroup
	 */
	public List<Program> getCurrentProgramsInGroup() {
		return currentProgramsInGroup;
	}

	/**
	 * @return the currentRecordings
	 */
	public List<Program> getCurrentRecordings() {
		return currentRecordings;
	}

	/**
	 * @param currentRecordings the currentRecordings to set
	 */
	public void setCurrentRecordings( List<Program> currentRecordings ) {
		this.currentRecordings = currentRecordings;
	}

	/**
	 * @param currentProgramsInGroup the currentProgramsInGroup to set
	 */
	public void setCurrentProgramsInGroup( List<Program> currentProgramsInGroup ) {
		this.currentProgramsInGroup = currentProgramsInGroup;
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
