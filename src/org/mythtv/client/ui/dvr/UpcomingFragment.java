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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.mythtv.R;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.service.dvr.DvrServiceHelper;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Daniel Frey
 *
 */
public class UpcomingFragment extends MythtvListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = UpcomingFragment.class.getSimpleName();
	private static final SimpleDateFormat parser = new SimpleDateFormat( "yyyy-MM-dd'T'hh:mm:ss" );
	private static final SimpleDateFormat formatter = new SimpleDateFormat( "hh:mm a" );
	private static final int REFRESH_ID = Menu.FIRST + 2;

	private UpcomingCursorAdapter adapter;

	private UpcomingReceiver upcomingReceiver;

	private DvrServiceHelper mDvrServiceHelper;
	private ProgramHelper mProgramHelper;
	private ProgressDialog mProgressDialog;

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		Log.v( TAG, "onCreateLoader : enter" );
		
		String[] projection = { BaseColumns._ID, ProgramConstants.FIELD_TITLE, ProgramConstants.FIELD_SUB_TITLE, ProgramConstants.FIELD_START_TIME, ProgramConstants.FIELD_END_TIME, ProgramConstants.FIELD_CATEGORY, ProgramConstants.FIELD_CHANNEL_ID };
		
		String selection = ProgramConstants.FIELD_PROGRAM_TYPE + " = ?";
		
		String[] selectionArgs = new String[] { ProgramConstants.ProgramType.UPCOMING.name() };
		
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
	    
	    if( null != mProgressDialog ) {
	    	mProgressDialog.dismiss();
	    }
	    
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

		getLoaderManager().initLoader( 0, null, this );
		 
	    adapter = new UpcomingCursorAdapter( getActivity().getApplicationContext() );
	    
	    setListAdapter( adapter );
		
		Log.v( TAG, "onActivityCreated : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onPause()
	 */
	@Override
	public void onPause() {
		Log.v( TAG, "onPause : enter" );
		super.onPause();

		// Unregister for broadcast
		if( null != upcomingReceiver ) {
			try {
				getActivity().unregisterReceiver( upcomingReceiver );
				upcomingReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}
		
		Log.v( TAG, "onPause : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();
	    
		mDvrServiceHelper = DvrServiceHelper.getInstance( getActivity() );

		IntentFilter upcomingFilter = new IntentFilter( DvrServiceHelper.UPCOMING_RESULT );
		upcomingFilter.setPriority( IntentFilter.SYSTEM_LOW_PRIORITY );
        upcomingReceiver = new UpcomingReceiver();
        getActivity().registerReceiver( upcomingReceiver, upcomingFilter );

		Cursor upcomingCursor = getActivity().getContentResolver().query( ProgramConstants.CONTENT_URI, new String[] { BaseColumns._ID }, ProgramConstants.FIELD_PROGRAM_TYPE + " = ?", new String[] { ProgramConstants.ProgramType.UPCOMING.name() }, null );
		Log.v( TAG, "onResume : upcoming count=" + upcomingCursor.getCount() );
		if( upcomingCursor.getCount() == 0 ) {
			loadData();
		}
        upcomingCursor.close();
        
		Log.v( TAG, "onResume : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@TargetApi( 11 )
	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		Log.v( TAG, "onCreateOptionsMenu : enter" );
		super.onCreateOptionsMenu( menu, inflater );

		MenuItem refresh = menu.add( Menu.NONE, REFRESH_ID, Menu.NONE, "Refresh" );
	    if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
	    	refresh.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
	    }
		
		Log.v( TAG, "onCreateOptionsMenu : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.AbstractRecordingsActivity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );
		
		switch( item.getItemId() ) {
		case REFRESH_ID:
			Log.d( TAG, "onOptionsItemSelected : refresh selected" );

			loadData();
		    
	        return true;
		}
		
		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	// internal helpers

	private void loadData() {
		Log.v( TAG, "loadData : enter" );
		
		mProgressDialog = ProgressDialog.show( getActivity(), 
				this.getString(R.string.please_wait_title_str),
				this.getString(R.string.loading_upcoming_recordings_msg_str), true, true );

		mDvrServiceHelper.getUpcomingList();
		
		Log.v( TAG, "loadData : exit" );
	}
	
	@SuppressWarnings( "unused" )
	private class UpcomingCursorAdapter extends CursorAdapter {

		private Context mContext;
		private LayoutInflater mInflater;
		private String currentStartDate, previousStartDate, previousStartTime, endTime, title;
		
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
		    refHolder.header = (LinearLayout) view.findViewById( R.id.upcoming_header_row );
		    refHolder.headerLabel = (TextView) view.findViewById( R.id.upcoming_header_label );
		    
		    refHolder.detail = (LinearLayout) view.findViewById( R.id.upcoming_detail_row );
		    refHolder.category = (View) view.findViewById( R.id.upcoming_category );
		    refHolder.title = (TextView) view.findViewById( R.id.upcoming_title );
		    refHolder.subTitle = (TextView) view.findViewById( R.id.upcoming_sub_title );

		    refHolder.channel = (TextView) view.findViewById( R.id.upcoming_channel );
		    refHolder.startTime = (TextView) view.findViewById( R.id.upcoming_start_time );
		    refHolder.dontRecord = (Button) view.findViewById( R.id.upcoming_dont_record );

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

			String sStartTime = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_START_TIME ) );
			
			currentStartDate = sStartTime.substring( 0, sStartTime.indexOf( 'T' ) );
			
			ViewHolder mHolder = (ViewHolder) view.getTag();
			
			if( cursor.isFirst() ) {
				Log.v( TAG, "UpcomingCursorAdapter.bindView : cursor at first position, displaying header" );

				mHolder.header.setVisibility( View.VISIBLE );
				mHolder.detail.setVisibility( View.GONE );

				mHolder.headerLabel.setText( currentStartDate );
			} else {
				Log.v( TAG, "UpcomingCursorAdapter.bindView : cursor is not at first position" );
				cursor.moveToPrevious();
				
				previousStartTime = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_START_TIME ) );
				previousStartDate = previousStartTime.substring( 0, previousStartTime.indexOf( 'T' ) );
				
				if( !currentStartDate.equals( previousStartDate ) ) {
					Log.v( TAG, "UpcomingCursorAdapter.bindView : section change" );
					
					mHolder.header.setVisibility( View.VISIBLE );
					//mHolder.detail.setVisibility( View.GONE );
					
					mHolder.headerLabel.setText( currentStartDate );
				} else {
					Log.v( TAG, "UpcomingCursorAdapter.bindView : show detail" );

					mHolder.header.setVisibility( View.GONE );
					//mHolder.detail.setVisibility( View.VISIBLE );
				}

				String sEndTime = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_END_TIME ) );
				String sTitle = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_TITLE ) );
				String sSubTitle = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_SUB_TITLE ) );
				String sCategory = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_CATEGORY ) );
				Integer iChannelId = cursor.getInt( cursor.getColumnIndex( ProgramConstants.FIELD_CHANNEL_ID ) );

				Date dStartTime = null;
				
				try {
					dStartTime = parser.parse( previousStartTime );
				} catch( ParseException e ) {
					Log.v( TAG, "UpcomingCursorAdapter.bindView : error parsing start and end dates" );
				}
				
				String sChannel = "";
				Cursor channelCursor = mContext.getContentResolver().query( ContentUris.withAppendedId( ChannelConstants.CONTENT_URI, iChannelId ), new String[] { ChannelConstants.FIELD_CHAN_NUM }, null, null, null );
				if( channelCursor.moveToFirst() ) {
					String sChannelNumber = channelCursor.getString( channelCursor.getColumnIndexOrThrow( ChannelConstants.FIELD_CHAN_NUM ) );
				
					sChannel = sChannelNumber; 
				}
				channelCursor.close();
				
				mHolder.category.setBackgroundColor( mProgramHelper.getCategoryColor( sCategory ) );
				mHolder.title.setText( sTitle );
				mHolder.subTitle.setText( sSubTitle );
				mHolder.channel.setText( sChannel );
				mHolder.startTime.setText( null != dStartTime ? formatter.format( dStartTime ) : ""  );

				cursor.moveToNext();
			}
			
			Log.v( TAG, "UpcomingCursorAdapter.bindView : exit" );
		}

		private class ViewHolder {
			
			LinearLayout header;
			TextView headerLabel;
			
			LinearLayout detail;
			View category;
			TextView title;
			TextView subTitle;

			TextView channel;
			TextView startTime;
			Button dontRecord;
			
			ViewHolder() { }

		}
		
	}
	
	private class UpcomingReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			Log.v( TAG, "UpcomingReceiver.onReceive : enter" );
			
			restartLoader();

			Log.v( TAG, "UpcomingReceiver.onReceive : exit" );
		}
		
	}

	private void restartLoader() {
		Log.v( TAG, "restartLoader : enter" );
		
		getLoaderManager().restartLoader( 0, null, this );

		Log.v( TAG, "restartLoader : exit" );
	}

}
