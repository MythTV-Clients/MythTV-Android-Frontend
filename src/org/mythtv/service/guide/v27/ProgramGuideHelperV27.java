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
/**
 * 
 */
package org.mythtv.service.guide.v27;

import java.util.ArrayList;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.dvr.RecordingConstants.ContentDetails;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.service.dvr.v27.ProgramHelperV27;
import org.mythtv.service.dvr.v27.RecordingHelperV27;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v027.MythServicesTemplate;
import org.mythtv.services.api.v027.beans.ChannelInfo;
import org.mythtv.services.api.v027.beans.Program;
import org.mythtv.services.api.v027.beans.ProgramGuide;
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
public class ProgramGuideHelperV27 extends AbstractBaseHelper {

	private static final String TAG = ProgramGuideHelperV27.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v027;
	
	private static MainApplication mMainApplication;
	private static MythServicesTemplate mMythServicesTemplate;
	
	private static ProgramGuideHelperV27 singleton;
	
	/**
	 * Returns the one and only ProgramGuideHelperV27. init() must be called before 
	 * any 
	 * @return
	 */
	public static ProgramGuideHelperV27 getInstance() {
		if( null == singleton ) {
			
			synchronized( ProgramGuideHelperV27.class ) {

				if( null == singleton ) {
					singleton = new ProgramGuideHelperV27();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private ProgramGuideHelperV27() { }

	public boolean process( final Context context, final LocationProfile locationProfile ) {
		Log.v( TAG, "process : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}
	
		mMainApplication = (MainApplication) context.getApplicationContext();
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		if( null == mMythServicesTemplate ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}
		
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
		
		Program program = ProgramHelperV27.getInstance().findProgram( context, locationProfile, ProgramConstants.CONTENT_URI_GUIDE, ProgramConstants.TABLE_NAME_GUIDE, channelId, startTime );
		
		Log.v( TAG, "findProgram : enter" );
		return program;
	}
	
	public static boolean deleteProgram( final Context context, final LocationProfile locationProfile, Integer channelId, DateTime programStartTime, DateTime recordingStartTime, Integer recordId ) {
		Log.v( TAG, "deleteProgram : enter" );
		
		boolean removed = ProgramHelperV27.getInstance().deleteProgram( context, locationProfile, ProgramConstants.CONTENT_URI_GUIDE, ProgramConstants.TABLE_NAME_GUIDE, channelId, programStartTime, recordingStartTime, recordId );
		
		Log.v( TAG, "deleteProgram : enter" );
		return removed;
	}
	
	// internal helpers
	
	private static void downloadProgramGuide( final Context context, final LocationProfile locationProfile ) throws RemoteException, OperationApplicationException {
		Log.v( TAG, "downloadProgramGuide : enter" );

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		ProgramHelperV27.getInstance().deletePrograms( context, locationProfile, ops, ProgramConstants.CONTENT_URI_GUIDE, ProgramConstants.TABLE_NAME_GUIDE );
		RecordingHelperV27.getInstance().deleteRecordings( context, locationProfile, ops, ContentDetails.GUIDE );

		DateTime startDownloading = new DateTime();
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
		int downloadDays = Integer.parseInt( sp.getString( "preference_program_guide_days", "14" ) );
		Log.v( TAG, "downloadProgramGuide : downloadDays=" + downloadDays );
		
		DateTime start = new DateTime( DateTimeZone.getDefault() ).withTimeAtStartOfDay();
		DateTime end = start.plusHours( 3 );
		for( int i = 0; i < ( ( downloadDays * 24 ) / 3 ); i++ ) {
			Log.i( TAG, "downloadProgramGuide : starting download for [" + ( i + 1 ) + " of " + ( ( downloadDays * 24 ) / 3 ) + "] " + DateUtils.getDateTimeUsingLocaleFormattingPretty( start, mMainApplication.getDateFormat(), mMainApplication.getClockType() ) + ", end time=" + DateUtils.getDateTimeUsingLocaleFormattingPretty( end, mMainApplication.getDateFormat(), mMainApplication.getClockType() ) );

			EtagInfoDelegate etag = mEtagDaoHelper.findByEndpointAndDataId( context, locationProfile, "GetProgramGuide", String.valueOf( i ) );
			Log.d( TAG, "downloadProgramGuide : etag=" + etag.getValue() );
			
			if( null == etag.getDate() || start.isAfter( etag.getDate() ) ) {
				Log.v( TAG, "downloadProgramGuide : next mythfilldatabase has passed" );
				
				ResponseEntity<ProgramGuide> responseEntity = mMythServicesTemplate.guideOperations().getProgramGuide( start, end, 1, null, false, etag );

				if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
					Log.i( TAG, "downloadProgramGuide : GetProgramGuide returned 200 OK" );
					ProgramGuide programGuide = responseEntity.getBody();

					if( null != programGuide ) {

						if( null != programGuide ) {
							load( context, locationProfile, programGuide );
						}

					}

					if( null != etag.getValue() ) {
						Log.i( TAG, "downloadProgramGuide : saving etag: " + etag.getValue() );

						etag.setEndpoint( "GetProgramGuide" );
						etag.setDataId( i );
						etag.setDate( locationProfile.getNextMythFillDatabase() );
						etag.setMasterHostname( locationProfile.getHostname() );
						etag.setLastModified( new DateTime( DateTimeZone.UTC ) );
						mEtagDaoHelper.save( context, locationProfile, etag );
					}

				}

				if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
					Log.i( TAG, "downloadProgramGuide : GetProgramGuide returned 304 Not Modified" );

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
		
		String tag = UUID.randomUUID().toString();
		int processed = -1;
		int count = 0;
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		
		for( ChannelInfo channel : programGuide.getChannels() ) {
		
			for( Program program : channel.getPrograms() ) {
				//Log.i( TAG, "load : count=" + count );

				program.setChannel( channel );
				program.setHostName( locationProfile.getHostname() );

				ProgramHelperV27.getInstance().processProgram( context, locationProfile, ProgramConstants.CONTENT_URI_GUIDE, ProgramConstants.TABLE_NAME_GUIDE, ops, program, tag );
				count++;

				if( count > BATCH_COUNT_LIMIT ) {
//					Log.i( TAG, "load : applying batch for '" + count + "' transactions, processing programs" );

					processBatch( context, ops, processed, count );

					count = 0;
				}

			}

		}

		if( !ops.isEmpty() ) {
//			Log.i( TAG, "load : applying final batch for '" + count + "' transactions, processing programs" );
			
			processBatch( context, ops, processed, count );
		}
		
//		Log.v( TAG, "load : exit" );
		return processed;
	}

}
