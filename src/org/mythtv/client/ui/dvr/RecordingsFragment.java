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
import org.mythtv.client.ui.util.PersistentListFragment;
import org.mythtv.services.api.dvr.Program;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Daniel Frey
 * 
 */
public class RecordingsFragment extends PersistentListFragment {

	private static final String TAG = RecordingsFragment.class.getSimpleName();

	private OnProgramGroupListener listener = null;
	private ProgramGroupAdapter adapter = null;
	private boolean persistentSelection = false;

	private List<Program> programs;
	private List<String> programGroups;
	private Map<String, List<Program>> recordingsInProgramGroups;
	
	public RecordingsFragment() {
		this( false );
	}

	public RecordingsFragment( boolean persistentSelection ) {
		this.persistentSelection = persistentSelection;
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		setRetainInstance( true );

		Log.v( TAG, "onCreate : exit" );
	}

	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );

		super.onResume();
	    
		if( null == programs || programs.isEmpty() ) {
			Log.v( TAG, "onResume : load recordings" );

			loadRecordings();
		}

		Log.v( TAG, "onResume : exit" );
	}
	
	@Override
	public void onActivityCreated( Bundle state ) {
		Log.v( TAG, "onActivityCreated : enter" );

		super.onActivityCreated( state );

		restoreState( state );

		if( persistentSelection ) {
			enablePersistentSelection();
		}

		Log.v( TAG, "onActivityCreated : exit" );
	}

	@Override
	public void onListItemClick( ListView l, View v, int position, long id ) {
		Log.v( TAG, "onListItemClick : enter" );

		super.onListItemClick( l, v, position, id );
	    
		if( null != listener ) {
			if( "all".equalsIgnoreCase( adapter.getItem( position ) ) ) {
				listener.onProgramGroupSelected( programs );
			} else {
				listener.onProgramGroupSelected( recordingsInProgramGroups.get( adapter.getItem( position ) ) );
			}
			
		}

		Log.v( TAG, "onListItemClick : exit" );
	}
	  
	public void loadRecordings() {
		Log.v( TAG, "loadRecordings : enter" );

		new DownloadRecordedTask().execute();

		Log.v( TAG, "loadRecordings : exit" );
	}

	public void setOnProgramGroupListener( OnProgramGroupListener listener ) {
		Log.v( TAG, "setOnProgramGroupListener : enter" );

		this.listener = listener;

		Log.v( TAG, "setOnProgramGroupListener : exit" );
	}

	public interface OnProgramGroupListener {
		void onProgramGroupSelected( List<Program> programs );
	}

	private void setPrograms( List<Program> programs ) {
		Log.v( TAG, "setPrograms : enter" );

		this.programs = programs;
		adapter = new ProgramGroupAdapter( programs );
		setListAdapter( adapter );
		
		//listener.onProgramGroupSelected( programs );
		
		Log.v( TAG, "setPrograms : exit" );
	}

	private void setProgramGroups( List<Program> programs ) {
		Log.v( TAG, "setProgramGroups : enter" );
		
		programGroups = new ArrayList<String>();
		
		String title;
		for( Program program : programs ) {
			Log.v( TAG, "setProgramGroups : program iteration" );

			title = program.getTitle();

			if( !programGroups.contains( title ) ) {
				Log.v( TAG, "setProgramGroups : adding program group" );

				programGroups.add( title );
			}
		}

		if( !programGroups.isEmpty() ) {
			Log.v( TAG, "setProgramGroups : sorting program groups" );

			Collections.sort( programGroups, String.CASE_INSENSITIVE_ORDER );
		}

		Log.v( TAG, "setProgramGroups : adding 'All' program group to start" );
		programGroups.add( 0, "All" );
				
		Log.v( TAG, "setProgramGroups : exit" );
	}
	
	private void setRecordingsInProgramGroups( List<Program> programs ) {
		Log.v( TAG, "setProgramGroups : enter" );
		
		recordingsInProgramGroups = new TreeMap<String, List<Program>>();
		
		String title;
		for( Program program : programs ) {
			Log.v( TAG, "setProgramGroups : program iteration" );
			
			title = program.getTitle();
			
			if( !recordingsInProgramGroups.containsKey( title ) ) {
				List<Program> recordingsInThisProgram = new ArrayList<Program>();
				recordingsInThisProgram.add( program );
				
				Log.v( TAG, "setProgramGroups : adding new program group" );
				recordingsInProgramGroups.put( title, recordingsInThisProgram );
			} else {
				Log.v( TAG, "setProgramGroups : updating program group" );

				recordingsInProgramGroups.get( title ).add( program );
			}
		}
		
		Log.v( TAG, "setProgramGroups : exit" );
	}
	
	private void exceptionDialolg( Throwable t ) {
		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

		builder
			.setTitle( R.string.exception )
			.setMessage( t.toString() )
			.setPositiveButton( R.string.close, null )
				.show();
	}

	private class DownloadRecordedTask extends AsyncTask<Void, Void, List<Program>> {

		private Exception e = null;

		@Override
		protected List<Program> doInBackground( Void... params ) {
			Log.v( TAG, "doInBackground : enter" );

			List<Program> lookup = null;

			try {
				Log.v( TAG, "doInBackground : lookup" );

				lookup = ( (MainApplication) getActivity().getApplicationContext() ).getMythServicesApi().dvrOperations().getRecordedList( 0, 0, true );
			} catch( Exception e ) {
				Log.v( TAG, "doInBackground : error" );

				this.e = e;
			}

			Log.v( TAG, "doInBackground : exit" );
			return lookup;
		}

		@Override
		protected void onPostExecute( List<Program> result ) {
			Log.v( TAG, "onPostExecute : enter" );

			if( null == e ) {
				setProgramGroups( result );
				setRecordingsInProgramGroups( result );
				setPrograms( result );
			} else {
				Log.e( TAG, "error getting programs", e );
				exceptionDialolg( e );
			}

			Log.v( TAG, "onPostExecute : exit" );
		}
	}

	private class ProgramGroupAdapter extends BaseAdapter {
		List<String> programGroups = null;

		ProgramGroupAdapter( List<Program> programs ) {
			super();

			List<String> sortedProgramGroups = new ArrayList<String>();
			for( Program program : programs ) {
				String title = program.getTitle();

				if( !sortedProgramGroups.contains( title ) ) {
					sortedProgramGroups.add( title );
				}
			}

			if( !sortedProgramGroups.isEmpty() ) {
				Collections.sort( sortedProgramGroups, String.CASE_INSENSITIVE_ORDER );
			}

			sortedProgramGroups.add( 0, "All" );
			
			this.programGroups = sortedProgramGroups;
		}

		@Override
		public int getCount() {
			return programGroups.size();
		}

		@Override
		public String getItem( int position ) {
			return programGroups.get( position );
		}

		@Override
		public long getItemId( int position ) {
			return position;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			View row = convertView;

			if( row == null ) {
				LayoutInflater inflater = getActivity().getLayoutInflater();

				row = inflater.inflate( R.layout.program_group_row, parent, false );
			}

			String programGroup = getItem( position );

			( (TextView) row ).setText( programGroup );

			return row;
		}
	}

}
