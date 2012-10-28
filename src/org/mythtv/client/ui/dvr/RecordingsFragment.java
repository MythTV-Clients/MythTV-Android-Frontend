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
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.client.ui.util.ProgramHelper;
import org.mythtv.db.dvr.ProgramConstants;
import org.mythtv.db.http.EtagConstants;
import org.mythtv.service.dvr.BannerDownloadService;
import org.mythtv.service.dvr.RecordedDownloadService;
import org.mythtv.service.dvr.cache.BannerLruMemoryCache;
import org.mythtv.service.util.RunningServiceHelper;
import org.mythtv.services.api.dvr.impl.DvrTemplate.Endpoint;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Daniel Frey
 * 
 */
public class RecordingsFragment extends MythtvListFragment implements LoaderManager.LoaderCallbacks<Cursor>  {

	private static final String TAG = RecordingsFragment.class.getSimpleName();
	private static final int REFRESH_ID = Menu.FIRST + 100;

	private OnProgramGroupListener listener = null;
	private ProgramGroupCursorAdapter adapter;
	
	private RecordedDownloadReceiver recordedDownloadReceiver = new RecordedDownloadReceiver();
	private BannerDownloadReceiver bannerDownloadReceiver = new BannerDownloadReceiver();

	private static ProgramHelper mProgramHelper;
	private RunningServiceHelper mRunningServiceHelper;

	private BannerLruMemoryCache imageCache;

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		Log.v( TAG, "onCreateLoader : enter" );
		
		String[] projection = { ProgramConstants._ID, ProgramConstants.FIELD_TITLE, ProgramConstants.FIELD_PROGRAM_GROUP, ProgramConstants.FIELD_INETREF, ProgramConstants.FIELD_CATEGORY };
		
		String selection = null;
		
		String[] selectionArgs = null;
		
		String sortOrder = ProgramConstants.FIELD_PROGRAM_GROUP;
		
	    CursorLoader cursorLoader = new CursorLoader( getActivity(), Uri.withAppendedPath( ProgramConstants.CONTENT_URI_RECORDED, "programGroups" ), projection, selection, selectionArgs, sortOrder );
		
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
		
		restartLoader();
		
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
		mRunningServiceHelper = new RunningServiceHelper( getActivity() );
		
		imageCache = new BannerLruMemoryCache( getActivity() );

		setHasOptionsMenu( true );
		setRetainInstance( true );

	    adapter = new ProgramGroupCursorAdapter( getActivity().getApplicationContext() );
	    
	    setListAdapter( adapter );

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

		IntentFilter recordedDownloadFilter = new IntentFilter( RecordedDownloadService.ACTION_DOWNLOAD );
		recordedDownloadFilter.addAction( RecordedDownloadService.ACTION_PROGRESS );
		recordedDownloadFilter.addAction( RecordedDownloadService.ACTION_COMPLETE );
        getActivity().registerReceiver( recordedDownloadReceiver, recordedDownloadFilter );

		IntentFilter bannerDownloadFilter = new IntentFilter( BannerDownloadService.ACTION_DOWNLOAD );
		bannerDownloadFilter.addAction( BannerDownloadService.ACTION_COMPLETE );
        getActivity().registerReceiver( bannerDownloadReceiver, bannerDownloadFilter );

		Log.v( TAG, "onStart : enter" );
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onStart();
	    
		Cursor cursor = getActivity().getContentResolver().query( Uri.withAppendedPath( EtagConstants.CONTENT_URI, "endpoint" ), null, EtagConstants.FIELD_ENDPOINT + " = ?" ,new String[] { Endpoint.GET_RECORDED_LIST.name() }, null );
		if( cursor.moveToFirst() ) {
			Long etagDate = cursor.getLong( cursor.getColumnIndexOrThrow( EtagConstants.FIELD_DATE ) );
			
			DateTime now = new DateTime();
			if( now.getMillis() - etagDate.longValue() > 3600000 ) {
				if( !mRunningServiceHelper.isServiceRunning( "org.mythtv.service.dvr.RecordedDownloadService" ) ) {
					getActivity().startService( new Intent( RecordedDownloadService.ACTION_DOWNLOAD ) );
				}
			}
		} else {
			if( !mRunningServiceHelper.isServiceRunning( "org.mythtv.service.dvr.RecordedDownloadService" ) ) {
				getActivity().startService( new Intent( RecordedDownloadService.ACTION_DOWNLOAD ) );
			}
		}
		cursor.close();

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
				recordedDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}

		if( null != bannerDownloadReceiver ) {
			try {
				getActivity().unregisterReceiver( bannerDownloadReceiver );
				bannerDownloadReceiver = null;
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}

		Log.v( TAG, "onStop : exit" );
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
		refresh.setIcon( R.drawable.ic_menu_refresh_default );
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

			if( !mRunningServiceHelper.isServiceRunning( "org.mythtv.service.dvr.RecordedDownloadService" ) ) {
				getActivity().startService( new Intent( RecordedDownloadService.ACTION_DOWNLOAD ) );
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
		Log.v( TAG, "onListItemClick : position=" + position + ", id=" + id + ", tag=" + v.getTag() );

		listener.onProgramGroupSelected( id );
		
		Log.v( TAG, "onListItemClick : exit" );
	}
	
	public void setOnProgramGroupListener( OnProgramGroupListener listener ) {
		Log.v( TAG, "setOnProgramGroupListener : enter" );

		this.listener = listener;

		Log.v( TAG, "setOnProgramGroupListener : exit" );
	}

	public interface OnProgramGroupListener {
		
		void onProgramGroupSelected( Long recordedId );
				
	}

	public void notifyDeleted() {
		getLoaderManager().restartLoader( 0, null, this );
	}
	
	// internal helpers
	
	private void restartLoader() {
		Log.v( TAG, "restartLoader : enter" );
		
		getLoaderManager().restartLoader( 0, null, this );

		Log.v( TAG, "restartLoader : exit" );
	}

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
			refHolder.programGroupDetail = (LinearLayout) view.findViewById( R.id.program_group_detail );
			refHolder.category = (View) view.findViewById( R.id.program_group_category );
			refHolder.programGroup = (TextView) view.findViewById( R.id.program_group_row );
			
			view.setTag( refHolder );
			
			//Log.v( TAG, "newView : exit" );
			return view;
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
		 */
		@SuppressWarnings( "deprecation" )
		@Override
		public void bindView( View view, Context context, Cursor cursor ) {

	        String title = cursor.getString( cursor.getColumnIndexOrThrow( ProgramConstants.FIELD_TITLE ) );
	        String category = cursor.getString( cursor.getColumnIndexOrThrow( ProgramConstants.FIELD_CATEGORY ) );
	        //Log.v( TAG, "bindView : id=" + id + ",title=" + title + ", category=" + category );

	        ViewHolder mHolder = (ViewHolder) view.getTag();
			
			mHolder.programGroup.setText( title );
			mHolder.category.setBackgroundColor( mProgramHelper.getCategoryColor( category ) );

			BitmapDrawable banner = imageCache.get( title );
			if( null != banner ) {
				mHolder.programGroupDetail.setBackgroundDrawable( banner );
				mHolder.programGroup.setVisibility( View.INVISIBLE );
			} else {
				mHolder.programGroupDetail.setBackgroundDrawable( null );
				mHolder.programGroup.setVisibility( View.VISIBLE );
			}
			
		}

	}

	private static class ViewHolder {
		
		LinearLayout programGroupDetail;
		View category;
		TextView programGroup;
		
		ViewHolder() { }

	}
	
	private class RecordedDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : enter" );
			
	        if ( intent.getAction().equals( RecordedDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : progress=" + intent.getStringExtra( RecordedDownloadService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( RecordedDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : complete=" + intent.getStringExtra( RecordedDownloadService.EXTRA_COMPLETE ) );
	        	
	        	if( intent.getExtras().containsKey( RecordedDownloadService.EXTRA_COMPLETE_UPTODATE ) ) {
	        		Toast.makeText( getActivity(), "Recorded Program are up to date!", Toast.LENGTH_SHORT ).show();
	        	} else {
	        		Toast.makeText( getActivity(), "Recorded Programs updated!", Toast.LENGTH_SHORT ).show();
	        		
	        		adapter.notifyDataSetChanged();
	        	}
	        	
	        }

        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : exit" );
		}
		
	}

	private class BannerDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( BannerDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "BannerDownloadReceiver.onReceive : complete=" + intent.getStringExtra( BannerDownloadService.EXTRA_COMPLETE ) );
	        	
	        	if( null != intent.getStringExtra( BannerDownloadService.EXTRA_COMPLETE_FILENAME ) && !"".equals( intent.getStringExtra( BannerDownloadService.EXTRA_COMPLETE_FILENAME ) ) ) {
	        		imageCache.remove( intent.getStringExtra( BannerDownloadService.EXTRA_COMPLETE_FILENAME ) );
	        	}

	        	adapter.notifyDataSetChanged();
	        }

		}
		
	}

}
