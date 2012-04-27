/**
 * 
 */
package org.mythtv.client.ui.dvr;

import java.util.ArrayList;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.util.PersistentListFragment;
import org.mythtv.services.api.dvr.Program;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author Daniel Frey
 * 
 */
public class ProgramGroupFragment extends PersistentListFragment {

	private String programGroupToLoad = null;

	public ProgramGroupFragment() {
		this( null );
	}

	public ProgramGroupFragment( String programGroup ) {
		super();

		this.programGroupToLoad = programGroup;
		setRetainInstance( true );
	}

	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
	    super.onActivityCreated( savedInstanceState );
	    
	    if( null != savedInstanceState ) {
	      
	    } else if( null != programGroupToLoad ) {
    		loadPrograms( programGroupToLoad );
	    }
	}

	public void loadPrograms( String programGroup ) {
		if( "all".equalsIgnoreCase( programGroup ) ) {
			loadAllPrograms();
		} else {
			List<Program> programsInProgramGroup = new ArrayList<Program>();
			List<Program> allPrograms = ( (MainApplication) getActivity().getApplicationContext() ).getCurrentRecordings();

			for( Program program : allPrograms ) {
				String title = program.getTitle();

				if( title.equals( programGroup ) ) {
					programsInProgramGroup.add( program );
				}
			}

			setListAdapter( new ProgramAdapter( programsInProgramGroup ) );
		}
	}
	
	public void loadAllPrograms() {
		List<Program> allPrograms = ( (MainApplication) getActivity().getApplicationContext() ).getCurrentRecordings();
		setListAdapter( new ProgramAdapter( allPrograms ) );
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

			TextView title = (TextView) row.findViewById( R.id.program_title );
			title.setText( !"".equals( program.getSubTitle() ) ? program.getSubTitle() : program.getTitle() );

			TextView description = (TextView) row.findViewById( R.id.program_title );
			description.setText( program.getDescription() );

			return row;
		}
	}

}
