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
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */
package org.mythtv.client.ui.media;

import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MediaDashboardFragment extends AbstractMythFragment {

	private final static String TAG = MediaDashboardFragment.class.getSimpleName();

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );

		View root = inflater.inflate( R.layout.fragment_media_dashboard, container );

		// Attach event handlers
		root.findViewById( R.id.media_btn_videos ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "videos.onClick : enter" );
				
//				if( UIUtils.isHoneycombTablet( getActivity() ) ) {
//					startActivity( new Intent( getActivity(), ScheduleMultiPaneActivity.class ) );
//				} else {
//					startActivity( new Intent( getActivity(), ScheduleActivity.class ) );
//				}

				Log.v( TAG, "videos.onClick : exit" );
			}

		} );

		root.findViewById( R.id.media_btn_music ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "music.onClick : enter" );

				Log.v( TAG, "music.onClick : exit" );
			}
		} );

		root.findViewById( R.id.media_btn_pictures ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "pictures.onClick : enter" );

				Log.v( TAG, "pictures.onClick : exit" );
			}
		} );

		Log.v( TAG, "onCreateView : enter" );
		return root;
	}

}
