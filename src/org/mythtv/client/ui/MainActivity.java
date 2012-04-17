package org.mythtv.client.ui;

import org.mythtv.client.ui.capture.CaptureCardsActivity;

import android.content.Intent;
import android.os.Bundle;

/**
 * @author Daniel Frey
 * 
 */
public class MainActivity extends AbstractMythActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	// ***************************************
	// Activity methods
	// ***************************************
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		startActivity( new Intent( this, CaptureCardsActivity.class ) );
		finish();
	}

}
