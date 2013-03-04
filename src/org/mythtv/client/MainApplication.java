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

import java.util.logging.Level;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.db.preferences.PlaybackProfileDaoHelper;
import org.mythtv.service.util.FileHelper;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.service.util.RunningServiceHelper;
import org.mythtv.services.api.MythServices;
import org.mythtv.services.connect.MythServicesServiceProvider;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * @author Daniel Frey
 * @author John Baab
 *
 */
public class MainApplication extends Application {

	private static final String TAG = MainApplication.class.getSimpleName();
	
	private MythServicesServiceProvider provider;
	
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
		
		//init Image Loader
		initImageLoader( getApplicationContext() );
		
		//Initialize DAO Helpers
		LocationProfileDaoHelper.getInstance().init( this );
		PlaybackProfileDaoHelper.getInstance().init( this );
		
		//Initialize Helpers
		FileHelper.getInstance().init( this.getExternalCacheDir() );
		NetworkHelper.getInstance().init( this );
		RunningServiceHelper.getInstance().init( this );

		String systemClock = Settings.System.getString( getApplicationContext().getContentResolver(), Settings.System.TIME_12_24 );
        if( null != systemClock ) {
        	this.clockType = systemClock;
        }

        String dateFormatOrder = Settings.System.getString( getContentResolver(), Settings.System.DATE_FORMAT );
        if( null != dateFormatOrder ) {
            
        	String format = new String( dateFormatOrder );
            if( format.equals( "Mdy" ) ){
                this.dateFormat = "MM-dd-yyyy";
            } else if( format.equals( "dMy" ) ) {
                this.dateFormat = "dd-MM-yyyy";
            } else if(format.equals( "yMd" ) ) {
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
		Log.v( TAG, "getMythServicesApi : enter" );
		
		provider = new MythServicesServiceProvider( LocationProfileDaoHelper.getInstance().findConnectedProfile().getUrl(), Level.FINE );
		
		Log.v( TAG, "getMythServicesApi : exit" );
		return provider.getApi();
	}

	public MythServices getMythServicesApi( LocationProfile profile ) {
		Log.v( TAG, "getMythServicesApi : enter" );
		
		MythServicesServiceProvider provider = new MythServicesServiceProvider( profile.getUrl() );
		
		Log.v( TAG, "getMythServicesApi : exit" );
		return provider.getApi();
	}

	public static void initImageLoader( Context context ) {
		int memoryCacheSize;
		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR ) {
			int memClass = ( (ActivityManager) context.getSystemService( Context.ACTIVITY_SERVICE ) ).getMemoryClass();
			memoryCacheSize = (memClass / 8) * 1024 * 1024; // 1/8 of app memory limit 
		} else {
			memoryCacheSize = 2 * 1024 * 1024;
		}

		// This configuration tuning is custom. You can tune every option, you may tune some of them, 
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder( context )
			.threadPriority( Thread.NORM_PRIORITY - 2 )
			.memoryCacheSize( memoryCacheSize )
			.denyCacheImageMultipleSizesInMemory()
			.discCacheFileNameGenerator( new Md5FileNameGenerator() )
			.tasksProcessingOrder( QueueProcessingType.LIFO )
			.enableLogging() // Not necessary in common
			.build();
		
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init( config );
	}
	
	/**
	 * @return the mObjectMapper
	 */
	public ObjectMapper getObjectMapper() {
		Log.v( TAG, "getObjectMapper : enter" );
		
		Log.v( TAG, "getObjectMapper : exit" );
		return mObjectMapper;
	}
	
    /**
     * @return the current clockType
     */
    public String getClockType() {
//		Log.v( TAG, "getClockType : enter" );
		
//		Log.v( TAG, "getClockType : exit" );
        return clockType;
    }

    /**
     * @param clockType the current clockType to set
     */
    public void setClockType( String clockType ) {
//		Log.v( TAG, "setClockType : enter" );

		this.clockType = clockType;

//		Log.v( TAG, "setClockType : exit" );
    }

    public String getDateFormat() {
//		Log.v( TAG, "getDateFormat : enter" );

//		Log.v( TAG, "getDateFormat : exit" );
        return dateFormat;
    }

    public void setDateFormat( String dateFormat ) {
//		Log.v( TAG, "setDateFormat : enter" );
		
        this.dateFormat = dateFormat;

//        Log.v( TAG, "setDateFormat : exit" );
    }

}
