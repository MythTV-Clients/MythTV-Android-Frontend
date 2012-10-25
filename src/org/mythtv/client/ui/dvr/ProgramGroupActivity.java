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
package org.mythtv.client.ui.dvr;

import org.mythtv.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;

/**
 * @author Daniel Frey
 * @author John Baab
 * 
 */
public class ProgramGroupActivity extends AbstractDvrActivity implements ProgramGroupFragment.OnEpisodeSelectedListener {

	private static final String TAG = ProgramGroupActivity.class.getSimpleName();

	public static final String EXTRA_PROGRAM_GROUP_KEY = "org.mythtv.client.ui.dvr.programGroup.EXTRA_PROGRAM_GROUP_KEY";

	private ProgramGroupFragment programGroupFragment = null;
	

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

		setContentView( R.layout.activity_dvr_program_group );

		Bundle extras = getIntent().getExtras(); 
		String programGroup = extras.getString( EXTRA_PROGRAM_GROUP_KEY );

		programGroupFragment = (ProgramGroupFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_program_group );
		programGroupFragment.setOnEpisodeSelectedListener(this);
		programGroupFragment.loadProgramGroup( programGroup );
		
		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );

		switch( item.getItemId() ) {
			case android.R.id.home:
				// app icon in action bar clicked; go home
				Intent intent = new Intent( this, RecordingsActivity.class );
				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
				startActivity( intent );

				return true;
		}

		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	/**
	 * This is called when an episode is selected in the ProgramGroupFragment. This activity
	 * is used on smaller screens when the ProgramGroupFragment cannot be displayed
	 * as part of the RecordingsActivity. 
	 */
	@Override
	public void onEpisodeSelected(FragmentActivity activity, int position,
			long id) {
		
		Intent i = new Intent( activity, VideoActivity.class );
		i.putExtra( VideoActivity.EXTRA_PROGRAM_KEY, id );
		startActivity( i );
		
	}

	
	
}
