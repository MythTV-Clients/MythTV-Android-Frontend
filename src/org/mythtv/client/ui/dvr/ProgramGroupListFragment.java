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

import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.services.api.dvr.Program;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );

		super.onActivityCreated( savedInstanceState );

		mainApplication = (MainApplication) getActivity().getApplicationContext();

		// Populate List
		setListAdapter( new ProgramAdapter() );

//		if( null == savedInstanceState ) {
//			int position = savedInstanceState.getInt( STATE_CHECKED, -1 );
//		
//			if( position > -1 ) {
//				getListView().setItemChecked( position, true );
//			}
//		}
		
		Log.v( TAG, "onActivityCreated : exit" );
	}

	private class ProgramAdapter extends ArrayAdapter<Program> {

		ProgramAdapter() {
			super( getActivity(), R.layout.program_row, R.id.program_title, mainApplication.getCurrentProgramsInGroup() );
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			Log.v( TAG, "getView : enter" );

			ProgramWrapper wrapper = null;
			
			if( null == convertView ) {
				Log.v( TAG, "getView : convertView is null" );

				convertView = LayoutInflater.from( getActivity() ).inflate( R.layout.program_row, null );
				wrapper = new ProgramWrapper( convertView );
				convertView.setTag( wrapper );
			} else {
				Log.v( TAG, "getView : convertView is not null" );

				wrapper = (ProgramWrapper) convertView.getTag();
			}
			
			wrapper.populateFrom( getItem( position ) );
			
			Log.v( TAG, "getView : exit" );
			return convertView;
		}

	}

	static class ProgramWrapper {

		private TextView title = null;
		private TextView description = null;

		ProgramWrapper( View row ) {
			title = (TextView) row.findViewById( R.id.program_title );
			description = (TextView) row.findViewById( R.id.program_description );
		}

		TextView getTitle() {
			return title;
		}
		
		TextView getDescription() {
			return description;
		}
		
		void populateFrom( Program program) {
			getTitle().setText( !"".equals( program.getSubTitle() ) ? program.getSubTitle() : program.getTitle() );
			getDescription().setText( program.getDescription() );
		}

	}

}
