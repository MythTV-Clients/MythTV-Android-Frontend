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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
					
					new CheckMythtvBackendConnectionTask().execute( "home" );
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
					
					new CheckMythtvBackendConnectionTask().execute( "away" );
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

	// internal helpers
	
	private void connectHomeLocation() {
		startActivity( new Intent( getActivity(), HomeActivity.class ) );
	}
	
	private void connectAwayLocation() {
		startActivity( new Intent( getActivity(), AwayActivity.class ) );
	}

	private void showBackendConnectionFailedWarning() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
		builder.setTitle( R.string.location_alert_error_title );
		builder.setNeutralButton( R.string.btn_ok, new DialogInterface.OnClickListener() {

			public void onClick( DialogInterface dialog, int which ) { }
			
		});
		builder.setMessage( R.string.location_alert_error_message_not_connected );
		builder.show();

	}
	
	private class CheckMythtvBackendConnectionTask extends AsyncTask<String, Void, ResponseEntity<String>> {

		private String connectedPofile;
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected ResponseEntity<String> doInBackground( String... args ) {
			Log.v( TAG, "CheckMythtvBackendConnectionTask.doInBackground : enter" );
			
			connectedPofile = args[ 0 ];
			
			try {
				Log.v( TAG, "CheckMythtvBackendConnectionTask.doInBackground : exit" );
				return getMainApplication().getMythServicesApi().mythOperations().getHostName();
			} catch( Exception e ) {
				Log.w( TAG, "CheckMythtvBackendConnectionTask.doInBackground : error connecting to backend", e );
				
				return null;
			}
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute( ResponseEntity<String> result ) {
			Log.v( TAG, "CheckMythtvBackendConnectionTask.onPostExecute : enter" );

			if( null != result ) {
				
				if( result.getStatusCode().equals( HttpStatus.OK ) ) {
					
					String hostname = result.getBody();
					if( null != hostname && !"".equals( hostname ) ) {

						if( connectedPofile.equals( "home" ) ) {
							connectHomeLocation();
						}

						if( connectedPofile.equals( "away" ) ) {
							connectAwayLocation();
						}
						
					} else {
					
						Log.v( TAG, "CheckMythtvBackendConnectionTask.onPostExecute : mythtv master backend not found" );

						showBackendConnectionFailedWarning();
					
					}
			
				} else {

					Log.v( TAG, "CheckMythtvBackendConnectionTask.onPostExecute : mythtv master backend not found" );

					showBackendConnectionFailedWarning();
				
				}
				
			} else {

				Log.v( TAG, "CheckMythtvBackendConnectionTask.onPostExecute : mythtv master backend not found" );

				showBackendConnectionFailedWarning();

			}
			
			Log.v( TAG, "CheckMythtvBackendConnectionTask.onPostExecute : exit" );
		}
		
	}
}
