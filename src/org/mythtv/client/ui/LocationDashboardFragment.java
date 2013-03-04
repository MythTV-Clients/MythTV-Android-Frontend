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
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.MythtvService;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LocationDashboardFragment extends AbstractMythFragment {

	private final static String TAG = LocationDashboardFragment.class.getSimpleName();

	private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	
	private ConnectReceiver connectReceiver = new ConnectReceiver();
	private String connectedProfile;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );
		
		Log.v( TAG, "onActivityCreated : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );
		
		View root = inflater.inflate( R.layout.fragment_location_dashboard, container );

		// Attach event handlers
		root.findViewById( R.id.btn_home ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "home.onClick : enter" );
				
					if( null == mLocationProfileDaoHelper.findSelectedHomeProfile() ) {
						
						AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
						builder.setTitle( R.string.location_alert_error_title );
						builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

							public void onClick( DialogInterface dialog, int which ) { }
							
						});
						builder.setMessage( R.string.location_alert_error_home_message );
						builder.show();

					} else {
					
						LocationProfile profile = mLocationProfileDaoHelper.findSelectedHomeProfile(); 
						mLocationProfileDaoHelper.setConnectedLocationProfile( (long) profile.getId() );
					
						connectedProfile = "home";

						getActivity().startService( new Intent( MythtvService.ACTION_CONNECT ) );
					}
			
				Log.v( TAG, "home.onClick : exit" );
			}

		} );

		root.findViewById( R.id.btn_away ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "away.onClick : enter" );

					if( null == mLocationProfileDaoHelper.findSelectedAwayProfile() ) {
						
						AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
						builder.setTitle( R.string.location_alert_error_title );
						builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

							public void onClick( DialogInterface dialog, int which ) { }
							
						});
						builder.setMessage( R.string.location_alert_error_away_message );
						builder.show();

					} else {
					
						LocationProfile profile = mLocationProfileDaoHelper.findSelectedAwayProfile(); 
						mLocationProfileDaoHelper.setConnectedLocationProfile( (long) profile.getId() );
					
						connectedProfile = "away";
						
						getActivity().startService( new Intent( MythtvService.ACTION_CONNECT ) );
					}

				Log.v( TAG, "away.onClick : exit" );
			}
		} );

		Log.v( TAG, "onCreateView : exit" );
		return root;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		Log.v( TAG, "onStart : enter" );
		super.onStart();

		IntentFilter connectFilter = new IntentFilter( MythtvService.ACTION_CONNECT );
		connectFilter.addAction(  MythtvService.ACTION_COMPLETE );
        getActivity().registerReceiver( connectReceiver, connectFilter );

		Log.v( TAG, "onStart : enter" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		Log.v( TAG, "onStop : enter" );
		super.onStop();

		// Unregister for broadcast
		if( null != connectReceiver ) {
			try {
				getActivity().unregisterReceiver( connectReceiver );
				//connectReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}

		Log.v( TAG, "onStop : exit" );
	}

	// internal helpers
	
	private void connectHomeLocation() {
		Log.i( TAG, "connectHomeLocation : starting home dashboard" );
		
//		startActivity( new Intent( getActivity(), HomeActivity.class ) );
	}
	
	private void connectAwayLocation() {
		Log.i( TAG, "connectAwayLocation : starting away dashboard" );

//		startActivity( new Intent( getActivity(), AwayActivity.class ) );
	}

	private class ConnectReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.i( TAG, "ConnectReceiver.onReceive : action=" + intent.getAction() );
			
	        if ( intent.getAction().equals( MythtvService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "ConnectReceiver.onReceive : complete=" + intent.getStringExtra( MythtvService.EXTRA_COMPLETE ) );
	        	
	        	if( intent.getBooleanExtra( MythtvService.EXTRA_COMPLETE_ONLINE, Boolean.FALSE ) ) {

	        	}

        		if( connectedProfile.equals( "home" ) ) {
					connectHomeLocation();
				}

				if( connectedProfile.equals( "away" ) ) {
					connectAwayLocation();
				}

	        }

		}
		
	}

}
