/**
 *  This file is part of MythTV for Android
 * 
 *  MythTV for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MythTV for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MythTV for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * This software can be found at <https://github.com/MythTV-Android/mythtv-for-android/>
 *
 */
package org.mythtv.service.dvr;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.service.AbstractMythtvProcessor;
import org.mythtv.service.channel.ChannelProcessor;
import org.mythtv.services.api.dvr.Program;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class ProgramProcessor extends AbstractMythtvProcessor {

	protected static final String TAG = ProgramProcessor.class.getSimpleName();

	private ChannelProcessor channelProcessor;
	private RecordingProcessor recordingProcessor;
	
	public ProgramProcessor( Context context ) {
		super( context );
		Log.v( TAG, "initialize : enter" );

		channelProcessor = new ChannelProcessor( context );
		recordingProcessor = new RecordingProcessor( context );
		
		Log.v( TAG, "initialize : exit" );
	}

	public Long updateProgramContentProvider( Program program, Long programGroupId, ProgramConstants.ProgramType programType ) {
		Log.v( TAG, "updateProgramContentProvider : enter" );

		Long channelId = channelProcessor.updateChannelContentProvider( program.getChannelInfo() );
		
		ContentValues values = new ContentValues();
		values.put( ProgramConstants.FIELD_PROGRAM_TYPE, null != programType ? programType.name() : "" );
		values.put( ProgramConstants.FIELD_START_TIME, null != program.getStartTime() ? sdf.format( program.getStartTime() ) : "" );
		values.put( ProgramConstants.FIELD_END_TIME, null != program.getEndTime() ? sdf.format( program.getEndTime() ) : "" );
		values.put( ProgramConstants.FIELD_TITLE, null != program.getTitle() ? program.getTitle() : "" );
		values.put( ProgramConstants.FIELD_SUB_TITLE, null != program.getSubTitle() ? program.getSubTitle() : "" );
		values.put( ProgramConstants.FIELD_CATEGORY, null != program.getCategory() ? program.getCategory() : "" );
		values.put( ProgramConstants.FIELD_CATEGORY_TYPE, null != program.getCategoryType() ? program.getCategoryType() : "" );
		values.put( ProgramConstants.FIELD_REPEAT, program.isRepeat() );
		values.put( ProgramConstants.FIELD_VIDEO_PROPS, program.getVideoProps() );
		values.put( ProgramConstants.FIELD_AUDIO_PROPS, program.getAudioProps() );
		values.put( ProgramConstants.FIELD_SUB_PROPS, program.getSubProps() );
		values.put( ProgramConstants.FIELD_SERIES_ID, null != program.getSeriesId() ? program.getSeriesId() : "" );
		values.put( ProgramConstants.FIELD_PROGRAM_ID, null != program.getProgramId() ? program.getProgramId() : "" );
		values.put( ProgramConstants.FIELD_STARS, program.getStars() );
		values.put( ProgramConstants.FIELD_FILE_SIZE, null != program.getFileSize() ? program.getFileSize() : "" );
		values.put( ProgramConstants.FIELD_LAST_MODIFIED, null != program.getLastModified() ? sdf.format( program.getLastModified() ) : "" );
		values.put( ProgramConstants.FIELD_PROGRAM_FLAGS, null != program.getProgramFlags() ? program.getProgramFlags() : "" );
		values.put( ProgramConstants.FIELD_HOSTNAME, null != program.getHostname() ? program.getHostname() : "" );
		values.put( ProgramConstants.FIELD_FILENAME, null != program.getFilename() ? program.getFilename() : "" );
		values.put( ProgramConstants.FIELD_AIR_DATE, null != program.getAirDate() ? sdf.format( program.getAirDate() ) : "" );
		values.put( ProgramConstants.FIELD_DESCRIPTION, null != program.getDescription() ? program.getDescription() : "" );
		values.put( ProgramConstants.FIELD_INETREF, null != program.getInetref() ? program.getInetref() : "" );
		values.put( ProgramConstants.FIELD_SEASON, null != program.getSeason() ? program.getSeason() : "" );
		values.put( ProgramConstants.FIELD_EPISODE, null != program.getEpisode() ? program.getEpisode() : "" );
		values.put( ProgramConstants.FIELD_PROGRAM_GROUP_ID, programGroupId );
		values.put( ProgramConstants.FIELD_CHANNEL_ID, channelId );
		
		Long programId = null;
		Cursor cursor = mContext.getContentResolver().query( ProgramConstants.CONTENT_URI,  new String[] { BaseColumns._ID }, ProgramConstants.FIELD_START_TIME + " = ? and " + ProgramConstants.FIELD_TITLE + " = ? and " + ProgramConstants.FIELD_SUB_TITLE + " = ? and " + ProgramConstants.FIELD_PROGRAM_ID + " = ? and " + ProgramConstants.FIELD_PROGRAM_TYPE + " = ?", new String[] { sdf.format( program.getStartTime() ), program.getTitle(), program.getSubTitle(), program.getProgramId(), programType.name() }, null );
		if( cursor.moveToFirst() ) {
			programId = cursor.getLong( cursor.getColumnIndexOrThrow( BaseColumns._ID ) );
			mContext.getContentResolver().update( ContentUris.withAppendedId( ProgramConstants.CONTENT_URI, programId ), values, null, null );
		} else {
			Uri programUri = mContext.getContentResolver().insert( ProgramConstants.CONTENT_URI, values );
			programId = ContentUris.parseId( programUri );
		}
		cursor.close();
		
		recordingProcessor.updateRecordingContentProvider( program.getRecording(), programId );
		
		Log.v( TAG, "updateProgramContentProvider : exit" );
		return programId;
	}

	public int removeDeletedPrograms( List<Long> programIds, ProgramConstants.ProgramType programType ) {
		Log.v( TAG, "removeDeletedPrograms : enter" );
	
		int count = 0;

		if( !programIds.isEmpty() ) {
			Log.v( TAG, "removeDeletedPrograms : looking up programs to remove" );

			StringBuilder sb = new StringBuilder();
			for( int i = 0; i < programIds.size(); i++ ) {
				sb.append( programIds.get( i ) );
				
				if( i < programIds.size() - 1 ) {
					sb.append( "," );
				}
			}
			if( Log.isLoggable( TAG, Log.VERBOSE ) ) {
				Log.v( TAG, "removeDeletedPrograms : existing program ids=" + sb.toString() );
			}
			
			List<Long> deleteIds = new ArrayList<Long>();
			Cursor cursor = mContext.getContentResolver().query( ProgramConstants.CONTENT_URI, new String[] { BaseColumns._ID }, BaseColumns._ID + " not in (" + sb.toString() + ") and " + ProgramConstants.FIELD_PROGRAM_TYPE + " = ?", new String[] { programType.name() }, null );
			while( cursor.moveToNext() ) {
				Long id = cursor.getLong( cursor.getColumnIndexOrThrow( BaseColumns._ID ) );
				deleteIds.add( id );

				Log.v( TAG, "removeDeletedPrograms : queing for deletion, id=" + id );
			}
			cursor.close();
			Log.v( TAG, "removeDeletedPrograms : " + programType.name() + ", delete count=" + deleteIds.size() );
			
			if( !deleteIds.isEmpty() ) {
				for( Long id : deleteIds ) {
					recordingProcessor.removeRecording( id );

					int deleted = mContext.getContentResolver().delete( ContentUris.withAppendedId( ProgramConstants.CONTENT_URI, id ), null, null );
					count += deleted;
					
					Log.v( TAG, "removeDeletedPrograms : deleted, id=" + deleted );
				}
			}
		}

		Log.v( TAG, "removeDeletedPrograms : exit" );
		return count;
	}

}
