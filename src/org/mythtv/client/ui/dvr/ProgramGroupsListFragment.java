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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.services.api.dvr.Program;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author Daniel Frey
 * 
 */
public class ProgramGroupsListFragment extends ListFragment {

	private static final String TAG = ProgramGroupsListFragment.class.getSimpleName();

	private MainApplication mainApplication;
	
	private int mCurrentProgram = 0;
	boolean mDualPane;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );

		super.onActivityCreated( savedInstanceState );

		setHasOptionsMenu( true );
		
		mainApplication = (MainApplication) getActivity().getApplicationContext();
		
		downloadPrograms();

		// Check to see if we have a frame in which to embed the details
		// fragment directly in the containing UI.
		View frame = getActivity().findViewById( R.id.dvr_frame_program_group );
		mDualPane = frame != null && frame.getVisibility() == View.VISIBLE;

		if( null != savedInstanceState ) {
			// Restore last state for checked position.
			mCurrentProgram = savedInstanceState.getInt( "CurrentProgram", 0 );
		}

		Log.v( TAG, "onActivityCreated : exit" );
	}

	@Override
	public void onSaveInstanceState( Bundle outState ) {
		Log.v( TAG, "onSaveInstanceState : enter" );

		super.onSaveInstanceState( outState );

		outState.putInt( "CurrentProgram", mCurrentProgram );

		Log.v( TAG, "onSaveInstanceState : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		Log.d( TAG, "onCreateOptionsMenu : enter" );
		
		super.onCreateOptionsMenu( menu, inflater );
	
		inflater.inflate( R.menu.refresh_menu, menu );

		Log.d( TAG, "onCreateOptionsMenu : exit" );
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.d( TAG, "onOptionsItemSelected : enter" );
		
		switch( item.getItemId() ) {
        case R.id.menu_refresh:
			Log.d( TAG, "onOptionsItemSelected : refresh selected" );
			
			mainApplication.setProgramGroups( null );
			mainApplication.setCurrentPrograms( null );
			mainApplication.setCurrentProgramsInGroup( null );
			
			downloadPrograms();
			
			return true;
		}

		Log.d( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	@Override
	public void onListItemClick( ListView l, View v, int position, long id ) {
		Log.v( TAG, "onListItemClick : enter" );

		showDetails( position );
		
		Log.v( TAG, "onListItemClick : exit" );
	}

	// ***************************************
	// Private methods
	// ***************************************

	private void showDetails( int index ) {
		Log.v( TAG, "showDetails : enter" );

		mCurrentProgram = index;

		String title = mainApplication.getProgramGroups().get( mCurrentProgram );
		if( mainApplication.getCurrentPrograms().containsKey( title ) ) {
			Log.v( TAG, "onListItemClick : found title, updating view" );

			List<Program> programsInGroup = mainApplication.getCurrentPrograms().get( title );
			mainApplication.setCurrentProgramsInGroup( programsInGroup );
			
			
			if( mDualPane ) {
				// Update fragments in place
				getListView().setItemChecked( index, true );

				// Check what fragment is currently shown, replace if needed.
				ProgramGroupListFragment programGroup = (ProgramGroupListFragment) getFragmentManager().findFragmentById( R.id.dvr_fragment_program_group );
				if( null == programGroup || programGroup.getShownIndex() != index ) {
					// Make new fragment to show this selection.
					programGroup = ProgramGroupListFragment.newInstance( index );

					// Execute a transaction, replacing any existing fragment
					// with this one inside the frame.
					FragmentTransaction ft = getFragmentManager().beginTransaction();
					ft.replace( R.id.dvr_fragment_program_group, programGroup );
					ft.setTransition( FragmentTransaction.TRANSIT_FRAGMENT_FADE );
					ft.commit();
				}
			} else {
				// Show activity
				Intent intent = new Intent();
				intent.setClass( getActivity(), ProgramGroupActivity.class );
				intent.putExtra( "index", index );
				startActivity( intent );
			}
		}

		Log.v( TAG, "showDetails : enter" );
	}

	private void refreshProgramGroups() {
		Log.v( TAG, "refreshProgramGroups : enter" );

		if( null == mainApplication.getProgramGroups() ) {
			Log.v( TAG, "refreshProgramGroups : exit, programGroups is empty" );
			return;
		}

		// Populate List
		setListAdapter( new ArrayAdapter<String>( getActivity(), android.R.layout.simple_list_item_activated_1,	mainApplication.getProgramGroups() ) );

		if( mDualPane ) {
			// In dual-pane mode, the list view highlights the selected item.
			getListView().setChoiceMode( ListView.CHOICE_MODE_SINGLE );

			// Make sure our UI is in the correct state.
			showDetails( mCurrentProgram );
		}

		Log.v( TAG, "refreshProgramGroups : exit" );
	}

	private void downloadPrograms() {
		Log.v( TAG, "downloadPrograms : enter" );

		new DownloadProgramsTask().execute();

		Log.v( TAG, "downloadPrograms : exit" );
	}

	// ***************************************
	// Private classes
	// ***************************************
	private class DownloadProgramsTask extends AsyncTask<Void, Void, List<Program>> {

		@Override
		protected List<Program> doInBackground( Void... params ) {
			Log.v( TAG, "DownloadProgramsTask.doInBackground : enter" );

			try {
				Log.v( TAG, "DownloadProgramsTask.doInBackground : exit" );

				return mainApplication.getMythServicesApi().dvrOperations().getRecordedList( 0, 0, true );
			} catch( Exception e ) {
				Log.e( TAG, "DownloadProgramsTask.doInBackground : error", e );
			}

			Log.v( TAG, "DownloadProgramsTask.doInBackground : exit, failed" );
			return null;
		}

		@Override
		protected void onPostExecute( List<Program> result ) {
			Log.v( TAG, "DownloadProgramsTask.onPostExecute : enter" );

			List<String> sortedProgramGroups = new ArrayList<String>();
			Map<String, List<Program>> sortedResult = new TreeMap<String, List<Program>>();
			for( Program program : result ) {
				String title = program.getTitle();

				if( !"livetv".equalsIgnoreCase( program.getRecording().getRecordingGroup() ) ) {
					if( sortedResult.containsKey( title ) ) {
						List<Program> groupPrograms = new ArrayList<Program>();
						groupPrograms.add( program );
						sortedResult.put( title, groupPrograms );
					} else {
						sortedResult.get( title ).add( program );
					}

					if( !sortedProgramGroups.contains( title ) ) {
						sortedProgramGroups.add( title );
					}
				}
			}

			if( !sortedProgramGroups.isEmpty() ) {
				Collections.sort( sortedProgramGroups, String.CASE_INSENSITIVE_ORDER );
			}

			mainApplication.setCurrentPrograms( sortedResult );
			mainApplication.setProgramGroups( sortedProgramGroups );

			refreshProgramGroups();

			Log.v( TAG, "DownloadProgramsTask.onPostExecute : exit" );
		}
	}

}
