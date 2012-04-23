/**
 * 
 */
package org.mythtv.client.ui.setup.capture;

import java.util.List;

import org.mythtv.client.ui.AbstractMythListActivity;
import org.mythtv.client.ui.setup.SetupActivity;
import org.mythtv.services.api.capture.CaptureCard;

import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
		Log.v( TAG, "onCreate : enter" );
		
		super.onCreate( savedInstanceState );

		final ActionBar actionBar = getActionBar();
		
		actionBar.setDisplayHomeAsUpEnabled( true );

		Log.v( TAG, "onCreate : exit" );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		Log.v( TAG, "onStart : enter" );

		super.onStart();

		if( null == captureCards ) {
			downloadCaptureCards();
		}

		Log.v( TAG, "onStart : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.BaseActivity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		Log.d( TAG, "onCreateOptionsMenu : enter" );

		Log.d( TAG, "onCreateOptionsMenu : exit" );
		return true;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.BaseActivity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.d( TAG, "onOptionsItemSelected : enter" );
		
		switch( item.getItemId() ) {
        case android.R.id.home:
			Log.d( TAG, "onOptionsItemSelected : home selected" );
			
			// app icon in action bar clicked; go home
            Intent intent = new Intent( this, SetupActivity.class );
            startActivity( intent );
            break;
		default:
			Log.d( TAG, "onOptionsItemSelected : nothing selected" );

			break;
		}

		Log.d( TAG, "onOptionsItemSelected : exit" );
		return true;
	}

	//***************************************
    // Private methods
    //***************************************
	private void refreshCaptureCards( List<CaptureCard> captureCards ) {	
		Log.v( TAG, "refreshCaptureCards : enter" );

		this.captureCards = captureCards;

		if( null == captureCards ) {
			Log.v( TAG, "refreshCaptureCards : exit, captureCards is empty" );
			return;
		}
		
		for( CaptureCard captureCard : captureCards ) {
			Log.i( TAG, captureCard.toString() );
		}
		
		setListAdapter( new CaptureCardsListAdapter( this, this.captureCards ) );

		Log.v( TAG, "refreshCaptureCards : exit" );
	}
		
	private void downloadCaptureCards() {
		Log.v( TAG, "downloadCaptureCards : enter" );

		new DownloadEventsTask().execute();

		Log.v( TAG, "downloadCaptureCards : exit" );
	}

	// ***************************************
	// Private classes
	// ***************************************
	private class DownloadEventsTask extends AsyncTask<Void, Void, List<CaptureCard>> {

		@Override
		protected List<CaptureCard> doInBackground( Void... params ) {
			Log.v( TAG, "DownloadEventsTask.doInBackground : enter" );

			try {
				Log.v( TAG, "DownloadEventsTask.doInBackground : exit" );
				return getApplicationContext().getMythServicesApi().captureOperations().getCaptureCardList();
			} catch( Exception e ) {
				Log.e( TAG, "DownloadEventsTask.doInBackground : error", e );
			}

			Log.v( TAG, "DownloadEventsTask.doInBackground : exit, failed" );
			return null;
		}

		@Override
		protected void onPostExecute( List<CaptureCard> result ) {
			Log.v( TAG, "DownloadEventsTask.onPostExecute : enter" );

			refreshCaptureCards( result );

			Log.v( TAG, "DownloadEventsTask.onPostExecute : exit" );
		}
	}

}
