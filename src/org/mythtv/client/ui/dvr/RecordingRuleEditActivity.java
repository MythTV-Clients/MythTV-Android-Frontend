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
import android.util.Log;
import android.view.MenuItem;

/**
 * @author Daniel Frey
 * 
 */
public class RecordingRuleEditActivity extends AbstractDvrActivity {

	private static final String TAG = RecordingRuleEditActivity.class.getSimpleName();

	public static final String EXTRA_RECORDING_RULE_EDIT_KEY = "org.mythtv.client.ui.dvr.recordingRule.EXTRA_RECORDING_RULE_EDIT_KEY";

	private RecordingRuleEditFragment recordingRuleFragment = null;

	private Integer recordingRuleId;
	
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

		Bundle extras = getIntent().getExtras(); 
		recordingRuleId = extras.getInt( EXTRA_RECORDING_RULE_EDIT_KEY );
		
		setContentView( R.layout.fragment_dvr_recording_rule_edit );

		recordingRuleFragment = (RecordingRuleEditFragment) getSupportFragmentManager().findFragmentById( R.id.fragment_dvr_recording_rule_edit );
		recordingRuleFragment.loadRecordingRule( recordingRuleId );
		
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
//				// app icon in action bar clicked; go home
//				Intent intent = new Intent( this, RecordingRuleActivity.class );
//				intent.putExtra( RecordingRuleActivity.EXTRA_RECORDING_RULE_KEY, recordingRuleId );
//				intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//				startActivity( intent );
				this.finish();

				return true;
		}

		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}
	
}
