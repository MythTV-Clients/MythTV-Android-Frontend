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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.mythtv.service.dvr.BannerDownloadService;
import org.mythtv.service.util.FileHelper;

import android.content.Context;
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

    private FileHelper mFileHelper;
	
	public BannerLruMemoryCache( Context context ) {
		super( 12 * 1024 * 1024 );
		Log.v( TAG, "initialize : enter" );

		mContext = context;
		mFileHelper = new FileHelper( mContext );
		
		Log.v( TAG, "initialize : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.util.LruCache#create(java.lang.Object)
	 */
	@SuppressWarnings( "deprecation" )
	@Override
	protected BitmapDrawable create( String key ) {
		Log.v( TAG, "create : enter" );

		File imageCache = mFileHelper.getProgramGroupDirectory( key );
		if( imageCache.exists() ) {

			File image = new File( imageCache, BannerDownloadService.BANNER_FILE );
			if( image.exists() ) {
				try {
					InputStream is = new FileInputStream( image );
					Bitmap bitmap = BitmapFactory.decodeStream( is );
					return new BitmapDrawable( bitmap );
				} catch( Exception e ) {
					Log.e( TAG, "create : error reading file" );
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
	protected int sizeOf( String key, BitmapDrawable value ) {
		
		File programGroupDirectory = mFileHelper.getProgramGroupDirectory( key );
		if( programGroupDirectory.exists() ) {

			File image = new File( programGroupDirectory, BannerDownloadService.BANNER_FILE );
			if( image.exists() ) {
				return (int) image.length();
			}
		
			File imageNa = new File( programGroupDirectory, BannerDownloadService.BANNER_FILE_NA );
			if( imageNa.exists() ) {
				return 0;
			}
		
		}

		return super.sizeOf( key, value );    
	}

}
