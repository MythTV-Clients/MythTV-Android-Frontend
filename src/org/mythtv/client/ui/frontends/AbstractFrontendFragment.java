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

import org.mythtv.client.MainApplication;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

/**
 * @author pot8oe
 *
 */
public class AbstractFrontendFragment extends Fragment {

	
	public MainApplication getApplicationContext() {
		return (MainApplication) getActivity().getApplicationContext();
	}
	
	/**
	 * When calling execute there must be 2 paramters.
	 * Frontend URL
	 * Command
	 * @author pot8oe
	 *
	 */
	protected class SendActionTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			getApplicationContext().getMythServicesApi().frontendOperations().sendAction(params[0], params[1], null, 0, 0);
			return null;
		}
		
	}
	
	
}
