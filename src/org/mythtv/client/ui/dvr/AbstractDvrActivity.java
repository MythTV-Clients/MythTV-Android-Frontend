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

import org.mythtv.client.ui.AbstractMythtvFragmentActivity;
import org.mythtv.client.ui.AwayActivity;
import org.mythtv.client.ui.HomeActivity;
import org.mythtv.service.util.FileHelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

/**
 * @author John Baab
 * 
 * 
 */
public abstract class AbstractDvrActivity extends AbstractMythtvFragmentActivity {

	protected static final String TAG = AbstractDvrActivity.class.getSimpleName();

	protected FileHelper mFileHelper = null;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		setupActionBar();

		mFileHelper = new FileHelper( this );
		
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
			
				// app icon in action bar clicked; go home
				if( getMainApplication().getConnectedLocationProfile().equals( "HOME" ) ) {
					Intent intent = new Intent( this, HomeActivity.class );
					intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
					startActivity( intent );
				} else {
					Intent intent = new Intent( this, AwayActivity.class );
					intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
					startActivity( intent );
				}

			return true;
		}

		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

}
