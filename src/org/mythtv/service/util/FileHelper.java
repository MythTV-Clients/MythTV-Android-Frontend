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
package org.mythtv.service.util;

import java.io.File;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

/**
 * @author Daniel Frey
 *
 */
public class FileHelper {

	private static final String TAG = FileHelper.class.getSimpleName();
	
	private static final String PROGRAM_GUIDE_DATA = "programGuide";
	private static final String PROGRAM_DATA = "program";
	private static final String IMAGE_DATA = "images";
	
	private Context mContext;
	
	public FileHelper( Context context ) {
		mContext = context;
	}
	
	public File getProgramGuideDataDirectory() {
//		Log.v( TAG, "getProgramGuideDataDirectory : enter" );
		
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
	
	public File getProgramImagesDataDirectory() {
//		Log.v( TAG, "getProgramImagesDataDirectory : enter" );
		
		File programDir = getProgramDataDirectory();
		if( null != programDir && programDir.exists() ) {
			
			File programImageDataDirectory = new File( programDir, IMAGE_DATA );
			programImageDataDirectory.mkdir();
			
			if( programImageDataDirectory.exists() ) {
//				Log.v( TAG, "getProgramImageDataDirectory : exit" );

				return programImageDataDirectory;
			}
			
		}
		
//		Log.v( TAG, "getProgramImageDataDirectory : exit, program image data directory doesn't exit" );
		return null;
	}
	
	// internal helpers
	
	private File getRootCacheDirectory() {
//		Log.v( TAG, "getRootCacheDirectory : enter" );
		
		File cacheDir = null;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO ) {
//    		Log.v( TAG, "getRootCacheDirectory : exit, returning froyo+ cache directory" );

        	return mContext.getExternalCacheDir();
        
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
