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

import java.util.Calendar;

import org.mythtv.R;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.service.util.DateUtils;

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
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Daniel Frey
 *
 */
public class UpcomingFragment extends MythtvListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = UpcomingFragment.class.getSimpleName();

	private UpcomingCursorAdapter adapter;

	private ProgramHelper mProgramHelper;

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		Log.v( TAG, "onCreateLoader : enter" );
		
		String startDate = args.getString( "START_DATE" );
		
		String[] projection = { ProgramConstants._ID, ProgramConstants.FIELD_TITLE, ProgramConstants.FIELD_SUB_TITLE, ProgramConstants.FIELD_START_TIME, ProgramConstants.FIELD_DURATION, ProgramConstants.FIELD_CATEGORY, ProgramConstants.FIELD_CHANNEL_NUMBER };
		
		String selection =  ProgramConstants.FIELD_START_DATE + " = ? AND " + ProgramConstants.FIELD_PROGRAM_TYPE + " = ?";
		
		String[] selectionArgs = new String[] { startDate, ProgramConstants.ProgramType.UPCOMING.name() };
		
	    CursorLoader cursorLoader = new CursorLoader( getActivity(), ProgramConstants.CONTENT_URI, projection, selection, selectionArgs, ProgramConstants.FIELD_START_TIME );
		
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

		mProgramHelper = ProgramHelper.createInstance( getActivity() );
		
		setHasOptionsMenu( true );
		
		setRetainInstance( true );

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
			Log.v( TAG, "UpcomingCursorAdapter.newView : enter" );
			
			View view = mInflater.inflate( R.layout.upcoming_row, parent, false );
		    
			ViewHolder refHolder = new ViewHolder();
		    refHolder.detail = (LinearLayout) view.findViewById( R.id.upcoming_detail_row );
		    refHolder.category = (View) view.findViewById( R.id.upcoming_category );
		    refHolder.title = (TextView) view.findViewById( R.id.upcoming_title );
		    refHolder.subTitle = (TextView) view.findViewById( R.id.upcoming_sub_title );

		    refHolder.channel = (TextView) view.findViewById( R.id.upcoming_channel );
		    refHolder.startTime = (TextView) view.findViewById( R.id.upcoming_start_time );
		    refHolder.duration = (TextView) view.findViewById( R.id.upcoming_duration );

		    view.setTag(refHolder);
		    
		    Log.v( TAG, "UpcomingCursorAdapter.newView : exit" );
			return view;
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
		 */
		@Override
		public void bindView( View view, Context context, Cursor cursor ) {
			Log.v( TAG, "UpcomingCursorAdapter.bindView : enter" );

			long lStartTime = cursor.getLong( cursor.getColumnIndex( ProgramConstants.FIELD_START_TIME ) );
			int iDuration = cursor.getInt( cursor.getColumnIndex( ProgramConstants.FIELD_DURATION ) );
			String sTitle = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_TITLE ) );
			String sSubTitle = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_SUB_TITLE ) );
			String sCategory = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_CATEGORY ) );
			String sChannelNumber = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_CHANNEL_NUMBER ) );
			
			ViewHolder mHolder = (ViewHolder) view.getTag();

			Calendar startTime = Calendar.getInstance();
			startTime.setTimeInMillis( lStartTime );
				
			mHolder.category.setBackgroundColor( mProgramHelper.getCategoryColor( sCategory ) );
			mHolder.title.setText( sTitle );
			mHolder.subTitle.setText( sSubTitle );
			mHolder.channel.setText( sChannelNumber );
			mHolder.startTime.setText( DateUtils.timeFormatter.format( startTime.getTime() ) );
			mHolder.duration.setText( iDuration + " minutes" );

			Log.v( TAG, "UpcomingCursorAdapter.bindView : exit" );
		}

		private class ViewHolder {
			
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
	
}
