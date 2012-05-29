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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Daniel Frey
 * @author John Baab
 * 
 */
public class ProgramGroupActivity extends AbstractProgramGroupActivity implements ProgramGroupFragment.OnProgramListener{

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
		programGroup.setOnProgramListener( this );
		programGroup.loadPrograms( programs );
		
		Log.v( TAG, "onCreate : exit" );
	}

	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );

		super.onResume();

		Log.v( TAG, "onResume : exit" );
	}
	
	public void onProgramSelected( Program program ) {
		Log.d( TAG, "onProgramGroupSelected : enter" );

		Log.v( TAG, "onProgramGroupSelected : starting program group activity" );

		getApplicationContext().setCurrentProgram( program );
		
		Intent i = new Intent( this, VideoActivity.class );
		startActivity( i );

		Log.d( TAG, "onProgramGroupSelected : exit" );
	}

}
