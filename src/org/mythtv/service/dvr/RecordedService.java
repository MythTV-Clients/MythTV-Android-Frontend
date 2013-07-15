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

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.dvr.RecordedDaoHelper;
import org.mythtv.db.dvr.programGroup.ProgramGroup;
import org.mythtv.db.dvr.programGroup.ProgramGroupDaoHelper;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.Bool;
import org.mythtv.services.api.MythServiceApiRuntimeException;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.ProgramList;
import org.mythtv.services.api.dvr.Programs;
import org.mythtv.services.api.dvr.impl.DvrTemplate.Endpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Daniel Frey
 *
 */
public class RecordedService extends MythtvService {

	private static final String TAG = RecordedService.class.getSimpleName();

    public static final String ACTION_DOWNLOAD = "org.mythtv.background.recorded.ACTION_DOWNLOAD";
    public static final String ACTION_REMOVE = "org.mythtv.background.recorded.ACTION_REMOVE";
    public static final String ACTION_PROGRESS = "org.mythtv.background.recorded.ACTION_PROGRESS";
    public static final String ACTION_COMPLETE = "org.mythtv.background.recorded.ACTION_COMPLETE";

    public static final String KEY_CHANNEL_ID = "KEY_CHANNEL_ID";
    public static final String KEY_START_TIMESTAMP = "KEY_START_TIMESTAMP";
    
    public static final String EXTRA_PROGRESS = "PROGRESS";
    public static final String EXTRA_PROGRESS_DATA = "PROGRESS_DATA";
    public static final String EXTRA_PROGRESS_ERROR = "PROGRESS_ERROR";
    public static final String EXTRA_COMPLETE = "COMPLETE";
    public static final String EXTRA_COMPLETE_UPTODATE = "COMPLETE_UPTODATE";
    public static final String EXTRA_COMPLETE_OFFLINE = "COMPLETE_OFFLINE";
    
	private RecordedDaoHelper mRecordedDaoHelper = RecordedDaoHelper.getInstance();
	private ProgramGroupDaoHelper mProgramGroupDaoHelper = ProgramGroupDaoHelper.getInstance();
	private EtagDaoHelper mEtagDaoHelper = EtagDaoHelper.getInstance();
	private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	
	public RecordedService() {
		super( "RecordedDownloadService" );
	}
	
	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent( Intent intent ) {
		Log.d( TAG, "onHandleIntent : enter" );
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
    		Log.i( TAG, "onHandleIntent : DOWNLOAD action selected" );

    		try {

    			download( locationProfile );

			} catch( Exception e ) {
				Log.e( TAG, "onHandleIntent : error", e );
				
				passed = false;
			} finally {

    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, "Recorded Programs Download Service Finished" );
   				completeIntent.putExtra( EXTRA_COMPLETE_UPTODATE, passed );
    			
    			sendBroadcast( completeIntent );
    		}
    		
        }
		
		if ( intent.getAction().equals( ACTION_REMOVE ) ) {
    		Log.i( TAG, "onHandleIntent : REMOVE action selected" );

    		boolean deleted = false;
    		try {

    			Bundle extras = intent.getExtras();
    			int channelId = extras.getInt( KEY_CHANNEL_ID );
    			long startTimestamp = extras.getLong( KEY_START_TIMESTAMP );
    			
    			if( channelId == 0 || startTimestamp == 0 ) {
    				passed = false;
    			} else {
    				deleted = removeRecorded( locationProfile, channelId, new DateTime( startTimestamp ) );
    			}
    			
			} catch( Exception e ) {
				Log.e( TAG, "onHandleIntent : error", e );
				
				passed = false;
			} finally {

    			Intent completeIntent = new Intent( ACTION_COMPLETE );
    			completeIntent.putExtra( EXTRA_COMPLETE, ( "Episode" + ( !deleted ? " NOT " : " " ) + "deleted!" ) );
    			
    			sendBroadcast( completeIntent );
    		}
    		
        }
		
		Log.d( TAG, "onHandleIntent : exit" );
	}

	// internal helpers
	
	private void download( final LocationProfile locationProfile ) throws Exception {
		Log.v( TAG, "download : enter" );

		EtagInfoDelegate etag = mEtagDaoHelper.findByEndpointAndDataId( this, locationProfile, Endpoint.GET_RECORDED_LIST.name(), "" );
		
		ResponseEntity<ProgramList> responseEntity = mMythtvServiceHelper.getMythServicesApi( locationProfile ).dvrOperations().getRecordedList( etag );

		DateTime date = DateUtils.convertUtc( new DateTime( System.currentTimeMillis() ) );
		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			Log.i( TAG, "download : " + Endpoint.GET_RECORDED_LIST.getEndpoint() + " returned 200 OK" );
			ProgramList programList = responseEntity.getBody();

			if( null != programList.getPrograms() ) {

				process( programList.getPrograms(), locationProfile );	

				if( null != etag.getValue() ) {
					Log.i( TAG, "download : saving etag: " + etag.getValue() );
					
					etag.setEndpoint( Endpoint.GET_RECORDED_LIST.name() );
					etag.setDate( date );
					etag.setMasterHostname( locationProfile.getHostname() );
					etag.setLastModified( date );
					mEtagDaoHelper.save( this, locationProfile, etag );
				}

			}

		}

		if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
			Log.i( TAG, "download : " + Endpoint.GET_RECORDED_LIST.getEndpoint() + " returned 304 Not Modified" );

			if( null != etag.getValue() ) {
				Log.i( TAG, "download : saving etag: " + etag.getValue() );

				etag.setLastModified( date );
				mEtagDaoHelper.save( this, locationProfile, etag );
			}

		}
			
		Log.v( TAG, "download : exit" );
	}

	private void process( Programs programs, final LocationProfile locationProfile ) throws JsonGenerationException, JsonMappingException, IOException, RemoteException, OperationApplicationException {
		Log.v( TAG, "process : enter" );
		
		Log.v( TAG, "process : saving recorded for host [" + locationProfile.getHostname() + ":" + locationProfile.getUrl() + "]" );
		
		int programsAdded = mRecordedDaoHelper.load( this, locationProfile, programs.getPrograms() );
		Log.v( TAG, "process : programsAdded=" + programsAdded );
		
		Log.v( TAG, "process : exit" );
	}

	private Boolean removeRecorded( final LocationProfile locationProfile, int channelId, DateTime startTimestamp ) throws MythServiceApiRuntimeException {
		Log.v( TAG, "removeRecorded : enter" );
		
		boolean removed = false;

		Intent intent = new Intent( LiveStreamService.ACTION_REMOVE );
		intent.putExtra( LiveStreamService.KEY_CHANNEL_ID, channelId );
		intent.putExtra( LiveStreamService.KEY_START_TIMESTAMP, startTimestamp.getMillis() );
		startService( intent );

		ResponseEntity<Bool> responseEntity = mMythtvServiceHelper.getMythServicesApi( locationProfile ).dvrOperations().removeRecorded( channelId, startTimestamp );
		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			removed = responseEntity.getBody().getBool();
			
			if( removed ) {
				
				removeRecordedLocal( locationProfile, channelId, startTimestamp );
					
			}
		}

		Log.v( TAG, "removeRecorded : exit" );
		return removed;
	}
	
	private void removeRecordedLocal( final LocationProfile locationProfile, int channelId, DateTime startTimestamp ) {
		Log.v( TAG, "removeRecordedLocal : enter" );
		
		Program program = mRecordedDaoHelper.findOne( this, locationProfile, channelId, startTimestamp );
		String title = program.getTitle();
		
		Log.v( TAG, "removeRecordedLocal : deleting recorded program in content provider" );
		int deleted = mRecordedDaoHelper.delete( this, locationProfile, program );
		if( deleted == 1 ) {
			Log.v( TAG, "removeRecordedLocal : recorded program deleted, clean up program group if needed" );

			removeProgramGroupLocal( locationProfile, title );
		}
		
		Log.v( TAG, "removeRecordedLocal : exit" );
	}

	private void removeProgramGroupLocal( final LocationProfile locationProfile, final String title ) {
		Log.v( TAG, "removeProgramGroupLocal : enter" );
		
		ProgramGroup programGroup = mProgramGroupDaoHelper.findByTitle( this, locationProfile, title );

		List<Program> programs = mRecordedDaoHelper.findAllByTitle( this, locationProfile, title );
		if( null == programs || programs.isEmpty() ) {

			mProgramGroupDaoHelper.delete( this, programGroup );
			
		}

		Log.v( TAG, "removeProgramGroupLocal : exit" );
	}
	
}
