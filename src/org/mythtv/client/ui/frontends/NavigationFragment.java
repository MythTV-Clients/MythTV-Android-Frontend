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
import org.mythtv.client.ui.MainMenuFragment;

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
public class NavigationFragment extends AbstractFrontendFragment implements OnClickListener  {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//inflate fragment layout
		View view = inflater.inflate(R.layout.fragment_mythmote_navigation, container, false);
		
		//set onclick listener for nav buttons
		((ImageButton)view.findViewById(R.id.imageButton_nav_info)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_nav_up)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_nav_tvguide)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_nav_left)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_nav_select)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_nav_right)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_nav_cancel)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_nav_down)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_nav_menu)).setOnClickListener(this);
		
		//set onclick listener for channel buttons
		((ImageButton)view.findViewById(R.id.imageButton_ch_up)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_ch_last)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_ch_down)).setOnClickListener(this);
		
		//set onclick listener for volume buttons
		((ImageButton)view.findViewById(R.id.imageButton_vol_up)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_vol_mute)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_vol_down)).setOnClickListener(this);
		
		//set onclick listener for media control buttons
		((ImageButton)view.findViewById(R.id.imageButton_mediactrl_rec)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_mediactrl_stop)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_mediactrl_pause)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_mediactrl_play)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_mediactrl_prev)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_mediactrl_rew)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_mediactrl_ff)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_mediactrl_next)).setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		
		final Frontend fe = MainMenuFragment.getSelectedFrontend();
		
		//exit if we don't have a frontend
		if(null == fe) return;
		
		switch(v.getId()){
		case R.id.imageButton_nav_info:
			new SendActionTask().execute(fe.getUrl(), "INFO");
			break;
			
		case R.id.imageButton_nav_up:
			new SendActionTask().execute(fe.getUrl(), "UP");
			break;
			
		case R.id.imageButton_nav_tvguide:
			new SendActionTask().execute(fe.getUrl(), "GUIDE");
			break;
			
		case R.id.imageButton_nav_left:
			new SendActionTask().execute(fe.getUrl(), "LEFT");
			break;
			
		case R.id.imageButton_nav_select:
			new SendActionTask().execute(fe.getUrl(), "SELECT");
			break;
			
		case R.id.imageButton_nav_right:
			new SendActionTask().execute(fe.getUrl(), "RIGHT");
			break;
			
		case R.id.imageButton_nav_cancel:
			new SendActionTask().execute(fe.getUrl(), "ESCAPE");
			break;
			
		case R.id.imageButton_nav_down:
			new SendActionTask().execute(fe.getUrl(), "DOWN");
			break;
			
		case R.id.imageButton_nav_menu:
			new SendActionTask().execute(fe.getUrl(), "MENU");
			break;
			
			
		case R.id.imageButton_ch_up:
			new SendActionTask().execute(fe.getUrl(), "CHANNELUP");
			break;
			
		case R.id.imageButton_ch_last:
			new SendActionTask().execute(fe.getUrl(), "PREVCHAN");
			break;
			
		case R.id.imageButton_ch_down:
			new SendActionTask().execute(fe.getUrl(), "CHANNELDOWN");
			break;
			
			
		case R.id.imageButton_vol_up:
			new SendActionTask().execute(fe.getUrl(), "VOLUMEUP");
			break;
			
		case R.id.imageButton_vol_mute:
			new SendActionTask().execute(fe.getUrl(), "MUTE");
			break;
			
		case R.id.imageButton_vol_down:
			new SendActionTask().execute(fe.getUrl(), "VOLUMEDOWN");
			break;
			
			
		case R.id.imageButton_mediactrl_rec:
			new SendActionTask().execute(fe.getUrl(), "TOGGLERECORD");
			break;
			
		case R.id.imageButton_mediactrl_stop:
			new SendActionTask().execute(fe.getUrl(), "STOPPLAYBACK");
			break;
			
		case R.id.imageButton_mediactrl_pause:
			new SendActionTask().execute(fe.getUrl(), "PAUSE");
			break;
			
		case R.id.imageButton_mediactrl_play:
			new SendActionTask().execute(fe.getUrl(), "PLAYBACK");
			break;
			
		case R.id.imageButton_mediactrl_prev:
			new SendActionTask().execute(fe.getUrl(), "JUMPRWND");
			break;
			
		case R.id.imageButton_mediactrl_rew:
			new SendActionTask().execute(fe.getUrl(), "RWNDSTICKY");
			break;
			
		case R.id.imageButton_mediactrl_ff:
			new SendActionTask().execute(fe.getUrl(), "FFWDSTICKY");
			break;
			
		case R.id.imageButton_mediactrl_next:
			new SendActionTask().execute(fe.getUrl(), "JUMPFFWD");
			break;
			
		};
		
	}
	
	
	
	
	
}
