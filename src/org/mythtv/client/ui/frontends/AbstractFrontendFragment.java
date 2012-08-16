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

import java.util.Timer;

import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.frontend.FrontendStatus;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * @author pot8oe
 *
 */
public class AbstractFrontendFragment extends Fragment {
	
	private final static String TAG = "AbstractFrontendFragment";
	private final static int STATUS_CHECK_INTERVAL_MS = 10000;
	
	protected static GetStatusTask sGetStatusTask;
	protected static Timer sStatusTimer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		//create only one get status task
		if(null == sGetStatusTask) {
			sGetStatusTask = new GetStatusTask();
			
			//kick it off with a status request
			final FrontendsFragment frontends = (FrontendsFragment) getFragmentManager().findFragmentById( R.id.frontends_fragment );
			final Frontend fe = frontends.getSelectedFrontend();
			//sGetStatusTask.execute(fe.getUrl());
		}
		
		//create only one status timer
		if(null == sStatusTimer){
			sStatusTimer = new Timer();
//			sStatusTimer.schedule(new TimerTask(){
//
//				@Override
//				public void run() {
//					final FrontendsFragment frontends = (FrontendsFragment) getFragmentManager().findFragmentById( R.id.frontends_fragment );
//					final Frontend fe = frontends.getSelectedFrontend();
//					sGetStatusTask.execute(fe.getUrl());
//				}
//				
//			}, STATUS_CHECK_INTERVAL_MS, STATUS_CHECK_INTERVAL_MS);
		}
		
		super.onCreate(savedInstanceState);
	}
	
	public MainApplication getApplicationContext() {
		return (MainApplication) getActivity().getApplicationContext();
	}
	
	private void showAlertDialog(final CharSequence title, final CharSequence message){
		this.getActivity().runOnUiThread(new Runnable(){

			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(title);
				builder.setMessage(message);
				builder.show();
			}
			
		});
	}

	/**
	 * When calling execute there must be 1 paramter:
	 * Frontend URL
	 * @author pot8oe
	 *
	 */
	protected class GetStatusTask extends AsyncTask<String, Void, Void> {

		FrontendStatus status;
		
		@Override
		protected Void doInBackground(String... params) {

			try {
				ETagInfo eTag = ETagInfo.createEmptyETag();
				status = getApplicationContext().getMythServicesApi()
						.frontendOperations().getStatus( params[0], eTag );
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				showAlertDialog("Get Status Error", e.getMessage());
			}
			return null;
		}
		
		
		public FrontendStatus getFrontendStatus()
		{
			return status;
		}
	}
	
	/**
	 * When calling execute there must be 2 paramters.
	 * Frontend URL
	 * Message
	 * @author pot8oe
	 *
	 */
	protected class SendMessageTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			try {
				getApplicationContext().getMythServicesApi()
						.frontendOperations().sendMessage(params[0], params[1]);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				showAlertDialog("Send Message Error", e.getMessage());
			}
			return null;
		}
		
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

			try {
				getApplicationContext().getMythServicesApi()
						.frontendOperations()
						.sendAction(params[0], params[1], null, 0, 0);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				showAlertDialog("Send Action Error", e.getMessage());
			}
			
			return null;
		}
		
	}
	
	
	
	
}
