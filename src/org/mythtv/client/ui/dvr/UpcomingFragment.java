/**
 * 
 */
package org.mythtv.client.ui.dvr;

import org.mythtv.R;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.service.dvr.DvrServiceHelper;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
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
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Daniel Frey
 *
 */
public class UpcomingFragment extends MythtvListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = UpcomingFragment.class.getSimpleName();
	private static final int REFRESH_ID = Menu.FIRST + 2;

	private UpcomingCursorAdapter adapter;

	private UpcomingReceiver upcomingReceiver;

	private DvrServiceHelper mDvrServiceHelper;

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		Log.v( TAG, "onCreateLoader : enter" );
		
		String[] projection = { BaseColumns._ID, ProgramConstants.FIELD_TITLE, ProgramConstants.FIELD_START_TIME, ProgramConstants.FIELD_END_TIME };
		
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

	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		setHasOptionsMenu( true );
		
		setRetainInstance( true );

		getLoaderManager().initLoader( 0, null, this );
		 
	    adapter = new UpcomingCursorAdapter(
	            getActivity().getApplicationContext(), R.layout.program_group_row,
	            null, new String[] { ProgramConstants.FIELD_TITLE }, new int[] { R.id.program_group_row },
	            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );
	    
	    setListAdapter( adapter );
		
		Log.v( TAG, "onCreate : exit" );
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

			mDvrServiceHelper.getUpcomingList();
		    
	        return true;
		}
		
		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	// internal helpers

	private class UpcomingCursorAdapter extends SimpleCursorAdapter {

		public UpcomingCursorAdapter( Context context, int layout, Cursor c, String[] from, int[] to, int flags ) {
			super( context, layout, c, from, to, flags );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			
			View row =  super.getView( position, convertView, parent );
			
			getCursor().moveToPosition( position );
		    try {
		        int titleIndex = getCursor().getColumnIndexOrThrow( ProgramConstants.FIELD_TITLE );
				int startTimeIndex = getCursor().getColumnIndexOrThrow( ProgramConstants.FIELD_START_TIME );
		        int endTimeIndex = getCursor().getColumnIndexOrThrow( ProgramConstants.FIELD_END_TIME );

		        String title = getCursor().getString( titleIndex );
		        String startTime = getCursor().getString( startTimeIndex );
		        String endTime = getCursor().getString( endTimeIndex );
		        Log.v( TAG, "getView : title=" + title + ", startTime=" + startTime + ", endTime=" + endTime );
		        
				TextView textView = (TextView) row.findViewById( R.id.program_group_row );
				textView.setText( title );
		    } catch( Exception e ) {
				Log.e( TAG, "getView : error", e );
		    }
		    
			return row;
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
