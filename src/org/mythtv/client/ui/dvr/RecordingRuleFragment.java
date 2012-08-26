/**
 * 
 */
package org.mythtv.client.ui.dvr;

import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;
import org.mythtv.service.util.NotificationHelper;
import org.mythtv.service.util.NotificationHelper.NotificationType;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.dvr.RecRule;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.TextView;

/**
 * @author Daniel Frey
 *
 */
public class RecordingRuleFragment extends AbstractMythFragment {

	private static final String TAG = RecordingRuleFragment.class.getSimpleName();
	
	private NotificationHelper mNotificationHelper;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		super.onCreate( savedInstanceState );

		mNotificationHelper = new NotificationHelper( getActivity() );

		Log.v( TAG, "onCreate : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );

		View v = inflater.inflate( R.layout.recording_rule, container, false );
		
		Log.v( TAG, "onCreateView : exit" );
		return v;
	}

	public void loadRecordingRule( Integer recordingRuleId ) {
		Log.v( TAG, "loadRecordingRule : enter" );

		new DownloadRecordingRuleTask().execute( recordingRuleId );

		Log.v( TAG, "loadRecordingRule : exit" );
	}
	
	// internal helpers

	private void setupForm( RecRule rule ) {
		Log.v( TAG, "setupForm : enter" );
		
		TextView title = (TextView) getActivity().findViewById( R.id.recording_rule_title );
		
		title.setText( rule.getTitle() );
		
		Log.v( TAG, "setupForm : exit" );
	}
	
	private class DownloadRecordingRuleTask extends AsyncTask<Integer, Void, RecRule> {

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected RecRule doInBackground( Integer... params ) {
			
			Integer id = params[ 0 ];
			
			String message = "Retrieving Recording Rule " + id.toString();
			mNotificationHelper.createNotification( "Mythtv for Android", message, NotificationType.UPLOAD );

			ETagInfo etag = ETagInfo.createEmptyETag();
			return getMainApplication().getMythServicesApi().dvrOperations().getRecordSchedule( id, etag );
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute( RecRule result ) {
			
			mNotificationHelper.completed();
			
			if( null != result ) {
				setupForm( result );
			}
		}
		
	}
	
}
