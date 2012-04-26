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

import java.util.List;

import org.mythtv.R;
import org.mythtv.services.api.dvr.Program;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author Daniel Frey
 *
 */
public class ProgramListAdapter extends BaseAdapter {

	private List<Program> programs;
	private final LayoutInflater layoutInflater;
	
	public ProgramListAdapter( Context context, List<Program> programs ) {
		this.programs = programs;
		this.layoutInflater = LayoutInflater.from( context );
	}
	
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		if( null == programs ) {
			return 0;
		}
		
		return programs.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Program getItem( int position ) {
		return programs.get( position );
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId( int position ) {
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView( int position, View convertView, ViewGroup parent ) {
		Program program = getItem( position );
		View view = convertView;
		
		if( null == view ) {
			view = layoutInflater.inflate( R.layout.program_list_item, parent, false );
		}

		if( null != program ) {
			TextView t = (TextView) view.findViewById( R.id.program_list_item_title ); 
			t.setText( program.getTitle() );
			
			t = (TextView) view.findViewById( R.id.program_list_item_subTitle ); 
			t.setText( program.getSubTitle() );
		}
		
		return view;
	}

}
