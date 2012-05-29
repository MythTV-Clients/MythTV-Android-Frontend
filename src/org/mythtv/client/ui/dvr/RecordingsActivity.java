/**
 * 
 */
package org.mythtv.client.ui.dvr;

import java.util.List;

import org.mythtv.R;
import org.mythtv.services.api.dvr.Program;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

/**
 * @author Daniel Frey
 * 
 */
public class RecordingsActivity extends AbstractRecordingsActivity implements RecordingsFragment.OnProgramGroupListener {

	private static final String TAG = RecordingsActivity.class.getSimpleName();
	
	private boolean isTwoPane = false;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.i( TAG, "onCreate : enter" );
		
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_dvr_recordings );

		RecordingsFragment recordings = (RecordingsFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_program_groups );

		recordings.setOnProgramGroupListener( this );

		isTwoPane = ( null != findViewById( R.id.fragment_dvr_program_group ) );
		
		Log.i( TAG, "onCreate : exit" );
	}

	public void addProgramGroupFragment( List<Program> programs ) {
		Log.d( TAG, "addProgramGroupFragment : enter" );

		FragmentManager fragMgr = getSupportFragmentManager();
		ProgramGroupFragment programGroupFragment = (ProgramGroupFragment) fragMgr.findFragmentById( R.id.fragment_dvr_program_group );
		FragmentTransaction xaction = fragMgr.beginTransaction();

		if( null == programGroupFragment ) {
			Log.v( TAG, "addProgramGroupFragment : creating new program group fragment" );

			programGroupFragment = new ProgramGroupFragment( programs );

			xaction.add( R.id.fragment_dvr_program_group, programGroupFragment ).setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN ).addToBackStack( null ).commit();
		}

		programGroupFragment.loadPrograms( programs );
		
		Log.d( TAG, "addProgramGroupFragment : exit" );
	}

	public void onProgramGroupSelected( List<Program> programs ) {
		Log.d( TAG, "onProgramGroupSelected : enter" );

		if( isTwoPane ) {
			Log.v( TAG, "onProgramGroupSelected : adding program group to pane" );

			addProgramGroupFragment( programs );
		} else {
			Log.v( TAG, "onProgramGroupSelected : starting program group activity" );

			getApplicationContext().setCurrentRecordingsInProgramGroup( programs );
			
			Intent i = new Intent( this, ProgramGroupActivity.class );
			startActivity( i );
		}

		Log.d( TAG, "onProgramGroupSelected : exit" );
	}

}
