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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractDaoHelper;
import org.mythtv.provider.MythtvProvider;
import org.mythtv.services.api.dvr.Program;
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
	public List<ProgramGroup> findAll( Context context ) {
		Log.v( TAG, "findAll : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "ProgramGroupDaoHelper is not initialized" );
		
		String selection = "";
		String[] selectionArgs = null;

		selection = appendLocationHostname( context, selection, null );
		
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
	public ProgramGroup findOne( Context context, Long id ) {
		Log.v( TAG, "findOne : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "ProgramGroupDaoHelper is not initialized" );
		
		ProgramGroup programGroup = null;
		
		Cursor cursor = context.getContentResolver().query( ContentUris.withAppendedId( ProgramGroupConstants.CONTENT_URI, id ), null, null, null, null );
		if( cursor.moveToFirst() ) {
			programGroup = convertCursorToProgramGroup( cursor );
		}
		cursor.close();
		
		Log.v( TAG, "findOne : exit" );
		return programGroup;
	}

	public ProgramGroup findByTitle( Context context, String title ) {
		Log.v( TAG, "findOne : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "ProgramGroupDaoHelper is not initialized" );
		
		String selection = ProgramGroupConstants.FIELD_TITLE + " = ?";
		String[] selectionArgs = new String[] { title };

		selection = appendLocationHostname( context, selection, null );
		
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
	protected int save( Context context, ProgramGroup programGroup ) {
		Log.v( TAG, "save : enter" );

		if( null == context ) 
			throw new RuntimeException( "ProgramGroupDaoHelper is not initialized" );
		
		ContentValues values = convertProgramGroupToContentValues( context, programGroup );

		String[] projection = new String[] { ProgramGroupConstants._ID };
		String selection = ProgramGroupConstants.FIELD_PROGRAM_GROUP + " = ?";
		String[] selectionArgs = new String[] { programGroup.getProgramGroup() };
		
		selection = appendLocationHostname( context, selection, null );

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
	public int deleteAll( Context context ) {
		Log.v( TAG, "deleteAll : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "ProgramGroupDaoHelper is not initialized" );
		
		String selection = "";
		
		selection = appendLocationHostname( context, selection, null );
		
		int deleted = context.getContentResolver().delete( ProgramGroupConstants.CONTENT_URI, selection, null );
		Log.v( TAG, "deleteAll : deleted=" + deleted );
		
		Log.v( TAG, "deleteAll : exit" );
		return deleted;
	}

	/**
	 * @param programGroup
	 * @return
	 */
	public int delete( Context context, ProgramGroup programGroup ) {
		Log.v( TAG, "delete : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "ProgramGroupDaoHelper is not initialized" );
		
		int deleted = context.getContentResolver().delete( ContentUris.withAppendedId( ProgramGroupConstants.CONTENT_URI, programGroup.getId() ), null, null );
		Log.v( TAG, "delete : deleted=" + deleted );
		
		Log.v( TAG, "delete : exit" );
		return deleted;
	}

	public int load( Context context, List<Program> programs ) throws RemoteException, OperationApplicationException {
		Log.v( TAG, "load : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "ProgramGroupDaoHelper is not initialized" );
		
		Log.v( TAG, "load : find all existing recordings" );
		Map<String, ProgramGroup> existing = new HashMap<String, ProgramGroup>();
		for( ProgramGroup programGroup : findAll( context ) ) {
			existing.put( programGroup.getProgramGroup(), programGroup );
		}

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
						existing.remove( cleaned );
					}

				}
				
			}
			
		}
		
		int loaded = -1;
		int count = 0;

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		
		String[] programGroupProjection = new String[] { ProgramGroupConstants._ID };
		String programGroupSelection = ProgramGroupConstants.FIELD_PROGRAM_GROUP + " = ?";

		programGroupSelection = appendLocationHostname( context, programGroupSelection, null );

		for( String key : programGroups.keySet() ) {
			ProgramGroup programGroup = programGroups.get( key );
			
			ContentValues programValues = convertProgramGroupToContentValues( context, programGroup );
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

		Log.v( TAG, "process : applying final batch for '" + count + "' transactions" );
		
		if( !ops.isEmpty() ) {

			ContentProviderResult[] results = context.getContentResolver().applyBatch( MythtvProvider.AUTHORITY, ops );
			loaded += results.length;

			if( results.length > 0 ) {
				ops.clear();
			}
		}

		Log.v( TAG, "load : remove deleted program groups" );
		for( String key : existing.keySet() ) {

			ops.add(  
				ContentProviderOperation.newDelete( ProgramGroupConstants.CONTENT_URI )
				.withSelection( programGroupSelection, new String[] { key } )
				.withYieldAllowed( true )
				.build()
			);
			
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

		if( !ops.isEmpty() ) {
			Log.v( TAG, "process : applying final batch for '" + count + "' transactions" );
			
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
	
	private ContentValues[] convertProgramGroupsToContentValuesArray( final Context context, final List<ProgramGroup> programGroups ) {
//		Log.v( TAG, "convertProgramGroupsToContentValuesArray : enter" );
		
		if( null != programGroups && !programGroups.isEmpty() ) {
			
			ContentValues contentValues;
			List<ContentValues> contentValuesArray = new ArrayList<ContentValues>();

			for( ProgramGroup programGroup : programGroups ) {

				contentValues = convertProgramGroupToContentValues( context, programGroup );
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

	private ContentValues convertProgramGroupToContentValues( final Context context, final ProgramGroup programGroup ) {
		
		LocationProfile mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( context );
		
		ContentValues values = new ContentValues();
		values.put( ProgramGroupConstants.FIELD_PROGRAM_GROUP, null != programGroup.getTitle() ? ArticleCleaner.clean( programGroup.getTitle() ) : "" );
		values.put( ProgramGroupConstants.FIELD_TITLE, null != programGroup.getTitle() ? programGroup.getTitle() : "" );
		values.put( ProgramGroupConstants.FIELD_CATEGORY, null != programGroup.getCategory() ? programGroup.getCategory() : "" );
		values.put( ProgramGroupConstants.FIELD_INETREF, null != programGroup.getInetref() ? programGroup.getInetref() : "" );
		values.put( ProgramGroupConstants.FIELD_HOSTNAME, mLocationProfile.getHostname() );
		
		return values;
	}

}
