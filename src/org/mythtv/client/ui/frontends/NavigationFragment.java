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
package org.mythtv.client.ui.frontends;

import org.mythtv.R;
import org.mythtv.client.ui.navigationDrawer.FrontendsRow;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.service.frontends.SendActionTask;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * @author pot8oe
 * 
 */
public class NavigationFragment extends AbstractFrontendFragment implements OnClickListener {

	private LocationProfile mLocationProfile;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		
		// inflate fragment layout
		View view = inflater.inflate( R.layout.fragment_mythmote_navigation, container, false );

		// set onclick listener for nav buttons
		( (ImageButton) view.findViewById( R.id.imageButton_nav_info ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_nav_up ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_nav_tvguide ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_nav_left ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_nav_select ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_nav_right ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_nav_cancel ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_nav_down ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_nav_menu ) ).setOnClickListener( this );

		// set onclick listener for channel buttons
		( (ImageButton) view.findViewById( R.id.imageButton_ch_up ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_ch_last ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_ch_down ) ).setOnClickListener( this );

		// set onclick listener for volume buttons
		( (ImageButton) view.findViewById( R.id.imageButton_vol_up ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_vol_mute ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_vol_down ) ).setOnClickListener( this );

		// set onclick listener for media control buttons
		( (ImageButton) view.findViewById( R.id.imageButton_mediactrl_rec ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_mediactrl_stop ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_mediactrl_pause ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_mediactrl_play ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_mediactrl_prev ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_mediactrl_rew ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_mediactrl_ff ) ).setOnClickListener( this );
		( (ImageButton) view.findViewById( R.id.imageButton_mediactrl_next ) ).setOnClickListener( this );

		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick( View v ) {

		final Frontend fe = FrontendsRow.getSelectedFrontend();

		// exit if we don't have a frontend
		if( null == fe )
			return;

		SendActionTask sendActionTask = new SendActionTask( mLocationProfile );

		switch( v.getId() ) {
		case R.id.imageButton_nav_info:
			sendActionTask.execute( fe.getUrl(), "INFO" );
			break;

		case R.id.imageButton_nav_up:
			sendActionTask.execute( fe.getUrl(), "UP" );
			break;

		case R.id.imageButton_nav_tvguide:
			sendActionTask.execute( fe.getUrl(), "GUIDE" );
			break;

		case R.id.imageButton_nav_left:
			sendActionTask.execute( fe.getUrl(), "LEFT" );
			break;

		case R.id.imageButton_nav_select:
			sendActionTask.execute( fe.getUrl(), "SELECT" );
			break;

		case R.id.imageButton_nav_right:
			sendActionTask.execute( fe.getUrl(), "RIGHT" );
			break;

		case R.id.imageButton_nav_cancel:
			sendActionTask.execute( fe.getUrl(), "ESCAPE" );
			break;

		case R.id.imageButton_nav_down:
			sendActionTask.execute( fe.getUrl(), "DOWN" );
			break;

		case R.id.imageButton_nav_menu:
			sendActionTask.execute( fe.getUrl(), "MENU" );
			break;

		case R.id.imageButton_ch_up:
			sendActionTask.execute( fe.getUrl(), "CHANNELUP" );
			break;

		case R.id.imageButton_ch_last:
			sendActionTask.execute( fe.getUrl(), "PREVCHAN" );
			break;

		case R.id.imageButton_ch_down:
			sendActionTask.execute( fe.getUrl(), "CHANNELDOWN" );
			break;

		case R.id.imageButton_vol_up:
			sendActionTask.execute( fe.getUrl(), "VOLUMEUP" );
			break;

		case R.id.imageButton_vol_mute:
			sendActionTask.execute( fe.getUrl(), "MUTE" );
			break;

		case R.id.imageButton_vol_down:
			sendActionTask.execute( fe.getUrl(), "VOLUMEDOWN" );
			break;

		case R.id.imageButton_mediactrl_rec:
			sendActionTask.execute( fe.getUrl(), "TOGGLERECORD" );
			break;

		case R.id.imageButton_mediactrl_stop:
			sendActionTask.execute( fe.getUrl(), "STOPPLAYBACK" );
			break;

		case R.id.imageButton_mediactrl_pause:
			sendActionTask.execute( fe.getUrl(), "PAUSE" );
			break;

		case R.id.imageButton_mediactrl_play:
			sendActionTask.execute( fe.getUrl(), "PLAYBACK" );
			break;

		case R.id.imageButton_mediactrl_prev:
			sendActionTask.execute( fe.getUrl(), "JUMPRWND" );
			break;

		case R.id.imageButton_mediactrl_rew:
			sendActionTask.execute( fe.getUrl(), "RWNDSTICKY" );
			break;

		case R.id.imageButton_mediactrl_ff:
			sendActionTask.execute( fe.getUrl(), "FFWDSTICKY" );
			break;

		case R.id.imageButton_mediactrl_next:
			sendActionTask.execute( fe.getUrl(), "JUMPFFWD" );
			break;

		}

	}

}
