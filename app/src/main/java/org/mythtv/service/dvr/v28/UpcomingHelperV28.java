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
package org.mythtv.service.dvr.v28;

import java.util.ArrayList;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.dvr.RecordingConstants;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.service.dvr.v27.ProgramHelperV27;
import org.mythtv.service.dvr.v27.RecordingHelperV27;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.MythServiceApiRuntimeException;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v027.MythServicesTemplate;
import org.mythtv.services.api.v027.beans.Program;
import org.mythtv.services.api.v027.beans.ProgramList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class UpcomingHelperV28 extends AbstractBaseHelper {

	private static final String TAG = UpcomingHelperV28.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v027;
	
	private static MythServicesTemplate mMythServicesTemplate;

	private static UpcomingHelperV28 singleton;
	
	/**
	 * Returns the one and only UpcomingHelperV28. init() must be called before 
	 * any 
	 * @return
	 */
	public static UpcomingHelperV28 getInstance() {
		if( null == singleton ) {
			
			synchronized( UpcomingHelperV28.class ) {

				if( null == singleton ) {
					singleton = new UpcomingHelperV28();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private UpcomingHelperV28() { }

	public boolean process( final Context context, final LocationProfile locationProfile ) {
		Log.v( TAG, "process : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}
		
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		
		boolean passed = true;

		try {

			downloadUpcoming( context, locationProfile );
			
		} catch( Exception e ) {
			Log.e( TAG, "process : error", e );
		
			passed = false;
		}

		Log.v( TAG, "process : exit" );
		return passed;
	}
	
	// internal helpers
	
	private void downloadUpcoming( final Context context, final LocationProfile locationProfile ) throws MythServiceApiRuntimeException, RemoteException, OperationApplicationException {
		Log.v( TAG, "downloadUpcoming : enter" );
	
		EtagInfoDelegate etag = mEtagDaoHelper.findByEndpointAndDataId( context, locationProfile, "GetUpcomingList", "" );
		Log.d( TAG, "downloadUpcoming : etag=" + etag.getValue() );
		
		ResponseEntity<ProgramList> responseEntity = mMythServicesTemplate.dvrOperations().getUpcomingList( null, null, Boolean.FALSE, etag );

		DateTime date = new DateTime( DateTimeZone.UTC );
		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			Log.i( TAG, "downloadUpcoming : GetUpcomingList returned 200 OK" );
			ProgramList programList = responseEntity.getBody();

			if( null != programList.getPrograms() ) {

				load( context, locationProfile, programList.getPrograms() );	

				if( null != etag.getValue() ) {
					Log.i( TAG, "downloadUpcoming : saving etag: " + etag.getValue() );
					
					etag.setEndpoint( "GetUpcomingList" );
					etag.setDate( date );
					etag.setMasterHostname( locationProfile.getHostname() );
					etag.setLastModified( date );
					mEtagDaoHelper.save( context, locationProfile, etag );
				}

			}

		}

		if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
			Log.i( TAG, "downloadUpcoming : GetUpcomingList returned 304 Not Modified" );

			if( null != etag.getValue() ) {
				Log.i( TAG, "downloadUpcoming : saving etag: " + etag.getValue() );

				etag.setLastModified( date );
				mEtagDaoHelper.save( context, locationProfile, etag );
			}

		}

		Log.v( TAG, "downloadUpcoming : exit" );
	}
	
	private int load( final Context context, final LocationProfile locationProfile, final Program[] programs ) throws RemoteException, OperationApplicationException {
		Log.d( TAG, "load : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "UpcomingHelperV28 is not initialized" );
		
		String tag = UUID.randomUUID().toString();
		int processed = -1;
		int count = 0;
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		
		boolean inError;

		for( Program program : programs ) {
//			Log.d( TAG, "load : count=" + count );
			
			if( null == program.getStartTime() || null == program.getEndTime() ) {
//				Log.w(TAG, "load : null starttime and or endtime" );
			
				inError = true;
			} else {
				inError = false;
			}

			// load upcoming program
			ProgramHelperV27.getInstance().processProgram( context, locationProfile, ProgramConstants.CONTENT_URI_UPCOMING, ProgramConstants.TABLE_NAME_UPCOMING, ops, program, tag );
			count++;
			
			// update program guide
			ProgramHelperV27.getInstance().processProgram( context, locationProfile, ProgramConstants.CONTENT_URI_GUIDE, ProgramConstants.TABLE_NAME_GUIDE, ops, program, tag );
			count++;

			if( !inError && null != program.getRecording() ) {
				
				if( program.getRecording().getRecordId() > 0 ) {
				
					// load upcoming recording
					RecordingHelperV27.getInstance().processRecording( context, locationProfile, ops, RecordingConstants.ContentDetails.UPCOMING, program, tag );
					count++;

					// update program guide recording
					RecordingHelperV27.getInstance().processRecording( context, locationProfile, ops, RecordingConstants.ContentDetails.GUIDE, program, tag );
					count++;

				}
				
			}
			
			if( count > BATCH_COUNT_LIMIT ) {
//				Log.i( TAG, "load : applying batch for '" + count + "' transactions, processing programs" );
				
				processBatch( context, ops, processed, count );

				count = 0;
			}
			
		}

		if( !ops.isEmpty() ) {
			Log.v( TAG, "load : applying final batch for '" + count + "' transactions" );
			
			processBatch( context, ops, processed, count );
		}

		ProgramHelperV28.getInstance().deletePrograms( context, locationProfile, ProgramConstants.CONTENT_URI_UPCOMING, ProgramConstants.TABLE_NAME_UPCOMING, tag );
//		RecordingHelperV28.getInstance().deleteRecordings( context, locationProfile, ops, RecordingConstants.ContentDetails.UPCOMING, tag );

		if( !ops.isEmpty() ) {
			Log.v( TAG, "load : applying delete batch for transactions" );
			
			processBatch( context, ops, processed, count );
		}

//		Log.v( TAG, "load : exit" );
		return processed;
	}

}
