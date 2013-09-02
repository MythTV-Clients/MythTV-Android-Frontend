/**
 * 
 */
package org.mythtv.service.dvr.v26;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v026.Bool;
import org.mythtv.services.api.v026.MythServicesTemplate;
import org.mythtv.services.api.v026.beans.Program;
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
public class ProgramHelperV26 extends AbstractBaseHelper {

	private static final String TAG = ProgramHelperV26.class.getSimpleName();

	private static final ApiVersion mApiVersion = ApiVersion.v026;

	private static final String[] programProjection = new String[] { ProgramConstants._ID };
	
	public static void processProgram( final Context context, final LocationProfile locationProfile, Uri uri, String table, ArrayList<ContentProviderOperation> ops, Program program, DateTime lastModified, DateTime startTime, int count ) {
		Log.d( TAG, "processProgram : enter" );
		
		String programSelection = table + "." + ProgramConstants.FIELD_CHANNEL_ID + " = ? AND " + table + "." + ProgramConstants.FIELD_START_TIME + " = ?";
		programSelection = appendLocationHostname( context, locationProfile, programSelection, ProgramConstants.TABLE_NAME_RECORDED );

		ContentValues programValues = convertProgramToContentValues( locationProfile, lastModified, program );
		Cursor programCursor = context.getContentResolver().query( uri, programProjection, programSelection, new String[] { String.valueOf( program.getChannelInfo().getChannelId() ), String.valueOf( startTime.getMillis() ) }, null );
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

		Log.d( TAG, "processProgram : exit" );
	}
	
	public static void deletePrograms( final Context context, final LocationProfile locationProfile, ArrayList<ContentProviderOperation> ops, Uri uri, String table, DateTime today ) {
		Log.d( TAG, "deletePrograms : enter" );
		
		String selection = table + "." + ProgramConstants.FIELD_LAST_MODIFIED_DATE + " < ?";
		String[] selectionArgs = new String[] { String.valueOf( today.getMillis() ) };
		
		selection = appendLocationHostname( context, locationProfile, selection, table );
		
		ops.add(  
			ContentProviderOperation.newDelete( uri )
				.withSelection( selection, selectionArgs )
				.withYieldAllowed( true )
				.build()
		);

		Log.d( TAG, "deletePrograms : exit" );
	}
	
	public static boolean deleteProgram( final Context context, final LocationProfile locationProfile, Uri uri, String table, Integer channelId, DateTime startTime ) {
		Log.d( TAG, "deleteProgram : enter" );
		
		if( !MythAccessFactory.isServerReachable( locationProfile.getUrl() ) ) {
			Log.w( TAG, "deleteProgram : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}
		
		MythServicesTemplate mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );

		ResponseEntity<Bool> response = mMythServicesTemplate.dvrOperations().removeRecorded( channelId, startTime );
		if( null != response ) {
			
			if( response.getStatusCode().equals( HttpStatus.OK ) ) {
				
				boolean removed = response.getBody().getBool();
				if( removed ) {
					// TODO: add remove from local db here
				}
				
				Log.d( TAG, "deleteProgram : exit" );
				return removed;
			}
			
		}
		
		Log.d( TAG, "deleteProgram : exit" );
		return false;
	}

	public static ContentValues convertProgramToContentValues( final LocationProfile locationProfile, final DateTime lastModified, final Program program ) {
		Log.v( TAG, "convertProgramToContentValues : enter" );
		
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
		values.put( ProgramConstants.FIELD_LAST_MODIFIED_DATE, lastModified.getMillis() );
		
		Log.v( TAG, "convertProgramToContentValues : exit" );
		return values;
	}

}
