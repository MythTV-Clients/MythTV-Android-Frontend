/**
 * 
 */
package org.mythtv.service.dvr.v27;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.dvr.RecordingConstants;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.service.channel.v27.ChannelHelperV27;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.Bool;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v027.MythServicesTemplate;
import org.mythtv.services.api.v027.beans.ChannelInfo;
import org.mythtv.services.api.v027.beans.Program;
import org.mythtv.services.api.v027.beans.RecordingInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.ContentProviderOperation;
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
public class ProgramHelperV27 extends AbstractBaseHelper {

	private static final String TAG = ProgramHelperV27.class.getSimpleName();

	private static final ApiVersion mApiVersion = ApiVersion.v027;

	private static final String[] programProjection = new String[] { ProgramConstants._ID };

	private static ProgramHelperV27 singleton;
	
	/**
	 * Returns the one and only ProgramHelperV27. init() must be called before 
	 * any 
	 * @return
	 */
	public static ProgramHelperV27 getInstance() {
		if( null == singleton ) {
			
			synchronized( ProgramHelperV27.class ) {

				if( null == singleton ) {
					singleton = new ProgramHelperV27();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private ProgramHelperV27() { }

	public void processProgram( final Context context, final LocationProfile locationProfile, Uri uri, String table, ArrayList<ContentProviderOperation> ops, Program program, DateTime lastModified, DateTime startTime, int count ) {
//		Log.d( TAG, "processProgram : enter" );
		
		String programSelection = table + "." + ProgramConstants.FIELD_CHANNEL_ID + " = ? AND " + table + "." + ProgramConstants.FIELD_START_TIME + " = ?";
		String[] programSelectionArgs = new String[] { String.valueOf( program.getChannel().getChanId() ), String.valueOf( startTime.getMillis() ) };
		
		programSelection = appendLocationHostname( context, locationProfile, programSelection, table );

		ContentValues programValues = convertProgramToContentValues( locationProfile, lastModified, program );
		Cursor programCursor = context.getContentResolver().query( uri, programProjection, programSelection, programSelectionArgs, null );
		if( programCursor.moveToFirst() ) {
//			Log.v( TAG, "processProgram : UPDATE PROGRAM " + count + ":" + program.getChannelInfo().getChannelId() + ":" + program.getStartTime() + ":" + program.getHostname() );

			Long id = programCursor.getLong( programCursor.getColumnIndexOrThrow( ProgramConstants._ID ) );
			ops.add( 
				ContentProviderOperation.newUpdate( ContentUris.withAppendedId( uri, id ) )
					.withValues( programValues )
					.withYieldAllowed( true )
					.build()
			);
			
		} else {
//			Log.v( TAG, "processProgram : INSERT PROGRAM " + count + ":" + program.getChannelInfo().getChannelId() + ":" + program.getStartTime() + ":" + program.getHostname() );

			ops.add(
				ContentProviderOperation.newInsert( uri )
					.withValues( programValues )
					.withYieldAllowed( true )
					.build()
			);
			
		}
		programCursor.close();
		count++;

//		Log.d( TAG, "processProgram : exit" );
	}
	
	public Program findProgram( final Context context, final LocationProfile locationProfile, Uri uri, String table, Integer channelId, DateTime startTime ) {
		Log.d( TAG, "findProgram : enter" );
		
		String programSelection = table + "." + ProgramConstants.FIELD_CHANNEL_ID + " = ? AND " + table + "." + ProgramConstants.FIELD_START_TIME + " = ?";
		String[] programSelectionArgs = new String[] { String.valueOf( channelId ), String.valueOf( startTime.getMillis() ) };
		
		programSelection = appendLocationHostname( context, locationProfile, programSelection, table );
		
		Program program = null;
		
		Cursor programCursor = context.getContentResolver().query( uri, null, programSelection, programSelectionArgs, null );
		if( programCursor.moveToFirst() ) {
//			Log.v( TAG, "findProgram : program=" + program.toString() );

			program = convertCursorToProgram( programCursor, table );
		}
		programCursor.close();

		Log.d( TAG, "findProgram : exit" );
		return program;
	}
	
	public void deletePrograms( final Context context, final LocationProfile locationProfile, ArrayList<ContentProviderOperation> ops, Uri uri, String table, DateTime today ) {
		Log.v( TAG, "deletePrograms : enter" );
		
		String selection = table + "." + ProgramConstants.FIELD_LAST_MODIFIED_DATE + " < ?";
		String[] selectionArgs = new String[] { String.valueOf( today.getMillis() ) };
		
		selection = appendLocationHostname( context, locationProfile, selection, table );
		
		ops.add(  
			ContentProviderOperation.newDelete( uri )
				.withSelection( selection, selectionArgs )
				.withYieldAllowed( true )
				.build()
		);

		Log.v( TAG, "deletePrograms : exit" );
	}
	
	public boolean deleteProgram( final Context context, final LocationProfile locationProfile, Uri uri, String table, Integer channelId, DateTime startTime, Integer recordId ) {
		Log.d( TAG, "deleteProgram : enter" );
		
		if( !MythAccessFactory.isServerReachable( locationProfile.getUrl() ) ) {
			Log.w( TAG, "deleteProgram : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}
		
		MythServicesTemplate mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );

		ResponseEntity<Bool> response = mMythServicesTemplate.dvrOperations().removeRecorded( channelId, startTime, EtagInfoDelegate.createEmptyETag() );
		if( null != response ) {
			
			if( response.getStatusCode().equals( HttpStatus.OK ) ) {
				
				boolean removed = response.getBody().getValue();
				if( removed ) {

					String programSelection = ProgramConstants.FIELD_CHANNEL_ID + " = ? AND " + ProgramConstants.FIELD_START_TIME + " = ?";
					String[] programSelectionArgs = new String[] { String.valueOf( channelId ), String.valueOf( startTime ) };

					programSelection = appendLocationHostname( context, locationProfile, programSelection, null );

					int deleted = context.getContentResolver().delete( uri, programSelection, programSelectionArgs );
					if( deleted == 1 ) {
						RecordingHelperV27.getInstance().deleteRecording( context, locationProfile, uri, table, recordId, startTime );
					}
					
				}
				
				Log.d( TAG, "deleteProgram : exit" );
				return removed;
			}
			
		}
		
		Log.d( TAG, "deleteProgram : exit" );
		return false;
	}

	private ContentValues convertProgramToContentValues( final LocationProfile locationProfile, final DateTime lastModified, final Program program ) {
//		Log.v( TAG, "convertProgramToContentValues : enter" );
		
		boolean inError;
		
		DateTime startTime = new DateTime( DateTimeZone.UTC );
		DateTime endTime = new DateTime( DateTimeZone.UTC );

		// If one timestamp is bad, leave them both set to 0.
		if( null == program.getStartTime() || null == program.getEndTime() ) {
//			Log.w(TAG, "convertProgramToContentValues : null starttime and or endtime" );
		
			inError = true;
		} else {
			startTime = program.getStartTime();
			endTime = program.getEndTime();
			
			inError = false;
		}

		ContentValues values = new ContentValues();
		values.put( ProgramConstants.FIELD_START_TIME, startTime.getMillis() );
		values.put( ProgramConstants.FIELD_END_TIME, endTime.getMillis() );
		values.put( ProgramConstants.FIELD_TITLE, null != program.getTitle() ? program.getTitle() : "" );
		values.put( ProgramConstants.FIELD_SUB_TITLE, null != program.getSubTitle() ? program.getSubTitle() : "" );
		values.put( ProgramConstants.FIELD_CATEGORY, null != program.getCategory() ? program.getCategory() : "" );
		values.put( ProgramConstants.FIELD_CATEGORY_TYPE, null != program.getCatType() ? program.getCatType() : "" );
		values.put( ProgramConstants.FIELD_REPEAT, program.isRepeat() ? 1 : 0 );
		values.put( ProgramConstants.FIELD_VIDEO_PROPS, program.getVideoProps() );
		values.put( ProgramConstants.FIELD_AUDIO_PROPS, program.getAudioProps() );
		values.put( ProgramConstants.FIELD_SUB_PROPS, program.getSubProps() );
		values.put( ProgramConstants.FIELD_SERIES_ID, null != program.getSeriesId() ? program.getSeriesId() : "" );
		values.put( ProgramConstants.FIELD_PROGRAM_ID, null != program.getProgramId() ? program.getProgramId() : "" );
		values.put( ProgramConstants.FIELD_STARS, program.getStars() );
		values.put( ProgramConstants.FIELD_FILE_SIZE, null != program.getFileSize() ? program.getFileSize() : -1 );
		values.put( ProgramConstants.FIELD_LAST_MODIFIED, null != program.getLastModified() ? DateUtils.dateTimeFormatter.print( program.getLastModified() ) : "" );
		values.put( ProgramConstants.FIELD_PROGRAM_FLAGS, null != program.getProgramFlags() ? program.getProgramFlags() : -1 );
		values.put( ProgramConstants.FIELD_HOSTNAME, null != program.getHostName() ? program.getHostName() : "" );
		values.put( ProgramConstants.FIELD_FILENAME, null != program.getFileName() ? program.getFileName() : "" );
		values.put( ProgramConstants.FIELD_AIR_DATE, null != program.getAirdate() ? DateUtils.dateTimeFormatter.print( program.getAirdate() ) : "" );
		values.put( ProgramConstants.FIELD_DESCRIPTION, null != program.getDescription() ? program.getDescription() : "" );
		values.put( ProgramConstants.FIELD_INETREF, null != program.getInetref() ? program.getInetref() : "" );
		values.put( ProgramConstants.FIELD_SEASON, null != program.getSeason() ? program.getSeason() : -1 );
		values.put( ProgramConstants.FIELD_EPISODE, null != program.getEpisode() ? program.getEpisode() : -1 );
		values.put( ProgramConstants.FIELD_CHANNEL_ID, null != program.getChannel() ? program.getChannel().getChanId() : -1 );
		values.put( ProgramConstants.FIELD_RECORD_ID, null != program.getRecording() ? program.getRecording().getRecordId() : -1 );
		values.put( ProgramConstants.FIELD_IN_ERROR, inError ? 1 : 0 );
		values.put( ProgramConstants.FIELD_MASTER_HOSTNAME, locationProfile.getHostname() );
		values.put( ProgramConstants.FIELD_LAST_MODIFIED_DATE, lastModified.getMillis() );
		
//		Log.v( TAG, "convertProgramToContentValues : exit" );
		return values;
	}

	private Program convertCursorToProgram( Cursor cursor, final String table ) {
//		Log.v( TAG, "convertCursorToProgram : enter" );

		DateTime startTime = null, endTime = null, lastModified = null, airDate = null;
		String title = "", subTitle = "", category = "", categoryType = "", seriesId = "", programId = "", hostname = "", filename = "", description = "", inetref = "";
		int repeat = -1, videoProps = -1, audioProps = -1, subProps = -1, programFlags = -1, season = -1, episode = -1;
		long fileSize = -1;
		double stars = 0.0f;
		
		ChannelInfo channelInfo = null;
		RecordingInfo recording = null;
		
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
			stars = cursor.getDouble( cursor.getColumnIndex( ProgramConstants.FIELD_STARS ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_FILE_SIZE ) != -1 ) {
			fileSize = cursor.getLong( cursor.getColumnIndex( ProgramConstants.FIELD_FILE_SIZE ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_LAST_MODIFIED ) != -1 ) {
			lastModified = new DateTime( cursor.getLong( cursor.getColumnIndex( ProgramConstants.FIELD_LAST_MODIFIED ) ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_PROGRAM_FLAGS ) != -1 ) {
			programFlags = cursor.getInt( cursor.getColumnIndex( ProgramConstants.FIELD_PROGRAM_FLAGS ) );
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
			season = cursor.getInt( cursor.getColumnIndex( ProgramConstants.FIELD_SEASON ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_EPISODE ) != -1 ) {
			episode = cursor.getInt( cursor.getColumnIndex( ProgramConstants.FIELD_EPISODE ) );
		}
		
		if( cursor.getColumnIndex( ProgramConstants.FIELD_CHANNEL_ID ) != -1 ) {
			channelInfo = ChannelHelperV27.getInstance().convertCursorToChannelInfo( cursor );
		}

		if( cursor.getColumnIndex( RecordingConstants.ContentDetails.getValueFromParent( table ).getTableName() + "_" + RecordingConstants.FIELD_RECORD_ID ) != -1 ) {
			recording = RecordingHelperV27.getInstance().convertCursorToRecording( cursor, table );
		}
		
		Program program = new Program();
		program.setStartTime( startTime );
		program.setEndTime( endTime );
		program.setTitle( title );
		program.setSubTitle( subTitle );
		program.setCategory( category );
		program.setCatType( categoryType );
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
		program.setHostName( hostname );
		program.setFileName( filename );
		program.setAirdate( null != airDate ? airDate.toLocalDate() : null );
		program.setDescription( description );
		program.setInetref( inetref );
		program.setSeason( season );
		program.setEpisode( episode );
		program.setChannel( channelInfo );
		program.setRecording( recording );
		
//		Log.v( TAG, "convertCursorToProgram : exit" );
		return program;
	}

}
