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
package org.mythtv.client.ui.dvr;

import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.UrlUtils;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.Programs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Daniel Frey
 * @author John Baab
 * 
 */
public class ProgramGroupFragment extends MythtvListFragment {

	private static final String TAG = ProgramGroupFragment.class.getSimpleName();

	private ProgramGroupRowAdapter adapter;
	
	private static ProgramHelper mProgramHelper; 
	
	private Programs programs;
	
	public ProgramGroupFragment() { }
	
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.i( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );
	    
		mProgramHelper = ProgramHelper.createInstance( getActivity() );
		
		Log.i( TAG, "onActivityCreated : exit" );
	}

	public void loadPrograms( Context context, Programs programs ) {
		Log.i( TAG, "loadPrograms : enter" );
		
		this.programs = programs;
		
		adapter = new ProgramGroupRowAdapter( context, programs.getPrograms() );
	    setListAdapter( adapter );

		Log.i( TAG, "loadPrograms : exit" );
	}
	
	@Override
	public void onListItemClick( ListView l, View v, int position, long id ) {
		Log.v( TAG, "onListItemClick : enter" );

		super.onListItemClick( l, v, position, id );
		
		Log.v (TAG, "onListItemClick : position=" + position + ", id=" + id );
	    
		Program program = programs.getPrograms().get( position );
		
		//leave if we did not get anything useful
		if(null == program || null == program.getChannelInfo()) return;
		
		Intent i = new Intent( getActivity(), VideoActivity.class );
		i.putExtra( VideoActivity.EXTRA_PROGRAM_CHANNEL_ID, program.getChannelInfo().getChannelId() );
		i.putExtra( VideoActivity.EXTRA_PROGRAM_START_TIME, DateUtils.dateTimeFormatter.print( program.getStartTime() ) );
		i.putExtra( VideoActivity.EXTRA_PROGRAM_GROUP, program.getTitle() );
		startActivity( i );

		Log.v( TAG, "onListItemClick : exit" );
	}
	
	private class ProgramGroupRowAdapter extends ArrayAdapter<Program> {

		private LayoutInflater mInflater;

		private List<Program> programGroups;
		
		public ProgramGroupRowAdapter( Context context, List<Program> programGroups ) {
			super( context, R.id.program_group_row, programGroups );
			Log.v( TAG, "ProgramGroupRowAdapter : enter" );

			mInflater = LayoutInflater.from( context );

			this.programGroups = programGroups;
			
			Log.v( TAG, "ProgramGroupRowAdapter : exit" );
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			Log.v( TAG, "ProgramGroupRowAdapter.getView : enter" );
			
			View v = convertView;
			ViewHolder mHolder;
			
			if( null == v ) {
				v = mInflater.inflate( R.layout.program_row, parent, false );
				
				mHolder = new ViewHolder();				
				mHolder.programGroupDetail = (LinearLayout) v.findViewById( R.id.program_group_detail );
				mHolder.category = (View) v.findViewById( R.id.program_category );
				mHolder.subTitle = (TextView) v.findViewById( R.id.program_sub_title );
				
				v.setTag( mHolder );
			} else {
				mHolder = (ViewHolder) v.getTag();
			}
						
			Program program = programGroups.get( position );

			mHolder.subTitle.setText( !"".equals( program.getSubTitle() ) ? program.getSubTitle() : program.getTitle() );
			mHolder.category.setBackgroundColor( mProgramHelper.getCategoryColor( program.getCategory() ) );
			
			Log.v( TAG, "ProgramGroupRowAdapter.getView : exit" );
			return v;
		}
		
	}

	private static class ViewHolder {
		
		LinearLayout programGroupDetail;
		View category;
		TextView subTitle;
		
		ViewHolder() { }

	}
	
}
