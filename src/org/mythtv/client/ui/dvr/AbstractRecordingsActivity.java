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

import org.mythtv.client.ui.AbstractMythtvFragmentActivity;

import android.app.ActionBar;
import android.os.Build;
import android.util.Log;

/**
 * @author Daniel Frey
 * 
 */
public abstract class AbstractRecordingsActivity extends AbstractMythtvFragmentActivity implements RecordingsFragment.OnProgramGroupListener {

	protected static final String TAG = AbstractRecordingsActivity.class.getSimpleName();

	protected void setupActionBar() {
		Log.v( TAG, "setupActionBar : enter" );

		if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
			Log.v( TAG, "setupActionBar : actionbar available" );
			
			ActionBar actionBar = getActionBar();
			
			actionBar.setDisplayHomeAsUpEnabled( true );
		}
		
		Log.v( TAG, "setupActionBar : exit" );
	}

}
