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

import java.io.File;

import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.db.channel.ChannelDaoHelper;
import org.mythtv.db.content.LiveStreamDaoHelper;
import org.mythtv.db.dvr.ProgramGuideDaoHelper;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.dvr.RecordingDaoHelper;
import org.mythtv.db.dvr.UpcomingDaoHelper;
import org.mythtv.db.dvr.programGroup.ProgramGroupDaoHelper;
import org.mythtv.db.frontends.FrontendDaoHelper;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.db.preferences.PlaybackProfileDaoHelper;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.service.util.RunningServiceHelper;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;

/**
 * @author Daniel Frey
 * @author John Baab
 *
 */
public class MainApplication extends Application {

	private static final String TAG = MainApplication.class.getSimpleName();
	
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
		EtagDaoHelper.getInstance();
		LocationProfileDaoHelper.getInstance();
		ChannelDaoHelper.getInstance();
		FrontendDaoHelper.getInstance();
		LiveStreamDaoHelper.getInstance();
		RecordingDaoHelper.getInstance();
		PlaybackProfileDaoHelper.getInstance();
		ProgramGuideDaoHelper.getInstance();
		ProgramGroupDaoHelper.getInstance();
		
		RecordedDaoHelper.getInstance();
		UpcomingDaoHelper.getInstance();
		
		//Initialize Helpers
		NetworkHelper.getInstance();
		RunningServiceHelper.getInstance();
		ProgramHelper.getInstance().init( this );
		MenuHelper.getInstance();

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

	public static void initImageLoader( Context context ) {

		File cacheDir = new File( context.getCacheDir(), "images" );
		if( !cacheDir.exists() ) {
			cacheDir.mkdir();
		}
				
		// This configuration tuning is custom. You can tune every option, you may tune some of them, 
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder( context )
			.threadPoolSize( 5 )
			.threadPriority( Thread.MIN_PRIORITY + 3 )
			.denyCacheImageMultipleSizesInMemory()
			.memoryCache( new UsingFreqLimitedMemoryCache( 2000000 ) )
			.discCache( new UnlimitedDiscCache( cacheDir ) )
			.build();
		
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init( config );
		
		L.disableLogging();
	}
	
	/**
	 * @return the mObjectMapper
	 */
	public ObjectMapper getObjectMapper() {
		return mObjectMapper;
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
    public void setClockType( String clockType ) {
		this.clockType = clockType;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat( String dateFormat ) {
        this.dateFormat = dateFormat;
    }

}
