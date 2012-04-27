/**
 * 
 */
package org.mythtv.client.ui.dvr;

import org.mythtv.R;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * @author Daniel Frey
 * 
 */
public abstract class AbstractRecordingsActivity extends FragmentActivity implements RecordingsFragment.OnProgramGroupListener {

	public void onItemSelected( String item ) {
		FragmentManager fragMgr = getSupportFragmentManager();
		ProgramGroupFragment programGroup = (ProgramGroupFragment) fragMgr.findFragmentById( R.id.program_group );
		FragmentTransaction xaction = fragMgr.beginTransaction();

		if( programGroup == null || programGroup.isRemoving() ) {
			programGroup = new ProgramGroupFragment( item );

			xaction.add( R.id.program_group, programGroup ).setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN ).addToBackStack( null ).commit();
		} else {
			programGroup.loadPrograms( item );
		}
	}
}
