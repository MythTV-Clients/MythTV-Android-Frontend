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
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.db.preferences.PlaybackProfileDaoHelper;
import org.mythtv.services.api.MythServices;
import org.mythtv.services.api.capture.CaptureCard;
import org.mythtv.services.connect.MythServicesServiceProvider;

import android.app.Application;
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

	private static final String TAG = MainApplication.class.getSimpleName();
	
	private LocationProfileDaoHelper mLocationProfileDaoHelper;
	private PlaybackProfileDaoHelper mPlaybackProfileDaoHelper;
	
	private MythServicesServiceProvider provider;
	
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
		
		mLocationProfileDaoHelper = new LocationProfileDaoHelper( this );
		mPlaybackProfileDaoHelper = new PlaybackProfileDaoHelper( this );
		
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
		Log.v( TAG, "getSelectedHomeLocationProfile : enter" );
		
		LocationProfile profile = mLocationProfileDaoHelper.findSelectedHomeProfile(); 
		if( null != profile ) {
			Log.v( TAG, "getSelectedHomeLocationProfile : profile=" + profile.toString() );
		}
		
		Log.v( TAG, "getSelectedHomeLocationProfile : exit" );
		return profile;
	}

	/**
	 * 
	 */
	public void connectSelectedHomeLocationProfile() {
		Log.v( TAG, "connectSelectedHomeLocationProfile : enter" );
		
		LocationProfile profile = mLocationProfileDaoHelper.findSelectedHomeProfile(); 
		if( null != profile ) {
			mLocationProfileDaoHelper.setConnectedLocationProfile( (long) profile.getId() );
		}

		Log.v( TAG, "connectSelectedHomeLocationProfile : exit" );
	}
	
	/**
	 * @return the selectedAwayLocationProfile
	 */
	public LocationProfile getSelectedAwayLocationProfile() {
		Log.v( TAG, "getSelectedAwayLocationProfile : enter" );
		
		LocationProfile profile = mLocationProfileDaoHelper.findSelectedAwayProfile(); 
		if( null != profile ) {
			Log.v( TAG, "getSelectedAwayLocationProfile : profile=" + profile.toString() );
		}
		
		Log.v( TAG, "getSelectedAwayLocationProfile : exit" );
		return profile;
	}

	/**
	 * 
	 */
	public void connectSelectedAwayLocationProfile() {
		Log.v( TAG, "connectSelectedAwayLocationProfile : enter" );
		
		LocationProfile profile = mLocationProfileDaoHelper.findSelectedAwayProfile(); 
		if( null != profile ) {
			mLocationProfileDaoHelper.setConnectedLocationProfile( (long) profile.getId() );
		}
		
		Log.v( TAG, "connectSelectedAwayLocationProfile : exit" );
	}
	
	/**
	 * @return the selectedHomePlaybackProfile
	 */
	public PlaybackProfile getSelectedHomePlaybackProfile() {
		Log.v( TAG, "getSelectedHomePlaybackProfile : enter" );

		PlaybackProfile profile = mPlaybackProfileDaoHelper.findSelectedHomeProfile(); 
		Log.v( TAG, "getSelectedHomePlaybackProfile : profile=" + profile.toString() );
		
		Log.v( TAG, "getSelectedHomePlaybackProfile : exit" );
		return profile;
	}

	/**
	 * @return the selectedAwayPlaybackProfile
	 */
	public PlaybackProfile getSelectedAwayPlaybackProfile() {
		Log.v( TAG, "getSelectedAwayPlaybackProfile : enter" );

		PlaybackProfile profile = mPlaybackProfileDaoHelper.findSelectedAwayProfile(); 
		Log.v( TAG, "getSelectedAwayPlaybackProfile : profile=" + profile.toString() );
		
		Log.v( TAG, "getSelectedAwayPlaybackProfile : exit" );
		return profile;
	}

	public LocationProfile getConnectedLocationProfile() {
		LocationProfile profile = mLocationProfileDaoHelper.findConnectedProfile();

		return profile;
	}

	/**
	 * @return the masterBackend
	 */
	public String getMasterBackend() {

		return getConnectedLocationProfile().getUrl();
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
