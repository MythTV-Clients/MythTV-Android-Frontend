/**
 * 
 */
package org.mythtv.client.ui.dvr;

import org.mythtv.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * @author Daniel Frey
 * 
 */
public class RecordingsActivity extends AbstractRecordingsActivity implements RecordingsFragment.OnProgramGroupListener {

	private boolean isTwoPane = false;

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.fragment_dvr_recordings );

		RecordingsFragment recordings = (RecordingsFragment) getSupportFragmentManager().findFragmentById( R.id.program_groups );

		recordings.setOnProgramGroupListener( this );

		isTwoPane = ( null != findViewById( R.id.program_group ) );

		if( isTwoPane ) {
			recordings.enablePersistentSelection();
		}
	}

	public void addProgramGroupFragment( String programGroup ) {
		FragmentManager fragMgr = getSupportFragmentManager();
		ProgramGroupFragment programGroupFragment = (ProgramGroupFragment) fragMgr.findFragmentById( R.id.program_group );
		FragmentTransaction xaction = fragMgr.beginTransaction();

		if( null == programGroupFragment ) {
			programGroupFragment = new ProgramGroupFragment( programGroup );

			xaction.add( R.id.program_group, programGroupFragment ).setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN ).addToBackStack( null ).commit();
		}

		programGroupFragment.loadPrograms( programGroup );
	}

	public void onProgramGroupSelected( String programGroup ) {
		if( isTwoPane ) {
			addProgramGroupFragment( programGroup );
		} else {
			startActivity( new Intent( this, ProgramGroupActivity.class ) );
		}
	}

}
