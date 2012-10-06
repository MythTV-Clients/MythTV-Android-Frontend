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
import org.mythtv.service.dvr.cache.ProgramGroupLruMemoryCache;
import org.mythtv.service.util.UrlUtils;
import org.mythtv.services.api.dvr.Programs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

/**
 * @author Daniel Frey
 * 
 */
public class RecordingsActivity extends AbstractDvrActivity implements RecordingsFragment.OnProgramGroupListener {

	private static final String TAG = RecordingsActivity.class.getSimpleName();
	
	private ProgramGroupLruMemoryCache cache;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		cache = new ProgramGroupLruMemoryCache( this );

		setContentView( R.layout.activity_dvr_recordings );

		RecordingsFragment recordingsFragment = (RecordingsFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_program_groups );
		recordingsFragment.setOnProgramGroupListener( this );

		Log.v( TAG, "onCreate : exit" );
	}

	public void onProgramGroupSelected( String programGroup ) {
		Log.d( TAG, "onProgramGroupSelected : enter" );
		
		if( null != findViewById( R.id.fragment_dvr_program_group ) ) {
			Log.v( TAG, "onProgramGroupSelected : adding program group to pane" );
			FragmentManager manager = getSupportFragmentManager();

			String cleaned = UrlUtils.encodeUrl( programGroup );

			Programs programs = cache.get( cleaned );
			if( null == programs || null == programs.getPrograms() || programs.getPrograms().isEmpty() ) {
				programs = ProgramGroupLruMemoryCache.getDownloadingPrograms( programGroup );
			}
			Log.d( TAG, "onProgramGroupSelected : programs=" + programs.toString() );
			
			ProgramGroupFragment programGroupFragment = (ProgramGroupFragment) manager.findFragmentById( R.id.fragment_dvr_program_group );
			FragmentTransaction transaction = manager.beginTransaction();

			if( null == programGroupFragment ) {
				Log.v( TAG, "onProgramGroupSelected : creating new programGroupFragment" );
				programGroupFragment = new ProgramGroupFragment();
			
				transaction
					.add( R.id.fragment_dvr_program_group, programGroupFragment )
					.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )
					.addToBackStack( null )
					.commit();
			}
			
			Log.v( TAG, "onProgramGroupSelected : setting program group to display" );
			programGroupFragment.loadPrograms( this, programs );
		} else {
			Log.v( TAG, "onProgramGroupSelected : starting program group activity" );

			Intent i = new Intent( this, ProgramGroupActivity.class );
			i.putExtra( ProgramGroupActivity.EXTRA_PROGRAM_GROUP_KEY, programGroup );
			startActivity( i );
		}

		Log.d( TAG, "onProgramGroupSelected : exit" );
	}

}
