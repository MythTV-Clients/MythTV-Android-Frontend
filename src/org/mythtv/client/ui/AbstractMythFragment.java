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
package org.mythtv.client.ui;

import org.mythtv.client.MainApplication;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.util.MythtvServiceHelper;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractMythFragment extends Fragment implements MythtvApplicationContext {

	protected MythtvServiceHelper mMythtvServiceHelper = MythtvServiceHelper.getInstance();
	protected LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	
	//***************************************
    // MythActivity methods
    //***************************************
	public MainApplication getMainApplication() {
		
		if( null != getActivity() ) {
			return (MainApplication) getActivity().getApplicationContext();
		} else {
			return null;
		}
	
	}

	protected void showAlertDialog( final CharSequence title, final CharSequence message ) {
		this.getActivity().runOnUiThread( new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
				builder.setTitle( title );
				builder.setMessage( message );
				builder.show();
			}

		} );
	}
	
	/**
	 * We use the fragment ID as a tag as well so we try both methodes of lookup
	 * @return
	 */
	protected Fragment findChildFragmentByIdOrTag(int id){
	  Fragment frag = this.getChildFragmentManager().findFragmentById(id);
	  if(null != frag) return frag;
	  frag = this.getChildFragmentManager().findFragmentByTag(Integer.toString(id));
	  return frag;
	}
	
}
