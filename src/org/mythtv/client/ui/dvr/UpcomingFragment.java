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

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.MainApplication;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.dvr.UpcomingDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.util.DateUtils;
import org.mythtv.services.api.dvr.Program;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Daniel Frey
 *
 */
public class UpcomingFragment extends MythtvListFragment implements LoaderManager.LoaderCallbacks<Cursor>  {

	private static final String TAG = UpcomingFragment.class.getSimpleName();
	private static final String START_DATE_KEY = "START_DATE_KEY";
	
	private UpcomingCursorAdapter adapter;

    private MainApplication mainApplication;

    private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	private ProgramHelper mProgramHelper = ProgramHelper.getInstance();
	private UpcomingDaoHelper mUpcomingDaoHelper = UpcomingDaoHelper.getInstance();
	
	public static UpcomingFragment newInstance( String formattedDay ) {
		
		UpcomingFragment fragment = new UpcomingFragment();
		
		Bundle args = new Bundle();
		args.putString( START_DATE_KEY, formattedDay );
		fragment.setArguments( args );
		
		return fragment;
	}
	
	public UpcomingFragment() {}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		Log.v( TAG, "onCreateLoader : enter" );
		
		String startDate = args.getString( START_DATE_KEY );
		DateTime startDay = new DateTime( startDate ).withTimeAtStartOfDay();
		DateTime endDay = startDay.plusDays( 1 );
		DateTime now = DateUtils.convertUtc( new DateTime() );

        mainApplication = getMainApplication();

		LocationProfile locationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		Log.v( TAG, "onCreateLoader : loading upcoming for profile " + locationProfile.getHostname() + " [" + locationProfile.getUrl() + "]" );

		String[] projection = { ProgramConstants._ID, ProgramConstants.FIELD_TITLE, ProgramConstants.FIELD_SUB_TITLE, ProgramConstants.FIELD_START_TIME, ProgramConstants.FIELD_CATEGORY };
		
		String selection = ProgramConstants.TABLE_NAME_UPCOMING + "." + ProgramConstants.FIELD_START_TIME + " >= ? AND " + 
						   ProgramConstants.TABLE_NAME_UPCOMING + "." + ProgramConstants.FIELD_START_TIME + " < ? AND " + 
						   ProgramConstants.TABLE_NAME_UPCOMING + "." + ProgramConstants.FIELD_START_TIME + " >= ? AND " + 
						   ProgramConstants.TABLE_NAME_UPCOMING + "." + ProgramConstants.FIELD_MASTER_HOSTNAME + " = ?";
		
		String[] selectionArgs = new String[] { String.valueOf( startDay.getMillis() ), String.valueOf( endDay.getMillis() ), String.valueOf( now.getMillis() ), locationProfile.getHostname() };
		
	    CursorLoader cursorLoader = new CursorLoader( getActivity(), ProgramConstants.CONTENT_URI_UPCOMING, projection, selection, selectionArgs, ProgramConstants.TABLE_NAME_UPCOMING + "." + ProgramConstants.FIELD_START_TIME );
		
	    Log.v( TAG, "onCreateLoader : exit" );
		return cursorLoader;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object)
	 */
	@Override
	public void onLoadFinished( Loader<Cursor> loader, Cursor cursor ) {
		Log.v( TAG, "onLoadFinished : enter" );
		
		adapter.swapCursor( cursor );
				
	    getListView().setFastScrollEnabled( true );
	    
		Log.v( TAG, "onLoadFinished : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader)
	 */
	@Override
	public void onLoaderReset( Loader<Cursor> loader ) {
		Log.v( TAG, "onLoaderReset : enter" );
		
		adapter.swapCursor( null );
		
		Log.v( TAG, "onLoaderReset : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		setHasOptionsMenu( true );
		
//		setRetainInstance( true );

		getLoaderManager().initLoader( 0, getArguments(), this );
		adapter = new UpcomingCursorAdapter( getActivity().getApplicationContext() );
	    setListAdapter( adapter );
		
		Log.v( TAG, "onActivityCreated : exit" );
	}

	// internal helpers

	@SuppressWarnings( "unused" )
	private class UpcomingCursorAdapter extends CursorAdapter {

		private Context mContext;
		private LayoutInflater mInflater;
		
		public UpcomingCursorAdapter( Context context ) {
			super( context, null, false );
			
			mContext = context;
			mInflater = LayoutInflater.from( context );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
		 */
		@Override
		public View newView( Context context, Cursor cursor, ViewGroup parent ) {
			//Log.v( TAG, "UpcomingCursorAdapter.newView : enter" );
			
			View view = mInflater.inflate( R.layout.upcoming_row, parent, false );
		    
			ViewHolder refHolder = new ViewHolder();
		    refHolder.category = (View) view.findViewById( R.id.upcoming_category );
		    refHolder.title = (TextView) view.findViewById( R.id.upcoming_title );
		    refHolder.subTitle = (TextView) view.findViewById( R.id.upcoming_sub_title );

		    refHolder.channel = (TextView) view.findViewById( R.id.upcoming_channel );
		    refHolder.startTime = (TextView) view.findViewById( R.id.upcoming_start_time );
		    refHolder.duration = (TextView) view.findViewById( R.id.upcoming_duration );

		    view.setTag(refHolder);
		    
		    //Log.v( TAG, "UpcomingCursorAdapter.newView : exit" );
			return view;
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
		 */
		@Override
		public void bindView( View view, Context context, Cursor cursor ) {
			//Log.v( TAG, "UpcomingCursorAdapter.bindView : enter" );

			Program program = mUpcomingDaoHelper.convertCursorToProgram( cursor );
			
			ViewHolder mHolder = (ViewHolder) view.getTag();

			mHolder.category.setBackgroundColor( mProgramHelper.getCategoryColor( program.getCategory() ) );
			mHolder.title.setText( program.getTitle() );
			mHolder.subTitle.setText( program.getSubTitle() );
			mHolder.channel.setText( null != program.getChannelInfo() ? program.getChannelInfo().getChannelNumber() : "" );
            mHolder.duration.setText( program.getDurationInMinutes() + " minutes" );
            mHolder.startTime.setText(DateUtils.getTimeWithLocaleFormatting(program.getStartTime(), mainApplication.getClockType()));

			//Log.v( TAG, "UpcomingCursorAdapter.bindView : exit" );
		}

	}

	private static class ViewHolder {
		
		View category;
		TextView title;
		TextView subTitle;

		TextView channel;
		TextView startTime;
		TextView duration;
		
		ViewHolder() { }

	}
	
}
