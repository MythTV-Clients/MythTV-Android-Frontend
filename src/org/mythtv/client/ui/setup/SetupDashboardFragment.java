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
package org.mythtv.client.ui.setup;

import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SetupDashboardFragment extends AbstractMythFragment {

	private final static String TAG = SetupDashboardFragment.class.getSimpleName();

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );

		View root = inflater.inflate( R.layout.fragment_setup_dashboard, container );

		// Attach event handlers
		root.findViewById( R.id.setup_btn_capture ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "recordings.onClick : enter" );

//				if( UIUtils.isHoneycombTablet( getActivity() ) ) {
//					startActivity( new Intent( getActivity(), ScheduleMultiPaneActivity.class ) );
//				} else {
//					startActivity( new Intent( getActivity(), ScheduleActivity.class ) );
//				}

				Log.v( TAG, "recordings.onClick : exit" );
			}

		} );

		Log.v( TAG, "onCreateView : enter" );
		return root;
	}

}
