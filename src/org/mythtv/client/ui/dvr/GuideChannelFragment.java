/**
 * 
 */
package org.mythtv.client.ui.dvr;

import org.mythtv.R;
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.db.channel.ChannelConstants;
import org.mythtv.db.channel.ChannelDaoHelper;
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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;

/**
 * @author dmfrey
 *
 */
public class GuideChannelFragment extends MythtvListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = GuideChannelFragment.class.getSimpleName();

	private ChannelDaoHelper mChannelDaoHelper = ChannelDaoHelper.getInstance();
	
	private ProgramGuideCursorAdapter mAdapter;
	private OnChannelScrollListener mOnChannelScrollListener;
	
	private LocationProfile mLocationProfile;
	
	public interface OnChannelScrollListener {
		
		void channelScroll( int first, int last, int screenCount, int totalCount );
		
	}
	
	/**
	 * 
	 */
	public GuideChannelFragment() { }

	/**
	 * @param mOnChannelScrollListener the mOnChannelScrollListener to set
	 */
	public void setOnChannelScrollListener( OnChannelScrollListener listener ) {
		this.mOnChannelScrollListener = listener;
	}

	public ChannelInfo getChannel( int position ) {
		long id = mAdapter.getItemId( position );
		
		return mChannelDaoHelper.findOne( getParentFragment().getActivity(), mLocationProfile, id ); 
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

			projection = new String[] { ChannelConstants._ID, ChannelConstants.FIELD_CHAN_NUM, ChannelConstants.FIELD_CALLSIGN };
			selection = ChannelConstants.FIELD_VISIBLE + " = ? AND " + ChannelConstants.FIELD_MASTER_HOSTNAME + " = ?";
			selectionArgs = new String[] { "1", mLocationProfile.getHostname() };
			sortOrder = ChannelConstants.FIELD_CHAN_NUM_FORMATTED;

			Log.v( TAG, "onCreateLoader : exit" );
			return new CursorLoader( getActivity(), ChannelConstants.CONTENT_URI, projection, selection, selectionArgs, sortOrder );

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


	// internal helpers
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( savedInstanceState );

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getParentFragment().getActivity() );
		
		mAdapter = new ProgramGuideCursorAdapter( getParentFragment().getActivity() );
	    setListAdapter( mAdapter );

		getLoaderManager().initLoader( 0, null, this );

	    getListView().setFastScrollEnabled( true );
		getListView().setOnScrollListener( new OnScrollListener() {

			/* (non-Javadoc)
			 * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.AbsListView, int, int, int)
			 */
			@Override
			public void onScroll( AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount ) {
				Log.v( TAG, "onScroll : enter" );

				//mOnChannelScrollListener.channelScroll( firstVisibleItem, visibleItemCount, totalItemCount );

				Log.v( TAG, "onScroll : exit" );
			}

			/* (non-Javadoc)
			 * @see android.widget.AbsListView.OnScrollListener#onScrollStateChanged(android.widget.AbsListView, int)
			 */
			@Override
			public void onScrollStateChanged( AbsListView  view, int scrollState ) {
				Log.v( TAG, "onScrollStateChanged : enter" );

				Log.v( TAG, "onScrollStateChanged : scrollState=" + scrollState );
		        
				int first = view.getFirstVisiblePosition();
		        int last = view.getLastVisiblePosition();
		        int count = view.getChildCount();

		        if( scrollState == SCROLL_STATE_IDLE ) {
		        	mOnChannelScrollListener.channelScroll( first, last, count, mAdapter.getCount() );
		        }

				Log.v( TAG, "onScrollStateChanged : exit" );
			}
			
		});

		Log.v( TAG, "onActivityCreated : exit" );
	}


	private class ProgramGuideCursorAdapter extends CursorAdapter {
		
		private LayoutInflater mInflater;

		public ProgramGuideCursorAdapter( Context context ) {
			super( context, null, false );
			
			mInflater = LayoutInflater.from( context );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
		 */
		@Override
		public void bindView( View view, Context context, Cursor cursor ) {
			
			ChannelInfo channel = ChannelDaoHelper.convertCursorToChannelInfo( cursor );

	        final ViewHolder mHolder = (ViewHolder) view.getTag();
			
			mHolder.channel.setText( channel.getChannelNumber() );
			mHolder.callsign.setText( channel.getCallSign() );

		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
		 */
		@Override
		public View newView( Context context, Cursor cursor, ViewGroup parent ) {
			
	        View view = mInflater.inflate( R.layout.program_guide_channel, parent, false );
			
			ViewHolder refHolder = new ViewHolder();
			refHolder.channel = (TextView) view.findViewById( R.id.program_guide_channel );
			refHolder.callsign = (TextView) view.findViewById( R.id.program_guide_callsign );
			
			view.setTag( refHolder );

			return view;
		}

	}

	private static class ViewHolder {
		
		TextView channel;
		TextView callsign;
		
	}

}
