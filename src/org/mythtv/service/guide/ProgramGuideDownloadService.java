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
package org.mythtv.service.guide;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.dvr.ProgramGuideDaoHelper;
import org.mythtv.db.dvr.UpcomingDaoHelper;
import org.mythtv.db.dvr.model.Program;
import org.mythtv.db.guide.GuideEndpoint;
import org.mythtv.db.guide.model.ProgramGuide;
import org.mythtv.db.guide.model.ProgramGuideWrapper;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.NetworkHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Daniel Frey
 *
 */
public class ProgramGuideDownloadService extends MythtvService {

	private static final String TAG = ProgramGuideDownloadService.class.getSimpleName();
	
    public static final String ACTION_DOWNLOAD = "org.mythtv.background.programGuideDownloadNew.ACTION_DOWNLOAD";
    public static final String ACTION_PROGRESS = "org.mythtv.background.programGuideDownloadNew.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.programGuideDownloadNew.ACTION_COMPLETE";

    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATE = "PROGRESS_DATE";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_UPTODATE = "COMPLETE_UPTODATE";
    public static final String EXTRA_COMPLETE_OFFLINE = "COMPLETE_OFFLINE";

	private ProgramGuideDaoHelper mProgramGuideDaoHelper = ProgramGuideDaoHelper.getInstance(); 
	private UpcomingDaoHelper mUpcomingDaoHelper = UpcomingDaoHelper.getInstance(); 
	
	public ProgramGuideDownloadService() {
		super( "ProgamGuideDownloadServiceNew" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.MythtvService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.v( TAG, "onHandleIntent : enter" );
		super.onHandleIntent( intent );

		boolean passed = true;
		
		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile( this );
		if( !NetworkHelper.getInstance().isMasterBackendConnected( this, locationProfile ) ) {
			Intent completeIntent = new Intent( ACTION_COMPLETE );
			completeIntent.putExtra( EXTRA_COMPLETE, "Master Backend unreachable" );
			completeIntent.putExtra( EXTRA_COMPLETE_OFFLINE, Boolean.TRUE );
			sendBroadcast( completeIntent );

			Log.d( TAG, "onHandleIntent : exit, Master Backend unreachable" );
			return;
		}

		if ( intent.getAction().equals( ACTION_DOWNLOAD ) ) {
			
			try {

				download( locationProfile );
				updateUpcoming( locationProfile );
				
			} catch( Exception e ) {
				Log.e( TAG, "onHandleIntent : error", e );
				
				passed = false;
			} finally {

				Intent completeIntent = new Intent( ACTION_COMPLETE );
				completeIntent.putExtra( EXTRA_COMPLETE, "Program Guide Download Service Finished" );
				completeIntent.putExtra( EXTRA_COMPLETE_UPTODATE, passed );
				sendBroadcast( completeIntent );
			
			}
			
		}
		
		Log.v( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private void download( final LocationProfile locationProfile ) throws Exception {
		Log.v( TAG, "download : enter" );
		
		DateTime startDownloading = new DateTime();
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( this );
		int downloadDays = Integer.parseInt( sp.getString( "preference_program_guide_days", "14" ) );
		Log.v( TAG, "download : downloadDays=" + downloadDays );
		
		DateTime start = new DateTime( DateTimeZone.getDefault() ).withTimeAtStartOfDay();
		DateTime end = start.plusHours( 3 );
		for( int i = 0; i < ( ( downloadDays * 24 ) / 3 ); i++ ) {
			Log.i( TAG, "download : starting download for [" + i + " of " + ( ( downloadDays * 24 ) / 3 ) + "] " + DateUtils.getDateTimeUsingLocaleFormattingPretty( start, mMainApplication.getDateFormat(), mMainApplication.getClockType() ) + ", end time=" + DateUtils.getDateTimeUsingLocaleFormattingPretty( end, mMainApplication.getDateFormat(), mMainApplication.getClockType() ) );

			EtagInfoDelegate etag = mEtagDaoHelper.findByEndpointAndDataId( this, locationProfile, GuideEndpoint.GET_PROGRAM_GUIDE.name(), String.valueOf( i ) );
			Log.v( TAG, "download : etag=" + etag.toString() );
			
			if( null == etag.getDate() || start.isAfter( etag.getDate() ) ) {
				Log.v( TAG, "download : next mythfilldatabase has passed" );
				
				ResponseEntity<ProgramGuideWrapper> responseEntity = mMythtvServiceHelper.getMythServicesApi( locationProfile ).guideOperations().getProgramGuide( start, end, 1, -1, false, etag );

				if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
					Log.i( TAG, "download : " + GuideEndpoint.GET_PROGRAM_GUIDE.name() + " returned 200 OK" );
					ProgramGuideWrapper programGuide = responseEntity.getBody();

					if( null != programGuide ) {

						if( null != programGuide.getProgramGuide() ) {
							process( programGuide.getProgramGuide(), locationProfile );
						}

					}

					if( null != etag.getValue() ) {
						Log.i( TAG, "download : saving etag: " + etag.getValue() );

						etag.setEndpoint( GuideEndpoint.GET_PROGRAM_GUIDE.name() );
						etag.setDataId( i );
						etag.setDate( locationProfile.getNextMythFillDatabase() );
						etag.setMasterHostname( locationProfile.getHostname() );
						etag.setLastModified( DateUtils.convertUtc( new DateTime( DateTimeZone.getDefault() ) ) );
						mEtagDaoHelper.save( this, locationProfile, etag );
					}

				}

				if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
					Log.i( TAG, "download : " + GuideEndpoint.GET_PROGRAM_GUIDE.name() + " returned 304 Not Modified" );

					if( null != etag.getValue() ) {
						Log.i( TAG, "download : saving etag: " + etag.getValue() );

						etag.setLastModified( DateUtils.convertUtc( new DateTime( DateTimeZone.getDefault() ) ) );
						mEtagDaoHelper.save( this, locationProfile, etag );
					}

				}

				start = end;
				end = end.plusHours( 3 );

			} else {
				Log.v( TAG, "download : next mythfilldatabase has NOT passed!" );
			}
			
		}

		Log.i( TAG, "download : interval=" + new Interval( startDownloading, new DateTime() ).toString() );
		
		Log.v( TAG, "download : exit" );
	}

	private void process( ProgramGuide programGuide, final LocationProfile locationProfile ) throws JsonGenerationException, JsonMappingException, IOException, RemoteException, OperationApplicationException {
		Log.v( TAG, "process : enter" );

		Log.v( TAG, "process : saving program guide for host [" + locationProfile.getHostname() + ":" + locationProfile.getUrl() + "]" );
		
		int programsAdded = mProgramGuideDaoHelper.loadProgramGuide( this, locationProfile, programGuide.getChannels() );
		Log.v( TAG, "process : programsAdded=" + programsAdded );
	
		Log.v( TAG, "process : exit" );
	}

	private void updateUpcoming( final LocationProfile locationProfile ) {
		Log.v( TAG, "updateUpcoming : enter" );
		
		List<Program> upcomings = mUpcomingDaoHelper.findAll( this, locationProfile );
		if( null != upcomings && !upcomings.isEmpty() ) {
			
			for( Program upcoming : upcomings ) {
//				Log.v( TAG, "updateUpcoming : upcoming=" + upcomings.toString() );
			
				Program program = mProgramGuideDaoHelper.findOne( this, locationProfile, upcoming.getChannelInfo().getChannelId(), upcoming.getStartTime() );
				if( null != program ) {
					
					if( null == program.getRecording() || ( null != program.getRecording() && program.getRecording().getStatus() > -2 ) ) {
						program.setRecording( upcoming.getRecording() );
						mProgramGuideDaoHelper.save( this, locationProfile, program );
					
						Log.v( TAG, "updateUpcoming : program updated!" );
					}
					
				}
				
			}
			
		}
		
		Log.v( TAG, "updateUpcoming : exit" );
	}
	
}
