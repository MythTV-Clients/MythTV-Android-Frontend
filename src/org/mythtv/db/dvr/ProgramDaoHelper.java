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
package org.mythtv.db.dvr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.mythtv.db.AbstractDaoHelper;
import org.mythtv.db.channel.ChannelDaoHelper;
import org.mythtv.provider.MythtvProvider;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.dvr.Program;

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
public abstract class ProgramDaoHelper extends AbstractDaoHelper {

	protected static final String TAG = ProgramDaoHelper.class.getSimpleName();
	
	protected ChannelDaoHelper mChannelDaoHelper;
	protected RecordingDaoHelper mRecordingDaoHelper;
	
	protected ProgramDaoHelper( Context context ) {
		super( context );
		
		mChannelDaoHelper = new ChannelDaoHelper( context );
		mRecordingDaoHelper = new RecordingDaoHelper( context );
		
	}
	
	/**
	 * @param uri
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	protected List<Program> findAll( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		Log.v( TAG, "findAll : enter" );
		
		List<Program> programs = new ArrayList<Program>();
		
		Cursor cursor = mContext.getContentResolver().query( uri, projection, selection, selectionArgs, sortOrder );
		while( cursor.moveToNext() ) {
			Program program = convertCursorToProgram( cursor );
			programs.add( program );
		}
		cursor.close();

		Log.v( TAG, "findAll : exit" );
		return programs;
	}
	
	/**
	 * @return
	 */
	public abstract List<Program> findAll();
	
	/**
	 * @param uri
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	protected Program findOne( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		Log.v( TAG, "findOne : enter" );
		
		Log.v( TAG, "findOne : selection=" + selection );
		if( null != selectionArgs ) {
			for( String selectionArg : selectionArgs ) {
				Log.v( TAG, "findOne : selectionArg=" + selectionArg );
			}
		}
		
		Program program = null;
		
		Cursor cursor = mContext.getContentResolver().query( uri, projection, selection, selectionArgs, sortOrder );
		if( cursor.moveToFirst() ) {
			program = convertCursorToProgram( cursor );
		}
		cursor.close();
		
		Log.v( TAG, "findOne : exit" );
		return program;
	}
	
	/**
	 * @param channelId
	 * @param startTime
	 * @return
	 */
	public abstract Program findOne( int channelId, DateTime startTime );

	/**
	 * @param uri
	 * @param program
	 * @return
	 */
	protected int save( Uri uri, Program program ) {
		Log.v( TAG, "save : enter" );

		ContentValues values = convertProgramToContentValues( program );

		String[] projection = new String[] { ProgramConstants._ID };
		String selection = ProgramConstants.FIELD_CHANNEL_ID + " = ? AND " + ProgramConstants.FIELD_START_TIME + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( program.getChannelInfo().getChannelId() ), String.valueOf( program.getStartTime().getMillis() ) };
		
		selection = appendLocationUrl( selection, null );

		int updated = -1;
		Cursor cursor = mContext.getContentResolver().query( uri, projection, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {
			Log.v( TAG, "save : updating existing program" );

			Long id = cursor.getLong( cursor.getColumnIndexOrThrow( ProgramConstants._ID ) );
			
			updated = mContext.getContentResolver().update( ContentUris.withAppendedId( uri, id ), values, null, null );
		} else {
			Uri inserted = mContext.getContentResolver().insert( uri, values );
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
	 * @param program
	 * @return
	 */
	public abstract int save( Program program );
	
	/**
	 * @return
	 */
	public int deleteAll( Uri uri ) {
		Log.v( TAG, "deleteAll : enter" );
		
		int deleted = mContext.getContentResolver().delete( uri, null, null );
		Log.v( TAG, "deleteAll : deleted=" + deleted );
		
		Log.v( TAG, "deleteAll : exit" );
		return deleted;
	}

	/**
	 * @return
	 */
	public abstract int deleteAll();
	
	/**
	 * @param uri
	 * @param program
	 * @return
	 */
	public int delete( Uri uri, Program program ) {
		Log.v( TAG, "delete : enter" );
		
		String selection = ProgramConstants.FIELD_CHANNEL_ID + " = ? AND " + ProgramConstants.FIELD_START_TIME + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( program.getChannelInfo().getChannelId() ), String.valueOf( program.getStartTime().getMillis() ) };

		selection = appendLocationUrl( selection, null );

		int deleted = mContext.getContentResolver().delete( uri, selection, selectionArgs );
		Log.v( TAG, "delete : deleted=" + deleted );
		
		Log.v( TAG, "delete : exit" );
		return deleted;
	}

	/**
	 * @param program
	 * @return
	 */
	public abstract int delete( Program program );
	
	/**
	 * @param uri
	 * @param programs
	 * @param table
	 * @return
	 * @throws RemoteException
	 * @throws OperationApplicationException
	 */
	protected int load( Uri uri, List<Program> programs, String table ) throws RemoteException, OperationApplicationException {
		Log.v( TAG, "load : enter" );
				
		Map<String, Program> recorded = new HashMap<String, Program>();
		for( Program program : findAll( uri, null, null, null, null ) ) {
			recorded.put( program.getFilename(), program );
		}
		
		int loaded = -1;
		int count = 0;
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		
		String[] programProjection = new String[] { ProgramConstants._ID };
		String programSelection = ProgramConstants.FIELD_CHANNEL_ID + " = ? AND " + ProgramConstants.FIELD_START_TIME + " = ?";

		programSelection = appendLocationUrl( programSelection, table );

		for( Program program : programs ) {

			DateTime startTime = new DateTime( program.getStartTime() );
			
			ContentValues programValues = convertProgramToContentValues( program );
			Cursor programCursor = mContext.getContentResolver().query( uri, programProjection, programSelection, new String[] { String.valueOf( program.getChannelInfo().getChannelId() ), String.valueOf( startTime.getMillis() ) }, null );
			if( programCursor.moveToFirst() ) {
				//Log.v( TAG, "load : UPDATE channel=" + program.getChannelInfo().getChannelNumber() + ", startTime=" + DateUtils.dateTimeFormatterPretty.print( startTime ) );

				Long id = programCursor.getLong( programCursor.getColumnIndexOrThrow( ProgramConstants._ID ) );
				ops.add( 
						ContentProviderOperation.newUpdate( ContentUris.withAppendedId( ProgramConstants.CONTENT_URI_PROGRAM, id ) )
							.withValues( programValues )
							.withYieldAllowed( true )
							.build()
					);
				
			} else {
				//Log.v( TAG, "load : INSERT channel=" + program.getChannelInfo().getChannelNumber() + ", startTime=" + DateUtils.dateTimeFormatterPretty.print( startTime ) );

				ops.add(  
						ContentProviderOperation.newInsert( uri )
							.withValues( programValues )
							.withYieldAllowed( true )
							.build()
					);
			}
			programCursor.close();
			count++;
			
			if( null != program.getRecording() ) {
				
				String[] recordingProjection = new String[] { ProgramConstants._ID };
				String recordingSelection = RecordingConstants.FIELD_RECORD_ID + " = ? AND " + RecordingConstants.FIELD_START_TS + " = ? AND " + RecordingConstants.FIELD_HOSTNAME + " = ?";

				ContentValues recordingValues = mRecordingDaoHelper.convertRecordingToContentValues( program.getRecording() );
				Cursor recordingCursor = mContext.getContentResolver().query( RecordingConstants.CONTENT_URI, recordingProjection, recordingSelection, new String[] { String.valueOf( program.getRecording().getRecordId() ), String.valueOf( program.getRecording().getStartTimestamp().getMillis() ), mLocationProfile.getUrl() }, null );
				if( recordingCursor.moveToFirst() ) {
					//Log.v( TAG, "load : UPDATE recording=" + program.getRecording().getRecordId() + ", startTime=" + DateUtils.dateTimeFormatterPretty.print( program.getRecording().getStartTimestamp() ) );

					ops.add( 
						ContentProviderOperation.newUpdate( ContentUris.withAppendedId( RecordingConstants.CONTENT_URI, program.getRecording().getRecordId() ) )
						.withValues( recordingValues )
						.withYieldAllowed( true )
						.build()
					);
				} else {
					//Log.v( TAG, "load : INSERT recording=" + program.getRecording().getRecordId() + ", startTime=" + DateUtils.dateTimeFormatterPretty.print( program.getRecording().getStartTimestamp() ) );

					ops.add(  
						ContentProviderOperation.newInsert( RecordingConstants.CONTENT_URI )
						.withValues( recordingValues )
						.withYieldAllowed( true )
						.build()
					);
				}
				recordingCursor.close();
				count++;
			}
			
			if( recorded.containsKey( program.getFilename() ) ) {
				recorded.remove( program.getFilename() );
			}

			if( count > 100 ) {
				Log.v( TAG, "process : applying batch for '" + count + "' transactions" );
				
				if( !ops.isEmpty() ) {
					//Log.v( TAG, "process : applying batch '" + channel.getCallSign() + "'" );
					
					ContentProviderResult[] results = mContext.getContentResolver().applyBatch( MythtvProvider.AUTHORITY, ops );
					loaded += results.length;
					
					if( results.length > 0 ) {
						ops.clear();
					}
				}

				count = -1;
			}
			
		}
		
		for( Program program : recorded.values() ) {

			DateTime startTime = new DateTime( program.getStartTime() );
			
			//Log.v( TAG, "load : DELETE Recording - channel=" + program.getChannelInfo().getChannelNumber() + ", startTime=" + DateUtils.dateTimeFormatterPretty.print( startTime ) );
			
			ops.add(  
				ContentProviderOperation.newDelete( uri )
				.withSelection( ProgramConstants.FIELD_FILENAME + " = ?", new String[] { program.getFilename() } )
				.withYieldAllowed( true )
				.build()
			);
			
			if( count > 100 ) {
				Log.v( TAG, "process : applying batch for '" + count + "' transactions" );
				
				if( !ops.isEmpty() ) {
					
					ContentProviderResult[] results = mContext.getContentResolver().applyBatch( MythtvProvider.AUTHORITY, ops );
					loaded += results.length;
					
					if( results.length > 0 ) {
						ops.clear();
					}

				}

				count = -1;
			}

		}
		
		if( !ops.isEmpty() ) {
			Log.v( TAG, "process : applying batch for '" + count + "' transactions" );
			
			ContentProviderResult[] results = mContext.getContentResolver().applyBatch( MythtvProvider.AUTHORITY, ops );
			loaded += results.length;
		}

		Log.v( TAG, "load : exit" );
		return loaded;
	}
	
	/**
	 * @param programs
	 * @return
	 */
	public abstract int load( List<Program> programs ) throws RemoteException, OperationApplicationException;
	
	/**
	 * @param cursor
	 * @return
	 */
	public Program convertCursorToProgram( Cursor cursor ) {
//		Log.v( TAG, "convertCursorToProgram : enter" );

//		Long id = null;
		DateTime startTime = null, endTime = null, lastModified = null, airDate = null;
		String title = "", subTitle = "", category = "", categoryType = "", seriesId = "", programId = "", fileSize = "", programFlags = "", hostname = "", filename = "", description = "", inetref = "", season = "", episode = "";
		int repeat = -1, videoProps = -1, audioProps = -1, subProps = -1;
		float stars = 0.0f;
		
		ChannelInfo channelInfo = null;
		
//		if( cursor.getColumnIndex( ProgramConstants._ID ) != -1 ) {
//			id = cursor.getLong( cursor.getColumnIndex( ProgramConstants._ID ) );
//		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_START_TIME ) != -1 ) {
			startTime = new DateTime( cursor.getLong( cursor.getColumnIndex( ProgramConstants.FIELD_START_TIME ) ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_END_TIME ) != -1 ) {
			endTime = new DateTime( cursor.getLong( cursor.getColumnIndex( ProgramConstants.FIELD_END_TIME ) ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_TITLE ) != -1 ) {
			title = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_TITLE ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_SUB_TITLE ) != -1 ) {
			subTitle = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_SUB_TITLE ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_CATEGORY ) != -1 ) {
			category = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_CATEGORY ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_CATEGORY_TYPE ) != -1 ) {
			categoryType = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_CATEGORY_TYPE ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_REPEAT ) != -1 ) {
			repeat = cursor.getInt( cursor.getColumnIndex( ProgramConstants.FIELD_REPEAT ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_VIDEO_PROPS ) != -1 ) {
			videoProps = cursor.getInt( cursor.getColumnIndex( ProgramConstants.FIELD_VIDEO_PROPS ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_AUDIO_PROPS ) != -1 ) {
			audioProps = cursor.getInt( cursor.getColumnIndex( ProgramConstants.FIELD_AUDIO_PROPS ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_SUB_PROPS ) != -1 ) {
			subProps = cursor.getInt( cursor.getColumnIndex( ProgramConstants.FIELD_SUB_PROPS ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_SERIES_ID ) != -1 ) {
			seriesId = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_SERIES_ID ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_PROGRAM_ID ) != -1 ) {
			programId = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_PROGRAM_ID ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_STARS ) != -1 ) {
			stars = cursor.getFloat( cursor.getColumnIndex( ProgramConstants.FIELD_STARS ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_FILE_SIZE ) != -1 ) {
			fileSize = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_FILE_SIZE ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_LAST_MODIFIED ) != -1 ) {
			lastModified = new DateTime( cursor.getLong( cursor.getColumnIndex( ProgramConstants.FIELD_LAST_MODIFIED ) ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_PROGRAM_FLAGS ) != -1 ) {
			programFlags = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_PROGRAM_FLAGS ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_HOSTNAME ) != -1 ) {
			hostname = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_HOSTNAME ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_FILENAME ) != -1 ) {
			filename = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_FILENAME ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_AIR_DATE ) != -1 ) {
			airDate = new DateTime( cursor.getLong( cursor.getColumnIndex( ProgramConstants.FIELD_AIR_DATE ) ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_DESCRIPTION ) != -1 ) {
			description = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_DESCRIPTION ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_INETREF ) != -1 ) {
			inetref = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_INETREF ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_SEASON ) != -1 ) {
			season = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_SEASON ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_EPISODE ) != -1 ) {
			episode = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_EPISODE ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_CHANNEL_ID ) != -1 ) {
			channelInfo = mChannelDaoHelper.convertCursorToChannelInfo( cursor );
		}
		
//		if( cursor.getColumnIndex( ProgramConstants.FIELD_ ) != -1 ) {
//			defaultAuth = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_ ) );
//		}

		Program program = new Program();
		program.setStartTime( startTime );
		program.setEndTime( endTime );
		program.setTitle( title );
		program.setSubTitle( subTitle );
		program.setCategory( category );
		program.setCategoryType( categoryType );
		program.setRepeat( repeat == 1 ? true : false );
		program.setVideoProps( videoProps );
		program.setAudioProps( audioProps );
		program.setSubProps( subProps );
		program.setSeriesId( seriesId );
		program.setProgramId( programId );
		program.setStars( stars );
		program.setFileSize( fileSize );
		program.setLastModified( lastModified );
		program.setProgramFlags( programFlags );
		program.setHostname( hostname );
		program.setFilename( filename );
		program.setAirDate( airDate );
		program.setDescription( description );
		program.setInetref( inetref );
		program.setSeason( season );
		program.setEpisode( episode );
		program.setChannelInfo( channelInfo );
//		program.set;
//		program.set;
		
//		Log.v( TAG, "convertCursorToProgram : id=" + id + ", program=" + program.toString() );

//		Log.v( TAG, "convertCursorToProgram : exit" );
		return program;
	}

	protected ContentValues[] convertProgramsToContentValuesArray( final List<Program> programs ) {
//		Log.v( TAG, "convertProgramsToContentValuesArray : enter" );
		
		if( null != programs && !programs.isEmpty() ) {
			
			ContentValues contentValues;
			List<ContentValues> contentValuesArray = new ArrayList<ContentValues>();

			for( Program program : programs ) {

				contentValues = convertProgramToContentValues( program );
				contentValuesArray.add( contentValues );
				
			}			
			
			if( !contentValuesArray.isEmpty() ) {
				
//				Log.v( TAG, "convertProgramsToContentValuesArray : exit" );
				return contentValuesArray.toArray( new ContentValues[ contentValuesArray.size() ] );
			}
			
		}
		
//		Log.v( TAG, "convertProgramsToContentValuesArray : exit, no programs to convert" );
		return null;
	}

	protected ContentValues convertProgramToContentValues( final Program program ) {
		
		DateTime startTime = new DateTime( program.getStartTime().getMillis() );
		DateTime endTime = new DateTime( program.getEndTime().getMillis() );
		
		ContentValues values = new ContentValues();
		values.put( ProgramConstants.FIELD_START_TIME, startTime.getMillis() );
		values.put( ProgramConstants.FIELD_END_TIME, endTime.getMillis() );
		values.put( ProgramConstants.FIELD_TITLE, null != program.getTitle() ? program.getTitle() : "" );
		values.put( ProgramConstants.FIELD_SUB_TITLE, null != program.getSubTitle() ? program.getSubTitle() : "" );
		values.put( ProgramConstants.FIELD_CATEGORY, null != program.getCategory() ? program.getCategory() : "" );
		values.put( ProgramConstants.FIELD_CATEGORY_TYPE, null != program.getCategoryType() ? program.getCategoryType() : "" );
		values.put( ProgramConstants.FIELD_REPEAT, program.isRepeat() ? 1 : 0 );
		values.put( ProgramConstants.FIELD_VIDEO_PROPS, program.getVideoProps() );
		values.put( ProgramConstants.FIELD_AUDIO_PROPS, program.getAudioProps() );
		values.put( ProgramConstants.FIELD_SUB_PROPS, program.getSubProps() );
		values.put( ProgramConstants.FIELD_SERIES_ID, null != program.getSeriesId() ? program.getSeriesId() : "" );
		values.put( ProgramConstants.FIELD_PROGRAM_ID, null != program.getProgramId() ? program.getProgramId() : "" );
		values.put( ProgramConstants.FIELD_STARS, program.getStars() );
		values.put( ProgramConstants.FIELD_FILE_SIZE, null != program.getFileSize() ? program.getFileSize() : "" );
		values.put( ProgramConstants.FIELD_LAST_MODIFIED, null != program.getLastModified() ? DateUtils.dateTimeFormatter.print( program.getLastModified() ) : "" );
		values.put( ProgramConstants.FIELD_PROGRAM_FLAGS, null != program.getProgramFlags() ? program.getProgramFlags() : "" );
		values.put( ProgramConstants.FIELD_HOSTNAME, null != program.getHostname() ? program.getHostname() : "" );
		values.put( ProgramConstants.FIELD_FILENAME, null != program.getFilename() ? program.getFilename() : "" );
		values.put( ProgramConstants.FIELD_AIR_DATE, null != program.getAirDate() ? DateUtils.dateTimeFormatter.print( program.getAirDate() ) : "" );
		values.put( ProgramConstants.FIELD_DESCRIPTION, null != program.getDescription() ? program.getDescription() : "" );
		values.put( ProgramConstants.FIELD_INETREF, null != program.getInetref() ? program.getInetref() : "" );
		values.put( ProgramConstants.FIELD_SEASON, null != program.getSeason() ? program.getSeason() : "" );
		values.put( ProgramConstants.FIELD_EPISODE, null != program.getEpisode() ? program.getEpisode() : "" );
		values.put( ProgramConstants.FIELD_CHANNEL_ID, null != program.getChannelInfo() ? program.getChannelInfo().getChannelId() : -1 );
		values.put( ProgramConstants.FIELD_RECORD_ID, null != program.getRecording() ? program.getRecording().getRecordId() : -1 );
		values.put( ProgramConstants.FIELD_HOSTNAME, mLocationProfile.getHostname() );
		
		return values;
	}

}
