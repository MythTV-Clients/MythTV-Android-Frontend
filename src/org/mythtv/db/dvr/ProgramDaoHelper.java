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
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractDaoHelper;
import org.mythtv.db.channel.ChannelDaoHelper;
import org.mythtv.db.content.LiveStreamConstants;
import org.mythtv.db.content.LiveStreamDaoHelper;
import org.mythtv.provider.MythtvProvider;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.MythtvServiceHelper;
import org.mythtv.services.api.Bool;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.content.LiveStreamInfo;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.Recording;
import org.springframework.http.ResponseEntity;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public abstract class ProgramDaoHelper extends AbstractDaoHelper {

	protected static final String TAG = ProgramDaoHelper.class.getSimpleName();
	
	protected MythtvServiceHelper mMythtvServiceHelper = MythtvServiceHelper.getInstance();
	protected ChannelDaoHelper mChannelDaoHelper = ChannelDaoHelper.getInstance();
	protected LiveStreamDaoHelper mLiveStreamDaoHelper = LiveStreamDaoHelper.getInstance();
	protected RecordingDaoHelper mRecordingDaoHelper = RecordingDaoHelper.getInstance();
	
	protected ProgramDaoHelper() {
		super();
	}
	
	/**
	 * @param uri
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	protected List<Program> findAll( final Context context, final Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		Log.v( TAG, "findAll : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "ProgramDaoHelper is not initialized" );
		
		List<Program> programs = new ArrayList<Program>();
		
		Cursor cursor = context.getContentResolver().query( uri, projection, selection, selectionArgs, sortOrder );
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
	public abstract List<Program> findAll( final Context context, final LocationProfile locationProfile );
	
	/**
	 * @param uri
	 * @param projection
	 * @param selection
	 * @param selectionArgs
	 * @param sortOrder
	 * @return
	 */
	protected Program findOne( final Context context, final Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
		Log.v( TAG, "findOne : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "ProgramDaoHelper is not initialized" );
		
		Log.v( TAG, "findOne : selection=" + selection );
		if( null != selectionArgs ) {
			for( String selectionArg : selectionArgs ) {
				Log.v( TAG, "findOne : selectionArg=" + selectionArg );
			}
		}
		
		Program program = null;
		
		Cursor cursor = context.getContentResolver().query( uri, projection, selection, selectionArgs, sortOrder );
		if( cursor.moveToFirst() ) {
			program = convertCursorToProgram( cursor );
		}
		cursor.close();
		
		Log.v( TAG, "findOne : exit" );
		return program;
	}
	
	/**
	 * @param context
	 * @param locationProfile
	 * @param channelId
	 * @param startTime
	 * @return
	 */
	public abstract Program findOne( final Context context, final LocationProfile locationProfile, final int channelId, final DateTime startTime );

	/**
	 * @param uri
	 * @param program
	 * @return
	 */
	protected int save( final Context context, final Uri uri, final LocationProfile locationProfile, Program program ) {
		Log.v( TAG, "save : enter" );

		if( null == context ) 
			throw new RuntimeException( "ProgramDaoHelper is not initialized" );
		
		ContentValues values = convertProgramToContentValues( locationProfile, program );

		String[] projection = new String[] { ProgramConstants._ID };
		String selection = ProgramConstants.FIELD_CHANNEL_ID + " = ? AND " + ProgramConstants.FIELD_START_TIME + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( program.getChannelInfo().getChannelId() ), String.valueOf( program.getStartTime().getMillis() ) };
		
		selection = appendLocationHostname( context, locationProfile, selection, null );

		int updated = -1;
		Cursor cursor = context.getContentResolver().query( uri, projection, selection, selectionArgs, null );
		if( cursor.moveToFirst() ) {
			Log.v( TAG, "save : updating existing program" );

			Long id = cursor.getLong( cursor.getColumnIndexOrThrow( ProgramConstants._ID ) );
			
			updated = context.getContentResolver().update( ContentUris.withAppendedId( uri, id ), values, null, null );
		} else {
			Uri inserted = context.getContentResolver().insert( uri, values );
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
	public abstract int save( final Context context, final LocationProfile locationProfile, Program program );
	
	/**
	 * @return
	 */
	public int deleteAll( final Context context, final Uri uri ) {
		Log.v( TAG, "deleteAll : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "ProgramDaoHelper is not initialized" );
		
		int deleted = context.getContentResolver().delete( uri, null, null );
		Log.v( TAG, "deleteAll : deleted=" + deleted );
		
		Log.v( TAG, "deleteAll : exit" );
		return deleted;
	}

	/**
	 * @return
	 */
	public abstract int deleteAll( final Context context );
	
	/**
	 * @param uri
	 * @param program
	 * @return
	 */
	public int delete( final Context context, final Uri uri, final LocationProfile locationProfile, Program program ) {
		Log.v( TAG, "delete : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "ProgramDaoHelper is not initialized" );
		
		String selection = ProgramConstants.FIELD_CHANNEL_ID + " = ? AND " + ProgramConstants.FIELD_START_TIME + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( program.getChannelInfo().getChannelId() ), String.valueOf( program.getStartTime().getMillis() ) };

		selection = appendLocationHostname( context, locationProfile, selection, null );

		int deleted = context.getContentResolver().delete( uri, selection, selectionArgs );
		Log.v( TAG, "delete : deleted=" + deleted );
		
		Log.v( TAG, "delete : exit" );
		return deleted;
	}

	/**
	 * @param program
	 * @return
	 */
	public abstract int delete( final Context context, final LocationProfile locationProfile, Program program );
	
	/**
	 * @param uri
	 * @param programs
	 * @param table
	 * @return
	 * @throws RemoteException
	 * @throws OperationApplicationException
	 */
	protected int load( final Context context, final Uri uri, final LocationProfile locationProfile, List<Program> programs, String table ) throws RemoteException, OperationApplicationException {
		Log.v( TAG, "load : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "ProgramDaoHelper is not initialized" );
		
		Log.v( TAG, "load : find all existing recordings, table=" + table );
		String recordedSelection = "";
		
		recordedSelection = appendLocationHostname( context, locationProfile, recordedSelection, table );
		Log.v( TAG, "load : recordedSelection=" + recordedSelection );
		
		Map<String, Program> recorded = new HashMap<String, Program>();
		for( Program program : findAll( context, uri, null, recordedSelection, null, null ) ) {
			recorded.put( program.getFilename(), program );
		}
		
		int loaded = -1;
		int count = 0;
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		
		String[] programProjection = new String[] { ProgramConstants._ID };
		String programSelection = table + "." + ProgramConstants.FIELD_CHANNEL_ID + " = ? AND " + table + "." + ProgramConstants.FIELD_START_TIME + " = ?";

		programSelection = appendLocationHostname( context, locationProfile, programSelection, table );
		
		boolean inError;
		
		for( Program program : programs ) {

			if( null == program.getStartTime() || null == program.getEndTime() ) {
				Log.w(TAG, "convertProgramToContentValues : null starttime and or endtime" );
			
				inError = true;
			} else {
				inError = false;
			}

			DateTime startTime = new DateTime( program.getStartTime() );
			
			ContentValues programValues = convertProgramToContentValues( locationProfile, program );
			Cursor programCursor = context.getContentResolver().query( uri, programProjection, programSelection, new String[] { String.valueOf( program.getChannelInfo().getChannelId() ), String.valueOf( startTime.getMillis() ) }, null );
			if( programCursor.moveToFirst() ) {
				//Log.v( TAG, "load : UPDATE PROGRAM channel=" + program.getChannelInfo().getChannelNumber() + ", startTime=" + DateUtils.dateTimeFormatterPretty.print( startTime ) + "(" + startTime + ")" );

				Long id = programCursor.getLong( programCursor.getColumnIndexOrThrow( ProgramConstants._ID ) );
				ops.add( 
						ContentProviderOperation.newUpdate( ContentUris.withAppendedId( uri, id ) )
							.withValues( programValues )
							.withYieldAllowed( true )
							.build()
					);
				
			} else {
				//Log.v( TAG, "load : INSERT PROGRAM channel=" + program.getChannelInfo().getChannelNumber() + ", startTime=" + DateUtils.dateTimeFormatterPretty.print( startTime ) + "(" + startTime + ")" );

				ops.add(  
						ContentProviderOperation.newInsert( uri )
							.withValues( programValues )
							.withYieldAllowed( true )
							.build()
					);
			}
			programCursor.close();
			count++;
			
			if( null != program.getChannelInfo() ) {
				
				if( null == mChannelDaoHelper.findByChannelId( context, locationProfile, (long) program.getChannelInfo().getChannelId() ) ) {
					Log.v( TAG, "load : adding non-existent channel" );

					mChannelDaoHelper.save( context, locationProfile, program.getChannelInfo() );
				}
				
			}
			
			if( !inError && null != program.getRecording() ) {
				
				String[] recordingProjection = new String[] { RecordingConstants.TABLE_NAME + "_" + RecordingConstants._ID };
				String recordingSelection = RecordingConstants.FIELD_RECORD_ID + " = ? AND " + RecordingConstants.FIELD_START_TIME + " = ? AND " + RecordingConstants.FIELD_MASTER_HOSTNAME + " = ?";
				String[] recordingSelectionArgs = new String[] { String.valueOf( program.getRecording().getRecordId() ), String.valueOf( program.getStartTime().getMillis() ), locationProfile.getHostname() };
				
				Log.v( TAG, "load : recording=" + program.getRecording().toString() );
				
				ContentValues recordingValues = mRecordingDaoHelper.convertRecordingToContentValues( locationProfile, program.getRecording(), program.getStartTime() );
				Cursor recordingCursor = context.getContentResolver().query( RecordingConstants.CONTENT_URI, recordingProjection, recordingSelection, recordingSelectionArgs, null );
				if( recordingCursor.moveToFirst() ) {
					Log.v( TAG, "load : UPDATE RECORDING program=" + program.getTitle() + ", recording=" + program.getRecording().getRecordId() );

					Long id = recordingCursor.getLong( recordingCursor.getColumnIndexOrThrow( RecordingConstants.TABLE_NAME + "_" + RecordingConstants._ID ) );					
					ops.add( 
						ContentProviderOperation.newUpdate( ContentUris.withAppendedId( RecordingConstants.CONTENT_URI, id ) )
						.withValues( recordingValues )
						.withYieldAllowed( true )
						.build()
					);
				} else {
					Log.v( TAG, "load : INSERT RECORDING program=" + program.getTitle() + ", recording=" + program.getRecording().getRecordId() );

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

			if( results.length > 0 ) {
				ops.clear();
			}
		}

		Log.v( TAG, "load : remove deleted recordings" );
		for( Program program : recorded.values() ) {
			Log.v( TAG, "load : remove deleted recording - " + program.getTitle() + " [" + program.getSubTitle() + "]" );
			
			// Delete any live stream details
			LiveStreamInfo liveStreamInfo = mLiveStreamDaoHelper.findByProgram( context, locationProfile, program );
			if( null != liveStreamInfo ) {
				Log.v( TAG, "load : remove live stream" );
				
				RemoveStreamTask removeStreamTask = new RemoveStreamTask();
				removeStreamTask.setContext( context );
				removeStreamTask.setLocationProfile( locationProfile );
				removeStreamTask.execute( liveStreamInfo );
				
				mLiveStreamDaoHelper.delete( context, locationProfile, liveStreamInfo );
			}
			
			if( null != program.getRecording() ) {
				Log.v( TAG, "load : remove recording" );
				
				ops.add(  
					ContentProviderOperation.newDelete( RecordingConstants.CONTENT_URI )
					.withSelection( RecordingConstants.FIELD_RECORD_ID + " = ? AND " + RecordingConstants.FIELD_START_TIME + " = ? AND " + RecordingConstants.FIELD_MASTER_HOSTNAME + " = ?", new String[] { String.valueOf( program.getRecording().getRecordId() ), String.valueOf( program.getStartTime().getMillis() ), locationProfile.getHostname() } )
					.withYieldAllowed( true )
					.build()
				);

			}
			
			Log.v( TAG, "load : remove program" );
			ops.add(  
				ContentProviderOperation.newDelete( uri )
				.withSelection( ProgramConstants.FIELD_FILENAME + " = ?", new String[] { program.getFilename() } )
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
	 * @param programs
	 * @return
	 */
	public abstract int load( final Context context, final LocationProfile locationProfile, List<Program> programs ) throws RemoteException, OperationApplicationException;
	
	/**
	 * @param cursor
	 * @return
	 */
	public Program convertCursorToProgram( Cursor cursor ) {
//		Log.v( TAG, "convertCursorToProgram : enter" );

//		Long id = null;
		DateTime startTime = null, endTime = null, lastModified = null, airDate = null;
		String title = "", subTitle = "", category = "", categoryType = "", seriesId = "", programId = "", fileSize = "", programFlags = "", hostname = "", filename = "", description = "", inetref = "", season = "", episode = "", masterHostname = "";
		int repeat = -1, videoProps = -1, audioProps = -1, subProps = -1;
		float stars = 0.0f;
		
		ChannelInfo channelInfo = null;
		Recording recording = null;
		LiveStreamInfo liveStreamInfo = null;
		
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
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_MASTER_HOSTNAME ) != -1 ) {
			masterHostname = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_MASTER_HOSTNAME ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_CHANNEL_ID ) != -1 ) {
			channelInfo = mChannelDaoHelper.convertCursorToChannelInfo( cursor );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_RECORD_ID ) != -1 ) {
			recording = mRecordingDaoHelper.convertCursorToRecording( cursor );
		}
		
		if( cursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "_" + LiveStreamConstants.FIELD_ID ) != -1 ) {
			liveStreamInfo = mLiveStreamDaoHelper.convertCursorToLiveStreamInfo( cursor );
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
		program.setRecording( recording );
//		program.set;
//		program.set;
		
//		Log.v( TAG, "convertCursorToProgram : id=" + id + ", program=" + program.toString() );

//		Log.v( TAG, "convertCursorToProgram : exit" );
		return program;
	}

	protected ContentValues[] convertProgramsToContentValuesArray( final LocationProfile locationProfile, final List<Program> programs ) {
//		Log.v( TAG, "convertProgramsToContentValuesArray : enter" );
		
		if( null != programs && !programs.isEmpty() ) {
			
			ContentValues contentValues;
			List<ContentValues> contentValuesArray = new ArrayList<ContentValues>();

			for( Program program : programs ) {

				contentValues = convertProgramToContentValues( locationProfile, program );
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

	protected ContentValues convertProgramToContentValues( final LocationProfile locationProfile, final Program program ) {
		
		boolean inError;
		
		DateTime startTime = new DateTime( 0 );
		DateTime endTime = new DateTime( 0 );

		// If one timestamp is bad, leave them both set to 0.
		if( null == program.getStartTime() || null == program.getEndTime() ) {
			Log.w(TAG, "convertProgramToContentValues : null starttime and or endtime" );
		
			inError = true;
		} else {
			startTime = new DateTime( program.getStartTime().getMillis() );
			endTime = new DateTime( program.getEndTime().getMillis() );
			
			inError = false;
		}

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
		values.put( ProgramConstants.FIELD_IN_ERROR, inError ? 1 : 0 );
		values.put( ProgramConstants.FIELD_MASTER_HOSTNAME, locationProfile.getHostname() );
		
		return values;
	}

	private class RemoveStreamTask extends AsyncTask<LiveStreamInfo, Void, ResponseEntity<Bool>> {

		private Context mContext;
		private LocationProfile mLocationProfile;
		
		private Exception e = null;

		@Override
		protected ResponseEntity<Bool> doInBackground( LiveStreamInfo... params ) {
			Log.v( TAG, "RemoveStreamTask : enter" );

			if( null == mContext ) 
				throw new RuntimeException( "RemoveStreamTask not initalized!" );
			
			try {
				Log.v( TAG, "RemoveStreamTask : api" );
				
				LiveStreamInfo liveStreamInfo = params[ 0 ];
				
				if( null != liveStreamInfo ) {
					return mMythtvServiceHelper.getMythServicesApi( mLocationProfile ).contentOperations().removeLiveStream( liveStreamInfo.getId() );
				}
				
			} catch( Exception e ) {
				Log.v( TAG, "RemoveStreamTask : error" );

				this.e = e;
			}

			Log.v( TAG, "RemoveStreamTask : exit" );
			return null;
		}

		@Override
		protected void onPostExecute( ResponseEntity<Bool> result ) {
			Log.v( TAG, "RemoveStreamTask onPostExecute : enter" );

			if( null == e ) {
				Log.v( TAG, "RemoveStreamTask onPostExecute : no error occurred" );
				
				if( null != result ) {
					
					if( result.getBody().getBool().booleanValue() ) {
						Log.v( TAG, "RemoveStreamTask onPostExecute : live stream removed" );
					}
					
				}
			} else {
				Log.e( TAG, "error removing live stream", e );
			}

			Log.v( TAG, "RemoveStreamTask onPostExecute : exit" );
		}
		
		public void setContext( Context context ) {
			this.mContext = context;
		}
		
		public void setLocationProfile( LocationProfile locationProfile ) {
			this.mLocationProfile = locationProfile;
		}
		
	}

}
