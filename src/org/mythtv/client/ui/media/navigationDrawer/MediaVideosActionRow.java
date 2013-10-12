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
/**
 * 
 */
package org.mythtv.client.ui.media.navigationDrawer;

import org.mythtv.R;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

/**
 * @author dmfrey
 *
 */
public class MediaVideosActionRow extends MediaActionRow {

	private static final String FRAGMENT = "org.mythtv.client.ui.media.VideosParentFragment";
	
	public MediaVideosActionRow( Context context, int actionResId ) {
		super( context, actionResId, false );
	}
	
	@Override
	public View getView(View convertView) {
		View view = super.getView(convertView);
		
		ImageView imgView = (ImageView)view.findViewById( R.id.navigation_drawer_action_icon );
		imgView.setImageResource( R.drawable.ic_videos_default );
		
		return view;
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.NavigationDrawerActivity.Row#getViewType()
	 */
	@Override
	public int getViewType() {
		return MediaRowType.VIDEOS_ROW.ordinal();
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.navigationDrawer.ActionRow#getFragment()
	 */
	@Override
	public String getFragment() {
		return FRAGMENT;
	}
	
}
