package org.mythtv.client.ui.frontends;

import org.mythtv.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class ChannelControlFragment extends AbstractFrontendFragment implements OnClickListener  {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//inflate fragment layout
		View view = inflater.inflate(R.layout.fragment_mythmote_channel_control, container, false);
		
		//set onclick listener for each button
		((ImageButton)view.findViewById(R.id.imageButton_ch_up)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_ch_last)).setOnClickListener(this);
		((ImageButton)view.findViewById(R.id.imageButton_ch_down)).setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		
		final FrontendsFragment frontends = (FrontendsFragment) getFragmentManager().findFragmentById( R.id.frontends_fragment );
		final Frontend fe = frontends.getSelectedFrontend();
		
		//exit if we don't have a frontend
		if(null == fe) return;
		
		switch(v.getId()){
		case R.id.imageButton_ch_up:
			new SendActionTask().execute(fe.getUrl(), "CHANNELUP");
			break;
			
		case R.id.imageButton_ch_last:
			new SendActionTask().execute(fe.getUrl(), "PREVCHAN");
			break;
			
		case R.id.imageButton_ch_down:
			new SendActionTask().execute(fe.getUrl(), "CHANNELDOWN");
			break;

		};
		
	}
	
}