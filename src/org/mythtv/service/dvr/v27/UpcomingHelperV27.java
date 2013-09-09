/**
 * 
 */
package org.mythtv.service.dvr.v27;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.dvr.RecordingConstants;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.service.channel.v27.ChannelHelperV27;
import org.mythtv.services.api.ApiVersion;
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
public class UpcomingHelperV27 extends AbstractBaseHelper {

	private static final String TAG = UpcomingHelperV27.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v027;
	
	private static MythServicesTemplate mMythServicesTemplate;

	private static UpcomingHelperV27 singleton;
	
	/**
	 * Returns the one and only UpcomingHelperV27. init() must be called before 
	 * any 
	 * @return
	 */
	public static UpcomingHelperV27 getInstance() {
		if( null == singleton ) {
			
			synchronized( UpcomingHelperV27.class ) {

				if( null == singleton ) {
					singleton = new UpcomingHelperV27();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private UpcomingHelperV27() { }

	public boolean process( final Context context, final LocationProfile locationProfile ) {
		Log.v( TAG, "process : enter" );
		
		if( !MythAccessFactory.isServerReachable( locationProfile.getUrl() ) ) {
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

		Log.v( TAG, "process : enter" );
		return passed;
	}
	
	// internal helpers
	
	private void downloadUpcoming( final Context context, final LocationProfile locationProfile ) throws RemoteException, OperationApplicationException {
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
			throw new RuntimeException( "UpcomingHelperV27 is not initialized" );
		
		DateTime today = new DateTime( DateTimeZone.UTC ).withTimeAtStartOfDay();
		DateTime lastModified = new DateTime( DateTimeZone.UTC );
		
		int processed = -1;
		int count = 0;
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		
		boolean inError;

		List<Integer> channelsChecked = new ArrayList<Integer>();
		
		for( Program program : programs ) {
			Log.d( TAG, "load : count=" + count );
			
			if( null == program.getStartTime() || null == program.getEndTime() ) {
//				Log.w(TAG, "load : null starttime and or endtime" );
			
				inError = true;
			} else {
				inError = false;
			}

			DateTime startTime = program.getStartTime();

			// load upcoming program
			ProgramHelperV27.getInstance().processProgram( context, locationProfile, ProgramConstants.CONTENT_URI_UPCOMING, ProgramConstants.TABLE_NAME_UPCOMING, ops, program, lastModified, startTime, count );
			count++;
			
			// update program guide
			ProgramHelperV27.getInstance().processProgram( context, locationProfile, ProgramConstants.CONTENT_URI_GUIDE, ProgramConstants.TABLE_NAME_GUIDE, ops, program, lastModified, startTime, count );
			count++;

			if( null != program.getChannel() ) {

				if( !channelsChecked.contains( program.getChannel().getChanId() ) ) {
					
					ChannelHelperV27.getInstance().processChannel( context, locationProfile, ops, program.getChannel(), lastModified, count );
					count++;
					
					channelsChecked.add( program.getChannel().getChanId() );
			
				}

			}
			
			if( !inError && null != program.getRecording() ) {
				
				if( program.getRecording().getRecordId() > 0 ) {
				
					// load upcoming recording
					RecordingHelperV27.getInstance().processRecording( context, locationProfile, ops, RecordingConstants.ContentDetails.UPCOMING, program, lastModified, startTime, count );
					count++;

					// update program guide recording
					RecordingHelperV27.getInstance().processRecording( context, locationProfile, ops, RecordingConstants.ContentDetails.GUIDE, program, lastModified, startTime, count );
					count++;

				}
				
			}
			
			if( count > BATCH_COUNT_LIMIT ) {
//				Log.i( TAG, "load : applying batch for '" + count + "' transactions, processing programs" );
				
				processBatch( context, ops, processed, count );

				count = 0;
			}
			
		}

		processBatch( context, ops, processed, count );

//		Log.v( TAG, "load : DELETE PROGRAMS" );
		ProgramHelperV27.getInstance().deletePrograms( context, locationProfile, ops, ProgramConstants.CONTENT_URI_UPCOMING, ProgramConstants.TABLE_NAME_UPCOMING, today );

//		Log.v( TAG, "load : DELETE RECORDINGS" );
		RecordingHelperV27.getInstance().deleteRecordings( ops, RecordingConstants.ContentDetails.UPCOMING, today );

		processBatch( context, ops, processed, count );

//		Log.v( TAG, "load : exit" );
		return processed;
	}

}
