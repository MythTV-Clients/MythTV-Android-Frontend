package org.mythtv.client.ui;

import org.mythtv.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainMenuFragment extends Fragment{

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View mainMenuView = inflater.inflate(R.layout.fragment_main_menu, container, false);
		
		return mainMenuView; 
	}

	
}
