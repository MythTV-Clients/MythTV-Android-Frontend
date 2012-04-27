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

import org.mythtv.R;
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

	public RecordingsFragment() {
		this( false );
	}

	public RecordingsFragment( boolean persistentSelection ) {
		this.persistentSelection = persistentSelection;
	}

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		setRetainInstance( true );
	}

	@Override
	public void onActivityCreated( Bundle state ) {
		super.onActivityCreated( state );

		restoreState( state );

		if( persistentSelection ) {
			enablePersistentSelection();
		}
	}

	@Override
	public void onListItemClick( ListView l, View v, int position, long id ) {
		super.onListItemClick( l, v, position, id );
	    
		if( null != listener ) {
			listener.onProgramGroupSelected( adapter.getItem( position ) );
		}
	}
	  
	public void loadRecordings( String url ) {
		new DownloadProgramsTask().execute();
	}

	public void setOnProgramGroupListener( OnProgramGroupListener listener ) {
		this.listener = listener;
	}

	public interface OnProgramGroupListener {
		void onProgramGroupSelected( String programGroup );
	}

	private void setPrograms( List<Program> programs ) {
		getApplicationContext().setCurrentRecordings( programs );
		adapter = new ProgramGroupAdapter( programs );
		setListAdapter( adapter );
	}

	private void exceptionDialolg( Throwable t ) {
		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

		builder
			.setTitle( R.string.exception )
			.setMessage( t.toString() )
			.setPositiveButton( R.string.close, null )
				.show();
	}

	private class DownloadProgramsTask extends AsyncTask<Void, Void, List<Program>> {

		private Exception e = null;

		@Override
		protected List<Program> doInBackground( Void... params ) {
			List<Program> programs = null;

			try {
				programs = getApplicationContext().getMythServicesApi().dvrOperations().getRecordedList( 0, 0, true );
			} catch( Exception e ) {
				this.e = e;
			}

			return programs;
		}

		@Override
		protected void onPostExecute( List<Program> result ) {
			if( null == e ) {
				setPrograms( result );
			} else {
				Log.e( TAG, "error getting programs", e );
				exceptionDialolg( e );
			}
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
					Log.v( TAG, "DownloadProgramsTask.onPostExecute : adding program group to list" );

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
