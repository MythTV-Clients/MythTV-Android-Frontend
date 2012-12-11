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
package org.mythtv.db.dvr.programGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.mythtv.db.AbstractDaoHelper;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.utils.ArticleCleaner;

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
public class ProgramGroupDaoHelper extends AbstractDaoHelper {

	private static final String TAG = ProgramGroupDaoHelper.class.getSimpleName();
	
	public ProgramGroupDaoHelper( Context context ) {
		super( context );
	}
	
	/**
	 * @return
	 */
	public List<ProgramGroup> findAll() {
		Log.v( TAG, "findAll : enter" );
		
		String selection = ProgramGroupConstants.FIELD_HOSTNAME + " = ?";
		String[] selectionArgs = new String[] { mLocationProfile.getHostname() };

		List<ProgramGroup> programGroups = new ArrayList<ProgramGroup>();
		
		Cursor cursor = mContext.getContentResolver().query( ProgramGroupConstants.CONTENT_URI, null, selection, selectionArgs, ProgramGroupConstants.FIELD_PROGRAM_GROUP );
		while( cursor.moveToNext() ) {
			ProgramGroup programGroup = convertCursorToProgramGroup( cursor );
			programGroups.add( programGroup );
		}
		cursor.close();

		Log.v( TAG, "findAll : exit" );
		return programGroups;
	}

	/**
	 * @param id
	 * @return
	 */
	public ProgramGroup findOne( Long id ) {
		Log.v( TAG, "findOne : enter" );
		
		ProgramGroup programGroup = null;
		
		Cursor cursor = mContext.getContentResolver().query( ContentUris.withAppendedId( ProgramGroupConstants.CONTENT_URI, id ), null, null, null, null );
		if( cursor.moveToFirst() ) {
			programGroup = convertCursorToProgramGroup( cursor );
		}
		cursor.close();
		
		Log.v( TAG, "findOne : exit" );
		return programGroup;
	}

	public ProgramGroup findByTitle( String title ) {
		Log.v( TAG, "findOne : enter" );
		
		String selection = ProgramGroupConstants.FIELD_TITLE + " = ? AND " + ProgramGroupConstants.FIELD_HOSTNAME + " = ?";
		String[] selectionArgs = new String[] { title, mLocationProfile.getHostname() };

		ProgramGroup programGroup = null;
		
		Cursor cursor = mContext.getContentResolver().query( ProgramGroupConstants.CONTENT_URI, null, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {
			programGroup = convertCursorToProgramGroup( cursor );
		}
		cursor.close();
		
		Log.v( TAG, "findOne : exit" );
		return programGroup;
	}

	/**
	 * @param programGroup
	 * @return
	 */
	protected int save( ProgramGroup programGroup ) {
		Log.v( TAG, "save : enter" );

		ContentValues values = convertProgramGroupToContentValues( programGroup );

		String[] projection = new String[] { ProgramConstants._ID };
		String selection = ProgramGroupConstants.FIELD_PROGRAM_GROUP + " = ? AND " + ProgramGroupConstants.FIELD_HOSTNAME + " = ?";
		String[] selectionArgs = new String[] { programGroup.getProgramGroup(), mLocationProfile.getUrl() };
		
		int updated = -1;
		Cursor cursor = mContext.getContentResolver().query( ProgramGroupConstants.CONTENT_URI, projection, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {
			Log.v( TAG, "save : updating existing program group" );

			Long id = cursor.getLong( cursor.getColumnIndexOrThrow( ProgramGroupConstants._ID ) );
			
			updated = mContext.getContentResolver().update( ContentUris.withAppendedId( ProgramGroupConstants.CONTENT_URI, id ), values, null, null );
		} else {
			Uri inserted = mContext.getContentResolver().insert( ProgramGroupConstants.CONTENT_URI, values );
			if( null != inserted ) {
				updated = 1;
			}
		}
		cursor.close();
		Log.v( TAG, "save : updated=" + updated );

		Log.v( TAG, "save : exit" );
		return updated;
	}

	/**
	 * @return
	 */
	public int deleteAll() {
		Log.v( TAG, "deleteAll : enter" );
		
		int deleted = mContext.getContentResolver().delete( ProgramGroupConstants.CONTENT_URI, null, null );
		Log.v( TAG, "deleteAll : deleted=" + deleted );
		
		Log.v( TAG, "deleteAll : exit" );
		return deleted;
	}

	/**
	 * @param programGroup
	 * @return
	 */
	public int delete( ProgramGroup programGroup ) {
		Log.v( TAG, "delete : enter" );
		
		int deleted = mContext.getContentResolver().delete( ContentUris.withAppendedId( ProgramGroupConstants.CONTENT_URI, programGroup.getId() ), null, null );
		Log.v( TAG, "delete : deleted=" + deleted );
		
		Log.v( TAG, "delete : exit" );
		return deleted;
	}

	public int load( List<Program> programs ) {
		Log.v( TAG, "load : enter" );
		
		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile();
		
		deleteAll();
		
		Map<String, ProgramGroup> programGroups = new TreeMap<String, ProgramGroup>();
		for( Program program : programs ) {
			
			if( null != program.getRecording() ) {
				
				if( null != program.getRecording().getRecordingGroup() && !"livetv".equalsIgnoreCase( program.getRecording().getRecordingGroup() ) ) {
					
					String cleaned = ArticleCleaner.clean( program.getTitle() );
					if( !programGroups.containsKey( cleaned ) ) {
						
						ProgramGroup programGroup = new ProgramGroup();
						programGroup.setTitle( program.getTitle() );
						programGroup.setCategory( program.getCategory() );
						programGroup.setInetref( program.getInetref() );
						
						programGroups.put( cleaned, programGroup );
					}

				}
				
			}
			
		}
		
		int loaded = -1;
		
		ContentValues[] contentValuesArray = convertProgramGroupsToContentValuesArray( new ArrayList<ProgramGroup>( programGroups.values() ) );
		if( null != contentValuesArray ) {
			Log.v( TAG, "load : programGroups=" + contentValuesArray.length );

			loaded = mContext.getContentResolver().bulkInsert( ProgramGroupConstants.CONTENT_URI, contentValuesArray );
			Log.v( TAG, "load : loaded=" + loaded );
		}
		
		
		Log.v( TAG, "load : exit" );
		return loaded;
	}

	/**
	 * @param cursor
	 * @return
	 */
	public ProgramGroup convertCursorToProgramGroup( Cursor cursor ) {
		Log.v( TAG, "convertCursorToProgramGroup : enter" );

		Long id = null;
		String programGroup = "", title = "", category = "", inetref = "";
		
		if( cursor.getColumnIndex( ProgramGroupConstants._ID ) != -1 ) {
			id = cursor.getLong( cursor.getColumnIndex( ProgramGroupConstants._ID ) );
		}

		if( cursor.getColumnIndex( ProgramGroupConstants.FIELD_PROGRAM_GROUP ) != -1 ) {
			programGroup = cursor.getString( cursor.getColumnIndex( ProgramGroupConstants.FIELD_PROGRAM_GROUP ) );
		}
		
		if( cursor.getColumnIndex( ProgramGroupConstants.FIELD_TITLE ) != -1 ) {
			title = cursor.getString( cursor.getColumnIndex( ProgramGroupConstants.FIELD_TITLE ) );
		}
		
		if( cursor.getColumnIndex( ProgramGroupConstants.FIELD_CATEGORY ) != -1 ) {
			category = cursor.getString( cursor.getColumnIndex( ProgramGroupConstants.FIELD_CATEGORY ) );
		}

		if( cursor.getColumnIndex( ProgramGroupConstants.FIELD_INETREF ) != -1 ) {
			inetref = cursor.getString( cursor.getColumnIndex( ProgramGroupConstants.FIELD_INETREF ) );
		}

		if( cursor.getColumnIndex( ProgramGroupConstants.FIELD_HOSTNAME ) != -1 ) {
			Log.v( TAG, "convertCursorToProgramGroup : hostname=" + cursor.getString( cursor.getColumnIndex( ProgramGroupConstants.FIELD_HOSTNAME ) ) );
		}

		ProgramGroup group = new ProgramGroup();
		group.setId( id );
		group.setProgramGroup( programGroup );
		group.setTitle( title );
		group.setCategory( category );
		group.setInetref( inetref );
		
		Log.v( TAG, "convertCursorToProgramGroup : exit" );
		return group;
	}

	// internal helpers
	
	private ContentValues[] convertProgramGroupsToContentValuesArray( final List<ProgramGroup> programGroups ) {
//		Log.v( TAG, "convertProgramGroupsToContentValuesArray : enter" );
		
		if( null != programGroups && !programGroups.isEmpty() ) {
			
			ContentValues contentValues;
			List<ContentValues> contentValuesArray = new ArrayList<ContentValues>();

			for( ProgramGroup programGroup : programGroups ) {

				contentValues = convertProgramGroupToContentValues( programGroup );
				contentValuesArray.add( contentValues );
				
			}			
			
			if( !contentValuesArray.isEmpty() ) {
				
//				Log.v( TAG, "convertProgramGroupsToContentValuesArray : exit" );
				return contentValuesArray.toArray( new ContentValues[ contentValuesArray.size() ] );
			}
			
		}
		
//		Log.v( TAG, "convertProgramGroupsToContentValuesArray : exit, no programs to convert" );
		return null;
	}

	private ContentValues convertProgramGroupToContentValues( final ProgramGroup programGroup ) {
		
		ContentValues values = new ContentValues();
		values.put( ProgramGroupConstants.FIELD_PROGRAM_GROUP, null != programGroup.getTitle() ? ArticleCleaner.clean( programGroup.getTitle() ) : "" );
		values.put( ProgramGroupConstants.FIELD_TITLE, null != programGroup.getTitle() ? programGroup.getTitle() : "" );
		values.put( ProgramGroupConstants.FIELD_CATEGORY, null != programGroup.getCategory() ? programGroup.getCategory() : "" );
		values.put( ProgramGroupConstants.FIELD_INETREF, null != programGroup.getInetref() ? programGroup.getInetref() : "" );
		values.put( ProgramGroupConstants.FIELD_HOSTNAME, mLocationProfile.getHostname() );
		
		return values;
	}

}
