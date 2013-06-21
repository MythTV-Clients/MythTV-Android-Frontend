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
import org.mythtv.client.ui.preferences.LocationProfile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BackendStatusFragment extends AbstractMythFragment {

	private static final String TAG = BackendStatusFragment.class.getSimpleName();
	
	private View mView;
	
	private LocationProfile mLocationProfile;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.d( TAG, "onCreateView : enter" );
		
		mView = inflater.inflate( R.layout.fragment_backend_status, container, false );
		
		Log.d( TAG, "onCreateView : exit" );
		return mView;
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.d( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		if( null != mView ) {
			
			TextView tView = (TextView) mView.findViewById( R.id.textview_status );
			if( null != tView ) {
//				tView.setText( this.getStatusText() );
			}
			
		}
		
		Log.d( TAG, "onActivityCreated : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.d( TAG, "onResume : enter" );
		super.onResume();

		if( null != mView ) {
			
			TextView tView = (TextView) mView.findViewById( R.id.textview_status );
			if( null != tView ) {
				tView.setText( this.getStatusText() );
			}
			
		}
	
		Log.d( TAG, "onResume : exit" );
	}

	// internal helpers

	private String getStatusText() {
		Log.v( TAG, "getStatusText : enter" );

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		
		if( null == mLocationProfile ) {
			Log.v( TAG, "getStatusText : exit, no connected profiles found" );

			return "Backend profile is not selected";
		}
		
		new BackendStatusTask().execute();
		
		Log.v( TAG, "getStatusText : exit" );
		return ( mLocationProfile.isConnected() ? "Connected to " : "NOT Connected to " ) + mLocationProfile.getName();
	}

}
