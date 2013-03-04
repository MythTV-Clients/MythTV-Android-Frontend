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
package org.mythtv.service.util;

import java.io.File;

import android.os.Build;
import android.os.Environment;

/**
 * @author Daniel Frey
 *
 */
public class FileHelper {

//	private static final String TAG = FileHelper.class.getSimpleName();
	
	private static FileHelper singleton;
	
	private static final String CHANNEL_DATA = "channel";
	private static final String PROGRAM_GUIDE_DATA = "programGuide";
	private static final String PROGRAM_DATA = "program";
	private static final String PROGRAM_RECORDED_DATA = "recorded";
	private static final String PROGRAM_UPCOMING_DATA = "upcoming";
	private static final String PROGRAM_GROUPS_DATA = "groups";
	private static final String IMAGE_DATA = "images";
	
	private File mFile = null;
	
	public static FileHelper getInstance() {
		if( null == singleton ) singleton = new FileHelper();
		
		return singleton;
	}
	
	private FileHelper() {	}
	
	/**
	 * Initialized the FileHelper with cache directory
	 * @param file: getExternalCacheDir() API8+ cache directory
	 */
	public void init( File file ) {
		mFile = file;
	}
	
	/**
	 * Returns true when 
	 * @return
	 */
	public boolean isInitialized(){
		return null != mFile;
	}
	
	public File getChannelDataDirectory() {
//		Log.v( TAG, "getChannelDataDirectory : enter" );
		
		/* Check if external cache dir file is initialized */
		if( !this.isInitialized() ) 
			throw new RuntimeException( "FileHelper not initialized with cache directory" );

		File cacheDir = getRootCacheDirectory();
		if( null != cacheDir && cacheDir.exists() ) {
			
			File channelDataDirectory = new File( cacheDir, CHANNEL_DATA );
			channelDataDirectory.mkdir();
			
			if( channelDataDirectory.exists() ) {
//				Log.v( TAG, "getChannelDataDirectory : exit" );

				return channelDataDirectory;
			}
			
		}
		
//		Log.v( TAG, "getChannelDataDirectory : exit, channel data directory doesn't exit" );
		return null;
	}
	
	public File getProgramGuideDataDirectory() {
//		Log.v( TAG, "getProgramGuideDataDirectory : enter" );
		
		/* Check if external cache dir file is initialized */
		if( !this.isInitialized() ) 
			throw new RuntimeException( "FileHelper not initialized with cache directory" );

		File cacheDir = getRootCacheDirectory();
		if( null != cacheDir && cacheDir.exists() ) {
			
			File programGuideDataDirectory = new File( cacheDir, PROGRAM_GUIDE_DATA );
			programGuideDataDirectory.mkdir();
			
			if( programGuideDataDirectory.exists() ) {
//				Log.v( TAG, "getProgramGuideDataDirectory : exit" );

				return programGuideDataDirectory;
			}
			
		}
		
//		Log.v( TAG, "getProgramGuideDataDirectory : exit, program guide data directory doesn't exit" );
		return null;
	}
	
	public File getProgramGuideImagesDataDirectory() {
//		Log.v( TAG, "getProgramGuideImagesDataDirectory : enter" );
		
		/* Check if external cache dir file is initialized */
		if( !this.isInitialized() ) 
			throw new RuntimeException( "FileHelper not initialized with cache directory" );

		File programGuideDir = getProgramGuideDataDirectory();
		if( null != programGuideDir && programGuideDir.exists() ) {
			
			File programGuideImageDataDirectory = new File( programGuideDir, IMAGE_DATA );
			programGuideImageDataDirectory.mkdir();
			
			if( programGuideImageDataDirectory.exists() ) {
//				Log.v( TAG, "getProgramGuideImageDataDirectory : exit" );

				return programGuideImageDataDirectory;
			}
			
		}
		
//		Log.v( TAG, "getProgramGuideImageDataDirectory : exit, program guide image data directory doesn't exit" );
		return null;
	}
	
	public File getProgramDataDirectory() {
//		Log.v( TAG, "getProgramDataDirectory : enter" );
		
		/* Check if external cache dir file is initialized */
		if( !this.isInitialized() ) 
			throw new RuntimeException( "FileHelper not initialized with cache directory" );

		File cacheDir = getRootCacheDirectory();
		if( null != cacheDir && cacheDir.exists() ) {
			
			File programDataDirectory = new File( cacheDir, PROGRAM_DATA );
			programDataDirectory.mkdir();
			
			if( programDataDirectory.exists() ) {
//				Log.v( TAG, "getProgramDataDirectory : exit" );

				return programDataDirectory;
			}
			
		}
		
//		Log.v( TAG, "getProgramDataDirectory : exit, program data directory doesn't exit" );
		return null;
	}
	
	public File getProgramUpcomingDataDirectory() {
//		Log.v( TAG, "getProgramUpcomingDataDirectory : enter" );
		
		/* Check if external cache dir file is initialized */
		if( !this.isInitialized() ) 
			throw new RuntimeException( "FileHelper not initialized with cache directory" );
	
		File programDir = getProgramDataDirectory();
		if( null != programDir && programDir.exists() ) {
			
			File programUpcomingDataDirectory = new File( programDir, PROGRAM_UPCOMING_DATA );
			programUpcomingDataDirectory.mkdir();
			
			if( programUpcomingDataDirectory.exists() ) {
//				Log.v( TAG, "getProgramUpcomingDataDirectory : exit" );

				return programUpcomingDataDirectory;
			}
			
		}
		
//		Log.v( TAG, "getProgramUpcomingDataDirectory : exit, program upcoming data directory doesn't exit" );
		return null;
	}
	
	public File getProgramRecordedDataDirectory() {
//		Log.v( TAG, "getProgramRecordedDataDirectory : enter" );
		
		/* Check if external cache dir file is initialized */
		if( !this.isInitialized() ) 
			throw new RuntimeException( "FileHelper not initialized with cache directory" );
		
		File programDir = getProgramDataDirectory();
		if( null != programDir && programDir.exists() ) {
			
			File programRecordedDataDirectory = new File( programDir, PROGRAM_RECORDED_DATA );
			programRecordedDataDirectory.mkdir();
			
			if( programRecordedDataDirectory.exists() ) {
//				Log.v( TAG, "getProgramRecordedDataDirectory : exit" );

				return programRecordedDataDirectory;
			}
			
		}
		
//		Log.v( TAG, "getProgramRecordedDataDirectory : exit, program recorded data directory doesn't exit" );
		return null;
	}

	public File getProgramGroupsDataDirectory() {
//		Log.v( TAG, "getProgramGroupsDataDirectory : enter" );
		
		/* Check if external cache dir file is initialized */
		if( !this.isInitialized() ) 
			throw new RuntimeException( "FileHelper not initialized with cache directory" );
		
		File programDir = getProgramDataDirectory();
		if( null != programDir && programDir.exists() ) {
			
			File programGroupsDataDirectory = new File( programDir, PROGRAM_GROUPS_DATA );
			programGroupsDataDirectory.mkdir();
			
			if( programGroupsDataDirectory.exists() ) {
//				Log.v( TAG, "getProgramGroupsDataDirectory : exit" );

				return programGroupsDataDirectory;
			}
			
		}
		
//		Log.v( TAG, "getProgramGroupsDataDirectory : exit, program groups data directory doesn't exit" );
		return null;
	}

	public File getProgramGroupDirectory( String title ) {
//		Log.v( TAG, "getProgramGroupDirectory : enter" );
		
		/* Check if external cache dir file is initialized */
		if( !this.isInitialized() ) 
			throw new RuntimeException( "FileHelper not initialized with cache directory" );
			
		String encodedTitle = UrlUtils.encodeUrl( title );
		
		File programGroupsDataDir = getProgramGroupsDataDirectory();
		if( null != programGroupsDataDir && programGroupsDataDir.exists() ) {
			
			File programGroupDirectory = new File( programGroupsDataDir, encodedTitle );
			programGroupDirectory.mkdir();
			
			if( programGroupDirectory.exists() ) {
//				Log.v( TAG, "getProgramGroupDirectory : exit" );

				return programGroupDirectory;
			}
			
		}
		
//		Log.v( TAG, "getProgramGroupDirectory : exit, program group directory doesn't exit" );
		return null;
	}

	// internal helpers
	
	private File getRootCacheDirectory() {
//		Log.v( TAG, "getRootCacheDirectory : enter" );
		
		String state = Environment.getExternalStorageState();
		if( !Environment.MEDIA_MOUNTED.equals( state ) ) {
			return null;
		}
		
		File cacheDir = null;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ) {
//    		Log.v( TAG, "getRootCacheDirectory : exit, returning froyo+ cache directory" );

        	return mFile;
        
        } else {
        	
        	File root = Environment.getExternalStorageDirectory();
        	
        	File appRoot = new File( root, "org.mythtv" );
        	appRoot.mkdir();
        	
        	if( appRoot.exists() ) {
        		
        		cacheDir = new File( appRoot, "cache" );
        		cacheDir.mkdir();
        		if( cacheDir.exists() ) {
//        			Log.v( TAG, "getRootCacheDirectory : exit, returning pre-froyo cache directory" );

        			return cacheDir;
        		}
        		
        	}
        	
        }
		
//		Log.v( TAG, "getRootCacheDirectory : exit, cache directory does not exist" );
		return null;
	}

}
