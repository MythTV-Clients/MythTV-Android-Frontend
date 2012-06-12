package org.mythtv.client.ui.frontends;

import org.mythtv.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

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