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

import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.db.dvr.ProgramGuideDaoHelper;
import org.mythtv.db.dvr.UpcomingDaoHelper;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.db.status.model.BackendStatus;
import org.mythtv.service.util.RunningServiceHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * @author Daniel Frey
 *
 */
public abstract class AbstractMythFragment extends Fragment implements MythtvApplicationContext {

	protected static final String TAG = AbstractMythFragment.class.getSimpleName();
	
	protected SharedPreferences preferences = null;
	protected EtagDaoHelper mEtagDaoHelper = EtagDaoHelper.getInstance();
	protected RunningServiceHelper mRunningServiceHelper = RunningServiceHelper.getInstance();
	protected LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	protected ProgramGuideDaoHelper mProgramGuideDaoHelper = ProgramGuideDaoHelper.getInstance();
	protected UpcomingDaoHelper mUpcomingDaoHelper = UpcomingDaoHelper.getInstance();
	
	protected BackendStatus mStatus;
	
    // ***************************************
    // MythActivity methods
    // ***************************************
    public MainApplication getMainApplication() {

    	if( null != getActivity() ) {
    		return (MainApplication) getActivity().getApplicationContext();
    	} else {
    		return null;
    	}

    }

    /* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		preferences = getActivity().getSharedPreferences( getString( R.string.app_name ), Context.MODE_PRIVATE );

	}

	protected void showAlertDialog( final CharSequence title, final CharSequence message ) {
	
    	this.getActivity().runOnUiThread( new Runnable() {
    		
    		/* (non-Javadoc)
    		 * @see java.lang.Runnable#run()
    		 */
    		@Override
    		public void run() {
    			AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
    			builder.setTitle( title );
    			builder.setMessage( message );
    			builder.show();
    		}

    	});
    }
	
}
