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
package org.mythtv.service;

import java.util.TimeZone;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mythtv.client.MainApplication;
import org.mythtv.service.util.FileHelper;

import android.app.IntentService;
import android.content.Intent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * @author Daniel Frey
 *
 */
public abstract class MythtvService extends IntentService {

	protected static final String TAG = MythtvService.class.getSimpleName();
	
	public static enum Method { GET, POST, PUT, DELETE };

	public static final DateTimeZone zone = DateTimeZone.forID( TimeZone.getDefault().getID() );
	
	protected static final int REQUEST_INVALID = -1;

	public static final String METHOD_EXTRA = "org.mythtv.service.METHOD_EXTRA";
	public static final String RESOURCE_TYPE_EXTRA = "org.mythtv.service.RESOURCE_TYPE_EXTRA";

	public static final String SERVICE_CALLBACK = "org.mythtv.service.SERVICE_CALLBACK";
	public static final String ORIGINAL_INTENT_EXTRA = "org.mythtv.service.ORIGINAL_INTENT_EXTRA";

	public static final DateTimeFormatter fileDateTimeFormatter = DateTimeFormat.forPattern( "yyyy-MM-dd'T'HH-mm-ss" );

	public static final String FILENAME_EXT = ".json";
    
	protected static ObjectMapper mObjectMapper;
	protected FileHelper mFileHelper;
    protected MainApplication mMainApplication;
	

	public MythtvService( String name ) {
		super( name );
		
		mObjectMapper = new ObjectMapper();
		mObjectMapper.registerModule( new JodaModule() );
		
		mFileHelper = new FileHelper( this );

	}

	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		mMainApplication = (MainApplication) MythtvService.this.getApplicationContext();
	}

}
