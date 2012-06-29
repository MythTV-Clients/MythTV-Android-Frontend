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
 * @author Daniel Frey <dmfrey at gmail dot com>
 * 
 * This software can be found at <https://github.com/dmfrey/mythtv-for-android/>
 *
 */
package org.mythtv.client.ui.dvr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.mythtv.R;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.db.dvr.ProgramGroupConstants;
import org.mythtv.service.dvr.DvrServiceHelper;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
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
	
	private Long requestId;
	private BroadcastReceiver requestReceiver;

	private DvrServiceHelper mDvrServiceHelper;

	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		Log.v( TAG, "onCreateLoader : enter" );
		
		String[] projection = { BaseColumns._ID, ProgramGroupConstants.FIELD_PROGRAM_GROUP, ProgramGroupConstants.FIELD_INETREF, ProgramGroupConstants.FIELD_BANNER_URL };
		 
	    CursorLoader cursorLoader = new CursorLoader( getActivity(), ProgramGroupConstants.CONTENT_URI, projection, null, null, ProgramGroupConstants.FIELD_PROGRAM_GROUP );
		
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
		 
	    adapter = new ProgramGroupCursorAdapter(
	            getActivity().getApplicationContext(), R.layout.program_group_row,
	            null, new String[] { ProgramGroupConstants.FIELD_PROGRAM_GROUP }, new int[] { R.id.program_group_row },
	            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );
	    
	    setListAdapter( adapter );
		
		Log.v( TAG, "onCreate : exit" );
	}

	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );

		super.onResume();
	    
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
	 * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		Log.v( TAG, "onOptionsItemSelected : enter" );
		
		switch( item.getItemId() ) {
		case REFRESH_ID:
			Log.d( TAG, "onOptionsItemSelected : prefs selected" );

			IntentFilter filter = new IntentFilter( DvrServiceHelper.ACTION_REQUEST_RESULT );
			requestReceiver = new BroadcastReceiver() {

				@Override
				public void onReceive( Context context, Intent intent ) {

					long resultRequestId = intent.getLongExtra( DvrServiceHelper.EXTRA_REQUEST_ID, 0 );

					Log.d( TAG, "Received intent " + intent.getAction() + ", request ID " + resultRequestId );

					if( resultRequestId == requestId ) {

						Log.d( TAG, "Result is for our request ID" );
						
						int resultCode = intent.getIntExtra( DvrServiceHelper.EXTRA_RESULT_CODE, 0 );

						Log.d( TAG, "Result code = " + resultCode );

						if( resultCode == 200 ) {
							Log.d( TAG, "Updating UI with new data" );
						} else {
							Log.d( TAG, "error occurred" );
						}
					} else {
						Log.d( TAG, "Result is NOT for our request ID" );
					}

				}
			};

			mDvrServiceHelper = DvrServiceHelper.getInstance( getActivity() );
			getActivity().registerReceiver( requestReceiver, filter );

			if( requestId == null ) {
				Log.i( TAG, "onResume : loading recordedList" );

				requestId = mDvrServiceHelper.getRecordingedList();
			} else if( mDvrServiceHelper.isRequestPending( requestId ) ) {
				Log.i( TAG, "onResume : recordedList waiting" );
			} else {
				Log.i( TAG, "onResume : recordedList loaded" );
			}
		    
	        return true;
		}
		
		Log.v( TAG, "onOptionsItemSelected : exit" );
		return super.onOptionsItemSelected( item );
	}

	@Override
	public void onActivityCreated( Bundle state ) {
		Log.v( TAG, "onActivityCreated : enter" );
		super.onActivityCreated( state );

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
		if( null != requestReceiver ) {
			try {
				getActivity().unregisterReceiver( requestReceiver );
			} catch( IllegalArgumentException e ) {
				Log.e( TAG, e.getLocalizedMessage(), e );
			}
		}
		
		Log.v( TAG, "onPause : exit" );
	}

	@Override
	public void onListItemClick( ListView l, View v, int position, long id ) {
		Log.v( TAG, "onListItemClick : enter" );

		super.onListItemClick( l, v, position, id );
		Log.v( TAG, "onListItemClick : position=" + position + ", id=" + id );

		Cursor cursor = getActivity().getApplicationContext().getContentResolver().query( ContentUris.withAppendedId( ProgramGroupConstants.CONTENT_URI, id ), new String[] { ProgramGroupConstants.FIELD_PROGRAM_GROUP }, null, null, null );
		if( cursor.moveToFirst() ) {
			int nameIndex = cursor.getColumnIndexOrThrow( ProgramGroupConstants.FIELD_PROGRAM_GROUP );
			String name = cursor.getString( nameIndex );
		
			listener.onProgramGroupSelected( name );
		}
		cursor.close();
		
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

	private class ProgramGroupCursorAdapter extends SimpleCursorAdapter {

		public ProgramGroupCursorAdapter( Context context, int layout, Cursor c, String[] from, int[] to, int flags ) {
			super( context, layout, c, from, to, flags );
		}

		/* (non-Javadoc)
		 * @see android.support.v4.widget.CursorAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@SuppressWarnings( "deprecation" )
		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			
			View row =  super.getView( position, convertView, parent );
			
			getCursor().moveToPosition( position );
		    try {
		        int nameIndex = getCursor().getColumnIndexOrThrow( ProgramGroupConstants.FIELD_PROGRAM_GROUP );
				int inetrefIndex = getCursor().getColumnIndexOrThrow( ProgramGroupConstants.FIELD_INETREF );
		        int bannerIndex = getCursor().getColumnIndexOrThrow( ProgramGroupConstants.FIELD_BANNER_URL );

		        String name = getCursor().getString( nameIndex );
		        String inetref = getCursor().getString( inetrefIndex );
		        String banner = getCursor().getString( bannerIndex );
		        Log.v( TAG, "getView : name=" + name + ", inetref=" + inetref + ", banner=" + banner );
		        
				TextView textView = (TextView) row.findViewById( R.id.program_group_row );
				if( null == banner || "".equals( banner ) || "N/A".equals( banner ) ) {
					Log.v( TAG, "getView : program group contains no artwork" );

					row.setBackgroundDrawable( null );
					textView.setText( name );
				} else {
					Log.v( TAG, "getView : program group contains artwork" );

		            File f = new File( banner );
					if( f.exists() ) {
						Log.v( TAG, "getView : loading banner from cache" );
						
						try {
							InputStream is = new FileInputStream( f );
							Bitmap bitmap = BitmapFactory.decodeStream( is );

							row.setBackgroundDrawable( new BitmapDrawable( bitmap ) );
							textView.setText( "" );
						} catch( Exception e ) {
							Log.e( TAG, "getView : error reading file" );

							row.setBackgroundDrawable( null );
							textView.setText( name );
						}
					} else {
						new DownloadBannerImageTask().execute( inetref );

						row.setBackgroundDrawable( null );
						textView.setText( name );
					}
				}
		    } catch( Exception e ) {
				Log.e( TAG, "getView : error", e );
		    }
		    
			return row;
		}
		
	}
	
	private class DownloadBannerImageTask extends AsyncTask<Object, Void, Bitmap> {

		private static final String BANNER_TYPE = "Banner";
		private static final String BANNERS_DIR = "Banners";
		
		private Exception e = null;

		private String inetref;
		
		@Override
		protected Bitmap doInBackground( Object... params ) {
			Log.v( TAG, "doInBackground : enter" );

			inetref = (String) params[ 0 ];
			
			Bitmap bitmap = null;

			try {
				Log.v( TAG, "doInBackground : lookup" );

				byte[] bytes = getApplicationContext().getMythServicesApi().contentOperations().getRecordingArtwork( BANNER_TYPE, inetref, -1, -1, -1 );
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

}
