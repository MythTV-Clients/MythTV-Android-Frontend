/**
 * 
 */
package org.mythtv.client.ui.dvr;

import org.joda.time.DateTime;
import org.mythtv.R;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.db.channel.ChannelDaoHelper;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.services.api.channel.ChannelInfo;

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

/**
 * @author dmfrey
 *
 */
public class GuideDataFragment extends MythtvListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = GuideDataFragment.class.getSimpleName();
	
	private ProgramGuideChannelCursorAdapter mAdapter;
	
	private LocationProfile mLocationProfile;

	private DateTime mDate;
	
	/**
	 * 
	 */
	public GuideDataFragment() { }

	public void setDate( DateTime date ) {
		this.mDate = date;
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		Log.v( TAG, "onCreateLoader : enter" );
		
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		
		switch( id ) {
		case 0 :
		    Log.v( TAG, "onCreateLoader : getting channels" );

			projection = new String[] { ChannelConstants._ID, ChannelConstants.FIELD_CHAN_ID + " AS " + ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_CHAN_ID, ChannelConstants.FIELD_CHAN_NUM + " AS " + ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_CHAN_NUM, ChannelConstants.FIELD_CALLSIGN + " AS " + ChannelConstants.TABLE_NAME + "_" + ChannelConstants.FIELD_CALLSIGN };
			selection = ChannelConstants.FIELD_VISIBLE + " = ? AND " + ChannelConstants.FIELD_MASTER_HOSTNAME + " = ?";
			selectionArgs = new String[] { "1", mLocationProfile.getHostname() };
			sortOrder = ChannelConstants.FIELD_CHAN_NUM_FORMATTED;

			Log.v( TAG, "onCreateLoader : exit" );
			return new CursorLoader( getActivity(), ChannelConstants.CONTENT_URI, projection, selection, selectionArgs, sortOrder );

		case 1 :
		    Log.v( TAG, "onCreateLoader : getting prorgrams for channels" );
			
		    int channelId = args.getInt( ProgramConstants.FIELD_CHANNEL_ID );
		    long date = mDate.getMillis();
		    
		    if( args.containsKey( ProgramConstants.FIELD_END_TIME ) ) {
		    	date = args.getLong( ProgramConstants.FIELD_END_TIME );
		    }
		    
		    DateTime start = new DateTime( date );
		    
			projection = new String[] { ProgramConstants._ID, ProgramConstants.FIELD_TITLE, ProgramConstants.FIELD_SUB_TITLE, ProgramConstants.FIELD_CATEGORY, ProgramConstants.FIELD_START_TIME, ProgramConstants.FIELD_END_TIME };
			selection = ProgramConstants.FIELD_CHANNEL_ID + " = ? AND " + ProgramConstants.FIELD_END_TIME + " >= ? AND " + ProgramConstants.FIELD_START_TIME + " < ? AND " + ProgramConstants.FIELD_MASTER_HOSTNAME + " = ?";
			selectionArgs = new String[] { String.valueOf( channelId ), String.valueOf( start.getMillis() ), String.valueOf( start.plusDays( 1 ).getMillis() ), mLocationProfile.getHostname() };
			sortOrder = ProgramConstants.FIELD_START_TIME;

			Log.v( TAG, "onCreateLoader : exit" );
			return new CursorLoader( getActivity(), ProgramConstants.CONTENT_URI_GUIDE, projection, selection, selectionArgs, sortOrder );
			
		default : 
		    Log.v( TAG, "onCreateLoader : exit, invalid id" );

		    return null;
		}
			
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object)
	 */
	@Override
	public void onLoadFinished( Loader<Cursor> loader, Cursor cursor ) {
		Log.v( TAG, "onLoadFinished : enter" );
		
		mAdapter.swapCursor( cursor );
		
		Log.v( TAG, "onLoadFinished : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader)
	 */
	@Override
	public void onLoaderReset( Loader<Cursor> loader ) {
		Log.v( TAG, "onLoaderReset : enter" );
		
		mAdapter.swapCursor( null );
		
		Log.v( TAG, "onLoaderReset : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {

		View view = inflater.inflate( R.layout.program_guide_data, null );
		
		return view;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getParentFragment().getActivity() );
		
		mAdapter = new ProgramGuideChannelCursorAdapter( getParentFragment().getActivity() );
	    setListAdapter( mAdapter );

		getLoaderManager().initLoader( 0, null, this );

		Log.v( TAG, "onActivityCreated : exit" );
	}

	// internal helpers
	
	private class ProgramGuideChannelCursorAdapter extends CursorAdapter {
	
		private LayoutInflater mInflater;

		public ProgramGuideChannelCursorAdapter( Context context ) {
			super( context, null, false );
			
			mInflater = LayoutInflater.from( context );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
		 */
		@Override
		public void bindView( View view, Context context, Cursor cursor ) {
			
			ChannelInfo channel = ChannelDaoHelper.convertCursorToChannelInfo( cursor );

	        final ChannelViewHolder mHolder = (ChannelViewHolder) view.getTag();

		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
		 */
		@Override
		public View newView( Context context, Cursor cursor, ViewGroup parent ) {
			
			View view = mInflater.inflate( R.layout.program_guide_data_row, parent, false );
			
			ChannelViewHolder refHolder = new ChannelViewHolder();
			refHolder.row = (LinearLayout) view.findViewById( R.id.program_guide_data_row );
			
			return view;
		}

	}
	
	private static class ChannelViewHolder {
		
		LinearLayout row;
		
	}

	private class ProgramGuideChannelRowCursorAdapter extends CursorAdapter {
		
		private LayoutInflater mInflater;

		public ProgramGuideChannelRowCursorAdapter( Context context ) {
			super( context, null, false );
			
			mInflater = LayoutInflater.from( context );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
		 */
		@Override
		public void bindView( View view, Context context, Cursor cursor ) {
			
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
		 */
		@Override
		public View newView( Context context, Cursor cursor, ViewGroup parent ) {
			return null;
		}

	}

}
