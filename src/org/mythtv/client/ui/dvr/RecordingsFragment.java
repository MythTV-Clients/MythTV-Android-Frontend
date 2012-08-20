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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
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

/**
 * @author Daniel Frey
 * 
 */
public class RecordingsFragment extends MythtvListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = RecordingsFragment.class.getSimpleName();
	private static final int REFRESH_ID = Menu.FIRST + 2;

	private OnProgramGroupListener listener = null;
	private ProgramGroupCursorAdapter adapter;
	
	private ProgramListReceiver programListReceiver;

	private DvrServiceHelper mDvrServiceHelper;
//	private ProgressDialog mProgressDialog;

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		Log.v( TAG, "onCreateLoader : enter" );
		
		String[] projection = { ProgramConstants._ID, ProgramConstants.FIELD_TITLE, ProgramConstants.FIELD_PROGRAM_GROUP, ProgramConstants.FIELD_INETREF };
		
		String selection = ProgramConstants.FIELD_PROGRAM_TYPE + " = ?";
		
		String[] selectionArgs = new String[] { ProgramConstants.ProgramType.RECORDED.name() };
		
		String sortOrder = ProgramConstants.FIELD_PROGRAM_GROUP;
		
	    CursorLoader cursorLoader = new CursorLoader( getActivity(), Uri.withAppendedPath( ProgramConstants.CONTENT_URI, "programGroups" ), projection, selection, selectionArgs, sortOrder );
		
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

//	    if( null != mProgressDialog ) {
//	    	mProgressDialog.dismiss();
//	    }
	    
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

		setHasOptionsMenu( true );
		setRetainInstance( true );

		getLoaderManager().initLoader( 0, null, this );
		 
	    adapter = new ProgramGroupCursorAdapter( getActivity().getApplicationContext() );
	    
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
		if( null != programListReceiver ) {
			try {
				getActivity().unregisterReceiver( programListReceiver );
				programListReceiver = null;
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

		IntentFilter programListFilter = new IntentFilter( DvrServiceHelper.PROGRAM_LIST_RESULT );
		programListFilter.setPriority( IntentFilter.SYSTEM_LOW_PRIORITY );
        programListReceiver = new ProgramListReceiver();
        getActivity().registerReceiver( programListReceiver, programListFilter );

		Cursor recordedCursor = getActivity().getContentResolver().query( ProgramConstants.CONTENT_URI, new String[] { ProgramConstants._ID }, ProgramConstants.FIELD_PROGRAM_TYPE + " = ?", new String[] { ProgramConstants.ProgramType.RECORDED.name() }, null );
		Log.v( TAG, "onResume : recorded count=" + recordedCursor.getCount() );
		if( recordedCursor.getCount() == 0 ) {
			loadData();
		}
        recordedCursor.close();
        
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

			loadData();
		    
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

		TextView programGroup = (TextView) v.findViewById( R.id.program_group_row );
		
		listener.onProgramGroupSelected( programGroup.getText().toString() );
		
		Log.v( TAG, "onListItemClick : exit" );
	}
	
	public void setOnProgramGroupListener( OnProgramGroupListener listener ) {
		Log.v( TAG, "setOnProgramGroupListener : enter" );

		this.listener = listener;

		Log.v( TAG, "setOnProgramGroupListener : exit" );
	}

	public interface OnProgramGroupListener {
		void onProgramGroupSelected( String programGroup );
	}

	// internal helpers
	
	private void loadData() {
		Log.v( TAG, "loadData : enter" );
		
//		mProgressDialog = ProgressDialog.show( getActivity(), 
//				this.getString(R.string.please_wait_title_str), 
//				this.getString(R.string.loading_recordings_msg_str), 
//				true, true );

		mDvrServiceHelper.getRecordingsList();
	    
		Log.v( TAG, "loadData : exit" );
	}
	
	private class ProgramGroupCursorAdapter extends CursorAdapter {

		private LayoutInflater mInflater;

		private Map<String, BitmapDrawable> images = new HashMap<String, BitmapDrawable>();
		
		public ProgramGroupCursorAdapter( Context context ) {
			super( context, null, false );
			
			mInflater = LayoutInflater.from( context );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#swapCursor(android.database.Cursor)
		 */
		@SuppressWarnings( "deprecation" )
		@Override
		public Cursor swapCursor( Cursor newCursor ) {
			Log.v( TAG, "swapCursor : enter" );

			if( null != newCursor ) {
				images.clear();

				while( newCursor.moveToNext() ) {
					String inetref = newCursor.getString( newCursor.getColumnIndexOrThrow( ProgramConstants.FIELD_INETREF ) );

					if( !images.containsKey( inetref ) ) {
						Log.v( TAG, "swapCursor : banner not found in adapter cache" );

			            File root = getActivity().getExternalCacheDir();
			            
			            File pictureDir = new File( root, DownloadBannerImageTask.BANNERS_DIR );
			            pictureDir.mkdirs();
			            
			            File f = new File( pictureDir, inetref + ".png" );
						if( f.exists() ) {
							Log.v( TAG, "swapCursor : loading banner from cache" );

							try {
								InputStream is = new FileInputStream( f );
								Bitmap bitmap = BitmapFactory.decodeStream( is );
								BitmapDrawable drawable = new BitmapDrawable( bitmap );

								images.put( inetref, drawable );
								Log.v( TAG, "swapCursor : banner added to adapter cache" );
							} catch( Exception e ) {
								Log.e( TAG, "swapCursor : error reading file" );
							}
						} else {
							Log.v( TAG, "swapCursor : banner not found in cache" );

							if( null != inetref && !"".equals( inetref ) ) {
								Log.v( TAG, "swapCursor : download banner" );

								new DownloadBannerImageTask().execute( inetref );
							}
						}
					}
				}

				newCursor.moveToFirst();
				notifyDataSetChanged();
			}
			
			Log.v( TAG, "swapCursor : exit" );
			return super.swapCursor( newCursor );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
		 */
		@Override
		public View newView( Context context, Cursor cursor, ViewGroup parent ) {
			Log.v( TAG, "newView : enter" );

	        View view = mInflater.inflate( R.layout.program_group_row, parent, false );
			
			ViewHolder refHolder = new ViewHolder();
			refHolder.programGroupDetail = (LinearLayout) view.findViewById( R.id.program_group_detail );
			refHolder.programGroup = (TextView) view.findViewById( R.id.program_group_row );
			
			view.setTag( refHolder );
			
			Log.v( TAG, "newView : exit" );
			return view;
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
		 */
		@SuppressWarnings( "deprecation" )
		@Override
		public void bindView( View view, Context context, Cursor cursor ) {
	        Log.v( TAG, "bindView : enter" );

	        String name = getCursor().getString( getCursor().getColumnIndexOrThrow( ProgramConstants.FIELD_TITLE ) );
	        String inetref = cursor.getString( getCursor().getColumnIndexOrThrow( ProgramConstants.FIELD_INETREF ) );
	        Log.v( TAG, "bindView : name=" + name + ", inetref=" + inetref );

	        ViewHolder mHolder = (ViewHolder) view.getTag();
			
			mHolder.programGroup.setText( name );
			if( images.containsKey( inetref ) ) {
				Log.v( TAG, "bindView : loading banner from adapter cache" );
					
				mHolder.programGroupDetail.setBackgroundDrawable( images.get( inetref ) );
				mHolder.programGroup.setVisibility( View.INVISIBLE );
			} else {
				Log.v( TAG, "bindView : banner not found in adapter cache" );

				mHolder.programGroupDetail.setBackgroundDrawable( null );
				mHolder.programGroup.setVisibility( View.VISIBLE );
			}
			
			Log.v( TAG, "bindView : exit" );
		}

		private class ViewHolder {
			
			LinearLayout programGroupDetail;
			TextView programGroup;
			
			ViewHolder() { }

		}
	}
	
	private class DownloadBannerImageTask extends AsyncTask<Object, Void, Bitmap> {

		public static final String BANNERS_DIR = "Banners";

		private static final String BANNER_TYPE = "Banner";
		
		private Exception e = null;

		private String inetref;
		
		@Override
		protected Bitmap doInBackground( Object... params ) {
			Log.v( TAG, "doInBackground : enter" );

			inetref = (String) params[ 0 ];
			
			Bitmap bitmap = null;

			try {
				Log.v( TAG, "doInBackground : lookup" );

				byte[] bytes = getApplicationContext().getMythServicesApi().contentOperations().getRecordingArtwork( BANNER_TYPE, inetref, -1, -1, -1, null );
				bitmap = BitmapFactory.decodeByteArray( bytes, 0, bytes.length );
			} catch( Exception e ) {
				Log.v( TAG, "doInBackground : error" );

				this.e = e;
			}

			Log.v( TAG, "doInBackground : exit" );
			return bitmap;
		}

		@Override
		protected void onPostExecute( Bitmap result ) {
			Log.v( TAG, "onPostExecute : enter" );

			if( null == e ) {
				Log.v( TAG, "onPostExecute : result size=" + result.getHeight() + "x" + result.getWidth() );

		        try {
		            File root = getActivity().getExternalCacheDir();
		            
		            File pictureDir = new File( root, BANNERS_DIR );
		            pictureDir.mkdirs();
		            
		            File f = new File( pictureDir, inetref + ".png" );
	                if( f.exists() ) {
		                return;
		            }
		
	                String name = f.getAbsolutePath();
	                FileOutputStream fos = new FileOutputStream( name );
	                result.compress( Bitmap.CompressFormat.PNG, 100, fos );
	                fos.flush();
	                fos.close();

					adapter.notifyDataSetChanged();
	                
		        } catch( Exception e ) {
		        	Log.e( TAG, "error saving file", e );
		        }
		 
			} else {
				Log.e( TAG, "error getting program group banner", e );
			}

			Log.v( TAG, "onPostExecute : exit" );
		}

	}

	private class ProgramListReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			Log.v( TAG, "ProgramListReceiver.onReceive : enter" );

			restartLoader();
			
			Log.v( TAG, "ProgramListReceiver.onReceive : exit" );
		}
		
	}

	private void restartLoader() {
		Log.v( TAG, "restartLoader : enter" );
		
//		ArrayList<Integer> programGroupIds = new ArrayList<Integer>();
//		Cursor cursor = getActivity().getContentResolver().query( ProgramConstants.CONTENT_URI, new String[] { ProgramConstants.FIELD_PROGRAM_GROUP_ID }, ProgramConstants.FIELD_PROGRAM_TYPE + " = ?", new String[] { ProgramConstants.ProgramType.RECORDED.name() }, null );
//		while( cursor.moveToNext() ) {
//			Integer programGroupId = cursor.getInt( cursor.getColumnIndexOrThrow( ProgramConstants.FIELD_PROGRAM_GROUP_ID ) );
//			if( !programGroupIds.contains( programGroupId ) ) {
//				programGroupIds.add( programGroupId );
//			}
//		}
//		cursor.close();
//		Log.v( TAG, "onCreate : programGroupIds=" + programGroupIds.toString() );
//		
//		Bundle bundle = new Bundle();
//		bundle.putIntegerArrayList( PROGRAM_GROUP_IDS_KEY, programGroupIds );
		
//		getLoaderManager().restartLoader( 0, bundle, this );
		getLoaderManager().restartLoader( 0, null, this );

		Log.v( TAG, "restartLoader : exit" );
	}
	
}
