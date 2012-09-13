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

import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.dvr.ProgramConstants.ProgramType;
import org.mythtv.service.AbstractMythtvProcessor;
import org.mythtv.service.util.NotificationHelper;
import org.mythtv.service.util.NotificationHelper.NotificationType;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.ProgramList;
import org.springframework.http.ResponseEntity;

import android.content.Context;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class ProgramListProcessor extends AbstractMythtvProcessor {

	protected static final String TAG = ProgramListProcessor.class.getSimpleName();

	private NotificationHelper mNotificationHelper;
	
	private ProgramProcessor programProcessor;
	
	public interface RecordingListProcessorCallback {

		void send( int resultCode );

	}

	public interface UpcomingListProcessorCallback {

		void send( int resultCode );

	}

	public ProgramListProcessor( Context context ) {
		super( context );
		Log.v( TAG, "initialize : enter" );
		
		mNotificationHelper = new NotificationHelper( context );
		
		programProcessor = new ProgramProcessor( context );
		
		Log.v( TAG, "initialize : exit" );
	}

	public void getRecordedList( RecordingListProcessorCallback callback ) {
		Log.v( TAG, "getRecordedList : enter" );

		String message = "Retrieving Recorded Programs";
		mNotificationHelper.createNotification( "Mythtv for Android", message, NotificationType.UPLOAD );
		ETagInfo eTag = ETagInfo.createEmptyETag();
		ResponseEntity<ProgramList> entity = application.getMythServicesApi().dvrOperations().getRecordedListResponseEntity( eTag );
		if( Log.isLoggable( TAG, Log.INFO ) ) {
			Log.i( TAG, "getRecordedList : entity status code = " + entity.getStatusCode().toString() );
		}
		mNotificationHelper.completed();
		
		switch( entity.getStatusCode() ) {
			case OK :
				int updated = programProcessor.resetRecordedPrograms();
				Log.v( TAG, "getRecordedList : updated=" + updated );

				message = "Updating Recorded Programs";
				
				mNotificationHelper.createNotification( "Mythtv for Android", message, NotificationType.UPLOAD );
				processProgramList( entity.getBody(), ProgramConstants.ProgramType.RECORDED );
				break;
			default :
				break;
		}
		
		callback.send( entity.getStatusCode().value() );
		
		Log.v( TAG, "getRecordedList : exit" );
	}

	public void getUpcomingList( UpcomingListProcessorCallback callback ) {
		Log.v( TAG, "getUpcomingList : enter" );

		String message = "Retrieving Upcoming Programs";
		mNotificationHelper.createNotification( "Mythtv for Android", message, NotificationType.UPLOAD );
		ETagInfo eTag = ETagInfo.createEmptyETag();
		ResponseEntity<ProgramList> entity = application.getMythServicesApi().dvrOperations().getUpcomingListResponseEntity( eTag );
		if( Log.isLoggable( TAG, Log.DEBUG ) ) {
			Log.d( TAG, "getUpcomingList : entity status code = " + entity.getStatusCode().toString() );
		}
		mNotificationHelper.completed();
		
		switch( entity.getStatusCode() ) {
			case OK :
				int updated = programProcessor.resetUpcomingPrograms();
				Log.v( TAG, "getUpcomingList : updated=" + updated );
				
				message = "Updating Upcoming Programs";
				mNotificationHelper.createNotification( "Mythtv for Android", message, NotificationType.UPLOAD );
				processProgramList( entity.getBody(), ProgramConstants.ProgramType.UPCOMING );
				break;
			default :
				break;
		}
		
		callback.send( entity.getStatusCode().value() );
		
		Log.v( TAG, "getUpcomingList : exit" );
	}

	// internal helpers
	
	private void processProgramList( ProgramList programList, ProgramConstants.ProgramType programType ) {
		Log.v( TAG, "processProgramList : enter" );

		if( null != programList && null != programList.getPrograms() && ( null != programList.getPrograms().getPrograms() && !programList.getPrograms().getPrograms().isEmpty() ) ) {

			Log.v( TAG, "processProgramList : " + programType.name() + ", count=" + programList.getPrograms().getPrograms().size() );

//			getMainApplication().setDatabaseLoading( true );
			
			int size = programList.getPrograms().getPrograms().size();
			int count = 0;
			for( Program program : programList.getPrograms().getPrograms() ) {
				
				if( !"livetv".equalsIgnoreCase( program.getRecording().getRecordingGroup() ) ) {
					programProcessor.updateProgramContentProvider( program, programType );
				}

				count++;

				double percentage = ( (float) count / (float) size ) * 100;
				Log.v( TAG, "processProgramList : percentage=" + percentage );
				mNotificationHelper.progressUpdate( percentage );
			}

//			getMainApplication().setDatabaseLoading( false );

		}

		mNotificationHelper.completed();
		
		Log.v( TAG, "processProgramList : exit" );
	}

	private void processProgramListBatch( ProgramList programList, ProgramType programType ) {
		Log.v( TAG, "processProgramListBatch : enter" );

		if( null != programList && null != programList.getPrograms() && ( null != programList.getPrograms().getPrograms() && !programList.getPrograms().getPrograms().isEmpty() ) ) {

			Log.v( TAG, "processProgramListBatch : " + programType.name() + ", count=" + programList.getPrograms().getPrograms().size() );

//			getMainApplication().setDatabaseLoading( true );
			
			programProcessor.batchUpdateProgramContentProvider( programList.getPrograms().getPrograms(), programType );
			
//			getMainApplication().setDatabaseLoading( false );

		}

		mNotificationHelper.completed();
		
		Log.v( TAG, "processProgramListBatch : exit" );
	}

}
