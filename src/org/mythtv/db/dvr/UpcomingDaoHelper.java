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
package org.mythtv.db.dvr;

import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.services.api.dvr.Program;

import android.content.ContentUris;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class UpcomingDaoHelper extends ProgramDaoHelper {

	private static final String TAG = UpcomingDaoHelper.class.getSimpleName();
	
	private static UpcomingDaoHelper singleton = null;

	/**
	 * Returns the one and only UpcomingDaoHelper. init() must be called before 
	 * any 
	 * 
	 * @return
	 */
	public static UpcomingDaoHelper getInstance() {
		if( null == singleton ) {

			synchronized( UpcomingDaoHelper.class ) {

				if( null == singleton ) {
					singleton = new UpcomingDaoHelper();
				}
			
			}

		}
		
		return singleton;
	}
	
	private UpcomingDaoHelper() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see org.mythtv.db.dvr.ProgramDaoHelper#findAll()
	 */
	@Override
	public List<Program> findAll( final Context context, final LocationProfile locationProfile ) {
		Log.d( TAG, "findAll : enter" );
		
		String selection = appendLocationHostname( context, locationProfile, "", ProgramConstants.TABLE_NAME_UPCOMING );

		List<Program> programs = findAll( context, ProgramConstants.CONTENT_URI_UPCOMING, null, selection, null, null, ProgramConstants.TABLE_NAME_UPCOMING );
		
		Log.d( TAG, "findAll : exit" );
		return programs;
	}

	/**
	 * @param title
	 * @return
	 */
	public List<Program> findAllByTitle( final Context context, final LocationProfile locationProfile, final String title ) {
		Log.d( TAG, "findAllByTitle : enter" );
		
		String selection = ProgramConstants.FIELD_TITLE + " = ?";
		String[] selectionArgs = new String[] { title };

		selection = appendLocationHostname( context, locationProfile, selection, ProgramConstants.TABLE_NAME_UPCOMING );
		
		List<Program> programs = findAll( context, ProgramConstants.CONTENT_URI_UPCOMING, null, selection, selectionArgs, null, ProgramConstants.TABLE_NAME_UPCOMING );
		if( null != programs && !programs.isEmpty() ) {
			for( Program program : programs ) {
				Log.v( TAG, "findAllByTitle : channelId=" + program.getChannelInfo().getChannelId() + ", startTime=" + program.getStartTime().getMillis() + ", program=" + program.toString() );
			}
		}
		
		Log.d( TAG, "findAllByTitle : exit" );
		return programs;
	}

	/**
	 * @param id
	 * @return
	 */
	public Program findOne( final Context context, final Long id ) {
		Log.d( TAG, "findOne : enter" );
		Log.d( TAG, "findOne : id=" + id );
		
		Program program = findOne( context, ContentUris.withAppendedId( ProgramConstants.CONTENT_URI_UPCOMING, id ), null, null, null, null, ProgramConstants.TABLE_NAME_UPCOMING );
		if( null != program ) {
			Log.d( TAG, "findOne : program=" + program.toString() );
		}
		
		Log.d( TAG, "findOne : exit" );
		return program;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.db.dvr.ProgramDaoHelper#findOne(int, org.joda.time.DateTime)
	 */
	@Override
	public Program findOne( final Context context, final LocationProfile locationProfile, final int channelId, final DateTime startTime ) {
		Log.d( TAG, "findOne : enter" );
		
		String selection = ProgramConstants.TABLE_NAME_RECORDED + "." + ProgramConstants.FIELD_CHANNEL_ID + " = ? AND " + ProgramConstants.TABLE_NAME_RECORDED + "." + ProgramConstants.FIELD_START_TIME + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( channelId ), String.valueOf( startTime.getMillis() ) };

		selection = appendLocationHostname( context, locationProfile, selection, ProgramConstants.TABLE_NAME_UPCOMING );
		
		Program program = findOne( context, ProgramConstants.CONTENT_URI_UPCOMING, null, selection, selectionArgs, null, ProgramConstants.TABLE_NAME_UPCOMING );
		if( null != program ) {
			Log.v( TAG, "findOne : program=" + program.toString() );
		} else {
			Log.v( TAG, "findOne : program not found!" );
		}
		
		Log.d( TAG, "findOne : exit" );
		return program;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.db.dvr.ProgramDaoHelper#save(org.mythtv.services.api.dvr.Program)
	 */
	@Override
	public int save( final Context context, final LocationProfile locationProfile, Program program ) {
		Log.d( TAG, "save : enter" );

		int saved = save( context, ProgramConstants.CONTENT_URI_UPCOMING, locationProfile, program, ProgramConstants.TABLE_NAME_UPCOMING );
		
		Log.d( TAG, "save : exit" );
		return saved;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.db.dvr.ProgramDaoHelper#deleteAll()
	 */
	@Override
	public int deleteAll( final Context context ) {
		Log.d( TAG, "deleteAll : enter" );

		int deleted = deleteAll( context, ProgramConstants.CONTENT_URI_UPCOMING );
		
		Log.d( TAG, "deleteAll : exit" );
		return deleted;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.db.dvr.ProgramDaoHelper#delete(org.mythtv.services.api.dvr.Program)
	 */
	@Override
	public int delete( final Context context, final LocationProfile locationProfile, Program program ) {
		Log.d( TAG, "delete : enter" );

		int deleted = delete( context, ProgramConstants.CONTENT_URI_UPCOMING, locationProfile, program, ProgramConstants.TABLE_NAME_UPCOMING );
		
		Log.d( TAG, "delete : exit" );
		return deleted;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.db.dvr.ProgramDaoHelper#load(java.util.List)
	 */
	@Override
	public int load( final Context context, final LocationProfile locationProfile, List<Program> programs ) throws RemoteException, OperationApplicationException {
		Log.d( TAG, "load : enter" );

		int loaded = load( context, ProgramConstants.CONTENT_URI_UPCOMING, locationProfile, programs, ProgramConstants.TABLE_NAME_UPCOMING );
		Log.d( TAG, "load : loaded=" + loaded );
		
		Log.d( TAG, "load : exit" );
		return loaded;
	}

	/**
	 * @param cursor
	 * @return
	 */
	public Program convertCursorToProgram( Cursor cursor ) {
		return convertCursorToProgram( cursor, ProgramConstants.TABLE_NAME_UPCOMING );
	}

}
