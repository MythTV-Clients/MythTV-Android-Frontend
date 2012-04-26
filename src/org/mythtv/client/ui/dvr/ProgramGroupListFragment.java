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
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */
package org.mythtv.client.ui.dvr;

import org.mythtv.client.MainApplication;
import org.mythtv.services.api.dvr.Program;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.ArrayAdapter;

/**
 * @author Daniel Frey
 * 
 */
public class ProgramGroupListFragment extends ListFragment {

	private static final String TAG = ProgramGroupListFragment.class.getSimpleName();

	private MainApplication mainApplication;

	/**
	 * Create a new instance of DetailsFragment, initialized to show the text at
	 * 'index'.
	 */
	public static ProgramGroupListFragment newInstance( int index ) {
		Log.v( TAG, "newInstance : enter" );
		
		ProgramGroupListFragment f = new ProgramGroupListFragment();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putInt( "index", index );
		f.setArguments( args );

		Log.v( TAG, "newInstance : exit" );
		return f;
	}

	public int getShownIndex() {
		Log.v( TAG, "getShownIndex : enter" );
		Log.v( TAG, "getShownIndex : exit" );
		return getArguments().getInt( "index", 0 );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );

		super.onActivityCreated( savedInstanceState );

		mainApplication = (MainApplication) getActivity().getApplicationContext();

		// Populate List
		setListAdapter( new ArrayAdapter<Program>( getActivity(), android.R.layout.simple_list_item_activated_1, mainApplication.getCurrentProgramsInGroup() ) );

		Log.v( TAG, "onActivityCreated : exit" );
	}

}
