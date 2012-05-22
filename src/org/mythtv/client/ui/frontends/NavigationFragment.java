package org.mythtv.client.ui.frontends;

import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.services.api.frontend.FrontendOperations;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class NavigationFragment extends Fragment implements OnClickListener  {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//inflate fragment layout
		View view = inflater.inflate(R.layout.fragment_navigation, container, false);
		
		//set onclick listener for each button
		((ImageButton)view.findViewById(R.id.imageButton_nav_info)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_nav_up)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_nav_tvguide)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_nav_left)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_nav_select)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_nav_right)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_nav_cancel)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_nav_down)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_nav_menu)).setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		
		final FrontendsFragment frontends = (FrontendsFragment) getFragmentManager().findFragmentById( R.id.frontends_fragment );
		final Frontend fe = frontends.getSelectedFrontend();
		
		//exit if we don't have a frontend
		if(null == fe) return;
		
		final FrontendOperations fOps = getApplicationContext().getMythServicesApi().frontendOperations();
		
		switch(v.getId()){
		case R.id.imageButton_nav_info:
			fOps.sendAction(fe.getUrl(), "INFO", null, 0, 0);
			break;
			
		case R.id.imageButton_nav_up:
			break;
			
		case R.id.imageButton_nav_tvguide:
			break;
			
		case R.id.imageButton_nav_left:
			break;
			
		case R.id.imageButton_nav_select:
			break;
			
		case R.id.imageButton_nav_right:
			break;
			
		case R.id.imageButton_nav_cancel:
			break;
			
		case R.id.imageButton_nav_down:
			break;
			
		case R.id.imageButton_nav_menu:
			break;
		};
		
	}
	
	public MainApplication getApplicationContext() {
		return (MainApplication) getActivity().getApplicationContext();
	}
	
}
