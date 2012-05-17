/**
 * 
 */
package org.mythtv.client.ui.dvr;

import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.util.PersistentListFragment;
import org.mythtv.services.api.dvr.Program;

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
 * @author John Baab
 * 
 */
public class ProgramGroupFragment extends PersistentListFragment {

	private OnProgramListener listener = null;
	private ProgramAdapter adapter = null;
	private static final String TAG = ProgramGroupFragment.class.getSimpleName();
	
	private List<Program> recordingsInGroup = null;
	
	public ProgramGroupFragment() {
		this( null );
	}

	public ProgramGroupFragment( List<Program> recordingsInGroup ) {
		super();
		Log.i( TAG, "initialize : enter" );

		this.recordingsInGroup = recordingsInGroup;
		setRetainInstance( true );

		Log.i( TAG, "initialize : exit" );
	}

	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.i( TAG, "onActivityCreated : enter" );

		super.onActivityCreated( savedInstanceState );
	    
	    if( null != savedInstanceState ) {
	      
	    } else if( null != recordingsInGroup ) {
    		loadPrograms( recordingsInGroup );
	    }

	    Log.i( TAG, "onActivityCreated : exit" );
	}

	public void loadPrograms( List<Program> recordingsInGroup ) {
		Log.i( TAG, "loadPrograms : enter" );

		adapter = new ProgramAdapter( recordingsInGroup );
		setListAdapter( adapter );

		Log.i( TAG, "loadPrograms : exit" );
	}
	
	public void setOnProgramListener( OnProgramListener listener ) {
		Log.v( TAG, "setOnProgramListener : enter" );

		this.listener = listener;

		Log.v( TAG, "setOnProgramListener : exit" );
	}
	
	public interface OnProgramListener {
		void onProgramSelected( Program program );
	}
	
	@Override
	public void onListItemClick( ListView l, View v, int position, long id ) {
		Log.v( TAG, "onListItemClick : enter" );

		super.onListItemClick( l, v, position, id );
		
		Log.v (TAG, "position : " + position);
	    
		if( null != listener ) {
			listener.onProgramSelected( adapter.getItem( position ) );
			//Program program = null;
			//listener.onProgramSelected(program);
		}

		Log.v( TAG, "onListItemClick : exit" );
	}
	
	private class ProgramAdapter extends BaseAdapter {
		List<Program> programs = null;

		ProgramAdapter( List<Program> programs ) {
			super();

			this.programs = programs;
		}

		@Override
		public int getCount() {
			return programs.size();
		}

		@Override
		public Program getItem( int position ) {
			return programs.get( position );
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

				row = inflater.inflate( R.layout.program_row, parent, false );
			}

			Program program = getItem( position );

			TextView title = (TextView) row.findViewById( R.id.program_sub_title );
			title.setText( !"".equals( program.getSubTitle().trim() ) ? program.getSubTitle() : program.getTitle() );

			//TextView description = (TextView) row.findViewById( R.id.program_description );
			//description.setText( program.getDescription() );

			return row;
		}
	}

}
