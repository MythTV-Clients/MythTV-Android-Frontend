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
package org.mythtv.client.ui.dvr;

import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DvrDashboardFragment extends AbstractMythFragment {

	private final static String TAG = DvrDashboardFragment.class.getSimpleName();

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );
		
		if( null == container ) {
			Log.v( TAG, "onCreateView : exit, container is null" );

			return null;
		}
		
		View root = inflater.inflate( R.layout.fragment_dvr_dashboard, container, false );

		// Attach event handlers
		root.findViewById( R.id.dvr_btn_recordings ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "recordings.onClick : enter" );

				startActivity( new Intent( getActivity(), RecordingsActivity.class ) );
				
				Log.v( TAG, "recordings.onClick : exit" );
			}

		} );

		root.findViewById( R.id.dvr_btn_upcoming ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "upcoming.onClick : enter" );

				startActivity( new Intent( getActivity(), UpcomingActivity.class ) );

				Log.v( TAG, "upcoming.onClick : exit" );
			}
		} );

		root.findViewById( R.id.dvr_btn_guide ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "guide.onClick : enter" );

				startActivity( new Intent( getActivity(), GuideActivity.class ) );

				Log.v( TAG, "guide.onClick : exit" );
			}
		} );

		root.findViewById( R.id.dvr_btn_recording_rules ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "recording_rules.onClick : enter" );

				startActivity( new Intent( getActivity(), RecordingRulesActivity.class ) );

				Log.v( TAG, "recording_rules.onClick : exit" );
			}
		} );

		Log.v( TAG, "onCreateView : exit" );
		return root;
	}

}
