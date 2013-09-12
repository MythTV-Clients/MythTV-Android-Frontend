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
import org.mythtv.client.ui.preferences.LocationProfile;
import org.mythtv.client.ui.util.MenuHelper;
import org.mythtv.client.ui.util.MenuItemRefreshAnimated;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.db.dvr.DvrEndpoint;
import org.mythtv.db.dvr.programGroup.ProgramGroup;
import org.mythtv.db.dvr.programGroup.ProgramGroupConstants;
import org.mythtv.db.dvr.programGroup.ProgramGroupDaoHelper;
import org.mythtv.db.http.EtagConstants;
import org.mythtv.db.http.EtagDaoHelper;
import org.mythtv.db.preferences.LocationProfileDaoHelper;
import org.mythtv.service.dvr.RecordedService;
import org.mythtv.service.util.DateUtils;
import org.mythtv.service.util.RunningServiceHelper;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * @author Daniel Frey
 * @author Thomas G. Kenny Jr
 * 
 */
public class RecordingsFragment extends MythtvListFragment implements LoaderManager.LoaderCallbacks<Cursor>  {

	private static final String TAG = RecordingsFragment.class.getSimpleName();
	
	private OnProgramGroupListener listener = null;
	private ProgramGroupCursorAdapter adapter;
	
	private RecordedDownloadReceiver recordedDownloadReceiver = new RecordedDownloadReceiver();

	private static ProgramHelper mProgramHelper = ProgramHelper.getInstance();
	private EtagDaoHelper mEtagDaoHelper = EtagDaoHelper.getInstance();
	private LocationProfileDaoHelper mLocationProfileDaoHelper = LocationProfileDaoHelper.getInstance();
	private MenuHelper mMenuHelper = MenuHelper.getInstance();
	private ProgramGroupDaoHelper mProgramGroupDaoHelper = ProgramGroupDaoHelper.getInstance();
	private RunningServiceHelper mRunningServiceHelper = RunningServiceHelper.getInstance();

	private LocationProfile mLocationProfile;
	
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private MenuItemRefreshAnimated mMenuItemRefresh;

	public RecordingsFragment() { }
	
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mMenuItemRefresh = new MenuItemRefreshAnimated(this.getActivity());
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		Log.v( TAG, "onCreateLoader : enter" );
		
		String[] projection = null;
		String selection = ProgramGroupConstants.FIELD_MASTER_HOSTNAME + " = ?";
		String[] selectionArgs = new String[] { mLocationProfile.getHostname() };
		String sortOrder = ProgramGroupConstants.FIELD_SORT + " DESC, " + ProgramGroupConstants.FIELD_PROGRAM_GROUP;
		
	    CursorLoader cursorLoader = new CursorLoader( getActivity(), ProgramGroupConstants.CONTENT_URI, projection, selection, selectionArgs, sortOrder );
		
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

		mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		
		options = new DisplayImageOptions.Builder()
			.cacheInMemory( true )
			.cacheOnDisc( true )
			.build();
		
		setHasOptionsMenu( true );

	    adapter = new ProgramGroupCursorAdapter( getActivity().getApplicationContext() );
	    
	    setListAdapter( adapter );

		getListView().setOnScrollListener( new PauseOnScrollListener( imageLoader, false, true ) );
		
		getLoaderManager().initLoader( 0, null, this );
		
		Log.v( TAG, "onActivityCreated : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		Log.v( TAG, "onStart : enter" );
		super.onStart();

		IntentFilter recordedDownloadFilter = new IntentFilter( RecordedService.ACTION_DOWNLOAD );
		recordedDownloadFilter.addAction( RecordedService.ACTION_PROGRESS );
		recordedDownloadFilter.addAction( RecordedService.ACTION_COMPLETE );
        getActivity().registerReceiver( recordedDownloadReceiver, recordedDownloadFilter );

		Log.v( TAG, "onStart : enter" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();

		if( null == mLocationProfile ) {
			mLocationProfile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
		}
		
		DateTime etag = mEtagDaoHelper.findDateByEndpointAndDataId( getActivity(), mLocationProfile, DvrEndpoint.GET_RECORDED_LIST.name(), "" );
		if( null != etag ) {
			
			DateTime now = DateUtils.convertUtc( new DateTime( System.currentTimeMillis() ) );
			if( now.getMillis() - etag.getMillis() > 3600000 ) {
				if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.dvr.RecordedDownloadService" ) ) {
					getActivity().startService( new Intent( RecordedService.ACTION_DOWNLOAD ) );
				}
			}
			
		} else {
			if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.dvr.RecordedDownloadService" ) ) {
				getActivity().startService( new Intent( RecordedService.ACTION_DOWNLOAD ) );
			}
		}

        Log.v( TAG, "onResume : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		Log.v( TAG, "onStop : enter" );
		super.onStop();

		// Unregister for broadcast
		if( null != recordedDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( recordedDownloadReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}

		Log.v( TAG, "onStop : exit" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
		Log.v( TAG, "onCreateOptionsMenu : enter" );
		super.onCreateOptionsMenu( menu, inflater );

		mMenuHelper.refreshMenuItem( getActivity(), menu, this.mMenuItemRefresh );
		
		Log.v( TAG, "onCreateOptionsMenu : exit" );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.dvr.AbstractRecordingsActivity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );
		
		switch( item.getItemId() ) {
		case MenuHelper.REFRESH_ID:
			Log.d( TAG, "onOptionsItemSelected : refresh selected" );

			Cursor cursor = getActivity().getContentResolver().query( Uri.withAppendedPath( EtagConstants.CONTENT_URI, "endpoint" ), null, EtagConstants.FIELD_ENDPOINT + " = ?" ,new String[] { DvrEndpoint.GET_RECORDED_LIST.name() }, null );
			if( cursor.moveToFirst() ) {
				Long id = cursor.getLong( cursor.getColumnIndexOrThrow( EtagConstants._ID ) );

				getActivity().getContentResolver().delete( ContentUris.withAppendedId( EtagConstants.CONTENT_URI, id ), null, null );
			}
			cursor.close();

			if( !mRunningServiceHelper.isServiceRunning( getActivity(), "org.mythtv.service.dvr.RecordedDownloadService" ) ) {
				getActivity().startService( new Intent( RecordedService.ACTION_DOWNLOAD ) );
				this.mMenuItemRefresh.startRefreshAnimation();
			}
		    
	        return true;
		}
		
		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	/* (non-Javadoc)
	 * @see org.mythtv.client.ui.util.MythtvListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick( ListView l, View v, int position, long id ) {
		Log.v( TAG, "onListItemClick : enter" );
		super.onListItemClick( l, v, position, id );

		Log.v( TAG, "onListItemClick : position=" + position + ", id=" + id );

		ProgramGroup programGroup = mProgramGroupDaoHelper.findOne( getActivity(), id );		
		if( null != programGroup ) {
			Log.v( TAG, "onListItemClick : selecting program group, programGroup=" + programGroup.toString() );

			if(null != listener)
				listener.onProgramGroupSelected( programGroup );
		}
		
		Log.v( TAG, "onListItemClick : exit" );
	}
	
	public void setOnProgramGroupListener( OnProgramGroupListener listener ) {
		Log.v( TAG, "setOnProgramGroupListener : enter" );

		this.listener = listener;

		Log.v( TAG, "setOnProgramGroupListener : exit" );
	}

	public interface OnProgramGroupListener {
		
		void onProgramGroupSelected( ProgramGroup programGroup );
				
	}

	public void notifyDeleted() {
		Log.v( TAG, "notifyDeleted : enter" );
		
		getLoaderManager().restartLoader( 0, null, this );

		Log.v( TAG, "notifyDeleted : exit" );
	}
	
	// internal helpers
	
	private class ProgramGroupCursorAdapter extends CursorAdapter {

		private LayoutInflater mInflater;

		public ProgramGroupCursorAdapter( Context context ) {
			super( context, null, false );
			
			mInflater = LayoutInflater.from( context );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
		 */
		@Override
		public View newView( Context context, Cursor cursor, ViewGroup parent ) {
			//Log.v( TAG, "newView : enter" );

	        View view = mInflater.inflate( R.layout.program_group_row, parent, false );
			
			ViewHolder refHolder = new ViewHolder();
			refHolder.category = (View) view.findViewById( R.id.program_group_category );
			refHolder.programGroup = (TextView) view.findViewById( R.id.program_group_row );
			refHolder.programGroupBanner = (ImageView) view.findViewById( R.id.program_group_row_banner );
			
			view.setTag( refHolder );
			
			//Log.v( TAG, "newView : exit" );
			return view;
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
		 */
		@Override
		public void bindView( View view, Context context, Cursor cursor ) {

			ProgramGroup programGroup = mProgramGroupDaoHelper.convertCursorToProgramGroup( cursor );

	        final ViewHolder mHolder = (ViewHolder) view.getTag();
			
			mHolder.programGroup.setText( programGroup.getTitle() );
			mHolder.category.setBackgroundColor( mProgramHelper.getCategoryColor( programGroup.getCategory() ) );

			String imageUri = mLocationProfileDaoHelper.findConnectedProfile( getActivity() ).getUrl() + "Content/GetRecordingArtwork?Type=Banner&Inetref=" + programGroup.getInetref();
			imageLoader.displayImage( imageUri, mHolder.programGroupBanner, options, new SimpleImageLoadingListener() {

				/* (non-Javadoc)
				 * @see com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener#onLoadingComplete(android.graphics.Bitmap)
				 */
				@Override
				public void onLoadingComplete( String imageUri, View view, Bitmap loadedImage ) {
			        mHolder.programGroup.setVisibility( View.GONE );
			        mHolder.programGroupBanner.setVisibility( View.VISIBLE );
				}

				/* (non-Javadoc)
				 * @see com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener#onLoadingFailed(com.nostra13.universalimageloader.core.assist.FailReason)
				 */
				@Override
				public void onLoadingFailed( String imageUri, View view, FailReason failReason ) {
			        mHolder.programGroup.setVisibility( View.VISIBLE );
			        mHolder.programGroupBanner.setVisibility( View.GONE );
				}
				
			});
			
		}

	}

	private static class ViewHolder {
		
		View category;
		TextView programGroup;
		ImageView programGroupBanner;
		
		ViewHolder() { }

	}
	
	private class RecordedDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : enter" );
			
	        if ( intent.getAction().equals( RecordedService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : progress=" + intent.getStringExtra( RecordedService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( RecordedService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : complete=" + intent.getStringExtra( RecordedService.EXTRA_COMPLETE ) );
	        	
	        	mMenuItemRefresh.stopRefreshAnimation();
	        	
//	        	LocationProfile profile = mLocationProfileDaoHelper.findConnectedProfile( getActivity() );
	        	
//	        	boolean inError = false;
//	        	Cursor errorCursor = getActivity().getContentResolver().query( ProgramConstants.CONTENT_URI_RECORDED, new String[] { ProgramConstants._ID }, ProgramConstants.TABLE_NAME_RECORDED + "." + ProgramConstants.FIELD_IN_ERROR + " = ? AND " + ProgramConstants.TABLE_NAME_RECORDED + "." + ProgramConstants.FIELD_MASTER_HOSTNAME + " = ?", new String[] { "1", profile.getHostname() }, null );
//	        	if( errorCursor.moveToFirst() ) {
//	        		inError = true;
//	        	}
//	        	errorCursor.close();

	        	if( intent.getExtras().containsKey( RecordedService.EXTRA_COMPLETE_UPTODATE ) ) {
	        	} else if( intent.getExtras().containsKey( RecordedService.EXTRA_COMPLETE_OFFLINE ) ) {
	        	} else {

	        		adapter.notifyDataSetChanged();
	        	}
	        	
	        }

        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : exit" );
		}
		
	}

}
