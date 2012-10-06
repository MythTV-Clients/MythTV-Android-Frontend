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
package org.mythtv.service.guide.cache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.service.MythtvService;
import org.mythtv.service.guide.ProgramGuideDownloadService;
import org.mythtv.service.util.FileHelper;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.guide.ProgramGuide;

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
public class ProgramGuideLruMemoryCache extends LruCache<DateTime, ProgramGuide> {

	private static final String TAG = ProgramGuideLruMemoryCache.class.getSimpleName();
	
	private final Context mContext;
    private final ObjectMapper mapper;

    private FileHelper mFileHelper;
	
	public ProgramGuideLruMemoryCache( Context context ) {
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
	protected ProgramGuide create( DateTime key ) {
		Log.v( TAG, "create : enter" );

		File programGuideCache = mFileHelper.getProgramGuideDataDirectory();
		if( programGuideCache.exists() ) {

			String sStart = MythtvService.fileDateTimeFormatter.print( key );
			String filename = sStart + MythtvService.FILENAME_EXT;
			Log.v( TAG, "create : loading data from file " + filename );
			
			File file = new File( programGuideCache, filename );
			if( file.exists() ) {
				Log.v( TAG, "create : file exists " + filename );
				
				try {
					InputStream is = new BufferedInputStream( new FileInputStream( file ), 8192 );
					return mapper.readValue( is, ProgramGuide.class );
				} catch( JsonParseException e ) {
					Log.e( TAG, "create : JsonParseException - error opening file " + filename, e );
				} catch( JsonMappingException e ) {
					Log.e( TAG, "create : JsonMappingException - error opening file " + filename, e );
				} catch( IOException e ) {
					Log.e( TAG, "create : IOException - error opening file " + filename, e );
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
	protected int sizeOf( DateTime key, ProgramGuide value ) {
		
		File programGuideCache = mFileHelper.getProgramGuideDataDirectory();
		if( programGuideCache.exists() ) {

			String sStart = ProgramGuideDownloadService.fileDateTimeFormatter.print( key );
			String filename = sStart + ProgramGuideDownloadService.FILENAME_EXT;
			
			File file = new File( programGuideCache, filename );
			if( file.exists() ) {
				return (int) file.length();
			}
		
		}

		return super.sizeOf( key, value );    
	}

	// internal helpers
	
	public static ProgramGuide getDownloadingProgramGuide( DateTime key ) {
		
		ProgramGuide guide = new ProgramGuide();
		
		List<ChannelInfo> channels = new ArrayList<ChannelInfo>();
		ChannelInfo channel = new ChannelInfo();
		channel.setChannelNumber( "" );
		
		List<Program> programs = new ArrayList<Program>();
		Program program = new Program();
		program.setStartTime( key );
		program.setEndTime( key.withTime( key.getHourOfDay(), 59, 59, 999 ) );
		program.setTitle( "Program Guide is currently downloading" );
		program.setSubTitle( "Please try this timeslot again later." );
		programs.add( program );
		
		channel.setPrograms( programs );
		channels.add( channel );
		
		guide.setChannels( channels );
		
		Log.i( TAG, "getDownloadingProgramGuide : guide=" + guide.toString() );
		
		return guide;

	}
	
}
