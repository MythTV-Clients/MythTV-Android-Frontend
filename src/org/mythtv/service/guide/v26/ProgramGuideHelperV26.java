/**
 * 
 */
package org.mythtv.service.guide.v26;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.service.channel.v26.ChannelHelperV26;
import org.mythtv.service.dvr.v26.ProgramHelperV26;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v026.MythServicesTemplate;
import org.mythtv.services.api.v026.beans.ChannelInfo;
import org.mythtv.services.api.v026.beans.Program;
import org.mythtv.services.api.v026.beans.ProgramGuide;
import org.mythtv.services.api.v026.beans.ProgramGuideWrapper;
import org.mythtv.services.api.v026.impl.GuideTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGuideHelperV26 extends AbstractBaseHelper {

	private static final String TAG = ProgramGuideHelperV26.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v026;
	
	private static MainApplication mMainApplication;
	private static MythServicesTemplate mMythServicesTemplate;
	
	public static boolean process( final Context context, final LocationProfile locationProfile ) {
		Log.v( TAG, "process : enter" );
		
		if( !MythAccessFactory.isServerReachable( locationProfile.getUrl() ) ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}
	
		mMainApplication = (MainApplication) context.getApplicationContext();
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		
		boolean passed = true;

		try {

			downloadProgramGuide( context, locationProfile );
			
		} catch( Exception e ) {
			Log.e( TAG, "process : error", e );
		
			passed = false;
		}

		Log.v( TAG, "process : exit" );
		return passed;
	}
	
	public static Program findProgram( final Context context, final LocationProfile locationProfile, Integer channelId, DateTime startTime ) {
		Log.v( TAG, "findProgram : enter" );
		
		Program program = ProgramHelperV26.findProgram( context, locationProfile, ProgramConstants.CONTENT_URI_GUIDE, ProgramConstants.TABLE_NAME_GUIDE, channelId, startTime );
		
		Log.v( TAG, "findProgram : enter" );
		return program;
	}
	
	public static boolean deleteProgram( final Context context, final LocationProfile locationProfile, Integer channelId, DateTime startTime, Integer recordId ) {
		Log.v( TAG, "deleteProgram : enter" );
		
		boolean removed = ProgramHelperV26.deleteProgram( context, locationProfile, ProgramConstants.CONTENT_URI_GUIDE, ProgramConstants.TABLE_NAME_GUIDE, channelId, startTime, recordId );
		
		Log.v( TAG, "deleteProgram : enter" );
		return removed;
	}
	
	// internal helpers
	
	private static void downloadProgramGuide( final Context context, final LocationProfile locationProfile ) throws RemoteException, OperationApplicationException {
		Log.v( TAG, "downloadProgramGuide : enter" );
	
		DateTime startDownloading = new DateTime();
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
		int downloadDays = Integer.parseInt( sp.getString( "preference_program_guide_days", "14" ) );
		Log.v( TAG, "download : downloadDays=" + downloadDays );
		
		DateTime start = new DateTime( DateTimeZone.getDefault() ).withTimeAtStartOfDay();
		DateTime end = start.plusHours( 3 );
		for( int i = 0; i < ( ( downloadDays * 24 ) / 3 ); i++ ) {
			Log.i( TAG, "download : starting download for [" + i + " of " + ( ( downloadDays * 24 ) / 3 ) + "] " + DateUtils.getDateTimeUsingLocaleFormattingPretty( start, mMainApplication.getDateFormat(), mMainApplication.getClockType() ) + ", end time=" + DateUtils.getDateTimeUsingLocaleFormattingPretty( end, mMainApplication.getDateFormat(), mMainApplication.getClockType() ) );

			EtagInfoDelegate etag = mEtagDaoHelper.findByEndpointAndDataId( context, locationProfile, GuideTemplate.Endpoint.GET_PROGRAM_GUIDE.name(), String.valueOf( i ) );
			Log.v( TAG, "download : etag=" + etag.toString() );
			
			if( null == etag.getDate() || start.isAfter( etag.getDate() ) ) {
				Log.v( TAG, "download : next mythfilldatabase has passed" );
				
				ResponseEntity<ProgramGuideWrapper> responseEntity = mMythServicesTemplate.guideOperations().getProgramGuide( start, end, 1, -1, false, etag );

				if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
					Log.i( TAG, "download : " + GuideTemplate.Endpoint.GET_PROGRAM_GUIDE.name() + " returned 200 OK" );
					ProgramGuideWrapper programGuide = responseEntity.getBody();

					if( null != programGuide ) {

						if( null != programGuide.getProgramGuide() ) {
							load( context, locationProfile, programGuide.getProgramGuide() );
						}

					}

					if( null != etag.getValue() ) {
						Log.i( TAG, "download : saving etag: " + etag.getValue() );

						etag.setEndpoint( GuideTemplate.Endpoint.GET_PROGRAM_GUIDE.name() );
						etag.setDataId( i );
						etag.setDate( locationProfile.getNextMythFillDatabase() );
						etag.setMasterHostname( locationProfile.getHostname() );
						etag.setLastModified( new DateTime( DateTimeZone.UTC ) );
						mEtagDaoHelper.save( context, locationProfile, etag );
					}

				}

				if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
					Log.i( TAG, "downloadProgramGuide : " + GuideTemplate.Endpoint.GET_PROGRAM_GUIDE.name() + " returned 304 Not Modified" );

					if( null != etag.getValue() ) {
						Log.i( TAG, "downloadProgramGuide : saving etag: " + etag.getValue() );

						etag.setLastModified( new DateTime( DateTimeZone.UTC ) );
						mEtagDaoHelper.save( context, locationProfile, etag );
					}

				}

				start = end;
				end = end.plusHours( 3 );

			} else {
				Log.v( TAG, "downloadProgramGuide : next mythfilldatabase has NOT passed!" );
			}
			
		}

		Log.i( TAG, "downloadProgramGuide : interval=" + new Interval( startDownloading, new DateTime() ).toString() );
		
		Log.v( TAG, "downloadProgramGuide : exit" );
	}
	
	private static int load( final Context context, final LocationProfile locationProfile, final ProgramGuide programGuide ) throws RemoteException, OperationApplicationException {
		Log.d( TAG, "load : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "ProgramGuideHelperV27 is not initialized" );
		
		DateTime today = new DateTime( DateTimeZone.UTC ).withTimeAtStartOfDay();
		DateTime lastModified = new DateTime( DateTimeZone.UTC );
		
		int processed = -1;
		int count = 0;
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		
		for( ChannelInfo channel : programGuide.getChannels() ) {
		
			ChannelInfo channelInfo = ChannelHelperV26.findChannel( context, locationProfile, channel.getChannelId() );
			if( null == channelInfo ) {
				ChannelHelperV26.processChannel( context, locationProfile, ops, channelInfo, lastModified, count );
				
				channelInfo = ChannelHelperV26.findChannel( context, locationProfile, channel.getChannelId() );
			}
			
			for( Program program : channel.getPrograms() ) {

				DateTime startTime = program.getStartTime();

				ProgramHelperV26.processProgram( context, locationProfile, ProgramConstants.CONTENT_URI_GUIDE, ProgramConstants.TABLE_NAME_GUIDE, ops, program, lastModified, startTime, count );

				if( count > BATCH_COUNT_LIMIT ) {
//					Log.i( TAG, "load : applying batch for '" + count + "' transactions, processing programs" );

					processBatch( context, ops, processed, count );

				}

			}

		}
		
		processBatch( context, ops, processed, count );

//		Log.v( TAG, "load : DELETE PROGRAMS" );
		ProgramHelperV26.deletePrograms( context, locationProfile, ops, ProgramConstants.CONTENT_URI_GUIDE, ProgramConstants.TABLE_NAME_GUIDE, today );

		processBatch( context, ops, processed, count );

//		Log.v( TAG, "load : exit" );
		return processed;
	}

}
