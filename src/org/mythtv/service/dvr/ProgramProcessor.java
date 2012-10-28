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
package org.mythtv.service.dvr;

import org.joda.time.DateTime;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.service.AbstractMythtvProcessor;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.utils.ArticleCleaner;

import android.content.ContentValues;
import android.content.Context;

/**
 * @author Daniel Frey
 *
 */
public abstract class ProgramProcessor extends AbstractMythtvProcessor {

	protected static final String TAG = ProgramProcessor.class.getSimpleName();

	public ProgramProcessor( Context context ) {
		super( context );
	}

	protected ContentValues convertProgramToContentValues( final Program program ) {
		
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
		values.put( ProgramConstants.FIELD_CHANNEL_ID, null != program.getChannelInfo() ? program.getChannelInfo().getChannelId() : "" );
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
