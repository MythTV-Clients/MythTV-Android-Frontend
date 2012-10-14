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
package org.mythtv.service.dvr.cache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.service.util.FileHelper;
import org.mythtv.services.api.dvr.Program;
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
public class UpcomingLruMemoryCache extends LruCache<String, Programs> {

	private static final String TAG = UpcomingLruMemoryCache.class.getSimpleName();
	
	private final Context mContext;
    private final ObjectMapper mapper;

    private FileHelper mFileHelper;
	
	public UpcomingLruMemoryCache( Context context ) {
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

		File upcomingDirectory = mFileHelper.getProgramUpcomingDataDirectory();
		if( upcomingDirectory.exists() ) {

			File file = new File( upcomingDirectory, key );
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
		
		File upcomingDirectory = mFileHelper.getProgramUpcomingDataDirectory();
		if( upcomingDirectory.exists() ) {

			File file = new File( upcomingDirectory, key );
			if( file.exists() ) {
				return (int) file.length();
			}
		
		}

		return super.sizeOf( key, value );    
	}

	// internal helpers
	
	public static Programs getEmptyPrograms() {
		
		Programs programs = new Programs();
		
		List<Program> programList = new ArrayList<Program>();
		Program program = new Program();
		program.setTitle( "No Upcoming Recordings available." );
		program.setSubTitle( "" );
		program.setStartTime( new DateTime() );
		program.setEndTime( new DateTime() );
		program.setCategory( "" );
		programList.add( program );
		programs.setPrograms( programList );
		
		Log.i( TAG, "getDownloadingPrograms : programs=" + programs.toString() );
		
		return programs;

	}
	
}
