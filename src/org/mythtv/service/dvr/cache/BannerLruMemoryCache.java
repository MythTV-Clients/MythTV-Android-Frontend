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
import java.io.InputStream;

import org.mythtv.service.dvr.BannerDownloadService;
import org.mythtv.service.util.FileHelper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class BannerLruMemoryCache extends LruCache<String, BitmapDrawable> {

	private static final String TAG = BannerLruMemoryCache.class.getSimpleName();
	
	private final Context mContext;
	private final Resources mResources;
	
	public BannerLruMemoryCache( Context context ) {
		super( 4 * 1024 * 1024 );
		Log.v( TAG, "initialize : enter" );

		mContext = context;
		
		mResources = mContext.getResources();
		
		Log.v( TAG, "initialize : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.util.LruCache#create(java.lang.Object)
	 */
	@Override
	protected BitmapDrawable create( String key ) {
		Log.v( TAG, "create : enter" );

		File programGroupDirectory = FileHelper.getInstance().getProgramGroupDirectory( key );
		if( programGroupDirectory.exists() ) {

			File image = new File( programGroupDirectory, BannerDownloadService.BANNER_FILE );
			if( image.exists() ) {
				try {
				    BitmapFactory.Options options = new BitmapFactory.Options();
				    options.inJustDecodeBounds=true;
				    options.inDither=false;                     //Disable Dithering mode
				    options.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
				    options.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
				    options.inTempStorage=new byte[ 32 * 1024 ]; 

				    BitmapFactory.decodeFile( image.getAbsolutePath(), options );
				    options.inSampleSize = getScale( options.outWidth, options.outHeight, (int) ( (float) options.outWidth * .70), (int) ( (float) options.outHeight * .70 ) );
				    options.inJustDecodeBounds=false;
				    
					InputStream is = new BufferedInputStream( new FileInputStream( image ) );
					Bitmap bitmap = BitmapFactory.decodeStream( is, null, options );
					is.close();
					
					return new BitmapDrawable( mResources, bitmap );
				} catch( Exception e ) {
					Log.e( TAG, "create : error reading file" );
				}
			}
		}
		
		Log.v( TAG, "create : exit" );
		return super.create( key );
	}

//	/* (non-Javadoc)
//	 * @see android.support.v4.util.LruCache#sizeOf(java.lang.Object, java.lang.Object)
//	 */
//	@Override
//	protected int sizeOf( String key, BitmapDrawable value ) {
//		
//		File programGroupDirectory = mFileHelper.getProgramGroupDirectory( key );
//		if( programGroupDirectory.exists() ) {
//
//			File image = new File( programGroupDirectory, BannerDownloadService.BANNER_FILE );
//			if( image.exists() ) {
//				return (int) image.length();
//			}
//		
//			File imageNa = new File( programGroupDirectory, BannerDownloadService.BANNER_FILE_NA );
//			if( imageNa.exists() ) {
//				return 0;
//			}
//		
//		}
//
//		return super.sizeOf( key, value );    
//	}

	// internal helpers
	
	private static int getScale( int originalWidth,int originalHeight, final int requiredWidth,final int requiredHeight ) {
		//a scale of 1 means the original dimensions 
		//of the image are maintained
		int scale=1;

		//calculate scale only if the height or width of 
		//the image exceeds the required value.
		if( ( originalWidth > requiredWidth ) || ( originalHeight > requiredHeight ) ) {
			//calculate scale with respect to
			//the smaller dimension
			if( originalWidth < originalHeight ) {
				scale=Math.round( (float) originalWidth / requiredWidth );
			} else {
				scale=Math.round( (float) originalHeight / requiredHeight );
			}
		}

		return scale;
	}

}
