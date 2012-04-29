/**
 *  This file is part of MythTV for Android
 * 
 *  MythTV for Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  MythTV for Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MythTV for Android.  If not, see <http://www.gnu.org/licenses/>.
 *   
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */
package org.mythtv.client.ui.dvr;

import java.util.List;

import org.mythtv.R;
import org.mythtv.services.api.dvr.Program;

import android.os.Bundle;
import android.util.Log;

/**
 * @author Daniel Frey
 * 
 */
public class ProgramGroupActivity extends AbstractRecordingsActivity {

	private static final String TAG = ProgramGroupActivity.class.getSimpleName();

	public static final String EXTRA_PROGRAM_GROUP_KEY = "org.mythtv.client.ui.dvr.programGroup.EXTRA_PROGRAM_GROUP_KEY";

	private ProgramGroupFragment programGroup = null;

	// ***************************************
	// Activity methods
	// ***************************************

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		setContentView( R.layout.fragment_dvr_program_group );

		programGroup = (ProgramGroupFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_program_group );

		List<Program> programs = getApplicationContext().getCurrentRecordingsInProgramGroup();
		programGroup.loadPrograms( programs );
		
		//String key = getIntent().getStringExtra( EXTRA_PROGRAM_GROUP_KEY );

		//if( key != null ) {
		//	Log.v( TAG, "onCreate : loading program group fragment for '" + key + "'" );

		//	setTitle( key );
		//	programGroup.loadPrograms( key );
		//}

		// if( getResources().getConfiguration().orientation ==
		// Configuration.ORIENTATION_LANDSCAPE ) {
		// // If the screen is now in landscape mode, we can show the
		// // dialog in-line with the list so we don't need this activity.
		// finish();
		// return;
		// }
		//
		// if( savedInstanceState == null ) {
		// Log.v( TAG, "onCreate : setting up fragment" );
		//
		// // During initial setup, plug in the details fragment.
		// ProgramGroupListFragment programGroup = new
		// ProgramGroupListFragment();
		// programGroup.setArguments( getIntent().getExtras() );
		// getSupportFragmentManager().beginTransaction().add(
		// android.R.id.content, programGroup ).commit();
		// }

		Log.v( TAG, "onCreate : exit" );
	}

	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );

		super.onResume();

//		items.setOnItemListener( this );

		Log.v( TAG, "onResume : exit" );
	}

	@Override
	public void onProgramGroupSelected( List<Program> programs ) {
		Log.v( TAG, "onProgramGroupSelected : enter" );
		
		//programGroup.loadPrograms( programs );

		Log.v( TAG, "onProgramGroupSelected : exit" );
	}

//	public void onItemSelected( RSSItem item ) {
//		Log.v( TAG, "onItemSelected : enter" );
//	
//		startActivity( new Intent( Intent.ACTION_VIEW, item.getLink() ) );
//
//	Log.v( TAG, "onItemSelected : exit" );
//	}

}
