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
package org.mythtv.client.ui.media;

import android.content.Intent;
import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * @author Daniel Frey
 *
 */
public class MediaDashboardFragment extends AbstractMythFragment {

	private final static String TAG = MediaDashboardFragment.class.getSimpleName();

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		Log.v( TAG, "onCreateView : enter" );

		if( null == container ) {
			Log.v( TAG, "onCreateView : exit, container is null" );

			return null;
		}
		
		View root = inflater.inflate( R.layout.fragment_media_dashboard, container, false );

		// Attach event handlers
		root.findViewById( R.id.media_btn_videos ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "videos.onClick : enter" );
				
//				if( UIUtils.isHoneycombTablet( getActivity() ) ) {
//					startActivity( new Intent( getActivity(), ScheduleMultiPaneActivity.class ) );
//				} else {
//					startActivity( new Intent( getActivity(), ScheduleActivity.class ) );
//				}

				Toast.makeText( getActivity(), "Videos - Coming Soon!", Toast.LENGTH_SHORT ).show();
				
				Log.v( TAG, "videos.onClick : exit" );
			}

		} );

		root.findViewById( R.id.media_btn_music ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "music.onClick : enter" );

				Toast.makeText( getActivity(), "Music - Coming Soon!", Toast.LENGTH_SHORT ).show();

				Log.v( TAG, "music.onClick : exit" );
			}
		} );

		root.findViewById( R.id.media_btn_pictures ).setOnClickListener( new View.OnClickListener() {
			public void onClick( View view ) {
				Log.v( TAG, "pictures.onClick : enter" );
                startActivity( new Intent( getActivity(), GalleryActivity.class ) );
				Log.v( TAG, "pictures.onClick : exit" );
			}
		} );

		Log.v( TAG, "onCreateView : enter" );
		return root;
	}

}
