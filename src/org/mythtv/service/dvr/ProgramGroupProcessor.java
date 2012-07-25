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
package org.mythtv.service.dvr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.dvr.ProgramGroupConstants;
import org.mythtv.service.AbstractMythtvProcessor;
import org.mythtv.service.util.ArticleCleaner;
import org.mythtv.services.api.content.ArtworkInfo;
import org.mythtv.services.api.dvr.Program;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGroupProcessor extends AbstractMythtvProcessor {

	protected static final String TAG = ProgramGroupProcessor.class.getSimpleName();

	public ProgramGroupProcessor( Context context ) {
		super( context );
		Log.v( TAG, "initialize : enter" );

		Log.v( TAG, "initialize : exit" );
	}

	public Long updateProgramGroupContentProvider( Program program, ProgramConstants.ProgramType programType ) {
		Log.v( TAG, "updateProgramGroupContentProvider : enter" );
		
		long programGroupId = 0;
		
		// Removing Grammar Articles.  English only at this time, needs internationalization
		String cleanTitle = ArticleCleaner.clean( program.getTitle() );
		
		ContentValues values = new ContentValues();
		values.put( ProgramGroupConstants.FIELD_PROGRAM_TYPE, null != programType ? programType.name() : "" );
		values.put( ProgramGroupConstants.FIELD_PROGRAM_GROUP, program.getTitle() );
		values.put( ProgramGroupConstants.FIELD_PROGRAM_GROUP_SORT, cleanTitle );
		values.put( ProgramGroupConstants.FIELD_INETREF, program.getInetref() );
		
		Cursor cursor = mContext.getContentResolver().query( ProgramGroupConstants.CONTENT_URI, new String[] { ProgramGroupConstants._ID }, ProgramGroupConstants.FIELD_PROGRAM_GROUP + " = ? and " + ProgramGroupConstants.FIELD_PROGRAM_TYPE + " = ?", new String[] { program.getTitle(), programType.name() }, null );
		if( cursor.moveToFirst() ) {
			Log.v( TAG, "updateProgramContentProvider : programGroup already exists" );
			
			programGroupId = cursor.getInt( cursor.getColumnIndexOrThrow( ProgramGroupConstants._ID ) );
		} else {
			Log.v( TAG, "updateProgramContentProvider : adding new programGroup" );
			
			String filename = "N/A";
			boolean bannerFound = false;

			//Log.v( TAG, "updateProgramContentProvider : looking for banner to download" );
			if( null != program.getArtwork() && !program.getArtwork().getArtworkInfos().isEmpty() ) {
				
				for( ArtworkInfo artworkInfo : program.getArtwork().getArtworkInfos() ) {
					if( "banner".equals( artworkInfo.getType() ) ) {
						Log.v( TAG, "updateProgramContentProvider : banner found" );

						bannerFound = true;
						
						break;
					}
				}
				
			}
			
			if( bannerFound && ( null != program.getInetref() && !"".equals( program.getInetref() ) ) ) {
				//Log.v( TAG, "updateProgramContentProvider : generating banner filename" );

				File root = mContext.getExternalCacheDir();
            
            	File pictureDir = new File( root, "Banners" );
            	pictureDir.mkdirs();
            
            	File f = new File( pictureDir, program.getInetref() + ".png" );
            	filename = f.getAbsolutePath();
            	f.delete();
			}
			values.put( ProgramGroupConstants.FIELD_BANNER_URL, filename );
			
			Uri programGroupUri = mContext.getContentResolver().insert( ProgramGroupConstants.CONTENT_URI, values );
			programGroupId = ContentUris.parseId( programGroupUri );
		}
		cursor.close();

		Log.v( TAG, "updateProgramGroupContentProvider : exit" );
		return programGroupId;
	}
	
	public int removeDeletedProgramGroups( List<Long> programGroupIds, ProgramConstants.ProgramType programType ) {
		Log.v( TAG, "removeDeletedProgramGroups : enter" );
		
		int count = 0;
		
		if( !programGroupIds.isEmpty() ) {
			Log.v( TAG, "removeDeletedProgramGroups : looking up program groups to remove" );

			StringBuilder sb = new StringBuilder();
			for( int i = 0; i < programGroupIds.size(); i++ ) {
				sb.append( programGroupIds.get( i ) );
				
				if( i < programGroupIds.size() - 1 ) {
					sb.append( "," );
				}
			}
			if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
				Log.v( TAG, "removeDeletedProgramGroups : existing program group ids=" + sb.toString() );
			}
			
			List<Long> deleteIds = new ArrayList<Long>();
			Cursor cursor = mContext.getContentResolver().query( ProgramGroupConstants.CONTENT_URI, new String[] { ProgramGroupConstants._ID }, ProgramGroupConstants._ID + " not in (" + sb.toString() + ") and " + ProgramGroupConstants.FIELD_PROGRAM_TYPE + " = ?", new String[] { programType.name() }, null );
			while( cursor.moveToNext() ) {
				Long id = cursor.getLong( cursor.getColumnIndexOrThrow( ProgramGroupConstants._ID ) );
				deleteIds.add( id );

				//Log.v( TAG, "removeDeletedProgramGroups : queing for deletion, id=" + id );
			}
			cursor.close();
			
			if( !deleteIds.isEmpty() ) {
				for( Long id : deleteIds ) {
					int deleted = mContext.getContentResolver().delete( ContentUris.withAppendedId( ProgramGroupConstants.CONTENT_URI, id ), null, null );
					count += deleted;
				}
			}
		}

		Log.v( TAG, "removeDeletedProgramGroups : exit" );
		return count;
	}
	
}
