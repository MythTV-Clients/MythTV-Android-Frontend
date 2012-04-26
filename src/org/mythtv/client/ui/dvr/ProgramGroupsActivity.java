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

import org.mythtv.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * @author Daniel Frey
 * 
 */
public class ProgramGroupsActivity extends FragmentActivity {

	private static final String TAG = ProgramGroupsActivity.class.getSimpleName();

	// ***************************************
	// Activity methods
	// ***************************************

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		
		super.onCreate( savedInstanceState );

		setContentView( R.layout.activity_dvr_program_groups );

		Log.v( TAG, "onCreate : exit" );
	}

}
