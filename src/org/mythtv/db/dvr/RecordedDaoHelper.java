/**
 * 
 */
package org.mythtv.db.dvr;

import java.util.List;

import org.joda.time.DateTime;
import org.mythtv.db.dvr.programGroup.ProgramGroupDaoHelper;
import org.mythtv.services.api.dvr.Program;

import android.content.Context;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class RecordedDaoHelper extends ProgramDaoHelper {

	private static final String TAG = RecordedDaoHelper.class.getSimpleName();
	
	private ProgramGroupDaoHelper mProgramGroupDaoHelper;
	
	public RecordedDaoHelper( Context context ) {
		super( context );
		
		mProgramGroupDaoHelper = new ProgramGroupDaoHelper( context );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.db.dvr.ProgramDaoHelper#finalAll()
	 */
	@Override
	public List<Program> finalAll() {
		Log.d( TAG, "findAll : enter" );
		
		List<Program> programs = findAll( ProgramConstants.CONTENT_URI_RECORDED, null, null, null, null );
		
		Log.d( TAG, "findAll : exit" );
		return programs;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.db.dvr.ProgramDaoHelper#findOne(int, org.joda.time.DateTime)
	 */
	@Override
	public Program findOne( Long channelId, DateTime startTime ) {
		Log.d( TAG, "findOne : enter" );
		
		String selection = ProgramConstants.FIELD_CHANNEL_ID + " = ? AND " + ProgramConstants.FIELD_START_TIME + " = ?";
		String[] selectionArgs = new String[] { String.valueOf( channelId ), String.valueOf( startTime.getMillis() ) };

		Program program = findOne( ProgramConstants.CONTENT_URI_RECORDED, null, selection, selectionArgs, null );

		Log.d( TAG, "findOne : exit" );
		return program;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.db.dvr.ProgramDaoHelper#save(org.mythtv.services.api.dvr.Program)
	 */
	@Override
	public int save( Program program ) {
		Log.d( TAG, "save : enter" );

		int saved = save( ProgramConstants.CONTENT_URI_RECORDED, program );
		
		Log.d( TAG, "save : exit" );
		return saved;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.db.dvr.ProgramDaoHelper#deleteAll()
	 */
	@Override
	public int deleteAll() {
		Log.d( TAG, "deleteAll : enter" );

		int deleted = deleteAll( ProgramConstants.CONTENT_URI_RECORDED );
		
		Log.d( TAG, "deleteAll : exit" );
		return deleted;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.db.dvr.ProgramDaoHelper#delete(org.mythtv.services.api.dvr.Program)
	 */
	@Override
	public int delete( Program program ) {
		Log.d( TAG, "delete : enter" );

		int deleted = delete( ProgramConstants.CONTENT_URI_RECORDED, program );
		
		Log.d( TAG, "delete : exit" );
		return deleted;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.db.dvr.ProgramDaoHelper#load(java.util.List)
	 */
	@Override
	public int load( List<Program> programs ) {
		Log.d( TAG, "load : enter" );

		deleteAll();
		
		int loaded = load( ProgramConstants.CONTENT_URI_RECORDED, programs );
		Log.d( TAG, "load : loaded=" + loaded );
		
		mProgramGroupDaoHelper.load( programs );
		
		Log.d( TAG, "load : exit" );
		return loaded;
	}

}
