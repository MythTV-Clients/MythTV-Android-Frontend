/**
 * 
 */
package org.mythtv.client.ui.frontends;

import org.mythtv.R;

import android.os.Bundle;
import android.util.Log;

/**
 * @author Daniel Frey
 *
 */
public class MythmoteActivity extends AbstractFrontendsActivity {

	private static final String TAG = MythmoteActivity.class.getSimpleName();
		
	private boolean isTwoPane = false;

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.AbstractMythtvFragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );
		
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_mythmote );

		FrontendsFragment frontends = (FrontendsFragment) getSupportFragmentManager().findFragmentById( R.id.frontends_fragment );

		isTwoPane = ( null != findViewById( R.id.fragment_dvr_program_group ) );
		if( isTwoPane ) {
//			frontends.enablePersistentSelection();
		}

		Log.v( TAG, "onCreate : exit" );
	}

//	@Override
//	public void onFrontendSelected( List<Frontend> frontend ) {
//		// TODO Auto-generated method stub
//		
//	}

}
