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
import org.mythtv.client.ui.navigationDrawer.FrontendsRow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class NumbersFragment extends AbstractFrontendFragment implements OnClickListener  {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//inflate fragment layout
		View mView = inflater.inflate(R.layout.fragment_mythmote_numbers, container, false);
		
		//set onclick listener for each button
		((Button)mView.findViewById(R.id.numbers_button0)).setOnClickListener(this);
		((Button)mView.findViewById(R.id.numbers_button1)).setOnClickListener(this);
		((Button)mView.findViewById(R.id.numbers_button2)).setOnClickListener(this);
		((Button)mView.findViewById(R.id.numbers_button3)).setOnClickListener(this);
		((Button)mView.findViewById(R.id.numbers_button4)).setOnClickListener(this);
		((Button)mView.findViewById(R.id.numbers_button5)).setOnClickListener(this);
		((Button)mView.findViewById(R.id.numbers_button6)).setOnClickListener(this);
		((Button)mView.findViewById(R.id.numbers_button7)).setOnClickListener(this);
		((Button)mView.findViewById(R.id.numbers_button8)).setOnClickListener(this);
		((Button)mView.findViewById(R.id.numbers_button9)).setOnClickListener(this);
		((Button)mView.findViewById(R.id.numbers_button_backspace)).setOnClickListener(this);
		((Button)mView.findViewById(R.id.numbers_button_enter)).setOnClickListener(this);
		
		return mView;
	}

	@Override
	public void onClick(View v) {
		
		final Frontend fe = FrontendsRow.getSelectedFrontend();
		
		//exit if we don't have a frontend
		if(null == fe) return;
		
		switch(v.getId()){
		case R.id.numbers_button0:
			new SendActionTask().execute(fe.getUrl(), "0");
			break;
			
		case R.id.numbers_button1:
			new SendActionTask().execute(fe.getUrl(), "1");
			break;
			
		case R.id.numbers_button2:
			new SendActionTask().execute(fe.getUrl(), "2");
			break;
			
		case R.id.numbers_button3:
			new SendActionTask().execute(fe.getUrl(), "3");
			break;
			
		case R.id.numbers_button4:
			new SendActionTask().execute(fe.getUrl(), "4");
			break;
			
		case R.id.numbers_button5:
			new SendActionTask().execute(fe.getUrl(), "5");
			break;
			
		case R.id.numbers_button6:
			new SendActionTask().execute(fe.getUrl(), "6");
			break;
			
		case R.id.numbers_button7:
			new SendActionTask().execute(fe.getUrl(), "7");
			break;
			
		case R.id.numbers_button8:
			new SendActionTask().execute(fe.getUrl(), "8");
			break;
			
		case R.id.numbers_button9:
			new SendActionTask().execute(fe.getUrl(), "9");
			break;
			
		case R.id.numbers_button_backspace:
			new SendActionTask().execute(fe.getUrl(), "BACKSPACE");
			break;
			
		case R.id.numbers_button_enter:
			new SendActionTask().execute(fe.getUrl(), "SELECT");
			break;
		};
		
	}
	
	
	
	
	
}
