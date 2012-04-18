/**
 * 
 */
package org.mythtv.client.ui.capture;

import java.util.List;

import org.mythtv.client.ui.AbstractMythListActivity;
import org.mythtv.services.api.capture.CaptureCard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Daniel Frey
 * 
 */
public class CaptureCardsActivity extends AbstractMythListActivity {

	private static final String TAG = CaptureCardsActivity.class.getSimpleName();

	private List<CaptureCard> captureCards;

	// ***************************************
	// Activity methods
	// ***************************************

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();

		if( null == captureCards ) {
			downloadCaptureCards();
		}
	}

	//***************************************
    // Private methods
    //***************************************
	private void refreshCaptureCards( List<CaptureCard> captureCards ) {	
		this.captureCards = captureCards;

		if( null == captureCards ) {
			return;
		}
		
		for( CaptureCard captureCard : captureCards ) {
			Log.i( TAG, captureCard.toString() );
		}
		
		setListAdapter( new CaptureCardsListAdapter( this, this.captureCards ) );
	}
		
	private void downloadCaptureCards() {
		new DownloadEventsTask().execute();
	}

	// ***************************************
	// Private classes
	// ***************************************
	private class DownloadEventsTask extends AsyncTask<Void, Void, List<CaptureCard>> {

		private Exception exception;

		@Override
		protected List<CaptureCard> doInBackground( Void... params ) {
			try {
				return getApplicationContext().getMythServicesApi().captureOperations().getCaptureCardList();
			} catch( Exception e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
				exception = e;
			}

			return null;
		}

		@Override
		protected void onPostExecute( List<CaptureCard> result ) {
			refreshCaptureCards( result );
		}
	}

}
