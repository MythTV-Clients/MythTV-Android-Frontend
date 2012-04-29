/**
 * 
 */
package org.mythtv.client.ui.dvr;

import org.mythtv.client.MainApplication;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * @author Daniel Frey
 * 
 */
public abstract class AbstractRecordingsActivity extends FragmentActivity implements RecordingsFragment.OnProgramGroupListener {

	private static final String TAG = AbstractRecordingsActivity.class.getSimpleName();
	
	public MainApplication getApplicationContext() {
		Log.v( TAG, "getApplicationContext : enter" );
		Log.v( TAG, "getApplicationContext : exit" );
		return (MainApplication) super.getApplicationContext();
	}

//	public void onRecordingSelected( String item ) {
//		Log.v( TAG, "onRecordingSelected : enter" );
//
//		FragmentManager fragMgr = getSupportFragmentManager();
//		ProgramGroupFragment programGroup = (ProgramGroupFragment) fragMgr.findFragmentById( R.id.fragment_dvr_program_group );
//		FragmentTransaction xaction = fragMgr.beginTransaction();
//
//		if( programGroup == null || programGroup.isRemoving() ) {
//			programGroup = new ProgramGroupFragment( item );
//
//			xaction.add( R.id.fragment_dvr_program_group, programGroup ).setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN ).addToBackStack( null ).commit();
//		} else {
//			programGroup.loadPrograms( item );
//		}
//
//		Log.v( TAG, "onRecordingSelected : exit" );
//	}

}
