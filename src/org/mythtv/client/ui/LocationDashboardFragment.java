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
package org.mythtv.client.ui;

import org.mythtv.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LocationDashboardFragment extends AbstractMythFragment {

	private final static String TAG = LocationDashboardFragment.class.getSimpleName();

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );
		
		View root = inflater.inflate( R.layout.fragment_location_dashboard, container );

		// Attach event handlers
		root.findViewById( R.id.btn_home ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "home.onClick : enter" );
				
				if( null != getMainApplication().getSelectedHomeLocationProfile() ) {
					getMainApplication().connectSelectedHomeLocationProfile();
					
					startActivity( new Intent( getActivity(), HomeActivity.class ) );
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
					builder.setTitle( R.string.location_alert_error_title );
					builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

						public void onClick( DialogInterface dialog, int which ) { }
						
					});
					builder.setMessage( R.string.location_alert_error_message );
					builder.show();
				}

				Log.v( TAG, "home.onClick : exit" );
			}

		} );

		root.findViewById( R.id.btn_away ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "away.onClick : enter" );

				if( null != getMainApplication().getSelectedAwayLocationProfile() ) {
					Log.v( TAG, "away.onClick : showing away dashboard" );

					getMainApplication().connectSelectedAwayLocationProfile();
					
					startActivity( new Intent( getActivity(), AwayActivity.class ) );
				} else {
					Log.v( TAG, "away.onClick : no away profile selected" );

					AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
					builder.setTitle( R.string.location_alert_error_title );
					builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

						public void onClick( DialogInterface dialog, int which ) { }
						
					});
					builder.setMessage( R.string.location_alert_error_message );
					builder.show();
				}

				Log.v( TAG, "away.onClick : exit" );
			}
		} );

		Log.v( TAG, "onCreateView : exit" );
		return root;
	}

}
