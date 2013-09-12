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
package org.mythtv.client.ui.frontends;

import java.util.Timer;

import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.AbstractMythFragment;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.service.frontends.GetStatusTask;

import android.os.Bundle;

/**
 * @author pot8oe
 * 
 */
public class AbstractFrontendFragment extends AbstractMythFragment implements GetStatusTask.TaskFinishedListener {

	private final static String TAG = AbstractFrontendFragment.class.getSimpleName();

	protected static GetStatusTask sGetStatusTask;
	protected static Timer sStatusTimer;

	private LocationProfile mLocationProfile;
	
	@Override
	public void onCreate( Bundle savedInstanceState ) {

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		
		// create only one get status task
		if( null == sGetStatusTask ) {
			sGetStatusTask = new GetStatusTask( getActivity(), mLocationProfile, this );

			// kick it off with a status request
//			final Frontend fe = MainMenuFragment.getSelectedFrontend();
			// sGetStatusTask.execute(fe.getUrl());
		}

		// create only one status timer
		if( null == sStatusTimer ) {
			sStatusTimer = new Timer();
			// sStatusTimer.schedule(new TimerTask(){
			//
			// @Override
			// public void run() {
			// final FrontendsFragment frontends = (FrontendsFragment)
			// getFragmentManager().findFragmentById( R.id.frontends_fragment );
			// final Frontend fe = frontends.getSelectedFrontend();
			// sGetStatusTask.execute(fe.getUrl());
			// }
			//
			// }, STATUS_CHECK_INTERVAL_MS, STATUS_CHECK_INTERVAL_MS);
		}

		super.onCreate( savedInstanceState );
	}
	
	public MainApplication getApplicationContext() {
		return (MainApplication) getActivity().getApplicationContext();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.frontends.GetStatusTask.TaskFinishedListener#onGetStatusTaskStarted()
	 */
	@Override
	public void onGetStatusTaskStarted() {
		
	}

	/* (non-Javadoc)
	 * @see org.mythtv.service.frontends.GetStatusTask.TaskFinishedListener#onGetStatusTaskFinished(org.mythtv.db.frontends.model.Status)
	 */
	@Override
	public void onGetStatusTaskFinished( org.mythtv.db.frontends.model.Status result ) {
		
	}

}
