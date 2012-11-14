/**
 * 
 */
package org.mythtv.db.dvr;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.service.util.DateUtils;
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
public abstract class ProgramDaoHelper {

	private static final String TAG = ProgramDaoHelper.class.getSimpleName();
	
	private Context mContext;
	
	protected ProgramDaoHelper( Context context ) {
		this.mContext = context;
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
	public abstract List<Program> finalAll();
	
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
	public abstract Program findOne( Long channelId, DateTime startTime );

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
	 * @param programs
	 * @param uri
	 * @return
	 */
	protected int load( Uri uri, List<Program> programs ) {
		Log.v( TAG, "load : enter" );
		
		int loaded = -1;
		
		ContentValues[] contentValuesArray = convertProgramsToContentValuesArray( programs );
		if( null != contentValuesArray ) {
			Log.v( TAG, "processPrograms : programs=" + contentValuesArray.length );

			loaded = mContext.getContentResolver().bulkInsert( uri, contentValuesArray );
			Log.v( TAG, "load : loaded=" + loaded );
		}
		
		
		Log.v( TAG, "load : exit" );
		return loaded;
	}
	
	/**
	 * @param programs
	 * @return
	 */
	public abstract int load( List<Program> programs );
	
	/**
	 * @param cursor
	 * @return
	 */
	public Program convertCursorToProgram( Cursor cursor ) {
		Log.v( TAG, "convertCursorToProgram : enter" );

		DateTime startTime = null, endTime = null, lastModified = null, airDate = null;
		String title = "", subTitle = "", category = "", categoryType = "", seriesId = "", programId = "", fileSize = "", programFlags = "", hostname = "", filename = "", description = "", inetref = "", season = "", episode = "";
		int repeat = -1, videoProps = -1, audioProps = -1, subProps = -1;
		float stars = 0.0f;
		
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
		
//		if( cursor.getColumnIndex( ProgramConstants.FIELD_ ) != -1 ) {
//			xmltvId = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_ ) );
//		}
//		
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
//		program.set;
//		program.set;
//		program.set;
		
		Log.v( TAG, "convertCursorToProgram : exit" );
		return program;
	}

	// internal helpers

	private ContentValues[] convertProgramsToContentValuesArray( final List<Program> programs ) {
		Log.v( TAG, "convertProgramsToContentValuesArray : enter" );
		
		if( null != programs && !programs.isEmpty() ) {
			
			ContentValues contentValues;
			List<ContentValues> contentValuesArray = new ArrayList<ContentValues>();

			for( Program program : programs ) {

				contentValues = convertProgramToContentValues( program );
				contentValuesArray.add( contentValues );
				
			}			
			
			if( !contentValuesArray.isEmpty() ) {
				
				Log.v( TAG, "convertProgramsToContentValuesArray : exit" );
				return contentValuesArray.toArray( new ContentValues[ contentValuesArray.size() ] );
			}
			
		}
		
		Log.v( TAG, "convertProgramsToContentValuesArray : exit, no programs to convert" );
		return null;
	}

	private ContentValues convertProgramToContentValues( final Program program ) {
		
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
		
		return values;
	}

}
