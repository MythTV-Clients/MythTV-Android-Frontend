/**
 * This file is part of MythTV Android Frontend
 *
 * MythTV Android Frontend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MythTV Android Frontend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MythTV Android Frontend.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This software can be found at <https://github.com/MythTV-Clients/MythTV-Android-Frontend/>
 */
package org.mythtv.client;

import static android.text.format.DateFormat.getDateFormatOrder;

import java.util.List;
import java.util.Map;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.preferences.PlaybackProfile;
import org.mythtv.db.MythtvDatabaseManager;
import org.mythtv.services.api.MythServices;
import org.mythtv.services.api.capture.CaptureCard;
import org.mythtv.services.connect.MythServicesServiceProvider;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * @author Daniel Frey
 * @author John Baab
 *
 */
public class MainApplication extends Application {

	public static enum LocationSetting {
		HOME,
		AWAY
	}
	
	private static final String TAG = MainApplication.class.getSimpleName();
	private static final String MASTER_BACKEND = "MASTER_BACKEND";
	
	private SharedPreferences mythtvPreferences;

	private MythServicesServiceProvider provider;
	
	private LocationSetting location;

	private String masterBackend;
	
	private List<String> captureCards;
	private Map<String,List<CaptureCard>> currentCaptureCards;
	
    private String clockType = "12h";
    private String dateFormat = "yyyy-MM-dd";

	protected ObjectMapper mObjectMapper;

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
		
        String systemClock = Settings.System.getString(getApplicationContext().getContentResolver(), Settings.System.TIME_12_24);
        if(systemClock != null) this.clockType = systemClock;

        char[] dateFormatOrder = getDateFormatOrder(getApplicationContext());
        if(dateFormatOrder != null){
            String format = new String(dateFormatOrder);
            if(format.equals("Mdy")){
                this.dateFormat = "MM-dd-yyyy";
            }else if(format.equals("dMy")){
                this.dateFormat = "dd-MM-yyyy";
            }else if(format.equals("yMd")){
                this.dateFormat = "yyyy-MM-dd";
            }
        }

        mObjectMapper = new ObjectMapper();
		mObjectMapper.registerModule( new JodaModule() );

		Log.v( TAG, "onCreate : exit" );
	}

	
	//***************************************
    // Private methods
    //***************************************

	
	//***************************************
    // Public methods
    //***************************************
	public MythServices getMythServicesApi() {
		if( null == provider ) {
			provider = new MythServicesServiceProvider( getMasterBackend() );
		}
		
		return provider.getApi();
	}

	/**
	 * @return the mObjectMapper
	 */
	public ObjectMapper getObjectMapper() {
		return mObjectMapper;
	}

	/**
	 * @return the selectedHomeLocationProfile
	 */
	public LocationProfile getSelectedHomeLocationProfile() {
		MythtvDatabaseManager db = new MythtvDatabaseManager( this );
		return db.fetchSelectedHomeLocationProfile();
	}

	/**
	 * 
	 */
	public void connectSelectedHomeLocationProfile() {
		setMasterBackend( getSelectedHomeLocationProfile().getUrl() );
		setLocation( LocationSetting.HOME );
	}
	
	/**
	 * @return the selectedAwayLocationProfile
	 */
	public LocationProfile getSelectedAwayLocationProfile() {
		MythtvDatabaseManager db = new MythtvDatabaseManager( this );
		return db.fetchSelectedAwayLocationProfile();
	}

	/**
	 * 
	 */
	public void connectSelectedAwayLocationProfile() {
		setMasterBackend( getSelectedAwayLocationProfile().getUrl() );
		setLocation( LocationSetting.AWAY );
	}
	
	/**
	 * @return the selectedHomePlaybackProfile
	 */
	public PlaybackProfile getSelectedHomePlaybackProfile() {
		MythtvDatabaseManager db = new MythtvDatabaseManager( this );
		return db.fetchSelectedHomePlaybackProfile();
	}

	/**
	 * @return the selectedAwayPlaybackProfile
	 */
	public PlaybackProfile getSelectedAwayPlaybackProfile() {
		MythtvDatabaseManager db = new MythtvDatabaseManager( this );
		return db.fetchSelectedAwayPlaybackProfile();
	}


	/**
	 * @return the masterBackend
	 */
	public String getMasterBackend() {
		if( null == masterBackend || "".equals( masterBackend ) ) {
			masterBackend = mythtvPreferences.getString( MASTER_BACKEND, null );
		}
		
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

	/**
	 * @return the location
	 */
	public LocationSetting getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	private void setLocation( LocationSetting location ) {
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
     * @return the current clockType
     */
    public String getClockType() {
        return clockType;
    }

    /**
     * @param clockType the current clockType to set
     */
    public void setClockType(String clockType) {
        this.clockType = clockType;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
