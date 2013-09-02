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
import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.db.content.LiveStreamConstants;
import org.mythtv.db.dvr.DvrEndpoint;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.dvr.RecordingConstants;
import org.mythtv.db.dvr.RecordingConstants.ContentDetails;
import org.mythtv.db.dvr.RemoveStreamTask;
import org.mythtv.db.http.model.EtagInfoDelegate;
import org.mythtv.service.channel.v26.ChannelHelperV26;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v026.MythServicesTemplate;
import org.mythtv.services.api.v026.beans.Program;
import org.mythtv.services.api.v026.beans.ProgramList;
import org.mythtv.services.api.v026.impl.DvrTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class RecordedHelperV26 extends AbstractBaseHelper {

	private static final String TAG = RecordedHelperV26.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v026;
	
	private static MythServicesTemplate mMythServicesTemplate;

	public static boolean process( final Context context, final LocationProfile locationProfile ) {
		Log.v( TAG, "process : enter" );
		
		if( !MythAccessFactory.isServerReachable( locationProfile.getUrl() ) ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}
		
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		
		boolean passed = true;

		try {

			downloadRecorded( context, locationProfile );
			
		} catch( Exception e ) {
			Log.e( TAG, "process : error", e );
		
			passed = false;
		}

		Log.v( TAG, "process : exit" );
		return passed;
	}
	
	public static boolean deleteRecorded( final Context context, final LocationProfile locationProfile, Integer channelId, DateTime startTime ) {
		Log.v( TAG, "deleteRecorded : enter" );
		
		boolean removed = ProgramHelperV26.deleteProgram( context, locationProfile, ProgramConstants.CONTENT_URI_RECORDED, ProgramConstants.TABLE_NAME_RECORDED, channelId, startTime );
		
		Log.v( TAG, "deleteRecorded : enter" );
		return removed;
	}
	
	// internal helpers
	
	private static void downloadRecorded( final Context context, final LocationProfile locationProfile ) throws RemoteException, OperationApplicationException {
		Log.v( TAG, "downloadRecorded : enter" );
	
		EtagInfoDelegate etag = mEtagDaoHelper.findByEndpointAndDataId( context, locationProfile, DvrTemplate.Endpoint.GET_RECORDED_LIST.name(), "" );
		
		ResponseEntity<ProgramList> responseEntity = mMythServicesTemplate.dvrOperations().getRecordedList( etag );

		DateTime date = new DateTime( DateTimeZone.UTC );
		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			Log.i( TAG, "download : " + DvrEndpoint.GET_RECORDED_LIST.getEndpoint() + " returned 200 OK" );
			ProgramList programList = responseEntity.getBody();

			if( null != programList.getPrograms() ) {

				load( context, locationProfile, programList.getPrograms().getPrograms() );	

				if( null != etag.getValue() ) {
					Log.i( TAG, "download : saving etag: " + etag.getValue() );
					
					etag.setEndpoint( DvrEndpoint.GET_RECORDED_LIST.name() );
					etag.setDate( date );
					etag.setMasterHostname( locationProfile.getHostname() );
					etag.setLastModified( date );
					mEtagDaoHelper.save( context, locationProfile, etag );
				}

			}

		}

		if( responseEntity.getStatusCode().equals( HttpStatus.NOT_MODIFIED ) ) {
			Log.i( TAG, "download : " + DvrEndpoint.GET_RECORDED_LIST.getEndpoint() + " returned 304 Not Modified" );

			if( null != etag.getValue() ) {
				Log.i( TAG, "download : saving etag: " + etag.getValue() );

				etag.setLastModified( date );
				mEtagDaoHelper.save( context, locationProfile, etag );
			}

		}

		Log.v( TAG, "downloadRecorded : exit" );
	}
	
	private static int load( final Context context, final LocationProfile locationProfile, final List<Program> programs ) throws RemoteException, OperationApplicationException {
		Log.d( TAG, "load : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "RecordedHelperV26 is not initialized" );
		
		DateTime today = new DateTime( DateTimeZone.UTC ).withTimeAtStartOfDay();
		DateTime lastModified = new DateTime( DateTimeZone.UTC );
		
		int processed = -1;
		int count = 0;
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		
		boolean inError;

		ContentDetails details = RecordingConstants.ContentDetails.getValueFromParent( ProgramConstants.TABLE_NAME_RECORDED );
//		Log.w(TAG, "load : details - parent=" + details.getParent() + ", tableName=" + details.getTableName() + ", contentUri=" + details.getContentUri().toString() );

		List<Integer> channelsChecked = new ArrayList<Integer>();
		
		for( Program program : programs ) {

			
			if( null != program.getRecording() && "livetv".equalsIgnoreCase( program.getRecording().getRecordingGroup() )  && !"deleted".equalsIgnoreCase( program.getRecording().getRecordingGroup() ) ) {
				continue;
			}
			
			if( null == program.getStartTime() || null == program.getEndTime() ) {
//				Log.w(TAG, "load : null starttime and or endtime" );
			
				inError = true;
			} else {
				inError = false;
			}

			DateTime startTime = program.getStartTime();
			
			ProgramHelperV26.processProgram( context, locationProfile, ProgramConstants.CONTENT_URI_RECORDED, ProgramConstants.TABLE_NAME_RECORDED, ops, program, lastModified, startTime, count );

			if( null != program.getChannelInfo() ) {

				if( !channelsChecked.contains( program.getChannelInfo().getChannelId() ) ) {
					
					ChannelHelperV26.processChannel( context, locationProfile, ops, program.getChannelInfo(), lastModified, count );
					
					channelsChecked.add( program.getChannelInfo().getChannelId() );
			
				}

			}
			
			if( !inError && null != program.getRecording() ) {
				
				if( program.getRecording().getRecordId() > 0 ) {
				
					RecordingHelperV26.processRecording( context, locationProfile, ops, details, program, lastModified, startTime, count );

				}
				
			}
			
			if( count > BATCH_COUNT_LIMIT ) {
//				Log.i( TAG, "load : applying batch for '" + count + "' transactions, processing programs" );
				
				processBatch( context, ops, processed, count );

			}
			
		}

		processBatch( context, ops, processed, count );

//		Log.v( TAG, "load : remove deleted recordings" );
		String deletedSelection = ProgramConstants.TABLE_NAME_RECORDED + "." + ProgramConstants.FIELD_LAST_MODIFIED + " < ?";
		String[] deletedSelectionArgs = new String[] { String.valueOf( today.getMillis() ) };
			
		deletedSelection = appendLocationHostname( context, locationProfile, deletedSelection, ProgramConstants.TABLE_NAME_RECORDED );
			
		Cursor deletedCursor = context.getContentResolver().query( ProgramConstants.CONTENT_URI_RECORDED, null, deletedSelection, deletedSelectionArgs, null );
		while( deletedCursor.moveToNext() ) {
//			Log.v( TAG, "load : remove deleted recording - " + program.getTitle() + " [" + program.getSubTitle() + "]" );

			long channelId = deletedCursor.getLong( deletedCursor.getColumnIndex( ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_CHAN_ID ) );
			long startTime = deletedCursor.getLong( deletedCursor.getColumnIndex( ProgramConstants.TABLE_NAME_RECORDED + "." + ProgramConstants.FIELD_START_TIME ) );
				
			// Delete any live stream details
			String liveStreamSelection = LiveStreamConstants.FIELD_CHAN_ID + " = ? AND " + LiveStreamConstants.FIELD_START_TIME + " = ?";
			String[] liveStreamSelectionArgs = new String[] { String.valueOf( channelId ), String.valueOf( startTime ) };

			liveStreamSelection = appendLocationHostname( context, locationProfile, liveStreamSelection, LiveStreamConstants.TABLE_NAME );
				
			Cursor liveStreamCursor = context.getContentResolver().query( LiveStreamConstants.CONTENT_URI, null, liveStreamSelection, liveStreamSelectionArgs, null );
			if( liveStreamCursor.moveToFirst() ) {
//				Log.v( TAG, "load : remove live stream" );

				int liveStreamId = liveStreamCursor.getInt( liveStreamCursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "." + LiveStreamConstants.FIELD_ID ) );
					
				RemoveStreamTask removeStreamTask = new RemoveStreamTask();
				removeStreamTask.setLocationProfile( locationProfile );
				removeStreamTask.execute( liveStreamId );

			}
			liveStreamCursor.close();

		}
		deletedCursor.close();

//		Log.v( TAG, "load : DELETE PROGRAMS" );
		ProgramHelperV26.deletePrograms( context, locationProfile, ops, ProgramConstants.CONTENT_URI_RECORDED, ProgramConstants.TABLE_NAME_RECORDED, today );

//		Log.v( TAG, "load : DELETE RECORDINGS" );
		RecordingHelperV26.deleteRecordings( ops, details, today );

		processBatch( context, ops, processed, count );

//		Log.v( TAG, "load : exit" );
		return processed;
	}

}
