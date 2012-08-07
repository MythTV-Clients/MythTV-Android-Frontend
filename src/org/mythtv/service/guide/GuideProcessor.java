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
package org.mythtv.service.guide;

import org.joda.time.DateTime;
import org.mythtv.client.MainApplication;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.service.AbstractMythtvProcessor;
import org.mythtv.service.channel.ChannelProcessor;
import org.mythtv.service.dvr.ProgramProcessor;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.NotificationHelper;
import org.mythtv.service.util.NotificationHelper.NotificationType;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.guide.ProgramGuide;
import org.mythtv.services.api.guide.ProgramGuideWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class GuideProcessor extends AbstractMythtvProcessor {

	protected static final String TAG = GuideProcessor.class.getSimpleName();

	private ChannelProcessor mChannelProcessor;
	private ProgramProcessor mProgramProcessor;
	
	private SharedPreferences mythtvPreferences;
	private boolean isGuideDataLoaded;
	
	private NotificationHelper mNotificationHelper;
	
	public interface GuideProcessorCallback {

		void send( int resultCode );

	}

	public GuideProcessor( Context context ) {
		super( context );
		Log.v( TAG, "initialize : enter" );
		
		mChannelProcessor = new ChannelProcessor( context );
		mProgramProcessor = new ProgramProcessor( context );
		
		mNotificationHelper = new NotificationHelper( context );
		
		mythtvPreferences = context.getSharedPreferences( "MythtvPreferences", Context.MODE_PRIVATE );
		isGuideDataLoaded = mythtvPreferences.getBoolean( MainApplication.GUIDE_DATA_LOADED, false );
		
		Log.v( TAG, "initialize : exit" );
	}

	public void getProgramGuide( GuideProcessorCallback guideCallback, NotifyCallback notifyCallback ) {
		Log.v( TAG, "getGuide : enter" );

		getMainApplication().setDatabaseLoading( true );
		
		DateTime start = DateUtils.getYesterday();
		DateTime end = DateUtils.getToday();
		
		String endDate = "";
		ResponseEntity<ProgramGuideWrapper> entity = null;
		for( int i = 0; i < 13; i++ ) {
			endDate = DateUtils.dateFormatter.print( end );
			Log.v( TAG, "getGuide : checking if guide data loaded for " + DateUtils.dateTimeFormatter.print( end ) );
			
			Cursor cursor = mContext.getContentResolver().query( ProgramConstants.CONTENT_URI, new String[] { ProgramConstants._ID }, ProgramConstants.FIELD_START_DATE + " = ? and " + ProgramConstants.FIELD_PROGRAM_TYPE + " = ?", new String[] { endDate, ProgramConstants.ProgramType.GUIDE.name() }, null );
			if( cursor.getCount() == 0 ) {
			
				Log.v( TAG, "getGuide : loading data for date " + DateUtils.dateTimeFormatter.print( start ) + " thru "  + DateUtils.dateTimeFormatter.print( end ) );

				String message = "Retrieving Program Guide for " + endDate;
				sendNotificationCallbackMessage( notifyCallback, message );
				mNotificationHelper.createNotification( "Mythtv for Android", message, NotificationType.UPLOAD );

				entity = application.getMythServicesApi().guideOperations().getProgramGuideResponseEntity( new DateTime( start ), new DateTime( end ), 1, -1, Boolean.TRUE );

				switch( entity.getStatusCode() ) {
					case OK :
						mNotificationHelper.completed();

						message = "Loading Program Guide for " + endDate;
						sendNotificationCallbackMessage( notifyCallback, message );
						mNotificationHelper.createNotification( "Mythtv for Android", message, NotificationType.DOWNLOAD );
						
						processProgramGuide( guideCallback, entity.getBody().getProgramGuide() );
						mNotificationHelper.completed();
						
						break;
					default :
						break;
				}
			}
			cursor.close();
			
			start = DateUtils.getNextDay( start );
			end = DateUtils.getNextDay( end );
		}
		
		getMainApplication().setDatabaseLoading( false );
		
		SharedPreferences.Editor editor = mythtvPreferences.edit();
		editor.putLong( MainApplication.NEXT_GUIDE_DATA_LOAD, DateUtils.getNextDayAfterMythfilldatabase().getMillis() );
		editor.commit();

		if( null == entity ) {
			guideCallback.send( HttpStatus.NOT_MODIFIED.value() );
		} else {
			guideCallback.send( entity.getStatusCode().value() );
		}
		
		Log.v( TAG, "getGuide : exit" );
	}

	// internal helpers
	
	private void sendNotificationCallbackMessage( NotifyCallback notifyCallback, String message ) {
		
		if( !isGuideDataLoaded ) {
			notifyCallback.notify( message );
		}
		
	}
	
	private long processProgramGuide( GuideProcessorCallback callback, ProgramGuide programGuide ) {
		Log.v( TAG, "processProgramGuide : enter" );

		long channelsProcessed = mChannelProcessor.batchUpdateChannelContentProvider( programGuide.getChannels() );
		
		int index = 0;
		for( ChannelInfo channelInfo : programGuide.getChannels() ) {
			if( channelInfo.isVisable() ) {

				mProgramProcessor.batchInsertProgramContentProvider( channelInfo.getPrograms(), channelInfo.getChannelNumber() );
			
			}
			
			index++;

			double percentage = ( index / programGuide.getChannels().size() ) * 100;
			mNotificationHelper.progressUpdate( percentage );
		}
		
		Log.v( TAG, "processProgramGuide : exit" );
		return channelsProcessed;
	}

}
