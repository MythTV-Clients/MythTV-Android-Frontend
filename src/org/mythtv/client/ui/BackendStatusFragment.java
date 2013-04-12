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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BackendStatusFragment extends AbstractMythFragment {

	private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	private LocationProfile mLocationProfile;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.fragment_backend_status, container, false);
		
		if(null != v){
			TextView tView = (TextView)v.findViewById(R.id.textview_status);
			if(null != tView){
				tView.setText(this.getStatusText());
			}
		}
		
		return v;
	}

	
	
	private String getStatusText(){
		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		
		if(null == mLocationProfile){
			return "Backend profile is not selected";
		}
		
		new BackendStatusTask().execute();
		
		return  (mLocationProfile.isConnected() ? "Connected to " : "NOT Connected to ") + mLocationProfile.getName();
	}


}
