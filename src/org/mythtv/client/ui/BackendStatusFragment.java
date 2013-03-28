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
