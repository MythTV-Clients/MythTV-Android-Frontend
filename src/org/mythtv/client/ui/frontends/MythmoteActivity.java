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
 * This software can be found at <https://github.com/MythTV-Android/mythtv-for-android/>
 *
 */
package org.mythtv.client.ui.frontends;

import org.mythtv.R;

import android.os.Bundle;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class MythmoteActivity extends AbstractFrontendsActivity {

	private static final String TAG = MythmoteActivity.class.getSimpleName();
		
	private boolean isTwoPane = false;

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.AbstractMythtvFragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_mythmote );

		FrontendsFragment frontends = (FrontendsFragment) getSupportFragmentManager().findFragmentById( R.id.frontends_fragment );

		isTwoPane = ( null != findViewById( R.id.fragment_dvr_program_group ) );
		if( isTwoPane ) {
//			frontends.enablePersistentSelection();
		}

		Log.v( TAG, "onCreate : exit" );
	}

//	@Override
//	public void onFrontendSelected( List<Frontend> frontend ) {
//		// TODO Auto-generated method stub
//		
//	}

}
