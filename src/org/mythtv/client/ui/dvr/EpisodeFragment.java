package org.mythtv.client.ui.dvr;

import org.mythtv.R;
import org.mythtv.client.ui.AbstractMythFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EpisodeFragment  extends AbstractMythFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View root = inflater.inflate( R.layout.fragment_dvr_episode, container, false );
		
		return root;
	}

	
	
}
