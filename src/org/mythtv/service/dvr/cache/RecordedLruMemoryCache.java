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
package org.mythtv.service.dvr.cache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.mythtv.service.dvr.RecordedDownloadService;
import org.mythtv.service.util.FileHelper;
import org.mythtv.services.api.dvr.Programs;

import android.content.Context;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * @author Daniel Frey
 *
 */
public class RecordedLruMemoryCache extends LruCache<String, Programs> {

	private static final String TAG = RecordedLruMemoryCache.class.getSimpleName();
	
	private final Context mContext;
    private final ObjectMapper mapper;

    private FileHelper mFileHelper;
	
	public RecordedLruMemoryCache( Context context ) {
		super( 12 * 1024 * 1024 );
		Log.v( TAG, "initialize : enter" );

		mContext = context;
		mFileHelper = new FileHelper( mContext );
		
		mapper = new ObjectMapper();
		mapper.registerModule( new JodaModule() );
		
		Log.v( TAG, "initialize : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.util.LruCache#create(java.lang.Object)
	 */
	@Override
	protected Programs create( String key ) {
		Log.v( TAG, "create : enter" );

		File programCache = mFileHelper.getProgramDataDirectory();
		if( programCache.exists() ) {

			File file = new File( programCache, key );
			if( file.exists() ) {
				Log.v( TAG, "create : recorded file exists" );
				
				try {
					InputStream is = new BufferedInputStream( new FileInputStream( file ), 8192 );
					return mapper.readValue( is, Programs.class );
				} catch( JsonParseException e ) {
					Log.e( TAG, "create : JsonParseException - error opening file 'recorded'", e );
				} catch( JsonMappingException e ) {
					Log.e( TAG, "create : JsonMappingException - error opening file 'recorded'", e );
				} catch( IOException e ) {
					Log.e( TAG, "create : IOException - error opening file 'recorded'", e );
				}
			}
		
		}
		
		Log.v( TAG, "create : exit" );
		return super.create( key );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.util.LruCache#sizeOf(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected int sizeOf( String key, Programs value ) {
		
		File programCache = mFileHelper.getProgramDataDirectory();
		if( programCache.exists() ) {

			File file = new File( programCache, RecordedDownloadService.RECORDED_FILE );
			if( file.exists() ) {
				return (int) file.length();
			}
		
		}

		return super.sizeOf( key, value );    
	}

	// internal helpers
	
	public static Programs getDownloadingPrograms() {
		
		Programs programs = new Programs();
		
		Log.i( TAG, "getDownloadingPrograms : programs=" + programs.toString() );
		
		return programs;

	}
	
}
