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

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
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

	private OnProgramGroupListener listener = null;
	private ProgramGroupCursorAdapter adapter;
	
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
	
	@Override
	public void onActivityCreated( Bundle state ) {
		Log.v( TAG, "onActivityCreated : enter" );

		super.onActivityCreated( state );

		Log.v( TAG, "onActivityCreated : exit" );
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

//	private void exceptionDialolg( Throwable t ) {
//		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
//
//		builder
//			.setTitle( R.string.exception )
//			.setMessage( t.toString() )
//			.setPositiveButton( R.string.close, null )
//				.show();
//	}

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
		        int idIndex = getCursor().getColumnIndexOrThrow( BaseColumns._ID );
		        int nameIndex = getCursor().getColumnIndexOrThrow( ProgramGroupConstants.FIELD_PROGRAM_GROUP );
				int inetrefIndex = getCursor().getColumnIndexOrThrow( ProgramGroupConstants.FIELD_INETREF );
		        int bannerIndex = getCursor().getColumnIndexOrThrow( ProgramGroupConstants.FIELD_BANNER_URL );

		        int id = getCursor().getInt( idIndex );
		        String name = getCursor().getString( nameIndex );
		        String inetref = getCursor().getString( inetrefIndex );
		        String banner = getCursor().getString( bannerIndex );
		        Log.v( TAG, "getView : id=" + id + ", name=" + name + ", inetref=" + inetref + ", banner=" + banner );
		        
				TextView textView = (TextView) row.findViewById( R.id.program_group_row );
				if( null == banner || "".equals( banner ) ) {
					Log.v( TAG, "getView : program group contains no artwork" );

					row.setBackgroundDrawable( null );
					textView.setText( name );

					if( !"N/A".equals( banner ) && ( null != inetref && !"".equals( inetref ) ) ) {
						new DownloadBannerImageTask().execute( id, name, inetref );
					}
				} else {
					Log.v( TAG, "getView : program group contains artwork" );

					File root = getActivity().getExternalCacheDir();

					File pictureDir = new File( root, DownloadBannerImageTask.BANNERS_DIR );
					pictureDir.mkdirs();

		            File f = new File( pictureDir, inetref + ".png" );
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

		private int id;
		private String title;
		private String inetref;
		
		@Override
		protected Bitmap doInBackground( Object... params ) {
			Log.v( TAG, "doInBackground : enter" );

			id = (Integer) params[ 0 ];
			title = (String) params[ 1 ];
			inetref = (String) params[ 2 ];
			
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
		
	                if( !f.exists() ) {
		                String name = f.getAbsolutePath();
		                FileOutputStream fos = new FileOutputStream( name );
		                result.compress( Bitmap.CompressFormat.PNG, 100, fos );
		                fos.flush();
		                fos.close();

		                ContentValues values = new ContentValues();
						values.put( ProgramGroupConstants.FIELD_PROGRAM_GROUP, title );
						values.put( ProgramGroupConstants.FIELD_INETREF, inetref );
						values.put( ProgramGroupConstants.FIELD_BANNER_URL, name );

						getActivity().getApplicationContext().getContentResolver().update( ContentUris.withAppendedId( ProgramGroupConstants.CONTENT_URI, id ), values, null, null );
						
						adapter.notifyDataSetChanged();
		            }
	                
		        } catch( Exception e ) {
		        	Log.e( TAG, "error saving file", e );
		        }
		 
			} else {
				Log.e( TAG, "error getting program group banner", e );

	        	ContentValues values = new ContentValues();
				values.put( ProgramGroupConstants.FIELD_PROGRAM_GROUP, title );
				values.put( ProgramGroupConstants.FIELD_INETREF, inetref );
				values.put( ProgramGroupConstants.FIELD_BANNER_URL, "N/A" );

				getActivity().getApplicationContext().getContentResolver().update( ContentUris.withAppendedId( ProgramGroupConstants.CONTENT_URI, id ), values, null, null );
			}

			Log.v( TAG, "onPostExecute : exit" );
		}

	}

}
