/**
 * 
 */
package org.mythtv.client.ui.dvr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Daniel Frey
 *
 */
public class UpcomingFragment extends MythtvListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = UpcomingFragment.class.getSimpleName();
	private static final SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" );
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
		
		String[] projection = { BaseColumns._ID, ProgramConstants.FIELD_TITLE, ProgramConstants.FIELD_SUB_TITLE, ProgramConstants.FIELD_START_TIME, ProgramConstants.FIELD_END_TIME };
		
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
		 
	    adapter = new UpcomingCursorAdapter( getActivity().getApplicationContext() );
	    
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

	@SuppressWarnings( "unused" )
	private class UpcomingCursorAdapter extends CursorAdapter {

		private LayoutInflater mInflater;
		private String currentStartDate, previousStartDate, endTime, title;
		
		public UpcomingCursorAdapter( Context context ) {
			super( context, null, false );
			
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
		    refHolder.title = (TextView) view.findViewById( R.id.upcoming_title );
		    refHolder.subTitle = (TextView) view.findViewById( R.id.upcoming_sub_title );

		    refHolder.detailFooter = (RelativeLayout) view.findViewById( R.id.upcoming_detail_footer );
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
			String sEndTime = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_END_TIME ) );
			String sTitle = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_TITLE ) );
			String sSubTitle = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_SUB_TITLE ) );

			currentStartDate = sStartTime.substring( 0, sStartTime.indexOf( 'T' ) );
			
			long diffInSeconds = -1;
			try {
				Date dStartTime = sdf.parse( sStartTime );
				Date dEndTime = sdf.parse( sEndTime );
				
				diffInSeconds = ( dEndTime.getTime() - dStartTime.getTime() ) / 1000;
				Log.v( TAG, "UpcomingCursorAdapter.bindView : seconds=" + diffInSeconds );
			} catch( ParseException e ) {
				Log.v( TAG, "UpcomingCursorAdapter.bindView : error parsing start and end dates" );
			}
			
			long diffInMinutes = ( diffInSeconds / 60 ) >= 60 ? diffInSeconds % 60 : diffInSeconds;
			Log.v( TAG, "UpcomingCursorAdapter.bindView : duration=" + diffInMinutes );
			
			ViewHolder mHolder = (ViewHolder) view.getTag();
			
			if( cursor.isFirst() ) {
				Log.v( TAG, "UpcomingCursorAdapter.bindView : cursor at first position, displaying header" );

				mHolder.header.setVisibility( View.VISIBLE );
				mHolder.headerLabel.setVisibility( View.VISIBLE );
				mHolder.headerLabel.setText( currentStartDate );
				mHolder.detail.setVisibility( View.GONE );
				mHolder.detailFooter.setVisibility( View.GONE );
				mHolder.dontRecord.setVisibility( View.GONE );
			} else {
				Log.v( TAG, "UpcomingCursorAdapter.bindView : cursor is not at first position" );
				cursor.moveToPrevious();
				
				String previousStartTime = cursor.getString( cursor.getColumnIndex( ProgramConstants.FIELD_START_TIME ) );
				previousStartDate = previousStartTime.substring( 0, previousStartTime.indexOf( 'T' ) );
				
				if( !currentStartDate.equals( previousStartDate ) ) {
					Log.v( TAG, "UpcomingCursorAdapter.bindView : section change" );
					
					mHolder.header.setVisibility( View.VISIBLE );
					mHolder.detail.setVisibility( View.GONE );
					mHolder.detailFooter.setVisibility( View.GONE );
					mHolder.dontRecord.setVisibility( View.GONE );
					
					mHolder.headerLabel.setText( currentStartDate );
				} else {
					Log.v( TAG, "UpcomingCursorAdapter.bindView : show detail" );

					mHolder.header.setVisibility( View.GONE );
					mHolder.detail.setVisibility( View.VISIBLE );
					mHolder.detailFooter.setVisibility( View.VISIBLE );
					mHolder.dontRecord.setVisibility( View.VISIBLE );
					
					mHolder.title.setText( sTitle );
					mHolder.subTitle.setText( sSubTitle );
					mHolder.startTime.setText( sStartTime.substring( sStartTime.indexOf( 'T' ) + 1 ) );
				}
				
				cursor.moveToNext();
			}
			
			Log.v( TAG, "UpcomingCursorAdapter.bindView : exit" );
		}

		private class ViewHolder {
			
			LinearLayout header;
			TextView headerLabel;
			
			LinearLayout detail;
			TextView title;
			TextView subTitle;

			RelativeLayout detailFooter;
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
