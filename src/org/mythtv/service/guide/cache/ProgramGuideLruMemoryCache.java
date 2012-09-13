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
package org.mythtv.service.guide.cache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.mythtv.service.guide.ProgramGuideDownloadService;
import org.mythtv.service.util.FileHelper;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.guide.ProgramGuide;

import android.content.Context;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGuideLruMemoryCache extends LruCache<DateTime, ProgramGuide> {

	private static final String TAG = ProgramGuideLruMemoryCache.class.getSimpleName();
	
	private final Context mContext;
    private final ObjectMapper mapper = new ObjectMapper();

    private FileHelper mFileHelper;
	
	public ProgramGuideLruMemoryCache( Context context ) {
		super( 12 * 1024 * 1024 );
		Log.v( TAG, "initialize : enter" );

		mContext = context;
		mFileHelper = new FileHelper( mContext );
		
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

			String sStart = ProgramGuideDownloadService.dateTimeFormatter.print( key );
			String filename = sStart + ProgramGuideDownloadService.FILENAME_EXT;
			Log.v( TAG, "create : loading data from file " + filename );
			
			File file = new File( programGuideCache, filename );
			Log.v( TAG, "create : file=" + file.getAbsolutePath() );
			if( file.exists() ) {
				Log.v( TAG, "create : file exists " + filename );
				
				try {
					return mapper.readValue( file, ProgramGuide.class );
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
		return getDownloadingProgramGuide( key );
	}

	// internal helpers
	
	private static ProgramGuide getDownloadingProgramGuide( DateTime key ) {
		
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
