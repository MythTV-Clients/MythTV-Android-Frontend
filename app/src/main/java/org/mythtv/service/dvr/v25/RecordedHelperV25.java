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
package org.mythtv.service.dvr.v25;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.joda.time.DateTime;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.db.AbstractBaseHelper;
import org.mythtv.db.channel.ChannelDaoHelper;
import org.mythtv.db.content.LiveStreamConstants;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.dvr.RecordingConstants;
import org.mythtv.db.dvr.RemoveStreamTask;
import org.mythtv.db.dvr.programGroup.ProgramGroup;
import org.mythtv.db.dvr.programGroup.ProgramGroupConstants;
import org.mythtv.db.dvr.programGroup.ProgramGroupDaoHelper;
import org.mythtv.service.channel.v25.ChannelHelperV25;
import org.mythtv.service.util.NetworkHelper;
import org.mythtv.services.api.ApiVersion;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.MythServiceApiRuntimeException;
import org.mythtv.services.api.connect.MythAccessFactory;
import org.mythtv.services.api.v025.MythServicesTemplate;
import org.mythtv.services.api.v025.beans.Program;
import org.mythtv.services.api.v025.beans.ProgramList;
import org.mythtv.services.utils.ArticleCleaner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class RecordedHelperV25 extends AbstractBaseHelper {

	private static final String TAG = RecordedHelperV25.class.getSimpleName();
	
	private static final ApiVersion mApiVersion = ApiVersion.v025;
	
	private static MythServicesTemplate mMythServicesTemplate;

	private static RecordedHelperV25 singleton;
	
	private final ChannelDaoHelper mChannelDaoHelper = ChannelDaoHelper.getInstance();
	
	/**
	 * Returns the one and only RecordedHelperV25. init() must be called before 
	 * any 
	 * @return
	 */
	public static RecordedHelperV25 getInstance() {
		if( null == singleton ) {
			
			synchronized( RecordedHelperV25.class ) {

				if( null == singleton ) {
					singleton = new RecordedHelperV25();
				}
			
			}
			
		}
		
		return singleton;
	}
	
	/**
	 * Constructor. No one but getInstance() can do this.
	 */
	private RecordedHelperV25() { }

	public boolean process( final Context context, final LocationProfile locationProfile ) {
		Log.v( TAG, "process : enter" );
		
		if( !NetworkHelper.getInstance().isMasterBackendConnected( context, locationProfile ) ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}
		
		mMythServicesTemplate = (MythServicesTemplate) MythAccessFactory.getServiceTemplateApiByVersion( mApiVersion, locationProfile.getUrl() );
		if( null == mMythServicesTemplate ) {
			Log.w( TAG, "process : Master Backend '" + locationProfile.getHostname() + "' is unreachable" );
			
			return false;
		}
		
		boolean passed = true;

		try {

			downloadRecorded( context, locationProfile );
			
		} catch( Exception e ) {

			if( e.toString().contains( "Invalid UTF-8" ) ) {
				Log.e( TAG, "process : INVALID UTF-8! Start mythbackend with valid LANG & LC_ALL (e.g. en_US.UTF-8" );
			}
			else {
				Log.e( TAG, "process : non UTF-8 exception ", e );
			}

			passed = false;
		}

		Log.v( TAG, "process : exit" );
		return passed;
	}
	
	public Integer countRecordedByTitle( final Context context, final LocationProfile locationProfile, String title ) {
		Log.v( TAG, "countRecordedByTitle : enter" );
		
		Integer count = ProgramHelperV25.getInstance().countProgramsByTitle( context, locationProfile, ProgramConstants.CONTENT_URI_RECORDED, ProgramConstants.TABLE_NAME_RECORDED, title );
//		Log.v( TAG, "countRecordedByTitle : count=" + count );
		
		Log.v( TAG, "countRecordedByTitle : exit" );
		return count;
	}
	
	public Program findRecorded( final Context context, final LocationProfile locationProfile, Integer channelId, DateTime startTime ) {
		Log.v( TAG, "findRecorded : enter" );
		
		Program program = ProgramHelperV25.getInstance().findProgram( context, locationProfile, ProgramConstants.CONTENT_URI_RECORDED, ProgramConstants.TABLE_NAME_RECORDED, channelId, startTime );
		
		Log.v( TAG, "findRecorded : enter" );
		return program;
	}
	
	public boolean deleteRecorded( final Context context, final LocationProfile locationProfile, Integer channelId, DateTime startTime, Integer recordId ) {
		Log.v( TAG, "deleteRecorded : enter" );
		
		boolean removed = false;
		
		ProgramHelperV25 programHelper = ProgramHelperV25.getInstance();
		
		Program program = programHelper.findProgram( context, locationProfile, ProgramConstants.CONTENT_URI_RECORDED, ProgramConstants.TABLE_NAME_RECORDED, channelId, startTime );
		if( null != program ) {
			Log.v( TAG, "deleteRecorded : program found!" );
			
			String title = program.getTitle();
			
			removed = programHelper.deleteProgram( context, locationProfile, ProgramConstants.CONTENT_URI_RECORDED, ProgramConstants.TABLE_NAME_RECORDED, program.getChannel().getChanId(), program.getStartTime(), program.getRecording().getStartTs(), recordId );
			if( removed ) {
//				Log.v( TAG, "deleteRecorded : program removed from backend" );

				Integer programCount = countRecordedByTitle( context, locationProfile, title );
				if( null == programCount ) {
//					Log.v( TAG, "deleteRecorded : programCount=" + programCount );

					ProgramGroupDaoHelper programGroupDaoHelper = ProgramGroupDaoHelper.getInstance();
				
					ProgramGroup programGroup = programGroupDaoHelper.findByTitle( context, locationProfile, title );
					if( null != programGroup ) {
//						Log.v( TAG, "deleteRecorded : programGroup found" );
						
						programGroupDaoHelper.delete( context, programGroup );
					}
					
				}
				
			}
		
		}
		
		Log.v( TAG, "deleteRecorded : exit" );
		return removed;
	}

	// internal helpers
	
	private void downloadRecorded( final Context context, final LocationProfile locationProfile ) throws MythServiceApiRuntimeException, RemoteException, OperationApplicationException {
		Log.v( TAG, "downloadRecorded : enter" );
	
		ResponseEntity<ProgramList> responseEntity = mMythServicesTemplate.dvrOperations().getRecordedList( Boolean.FALSE, null, null, ETagInfo.createEmptyETag() );

		if( responseEntity.getStatusCode().equals( HttpStatus.OK ) ) {
			Log.i( TAG, "download : GetRecordedList returned 200 OK" );
			ProgramList programList = responseEntity.getBody();

			if( null != programList.getPrograms() ) {

				load( context, locationProfile, programList.getPrograms() );	

			}

		}

		Log.v( TAG, "downloadRecorded : exit" );
	}
	
	private int load( final Context context, final LocationProfile locationProfile, final Program[] programs ) throws RemoteException, OperationApplicationException {
		Log.d( TAG, "load : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "RecordedHelperV25 is not initialized" );
		
		processProgramGroups( context, locationProfile, programs );

		String tag = UUID.randomUUID().toString();
		int processed = -1;
		int count = 0;
		
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		
		boolean inError;

		List<Integer> channelsChecked = new ArrayList<Integer>();
		
		for( Program program : programs ) {

			if( null != program.getRecording() && "livetv".equalsIgnoreCase( program.getRecording().getRecGroup() )  && !"deleted".equalsIgnoreCase( program.getRecording().getRecGroup() ) ) {
				Log.w( TAG, "load : program has no recording or program is in livetv or deleted recording groups:" + program.getTitle() + ":" + program.getSubTitle() + ":" + program.getChannel().getChanId() + ":" + program.getStartTime() + ":" + program.getHostName() + " (" + ( null == program.getRecording() ? "No Recording" : ( "livetv".equalsIgnoreCase( program.getRecording().getRecGroup() ) ? "LiveTv" : "Deleted" ) ) + ")" );

				continue;
			}
			
			if( null == program.getStartTime() || null == program.getEndTime() ) {
				Log.w( TAG, "load : null starttime and or endtime" );
			
				inError = true;
			} else {
				inError = false;
			}

			ProgramHelperV25.getInstance().processProgram( context, locationProfile, ProgramConstants.CONTENT_URI_RECORDED, ProgramConstants.TABLE_NAME_RECORDED, ops, program, tag );
			count++;
			
			if( null != program.getChannel() ) {

				if( !channelsChecked.contains( program.getChannel().getChanId() ) ) {

					if( null == mChannelDaoHelper.findByChannelId( context, locationProfile, Long.parseLong( String.valueOf( program.getChannel().getChanId() ) ) ) ) {

						ChannelHelperV25.getInstance().processChannel( context, locationProfile, ops, program.getChannel() );
						count++;
					
					}
					
					channelsChecked.add( program.getChannel().getChanId() );
			
				}

			}
			
			if( !inError && null != program.getRecording() ) {
				
				if( program.getRecording().getRecordId() > 0 ) {
				
					RecordingHelperV25.getInstance().processRecording( context, locationProfile, ops, RecordingConstants.ContentDetails.RECORDED, program, tag );
					count++;
					
				}
				
			}
			
			if( count > BATCH_COUNT_LIMIT ) {
				Log.i( TAG, "load : applying batch for '" + count + "' transactions" );
				
				processBatch( context, ops, processed, count );

				count = 0;
				
			}
			
		}

		if( !ops.isEmpty() ) {
			Log.i( TAG, "load : applying final batch for '" + count + "' transactions" );
			
			processBatch( context, ops, processed, count );
		}

		ProgramHelperV25.getInstance().findAllPrograms( context, locationProfile, ProgramConstants.CONTENT_URI_RECORDED, ProgramConstants.TABLE_NAME_RECORDED );

		Log.v( TAG, "load : remove deleted recording live streams" );
		String[] deletedProjection = new String[] { ProgramConstants.FIELD_CHANNEL_ID, ProgramConstants.FIELD_START_TIME, ProgramConstants.FIELD_TITLE, ProgramConstants.FIELD_SUB_TITLE, ProgramConstants.FIELD_LAST_MODIFIED_DATE };
		String deletedSelection = "not " + ProgramConstants.TABLE_NAME_RECORDED + "." + ProgramConstants.FIELD_LAST_MODIFIED_TAG + " = ?";
		String[] deletedSelectionArgs = new String[] { tag };
			
		deletedSelection = appendLocationHostname( context, locationProfile, deletedSelection, ProgramConstants.TABLE_NAME_RECORDED );
		
		int deleteCount = 0;
		Cursor deletedCursor = context.getContentResolver().query( ProgramConstants.CONTENT_URI_RECORDED, deletedProjection, deletedSelection, deletedSelectionArgs, null );
		while( deletedCursor.moveToNext() ) {

			long channelId = deletedCursor.getLong( deletedCursor.getColumnIndex( ProgramConstants.FIELD_CHANNEL_ID ) );
			long startTime = deletedCursor.getLong( deletedCursor.getColumnIndex( ProgramConstants.FIELD_START_TIME ) );

			// Delete any live stream details
			String liveStreamSelection = LiveStreamConstants.FIELD_CHAN_ID + " = ? AND " + LiveStreamConstants.FIELD_START_TIME + " = ?";
			String[] liveStreamSelectionArgs = new String[] { String.valueOf( channelId ), String.valueOf( startTime ) };

			liveStreamSelection = appendLocationHostname( context, locationProfile, liveStreamSelection, LiveStreamConstants.TABLE_NAME );
				
			Cursor liveStreamCursor = context.getContentResolver().query( LiveStreamConstants.CONTENT_URI, null, liveStreamSelection, liveStreamSelectionArgs, null );
			if( liveStreamCursor.moveToFirst() ) {
				Log.v( TAG, "load : remove live stream" );

				int liveStreamId = liveStreamCursor.getInt( liveStreamCursor.getColumnIndex( LiveStreamConstants.TABLE_NAME + "." + LiveStreamConstants.FIELD_ID ) );
					
				RemoveStreamTask removeStreamTask = new RemoveStreamTask( context, locationProfile );
				removeStreamTask.execute( liveStreamId );
			}
			liveStreamCursor.close();

			deleteCount++;
		}
		deletedCursor.close();
		Log.v( TAG, "load : queued deleted programs - " + deleteCount );

		ProgramHelperV25.getInstance().deletePrograms( context, locationProfile, ProgramConstants.CONTENT_URI_RECORDED, ProgramConstants.TABLE_NAME_RECORDED, tag );
//		RecordingHelperV25.getInstance().deleteRecordings( context, locationProfile, ops, RecordingConstants.ContentDetails.RECORDED, lastModified );

		if( !ops.isEmpty() ) {
			Log.i( TAG, "load : applying delete batch for transactions" );
			
			processBatch( context, ops, processed, count );
		}
		
//		Log.v( TAG, "load : exit" );
		return processed;
	}

	private void processProgramGroups( final Context context, final LocationProfile locationProfile, Program[] programs ) throws RemoteException, OperationApplicationException {
		Log.v( TAG, "processProgramGroups : enter" );
		
		if( null == context ) 
			throw new RuntimeException( "RecordedHelperV25 is not initialized" );
		
		Map<String, ProgramGroup> programGroups = new TreeMap<String, ProgramGroup>();
		for( Program program : programs ) {
			
			if( null != program.getRecording() ) {
				
				if( null != program.getRecording().getRecGroup() && !"livetv".equalsIgnoreCase( program.getRecording().getRecGroup() ) && !"deleted".equalsIgnoreCase( program.getRecording().getRecGroup() ) ) {
					String cleaned = ArticleCleaner.clean( program.getTitle() );
					if( !programGroups.containsKey( cleaned ) ) {
						
						ProgramGroup programGroup = new ProgramGroup();
						programGroup.setTitle( program.getTitle() );
						programGroup.setCategory( program.getCategory() );
						programGroup.setInetref( program.getInetref() );
						programGroup.setSort( 0 );
						
						programGroups.put( cleaned, programGroup );
					}

				}
				
			}
			
		}
		
		int processed = -1;
		int count = 0;

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		Log.v( TAG, "processProgramGroups : adding 'All' program group in programGroups" );
		ProgramGroup all = new ProgramGroup( null, "All", "All", "All", "", 1 );
		programGroups.put( all.getProgramGroup(), all );
		
		String[] programGroupProjection = new String[] { ProgramGroupConstants._ID };
		String programGroupSelection = ProgramGroupConstants.FIELD_PROGRAM_GROUP + " = ?";

		programGroupSelection = appendLocationHostname( context, locationProfile, programGroupSelection, null );

		for( String key : programGroups.keySet() ) {
			Log.v( TAG, "processProgramGroups : processing programGroup '" + key + "'" );
			
			ProgramGroup programGroup = programGroups.get( key );
			
			ContentValues programValues = convertProgramGroupToContentValues( locationProfile, programGroup );
			Cursor programGroupCursor = context.getContentResolver().query( ProgramGroupConstants.CONTENT_URI, programGroupProjection, programGroupSelection, new String[] { key }, null );
			if( programGroupCursor.moveToFirst() ) {

				Long id = programGroupCursor.getLong( programGroupCursor.getColumnIndexOrThrow( ProgramGroupConstants._ID ) );
				ops.add( 
					ContentProviderOperation.newUpdate( ContentUris.withAppendedId( ProgramGroupConstants.CONTENT_URI, id ) )
						.withValues( programValues )
						.withYieldAllowed( true )
						.build()
				);
				
			} else {

				ops.add(  
					ContentProviderOperation.newInsert( ProgramGroupConstants.CONTENT_URI )
						.withValues( programValues )
						.withYieldAllowed( true )
						.build()
				);
			}
			programGroupCursor.close();
			count++;

			if( count > 100 ) {
				Log.v( TAG, "processProgramGroups : applying batch for '" + count + "' transactions" );

				processBatch( context, ops, processed, count );
			}

		}

		if( !ops.isEmpty() ) {
			Log.v( TAG, "processProgramGroups : applying batch for '" + count + "' transactions" );
			
			processBatch( context, ops, processed, count );
		}

		Log.v( TAG, "processProgramGroups : remove deleted program groups" );
		ops = new ArrayList<ContentProviderOperation>();
		
		DateTime lastModified = new DateTime();
		lastModified = lastModified.minusHours( 1 );
		
		String deleteProgramGroupSelection = ProgramGroupConstants.FIELD_LAST_MODIFIED_DATE + " < ?";
		String[] deleteProgramGroupArgs = new String[] { String.valueOf( lastModified.getMillis() ) };

		deleteProgramGroupSelection = appendLocationHostname( context, locationProfile, deleteProgramGroupSelection, ProgramGroupConstants.TABLE_NAME );

		ops.add(  
			ContentProviderOperation.newDelete( ProgramGroupConstants.CONTENT_URI )
			.withSelection( deleteProgramGroupSelection, deleteProgramGroupArgs )
			.withYieldAllowed( true )
			.build()
		);
			
		if( !ops.isEmpty() ) {
			Log.v( TAG, "processProgramGroups : applying batch for '" + count + "' transactions" );
			
			processBatch( context, ops, processed, count );
		}

		Log.v( TAG, "processProgramGroups : exit" );
	}

	private ContentValues convertProgramGroupToContentValues( final LocationProfile locationProfile, final ProgramGroup programGroup ) {
		
		ContentValues values = new ContentValues();
		values.put( ProgramGroupConstants.FIELD_PROGRAM_GROUP, null != programGroup.getTitle() ? ArticleCleaner.clean( programGroup.getTitle() ) : "" );
		values.put( ProgramGroupConstants.FIELD_TITLE, null != programGroup.getTitle() ? programGroup.getTitle() : "" );
		values.put( ProgramGroupConstants.FIELD_CATEGORY, null != programGroup.getCategory() ? programGroup.getCategory() : "" );
		values.put( ProgramGroupConstants.FIELD_INETREF, null != programGroup.getInetref() ? programGroup.getInetref() : "" );
		values.put( ProgramGroupConstants.FIELD_SORT, programGroup.getSort() );
		values.put( ProgramGroupConstants.FIELD_MASTER_HOSTNAME, locationProfile.getHostname() );
		values.put( ProgramGroupConstants.FIELD_LAST_MODIFIED_DATE, new DateTime().getMillis() );
		
		return values;
	}

}
