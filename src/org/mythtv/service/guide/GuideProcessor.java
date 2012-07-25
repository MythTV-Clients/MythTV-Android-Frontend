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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.service.AbstractMythtvProcessor;
import org.mythtv.service.channel.ChannelProcessor;
import org.mythtv.service.dvr.ProgramProcessor;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.channel.ChannelInfo;
import org.mythtv.services.api.guide.ProgramGuide;
import org.mythtv.services.api.guide.ProgramGuideWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class GuideProcessor extends AbstractMythtvProcessor {

	protected static final String TAG = GuideProcessor.class.getSimpleName();

	private static final SimpleDateFormat startDateFormatter = new SimpleDateFormat( "yyyy-MM-dd" );

	private ChannelProcessor mChannelProcessor;
	private ProgramProcessor mProgramProcessor;
	
	public interface GuideProcessorCallback {

		void send( int resultCode );

	}

	public GuideProcessor( Context context ) {
		super( context );
		Log.v( TAG, "initialize : enter" );
		
		mChannelProcessor = new ChannelProcessor( context );
		mProgramProcessor = new ProgramProcessor( context );
		
		Log.v( TAG, "initialize : exit" );
	}

	public void getProgramGuide( GuideProcessorCallback guideCallback, NotifyCallback notifyCallback ) {
		Log.v( TAG, "getGuide : enter" );

		Date start = DateUtils.getYesterday();
		Date end = DateUtils.getNextDay( start );
		
		String startDate = "";
		ResponseEntity<ProgramGuideWrapper> entity = null;
		for( int i = 0; i < 14; i++ ) {
			
			startDate = startDateFormatter.format( start );
			
			Cursor cursor = mContext.getContentResolver().query( ProgramConstants.CONTENT_URI, new String[] { ProgramConstants._ID }, ProgramConstants.FIELD_START_DATE + " = ?", new String[] { startDate }, null );
			if( cursor.getCount() == 0 ) {
			
				Log.v( TAG, "getGuide : loading data for date " + dateTimeFormatter.format( start ) + " thru "  + dateTimeFormatter.format( end ) );

				notifyCallback.notify( "Retrieving Program Guide for " + startDate );

				entity = application.getMythServicesApi().guideOperations().getProgramGuideResponseEntity( start, end, 1, -1, Boolean.TRUE );

				switch( entity.getStatusCode() ) {
					case OK :
						notifyCallback.notify( "Loading Program Guide for " + startDate );

						processProgramGuide( guideCallback, entity.getBody().getProgramGuide() );
						break;
					default :
						break;
				}
			}
			cursor.close();
			
			start = DateUtils.getNextDay( start );
			end = DateUtils.getNextDay( start );
		}
		
		if( null == entity ) {
			guideCallback.send( HttpStatus.NOT_MODIFIED.value() );
		} else {
			guideCallback.send( entity.getStatusCode().value() );
		}
		
		Log.v( TAG, "getGuide : exit" );
	}

	// internal helpers
	
	private long processProgramGuide( GuideProcessorCallback callback, ProgramGuide programGuide ) {
		Log.v( TAG, "processProgramGuide : enter" );

		long channelsProcessed = mChannelProcessor.batchUpdateChannelContentProvider( programGuide.getChannels() );
		//Log.v( TAG, "processProgramGuide : channelsProcessed=" + channelsProcessed );
		
		for( ChannelInfo channelInfo : programGuide.getChannels() ) {
			if( channelInfo.isVisable() ) {

				long programsProcessed = mProgramProcessor.batchInsertProgramContentProvider( channelInfo.getPrograms(), channelInfo.getChannelNumber() );
				//Log.v( TAG, "processProgramGuide : finished processing " + programsProcessed + " for channel " + channelInfo.getChannelId() );
			}
		}
		
		Log.v( TAG, "processProgramGuide : exit" );
		return channelsProcessed;
	}

}
