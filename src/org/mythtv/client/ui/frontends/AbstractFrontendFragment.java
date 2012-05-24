package org.mythtv.client.ui.frontends;

import org.mythtv.client.MainApplication;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

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
