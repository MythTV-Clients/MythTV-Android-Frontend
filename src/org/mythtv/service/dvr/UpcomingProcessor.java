/**
 * 
 */
package org.mythtv.service.dvr;

import org.joda.time.DateTime;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.service.AbstractMythtvProcessor;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.Programs;
import org.mythtv.services.utils.ArticleCleaner;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class UpcomingProcessor extends AbstractMythtvProcessor {

	protected static final String TAG = UpcomingProcessor.class.getSimpleName();

	public UpcomingProcessor( Context context ) {
		super( context );
		Log.v( TAG, "initialize : enter" );

		Log.v( TAG, "initialize : exit" );
	}

	public int processPrograms( Programs programs ) {
		Log.v( TAG, "processPrograms : enter" );

		int result = 0;
		
		if( null != programs ) {
			
			// add delete here
			int deleted = mContext.getContentResolver().delete( ProgramConstants.CONTENT_URI_UPCOMING, null, null );
			Log.v( TAG, "processPrograms : programs deleted=" + deleted );
			
			ContentValues[] contentValuesArray = convertProgramsToContentValuesArray( programs );
			result = mContext.getContentResolver().bulkInsert( ProgramConstants.CONTENT_URI_UPCOMING, contentValuesArray );
			Log.v( TAG, "processPrograms : programs added=" + result );
			
//			Cursor cursor = mContext.getContentResolver().query( ProgramConstants.CONTENT_URI_UPCOMING, null, null, null, ProgramConstants.FIELD_START_TIME );
//			while( cursor.moveToNext() ) {
//				long lStartTime = cursor.getLong( cursor.getColumnIndex( ProgramConstants.FIELD_START_TIME ) );
//				int iDuration = cursor.getInt( cursor.getColumnIndex( ProgramConstants.FIELD_DURATION ) );
//				String sTitle = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_TITLE ) );
//				String sStartDate = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_START_DATE ) );
//				String sSubTitle = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_SUB_TITLE ) );
//				String sCategory = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_CATEGORY ) );
//				String sChannelNumber = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_CHANNEL_NUMBER ) );
//				
//				Log.v( TAG, "processPrograms : Title=" + sTitle + ", channel=" + sChannelNumber + ", startDate=" + sStartDate + ", startTime=" + DateUtils.dateTimeFormatter.print( new DateTime( lStartTime ) ) );
//			}
			
		}
		
		Log.v( TAG, "processPrograms : exit" );
		return result;
	}
	
	// internal helpers
	
	private ContentValues[] convertProgramsToContentValuesArray( final Programs programs ) {
		
		if( null != programs ) {
			
			int i = 0;
			ContentValues contentValues;
			ContentValues[] contentValuesArray = new ContentValues[ programs.getPrograms().size() ];
			for( Program program : programs.getPrograms() ) {
				
				contentValues = convertProgramToContentValues( program );
				contentValuesArray[ i ] = contentValues;
				
				i++;
			}
			
			return contentValuesArray;
		}
		
		return null;
	}
	
	private ContentValues convertProgramToContentValues( final Program program ) {
		
		long durationInMinutes = ( program.getEndTime().getMillis() / 60000 ) - ( program.getStartTime().getMillis() / 60000 );

		// Removing Grammar Articles.  English only at this time, needs internationalization
		String cleanTitle = ArticleCleaner.clean( program.getTitle() );

		DateTime startTime = new DateTime( program.getStartTime().getMillis() );
		DateTime endTime = new DateTime( program.getEndTime().getMillis() );
		
		ContentValues values = new ContentValues();
		values.put( ProgramConstants.FIELD_PROGRAM_GROUP, cleanTitle );
		values.put( ProgramConstants.FIELD_START_TIME, startTime.getMillis() );
		values.put( ProgramConstants.FIELD_END_TIME, endTime.getMillis() );
		values.put( ProgramConstants.FIELD_DURATION, durationInMinutes );
		values.put( ProgramConstants.FIELD_START_DATE, DateUtils.dateFormatter.print( startTime ) );
		values.put( ProgramConstants.FIELD_TIMESLOT_HOUR, startTime.getHourOfDay() );
		values.put( ProgramConstants.FIELD_TIMESLOT_MINUTE, startTime.getMinuteOfHour() );
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
		values.put( ProgramConstants.FIELD_CHANNEL_NUMBER, null != program.getChannelInfo() ? program.getChannelInfo().getChannelNumber() : "" );
		values.put( ProgramConstants.FIELD_STATUS, null != program.getChannelInfo() ? program.getRecording().getStatus() : -1 );
		values.put( ProgramConstants.FIELD_PRIORITY, null != program.getChannelInfo() ? program.getRecording().getStatus() : -1 );
		values.put( ProgramConstants.FIELD_START_TS, null != program.getChannelInfo() ? program.getRecording().getStartTimestamp().getMillis() : -1 );
		values.put( ProgramConstants.FIELD_END_TS, null != program.getChannelInfo() ? program.getRecording().getEndTimestamp().getMillis() : -1 );
		values.put( ProgramConstants.FIELD_RECORD_ID, null != program.getChannelInfo() ? program.getRecording().getRecordid() : -1 );
		values.put( ProgramConstants.FIELD_REC_GROUP, null != program.getChannelInfo() ? program.getRecording().getRecordingGroup() : "" );
		values.put( ProgramConstants.FIELD_PLAY_GROUP, null != program.getChannelInfo() ? program.getRecording().getPlayGroup() : "" );
		values.put( ProgramConstants.FIELD_STORAGE_GROUP, null != program.getChannelInfo() ? program.getRecording().getStorageGroup() : "" );
		values.put( ProgramConstants.FIELD_REC_TYPE, null != program.getChannelInfo() ? program.getRecording().getRecordingType() : -1 );
		values.put( ProgramConstants.FIELD_DUP_IN_TYPE, null != program.getChannelInfo() ? program.getRecording().getDuplicateInType() : -1 );
		values.put( ProgramConstants.FIELD_DUP_METHOD, null != program.getChannelInfo() ? program.getRecording().getDuplicateMethod() : -1 );
		values.put( ProgramConstants.FIELD_ENCODER_ID, null != program.getChannelInfo() ? program.getRecording().getEncoderId() : -1 );
		values.put( ProgramConstants.FIELD_PROFILE, null != program.getChannelInfo() ? program.getRecording().getProfile() : "" );
		return values;
	}
}
