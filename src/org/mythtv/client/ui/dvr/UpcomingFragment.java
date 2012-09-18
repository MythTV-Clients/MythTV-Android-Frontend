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
 * This software can be found at <https://github.com/MythTV-Android/mythtv-for-android/>
 *
 */
package org.mythtv.client.ui.dvr;

import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.service.MythtvService;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.dvr.Program;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Daniel Frey
 *
 */
public class UpcomingFragment extends MythtvListFragment {

	private static final String TAG = UpcomingFragment.class.getSimpleName();
	
	private UpcomingRowAdapter adapter;

	private ProgramHelper mProgramHelper;

	private List<Program> programs;
	
	public static UpcomingFragment newInstance( List<Program> programs ) {
		return new UpcomingFragment( programs );
	}
	
	private UpcomingFragment( List<Program> programs ) {
		this.programs = programs;
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );

		super.onActivityCreated( savedInstanceState );

		mProgramHelper = ProgramHelper.createInstance( getActivity() );
		
		setHasOptionsMenu( true );
		
		setRetainInstance( true );

		adapter = new UpcomingRowAdapter( getActivity(), programs );
	    setListAdapter( adapter );
		
		Log.v( TAG, "onActivityCreated : exit" );
	}

	// internal helpers

	private class UpcomingRowAdapter extends ArrayAdapter<Program> {

		private LayoutInflater mInflater;
		
		private List<Program> programs;
		
		public UpcomingRowAdapter( Context context, List<Program> programs ) {
			super( context, R.layout.upcoming_row, programs );
			
			mInflater = LayoutInflater.from( context );
			
			this.programs = programs;
		}

		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			
			View v = convertView;
			ViewHolder mHolder;
			if( null == v ) {
				v = mInflater.inflate( R.layout.upcoming_row, parent, false );
				
				mHolder = new ViewHolder();
				mHolder.detail = (LinearLayout) v.findViewById( R.id.upcoming_detail_row );
				mHolder.category = (View) v.findViewById( R.id.upcoming_category );
				mHolder.title = (TextView) v.findViewById( R.id.upcoming_title );
				mHolder.subTitle = (TextView) v.findViewById( R.id.upcoming_sub_title );

				mHolder.channel = (TextView) v.findViewById( R.id.upcoming_channel );
				mHolder.startTime = (TextView) v.findViewById( R.id.upcoming_start_time );
				mHolder.duration = (TextView) v.findViewById( R.id.upcoming_duration );
				
				v.setTag( mHolder );
			} else {
				mHolder = (ViewHolder) v.getTag();
			}
			
			Program program = programs.get( position );
			
			long durationInMinutes = ( program.getEndTime().getMillis() / 60000 ) - ( program.getStartTime().getMillis() / 60000 );
			
			Log.v( TAG, "getView : " + program.getStartTime().toString() );
			
			mHolder.category.setBackgroundColor( mProgramHelper.getCategoryColor( program.getCategory() ) );
			mHolder.title.setText( program.getTitle() );
			mHolder.subTitle.setText( program.getSubTitle() );
			mHolder.channel.setText( null != program.getChannelInfo() ? program.getChannelInfo().getChannelNumber() : "" );
			mHolder.startTime.setText( DateUtils.timeFormatter.print( program.getStartTime().withZone( MythtvService.zone) ) );
			mHolder.duration.setText( durationInMinutes > 1 ? durationInMinutes + " minutes" : "" );

			return v;
		}

	}
	
	private static class ViewHolder {
		
		LinearLayout detail;
		View category;
		TextView title;
		TextView subTitle;

		TextView channel;
		TextView startTime;
		TextView duration;
		
		ViewHolder() { }

	}
	
}
