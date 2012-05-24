package org.mythtv.client.ui.frontends;

import org.mythtv.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MediaControlFragment extends AbstractFrontendFragment implements OnClickListener  {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//inflate fragment layout
		View view = inflater.inflate(R.layout.fragment_media_control, container, false);
		
		//set onclick listener for each button
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
		
		final FrontendsFragment frontends = (FrontendsFragment) getFragmentManager().findFragmentById( R.id.frontends_fragment );
		final Frontend fe = frontends.getSelectedFrontend();
		
		//exit if we don't have a frontend
		if(null == fe) return;
		
		switch(v.getId()){
		case R.id.imageButton_mediactrl_rec:
			new SendActionTask().execute(fe.getUrl(), "TOGGLERECORD");
			break;
			
		case R.id.imageButton_mediactrl_stop:
			new SendActionTask().execute(fe.getUrl(), "STOP");
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
			new SendActionTask().execute(fe.getUrl(), "RWND");
			break;
			
		case R.id.imageButton_mediactrl_ff:
			new SendActionTask().execute(fe.getUrl(), "FFWD");
			break;
			
		case R.id.imageButton_mediactrl_next:
			new SendActionTask().execute(fe.getUrl(), "JUMPFFWD");
			break;

		};
		
	}
	
}