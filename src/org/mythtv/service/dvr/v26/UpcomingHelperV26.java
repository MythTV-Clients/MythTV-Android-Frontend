/**
 * 
 */
package org.mythtv.service.dvr.v26;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.dvr.DvrEndpoint;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.dvr.RecordingConstants;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.service.channel.v26.ChannelHelperV26;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v026.MythServicesTemplate;
import org.mythtv.services.api.v026.beans.Program;
import org.mythtv.services.api.v026.beans.ProgramList;
import org.mythtv.services.api.v026.impl.DvrTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class UpcomingHelperV26 extends AbstractBaseHelper {

	private static final String TAG = UpcomingHelperV26.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v026;
	
	private static MythServicesTemplate mMythServicesTemplate;

	private static UpcomingHelperV26 singleton;
	
	/**
	 * Returns the one and only UpcomingHelperV26. init() must be called before 
	 * any 
	 * @return
	 */
	public static UpcomingHelperV26 getInstance() {
		if( null == singleton ) {
			
			synchronized( UpcomingHelperV26.class ) {

				if( null == singleton ) {
					singleton = new UpcomingHelperV26();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private UpcomingHelperV26() { }

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
	
		EtagInfoDelegate etag = mEtagDaoHelper.findByEndpointAndDataId( context, locationProfile, DvrTemplate.Endpoint.GET_UPCOMING_LIST.name(), "" );
		Log.d( TAG, "downloadUpcoming : etag=" + etag.getValue() );
		
		ResponseEntity<ProgramList> responseEntity = mMythServicesTemplate.dvrOperations().getRecordedList( etag );

		DateTime date = new DateTime( DateTimeZone.UTC );
		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			Log.i( TAG, "downloadUpcoming : " + DvrEndpoint.GET_UPCOMING_LIST.getEndpoint() + " returned 200 OK" );
			ProgramList programList = responseEntity.getBody();

			if( null != programList.getPrograms() ) {

				load( context, locationProfile, programList.getPrograms().getPrograms() );	

				if( null != etag.getValue() ) {
					Log.i( TAG, "downloadUpcoming : saving etag: " + etag.getValue() );
					
					etag.setEndpoint( DvrEndpoint.GET_UPCOMING_LIST.name() );
					etag.setDate( date );
					etag.setMasterHostname( locationProfile.getHostname() );
					etag.setLastModified( date );
					mEtagDaoHelper.save( context, locationProfile, etag );
				}

			}

		}

		if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
			Log.i( TAG, "downloadUpcoming : " + DvrEndpoint.GET_UPCOMING_LIST.getEndpoint() + " returned 304 Not Modified" );

			if( null != etag.getValue() ) {
				Log.i( TAG, "downloadUpcoming : saving etag: " + etag.getValue() );

				etag.setLastModified( date );
				mEtagDaoHelper.save( context, locationProfile, etag );
			}

		}

		Log.v( TAG, "downloadUpcoming : exit" );
	}
	
	private int load( final Context context, final LocationProfile locationProfile, final List<Program> programs ) throws RemoteException, OperationApplicationException {
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

			if( null == program.getStartTime() || null == program.getEndTime() ) {
//				Log.w(TAG, "load : null starttime and or endtime" );
			
				inError = true;
			} else {
				inError = false;
			}

			DateTime startTime = program.getStartTime();
			
			// load upcoming program
			ProgramHelperV26.getInstance().processProgram( context, locationProfile, ProgramConstants.CONTENT_URI_UPCOMING, ProgramConstants.TABLE_NAME_UPCOMING, ops, program, lastModified, startTime, count );
			// update program guide
			ProgramHelperV26.getInstance().processProgram( context, locationProfile, ProgramConstants.CONTENT_URI_GUIDE, ProgramConstants.TABLE_NAME_GUIDE, ops, program, lastModified, startTime, count );

			if( null != program.getChannelInfo() ) {

				if( !channelsChecked.contains( program.getChannelInfo().getChannelId() ) ) {
					
					ChannelHelperV26.getInstance().processChannel( context, locationProfile, ops, program.getChannelInfo(), lastModified, count );
					
					channelsChecked.add( program.getChannelInfo().getChannelId() );
			
				}

			}
			
			if( !inError && null != program.getRecording() ) {
				
				if( program.getRecording().getRecordId() > 0 ) {
				
					// load upcoming recording
					RecordingHelperV26.getInstance().processRecording( context, locationProfile, ops, RecordingConstants.ContentDetails.UPCOMING, program, lastModified, startTime, count );
					// update program guide recording
					RecordingHelperV26.getInstance().processRecording( context, locationProfile, ops, RecordingConstants.ContentDetails.GUIDE, program, lastModified, startTime, count );

				}
				
			}
			
			if( count > BATCH_COUNT_LIMIT ) {
//				Log.i( TAG, "load : applying batch for '" + count + "' transactions, processing programs" );
				
				processBatch( context, ops, processed, count );

			}
			
		}

		processBatch( context, ops, processed, count );

//		Log.v( TAG, "load : DELETE PROGRAMS" );
		ProgramHelperV26.getInstance().deletePrograms( context, locationProfile, ops, ProgramConstants.CONTENT_URI_UPCOMING, ProgramConstants.TABLE_NAME_UPCOMING, today );

//		Log.v( TAG, "load : DELETE RECORDINGS" );
		RecordingHelperV26.getInstance().deleteRecordings( ops, RecordingConstants.ContentDetails.UPCOMING, today );

		processBatch( context, ops, processed, count );

//		Log.v( TAG, "load : exit" );
		return processed;
	}

	protected ContentValues convertProgramToContentValues( final LocationProfile locationProfile, final DateTime lastModified, final Program program ) {
		
		boolean inError;
		
		DateTime startTime = new DateTime( DateTimeZone.UTC );
		DateTime endTime = new DateTime( DateTimeZone.UTC );

		// If one timestamp is bad, leave them both set to 0.
		if( null == program.getStartTime() || null == program.getEndTime() ) {
//			Log.w(TAG, "convertProgramToContentValues : null starttime and or endtime" );
		
			inError = true;
		} else {
			startTime = program.getStartTime();
			endTime = program.getEndTime();
			
			inError = false;
		}

		ContentValues values = new ContentValues();
		values.put( ProgramConstants.FIELD_START_TIME, startTime.getMillis() );
		values.put( ProgramConstants.FIELD_END_TIME, endTime.getMillis() );
		values.put( ProgramConstants.FIELD_TITLE, null != program.getTitle() ? program.getTitle() : "" );
		values.put( ProgramConstants.FIELD_SUB_TITLE, null != program.getSubTitle() ? program.getSubTitle() : "" );
		values.put( ProgramConstants.FIELD_CATEGORY, null != program.getCategory() ? program.getCategory() : "" );
		values.put( ProgramConstants.FIELD_CATEGORY_TYPE, null != program.getCategoryType() ? program.getCategoryType() : "" );
		values.put( ProgramConstants.FIELD_REPEAT, program.isRepeat() ? 1 : 0 );
		values.put( ProgramConstants.FIELD_VIDEO_PROPS, program.getVideoProps() );
		values.put( ProgramConstants.FIELD_AUDIO_PROPS, program.getAudioProps() );
		values.put( ProgramConstants.FIELD_SUB_PROPS, program.getSubProps() );
		values.put( ProgramConstants.FIELD_SERIES_ID, null != program.getSeriesId() ? program.getSeriesId() : "" );
		values.put( ProgramConstants.FIELD_PROGRAM_ID, null != program.getProgramId() ? program.getProgramId() : "" );
		values.put( ProgramConstants.FIELD_STARS, program.getStars() );
		values.put( ProgramConstants.FIELD_FILE_SIZE, null != program.getFileSize() ? program.getFileSize() : "" );
		values.put( ProgramConstants.FIELD_LAST_MODIFIED, null != program.getLastModified() ? DateUtils.dateTimeFormatter.print( program.getLastModified() ) : "" );
		values.put( ProgramConstants.FIELD_PROGRAM_FLAGS, null != program.getProgramFlags() ? program.getProgramFlags() : "" );
		values.put( ProgramConstants.FIELD_HOSTNAME, null != program.getHostname() ? program.getHostname() : "" );
		values.put( ProgramConstants.FIELD_FILENAME, null != program.getFilename() ? program.getFilename() : "" );
		values.put( ProgramConstants.FIELD_AIR_DATE, null != program.getAirDate() ? DateUtils.dateTimeFormatter.print( program.getAirDate() ) : "" );
		values.put( ProgramConstants.FIELD_DESCRIPTION, null != program.getDescription() ? program.getDescription() : "" );
		values.put( ProgramConstants.FIELD_INETREF, null != program.getInetref() ? program.getInetref() : "" );
		values.put( ProgramConstants.FIELD_SEASON, null != program.getSeason() ? program.getSeason() : "" );
		values.put( ProgramConstants.FIELD_EPISODE, null != program.getEpisode() ? program.getEpisode() : "" );
		values.put( ProgramConstants.FIELD_CHANNEL_ID, null != program.getChannelInfo() ? program.getChannelInfo().getChannelId() : -1 );
		values.put( ProgramConstants.FIELD_RECORD_ID, null != program.getRecording() ? program.getRecording().getRecordId() : -1 );
		values.put( ProgramConstants.FIELD_IN_ERROR, inError ? 1 : 0 );
		values.put( ProgramConstants.FIELD_MASTER_HOSTNAME, locationProfile.getHostname() );
		values.put( ProgramConstants.FIELD_LAST_MODIFIED_DATE, lastModified.getMillis() );
		
		return values;
	}

}
