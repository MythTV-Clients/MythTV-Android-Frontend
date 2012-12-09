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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class RecordingRulesActivity extends AbstractDvrActivity implements RecordingRulesFragment.OnRecordingRuleListener {

	private static final String TAG = RecordingRulesActivity.class.getSimpleName();
	
	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.AbstractDvrActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_dvr_recording_rules );

		RecordingRulesFragment recordingRulesFragment = (RecordingRulesFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_recording_rules );
		recordingRulesFragment.setOnRecordingRuleListener( this );

		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.RecordingRulesFragment.OnRecordingRuleListener#onRecordingRuleSelected(java.lang.Integer)
	 */
	@Override
	public void onRecordingRuleSelected( Integer recordingRuleId ) {
		Log.d( TAG, "onRecordingRuleSelected : enter" );
		
		if( null != findViewById( R.id.fragment_dvr_recording_rule ) ) {
			Log.v( TAG, "onRecordingRuleSelected : adding recording rule to pane" );

			FragmentManager manager = getSupportFragmentManager();

			RecordingRuleFragment recordingRuleFragment = (RecordingRuleFragment) manager.findFragmentById( R.id.fragment_dvr_recording_rule );
			FragmentTransaction transaction = manager.beginTransaction();

			if( null == recordingRuleFragment ) {
				Log.v( TAG, "onRecordingRuleSelected : creating new recordingRuleFragment" );
				
				Bundle args = new Bundle();
				args.putInt( "RECORDING_RULE_ID", recordingRuleId );
				recordingRuleFragment = RecordingRuleFragment.newInstance( args );
				
				transaction
					.add( R.id.fragment_dvr_recording_rule, recordingRuleFragment )
					.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN )
					.addToBackStack( null )
					.commit();
			}
			
			Log.v( TAG, "onRecordingRuleSelected : setting recording rule to display" );
			recordingRuleFragment.loadRecordingRule( recordingRuleId );
		} else {
			Log.v( TAG, "onRecordingRuleSelected : starting recording rule activity" );

			Intent i = new Intent( this, RecordingRuleActivity.class );
			i.putExtra( RecordingRuleActivity.EXTRA_RECORDING_RULE_KEY, recordingRuleId );
			startActivity( i );
		}

		Log.d( TAG, "onRecordingRuleSelected : exit" );
	}

}
