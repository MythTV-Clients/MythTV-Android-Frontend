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
package org.mythtv.client.ui.frontends;

import org.mythtv.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * @author pot8oe
 *
 */
public class VolumeControlFragment extends AbstractFrontendFragment implements OnClickListener  {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//inflate fragment layout
		View view = inflater.inflate(R.layout.fragment_mythmote_volume_control, container, false);
		
		//set onclick listener for each button
		((ImageButton)view.findViewById(R.id.imageButton_vol_up)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_vol_mute)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_vol_down)).setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		
		final FrontendsFragment frontends = (FrontendsFragment) getFragmentManager().findFragmentById( R.id.frontends_fragment );
		final Frontend fe = frontends.getSelectedFrontend();
		
		//exit if we don't have a frontend
		if(null == fe) return;
		
		switch(v.getId()){
		case R.id.imageButton_vol_up:
			new SendActionTask().execute(fe.getUrl(), "VOLUMEUP");
			break;
			
		case R.id.imageButton_vol_mute:
			new SendActionTask().execute(fe.getUrl(), "MUTE");
			break;
			
		case R.id.imageButton_vol_down:
			new SendActionTask().execute(fe.getUrl(), "VOLUMEDOWN");
			break;

		};
		
	}
	
}