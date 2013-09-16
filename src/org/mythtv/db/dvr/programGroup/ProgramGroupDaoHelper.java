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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractDaoHelper;
import org.mythtv.db.dvr.model.Program;
import org.mythtv.provider.MythtvProvider;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.utils.ArticleCleaner;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGroupDaoHelper extends AbstractDaoHelper {

	private static final String TAG = ProgramGroupDaoHelper.class.getSimpleName();
	
	private static ProgramGroupDaoHelper singleton = null;

	/**
	 * Returns the one and only ProgramGroupDaoHelper. init() must be called before 
	 * any 
	 * 
	 * @return
	 */
	public static ProgramGroupDaoHelper getInstance() {
		if( null == singleton ) {

			synchronized( ProgramGroupDaoHelper.class ) {

				if( null == singleton ) {
					singleton = new ProgramGroupDaoHelper();
				}
			
			}

		}
		
		return singleton;
	}
	
	private ProgramGroupDaoHelper() {
		super();
	}

	/**
	 * @return
	 */
	public List<ProgramGroup> findAll( final Context context, final LocationProfile locationProfile ) {
		Log.v( TAG, "findAll : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		if( null == locationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}
		
		String selection = "";
		String[] selectionArgs = null;

		selection = appendLocationHostname( context, locationProfile, selection, null );
		
		List<ProgramGroup> programGroups = new ArrayList<ProgramGroup>();
		
		Cursor cursor = context.getContentResolver().query( ProgramGroupConstants.CONTENT_URI, null, selection, selectionArgs, ProgramGroupConstants.FIELD_PROGRAM_GROUP );
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
	public ProgramGroup findOne( final Context context, final Long id ) {
		Log.v( TAG, "findOne : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		ProgramGroup programGroup = null;
		
		Cursor cursor = context.getContentResolver().query( ContentUris.withAppendedId( ProgramGroupConstants.CONTENT_URI, id ), null, null, null, null );
		if( cursor.moveToFirst() ) {
			programGroup = convertCursorToProgramGroup( cursor );
		}
		cursor.close();
		
		Log.v( TAG, "findOne : exit" );
		return programGroup;
	}

	public Integer countProgramsByTitle( final Context context, final LocationProfile locationProfile, String title ) {
		Log.d( TAG, "countProgramsByTitle : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		if( null == locationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}
		
		String[] projection = new String[] { "count(" + ProgramGroupConstants.FIELD_TITLE + ")" };
		String selection = ProgramGroupConstants.FIELD_TITLE + " = ?";
		String[] selectionArgs = new String[] { title };
		
		selection = appendLocationHostname( context, locationProfile, selection, ProgramGroupConstants.TABLE_NAME );
		
		Integer count = null;
		
		Cursor cursor = context.getContentResolver().query( ProgramGroupConstants.CONTENT_URI, projection, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {
//			Log.v( TAG, "findProgram : program=" + program.toString() );

			count = cursor.getInt( 0 );
		}
		cursor.close();

		Log.d( TAG, "countProgramsByTitle : exit" );
		return count;
	}

	public ProgramGroup findByTitle( final Context context, final LocationProfile locationProfile, final String title ) {
		Log.v( TAG, "findOne : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		if( null == locationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}
		
		String selection = ProgramGroupConstants.FIELD_TITLE + " = ?";
		String[] selectionArgs = new String[] { title };

		selection = appendLocationHostname( context, locationProfile, selection, null );
		
		ProgramGroup programGroup = null;
		
		Cursor cursor = context.getContentResolver().query( ProgramGroupConstants.CONTENT_URI, null, selection, selectionArgs, null );
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
	public int save( final Context context, final LocationProfile locationProfile, ProgramGroup programGroup ) {
		Log.v( TAG, "save : enter" );

		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		if( null == locationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}
		
		ContentValues values = convertProgramGroupToContentValues( locationProfile, DateUtils.convertUtc( new DateTime( System.currentTimeMillis() ) ), programGroup );

		String[] projection = new String[] { ProgramGroupConstants._ID };
		String selection = ProgramGroupConstants.FIELD_PROGRAM_GROUP + " = ?";
		String[] selectionArgs = new String[] { programGroup.getProgramGroup() };
		
		selection = appendLocationHostname( context, locationProfile, selection, null );

		int updated = -1;
		Cursor cursor = context.getContentResolver().query( ProgramGroupConstants.CONTENT_URI, projection, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {
			Log.v( TAG, "save : updating existing program group" );

			Long id = cursor.getLong( cursor.getColumnIndexOrThrow( ProgramGroupConstants._ID ) );
			
			updated = context.getContentResolver().update( ContentUris.withAppendedId( ProgramGroupConstants.CONTENT_URI, id ), values, null, null );
		} else {
			Uri inserted = context.getContentResolver().insert( ProgramGroupConstants.CONTENT_URI, values );
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
	public int deleteAll( final Context context, final LocationProfile locationProfile ) {
		Log.v( TAG, "deleteAll : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		if( null == locationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}
		
		String selection = "";
		
		selection = appendLocationHostname( context, locationProfile, selection, null );
		
		int deleted = context.getContentResolver().delete( ProgramGroupConstants.CONTENT_URI, selection, null );
		Log.v( TAG, "deleteAll : deleted=" + deleted );
		
		Log.v( TAG, "deleteAll : exit" );
		return deleted;
	}

	/**
	 * @param programGroup
	 * @return
	 */
	public int delete( final Context context, ProgramGroup programGroup ) {
		Log.v( TAG, "delete : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		int deleted = context.getContentResolver().delete( ContentUris.withAppendedId( ProgramGroupConstants.CONTENT_URI, programGroup.getId() ), null, null );
		Log.v( TAG, "delete : deleted=" + deleted );
		
		Log.v( TAG, "delete : exit" );
		return deleted;
	}

	public int load( final Context context, final LocationProfile locationProfile, List<Program> programs ) throws RemoteException, OperationApplicationException {
		Log.v( TAG, "load : enter" );
		
		if( null == context ) {
			throw new IllegalArgumentException( "Context is required" );
		}
		
		if( null == locationProfile ) {
			throw new IllegalArgumentException( "LocationProfile is required" );
		}
		
		DateTime lastModified = new DateTime( DateTimeZone.UTC );
		
		Map<String, ProgramGroup> programGroups = new TreeMap<String, ProgramGroup>();
		for( Program program : programs ) {
			
			if( null != program.getRecording() ) {
				
				if( null != program.getRecording().getRecordingGroup() && !"livetv".equalsIgnoreCase( program.getRecording().getRecordingGroup() ) && !"deleted".equalsIgnoreCase( program.getRecording().getRecordingGroup() ) ) {
					String cleaned = ArticleCleaner.clean( program.getTitle() );
					if( !programGroups.containsKey( cleaned ) ) {
						
						ProgramGroup programGroup = new ProgramGroup();
						programGroup.setTitle( program.getTitle() );
						programGroup.setCategory( program.getCategory() );
						programGroup.setInetref( program.getInetref() );
						programGroup.setSort( 0 );
						
						programGroups.put( cleaned, programGroup );
					}

				}
				
			}
			
		}
		
		Log.v( TAG, "load : adding 'All' program group in programGroups" );
		ProgramGroup all = new ProgramGroup( null, "All", "All", "All", "", 1 );
		programGroups.put( all.getProgramGroup(), all );

		int loaded = -1;
		int count = 0;

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		
		String[] programGroupProjection = new String[] { ProgramGroupConstants._ID };
		String programGroupSelection = ProgramGroupConstants.FIELD_PROGRAM_GROUP + " = ?";

		programGroupSelection = appendLocationHostname( context, locationProfile, programGroupSelection, null );

		for( String key : programGroups.keySet() ) {
			Log.v( TAG, "load : processing programGroup '" + key + "'" );
			
			ProgramGroup programGroup = programGroups.get( key );
			
			ContentValues programValues = convertProgramGroupToContentValues( locationProfile, lastModified, programGroup );
			Cursor programGroupCursor = context.getContentResolver().query( ProgramGroupConstants.CONTENT_URI, programGroupProjection, programGroupSelection, new String[] { key }, null );
			if( programGroupCursor.moveToFirst() ) {

				Long id = programGroupCursor.getLong( programGroupCursor.getColumnIndexOrThrow( ProgramGroupConstants._ID ) );
				ops.add( 
					ContentProviderOperation.newUpdate( ContentUris.withAppendedId( ProgramGroupConstants.CONTENT_URI, id ) )
						.withValues( programValues )
						.withYieldAllowed( true )
						.build()
				);
				
			} else {

				ops.add(  
					ContentProviderOperation.newInsert( ProgramGroupConstants.CONTENT_URI )
						.withValues( programValues )
						.withYieldAllowed( true )
						.build()
				);
			}
			programGroupCursor.close();
			count++;

			if( count > 100 ) {
				Log.v( TAG, "process : applying batch for '" + count + "' transactions" );
				
				if( !ops.isEmpty() ) {
					
					ContentProviderResult[] results = context.getContentResolver().applyBatch( MythtvProvider.AUTHORITY, ops );
					loaded += results.length;
					
					if( results.length > 0 ) {
						ops.clear();
					}
				}

				count = -1;
			}

		}

		Log.v( TAG, "load : remove deleted program groups" );
		ops.add(  
			ContentProviderOperation.newDelete( ProgramGroupConstants.CONTENT_URI )
			.withSelection( ProgramGroupConstants.FIELD_LAST_MODIFIED_DATE + " < ?", new String[] { String.valueOf( lastModified.getMillis() ) } )
			.withYieldAllowed( true )
			.build()
		);
			
		if( !ops.isEmpty() ) {
			Log.v( TAG, "load : applying final batch for '" + count + "' transactions" );
			
			ContentProviderResult[] results = context.getContentResolver().applyBatch( MythtvProvider.AUTHORITY, ops );
			loaded += results.length;
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
		int sort = 0;
		
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

		if( cursor.getColumnIndex( ProgramGroupConstants.FIELD_SORT ) != -1 ) {
			sort = cursor.getInt( cursor.getColumnIndex( ProgramGroupConstants.FIELD_SORT ) );
		}

		if( cursor.getColumnIndex( ProgramGroupConstants.FIELD_MASTER_HOSTNAME ) != -1 ) {
			Log.v( TAG, "convertCursorToProgramGroup : hostname=" + cursor.getString( cursor.getColumnIndex( ProgramGroupConstants.FIELD_MASTER_HOSTNAME ) ) );
		}

		ProgramGroup group = new ProgramGroup();
		group.setId( id );
		group.setProgramGroup( programGroup );
		group.setTitle( title );
		group.setCategory( category );
		group.setInetref( inetref );
		group.setSort( sort );
		
		Log.v( TAG, "convertCursorToProgramGroup : exit" );
		return group;
	}

	// internal helpers
	
	private ContentValues[] convertProgramGroupsToContentValuesArray( final LocationProfile locationProfile, final DateTime lastModified, final List<ProgramGroup> programGroups ) {
//		Log.v( TAG, "convertProgramGroupsToContentValuesArray : enter" );
		
		if( null != programGroups && !programGroups.isEmpty() ) {
			
			ContentValues contentValues;
			List<ContentValues> contentValuesArray = new ArrayList<ContentValues>();

			for( ProgramGroup programGroup : programGroups ) {

				contentValues = convertProgramGroupToContentValues( locationProfile, lastModified, programGroup );
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

	private ContentValues convertProgramGroupToContentValues( final LocationProfile locationProfile, final DateTime lastModified, final ProgramGroup programGroup ) {
		
		ContentValues values = new ContentValues();
		values.put( ProgramGroupConstants.FIELD_PROGRAM_GROUP, null != programGroup.getTitle() ? ArticleCleaner.clean( programGroup.getTitle() ) : "" );
		values.put( ProgramGroupConstants.FIELD_TITLE, null != programGroup.getTitle() ? programGroup.getTitle() : "" );
		values.put( ProgramGroupConstants.FIELD_CATEGORY, null != programGroup.getCategory() ? programGroup.getCategory() : "" );
		values.put( ProgramGroupConstants.FIELD_INETREF, null != programGroup.getInetref() ? programGroup.getInetref() : "" );
		values.put( ProgramGroupConstants.FIELD_SORT, programGroup.getSort() );
		values.put( ProgramGroupConstants.FIELD_MASTER_HOSTNAME, locationProfile.getHostname() );
		values.put( ProgramGroupConstants.FIELD_LAST_MODIFIED_DATE, lastModified.getMillis() );
		
		return values;
	}

}
