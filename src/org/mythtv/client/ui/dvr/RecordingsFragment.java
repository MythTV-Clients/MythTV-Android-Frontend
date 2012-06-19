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
import java.io.FileOutputStream;
import java.util.List;

import org.mythtv.R;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.db.dvr.ProgramGroupConstants;

import android.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Daniel Frey
 * 
 */
public class RecordingsFragment extends MythtvListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = RecordingsFragment.class.getSimpleName();

//	private OnProgramGroupListener listener = null;
//	private ProgramGroupAdapter adapter = null;

	private SimpleCursorAdapter adapter;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<Cursor> onCreateLoader( int id, Bundle args ) {
		Log.v( TAG, "onCreateLoader : enter" );
		
		String[] projection = { BaseColumns._ID, ProgramGroupConstants.FIELD_PROGRAM_GROUP, ProgramGroupConstants.FIELD_INETREF };
		 
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
		 
	    adapter = new SimpleCursorAdapter(
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
	    
//		if( null != getApplicationContext().getRecordingsLoaded() ) {
//			Log.v( TAG, "onResume : recordings previously loaded" );
//			
//			Calendar now = Calendar.getInstance();
//			long nowTimeInMillis = now.getTimeInMillis();
//			long loadedTimeInMillis = getApplicationContext().getRecordingsLoaded().getTimeInMillis();
//			
//			long diff = loadedTimeInMillis - nowTimeInMillis;
//			if( diff / (60 * 1000) > 30 ) {
//				Log.v( TAG, "onResume : its been more than 30 minutes, refresh recordings" );
//				
//				getApplicationContext().getProgramGroups().clear();
//			}
//		}
		
//		if( null == getApplicationContext().getProgramGroups() || getApplicationContext().getProgramGroups().isEmpty() ) {
//			Log.v( TAG, "onResume : load recordings" );
//
//			loadRecordings();
//		} else {
//			Log.v( TAG, "onResume : restore recordings" );
//
//			setProgramGroupAdapter();
//		}

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
	    
//		if( null != listener ) {
//			Log.v( TAG, "onListItemClick : selecting programGroup at position " + position );
//			
//			listener.onProgramGroupSelected( adapter.getItem( position ) );
//		}

		Log.v( TAG, "onListItemClick : exit" );
	}
	  
//	public void loadRecordings() {
//		Log.v( TAG, "loadRecordings : enter" );
//
//		new DownloadRecordedTask().execute();
//
//		Log.v( TAG, "loadRecordings : exit" );
//	}

//	public void setOnProgramGroupListener( OnProgramGroupListener listener ) {
//		Log.v( TAG, "setOnProgramGroupListener : enter" );
//
//		this.listener = listener;
//
//		Log.v( TAG, "setOnProgramGroupListener : exit" );
//	}

	public interface OnProgramGroupListener {
		void onProgramGroupSelected( ProgramGroup programGroup );
	}

//	private void setRecordingsInProgramGroups( List<Program> programs ) {
//		Log.v( TAG, "setRecordingsInProgramGroups : enter" );
//		
//		List<ProgramGroup> programGroups = new ArrayList<ProgramGroup>();
//		ProgramGroup all = new ProgramGroup();
//		all.setName( "All" );
//		all.setRecordings( programs );
//		programGroups.add( all );
//		
//		Map<String, List<Program>>recordingsInProgramGroups = new TreeMap<String, List<Program>>();
//		
//		String title;
//		for( Program program : programs ) {
//			Log.v( TAG, "setRecordingsInProgramGroups : program iteration" );
//			
//			title = program.getTitle();
//
//			if( !recordingsInProgramGroups.containsKey( title ) ) {
//				List<Program> recordingsInThisProgramGroup = new ArrayList<Program>();
//				recordingsInThisProgramGroup.add( program );
//				
//				Log.v( TAG, "setRecordingsInProgramGroups : adding new program group, title=" + title );
//				recordingsInProgramGroups.put( title, recordingsInThisProgramGroup );
//			} else {
//				Log.v( TAG, "setRecordingsInProgramGroups : updating program group, title=" + title );
//
//				recordingsInProgramGroups.get( title ).add( program );
//			}
//		}
//
//		for( String key : recordingsInProgramGroups.keySet() ) {
//			ProgramGroup programGroup = new ProgramGroup();
//			programGroup.setName( key );
//			programGroup.setRecordings( recordingsInProgramGroups.get( key ) );
//			
//			programGroups.add( programGroup );
//			
//			for( Program program : programGroup.getRecordings() ) {
//				Log.v( TAG, "getView : programsInProgramGroup iteration" );
//
//				if( null == programGroup.getBanner() && ( null != program.getArtwork() && null != program.getArtwork().getArtworkInfos() && !program.getArtwork().getArtworkInfos().isEmpty() ) ) {
//					Log.v( TAG, "getView : programsInProgramGroup contains artwork" );
//
//					File root = getActivity().getExternalCacheDir();
//
//					File pictureDir = new File( root, DownloadBannerImageTask.BANNERS_DIR );
//					pictureDir.mkdirs();
//
//		            String filename = programGroup.getName();
//		            filename = filename.replace( ':', '_' );
//		            filename = filename.replace( '/', '_' );
//
//		            File f = new File( pictureDir, filename + ".png" );
//					if( f.exists() ) {
//						Log.v( TAG, "getView : loading banner from cache" );
//						
//						try {
//							InputStream is = new FileInputStream( f );
//							Bitmap bitmap = BitmapFactory.decodeStream( is );
//							programGroup.setBanner( new BitmapDrawable( bitmap ) );
//						} catch( Exception e ) {
//							Log.e( TAG, "getView : error reading file", e );
//						}
//						
//						break;
//					} else {
//						for( ArtworkInfo info : program.getArtwork().getArtworkInfos() ) {
//							Log.v( TAG, "getView : programsInProgramGroup artwork iteration" );
//
//							if( info.getStorageGroup().equals( DownloadBannerImageTask.BANNERS_DIR ) ) {
//								Log.v( TAG, "getView : programsInProgramGroup contains banner artwork" );
//
//								new DownloadBannerImageTask().execute( programGroup, program.getInetref() );
//
//								break;
//							}
//						}
//					}
//				}
//			}
//
//		}
//
//		getApplicationContext().setProgramGroups( programGroups );
//		getApplicationContext().setRecordingsLoaded( Calendar.getInstance() );
//		
//		setProgramGroupAdapter();
//		
//		Log.v( TAG, "setRecordingsInProgramGroups : exit" );
//	}
	
//	private void setProgramGroupAdapter() {
//		Log.v( TAG, "setProgramGroupAdapter : enter" );
//		
//		adapter = new ProgramGroupAdapter( getApplicationContext().getProgramGroups() );
//		setListAdapter( adapter );
//
//		Log.v( TAG, "setProgramGroupAdapter : exit" );
//	}
	
	private void exceptionDialolg( Throwable t ) {
		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

		builder
			.setTitle( R.string.exception )
			.setMessage( t.toString() )
			.setPositiveButton( R.string.close, null )
				.show();
	}

//	private class DownloadRecordedTask extends AsyncTask<Void, Void, List<Program>> {
//
//		private Exception e = null;
//
//		@Override
//		protected List<Program> doInBackground( Void... params ) {
//			Log.v( TAG, "doInBackground : enter" );
//
//			List<Program> lookup = null;
//
//			try {
//				Log.v( TAG, "doInBackground : lookup" );
//
//				lookup = getApplicationContext().getMythServicesApi().dvrOperations().getRecordedList( 0, 0, true );
//			} catch( Exception e ) {
//				Log.v( TAG, "doInBackground : error" );
//
//				this.e = e;
//			}
//
//			Log.v( TAG, "doInBackground : exit" );
//			return lookup;
//		}
//
//		@Override
//		protected void onPostExecute( List<Program> result ) {
//			Log.v( TAG, "onPostExecute : enter" );
//
//			if( null == e ) {
//
//				Log.v( TAG, "onPostExecute : filter livetv" );
//				List<Program> filteredResults = new ArrayList<Program>();
//				for( Program program : result ) {
//					if( !"livetv".equalsIgnoreCase( program.getRecording().getRecordingGroup() ) ) {
//						filteredResults.add( program );
//					}
//				}
//
//				setRecordingsInProgramGroups( filteredResults );
//			} else {
//				Log.e( TAG, "error getting programs", e );
//				exceptionDialolg( e );
//			}
//
//			Log.v( TAG, "onPostExecute : exit" );
//		}
//
//	}

	private class ProgramGroupAdapter extends BaseAdapter {
		
		List<ProgramGroup> programGroups = null;

		ProgramGroupAdapter( List<ProgramGroup> programGroups ) {
			super();

			this.programGroups = programGroups;
		}

		@Override
		public int getCount() {
			return programGroups.size();
		}

		@Override
		public ProgramGroup getItem( int position ) {
			return programGroups.get( position );
		}

		@Override
		public long getItemId( int position ) {
			return position;
		}

		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			Log.v( TAG, "getView : enter" );

			View row = convertView;

			if( row == null ) {
				LayoutInflater inflater = getActivity().getLayoutInflater();

				row = inflater.inflate( R.layout.program_group_row, parent, false );
			}

			ProgramGroup programGroup = getItem( position );
			Log.v( TAG, "getView : programGroup=" + programGroup.toString() );
			
			TextView textView = (TextView) row.findViewById( R.id.program_group_row );
			if( null == programGroup.getBanner() ) {
				Log.v( TAG, "getView : programGroup contains no artwork" );

				row.setBackgroundDrawable( null );

				textView.setText( programGroup.getName() );
			} else {
				Log.v( TAG, "getView : programGroup contains artwork" );

				row.setBackgroundDrawable( programGroup.getBanner() );

				textView.setText( "" );
			}
			
			Log.v( TAG, "getView : exit" );
			return row;
		}
	}

	private class DownloadBannerImageTask extends AsyncTask<Object, Void, Bitmap> {

		private static final String BANNER_TYPE = "Banner";
		private static final String BANNERS_DIR = "Banners";
		
		private Exception e = null;

		private ProgramGroup programGroup;
		
		@Override
		protected Bitmap doInBackground( Object... params ) {
			Log.v( TAG, "doInBackground : enter" );

			programGroup = (ProgramGroup) params[ 0 ];
			
			Bitmap bitmap = null;

			try {
				Log.v( TAG, "doInBackground : lookup" );

				byte[] bytes = getApplicationContext().getMythServicesApi().contentOperations().getRecordingArtwork( BANNER_TYPE, (String) params[ 1 ], -1, -1, -1 );
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

				programGroup.setBanner( new BitmapDrawable( result ) );
				
		        try {
		            File root = getActivity().getExternalCacheDir();
		            
		            File pictureDir = new File( root, BANNERS_DIR );
		            pictureDir.mkdirs();
		            
		            String filename = programGroup.getName();
		            filename = filename.replace( ':', '_' );
		            filename = filename.replace( '/', '_' );
		            
		            File f = new File( pictureDir, filename + ".png" );
	                if( f.exists() ) {
		                return;
		            }
		
	                if( !f.exists() ) {
		                String name = f.getAbsolutePath();
		                FileOutputStream fos = new FileOutputStream( name );
		                result.compress( Bitmap.CompressFormat.PNG, 100, fos );
		                fos.flush();
		                fos.close();
		            }
	                
//	                adapter.notifyDataSetChanged();
		        } catch( Exception e ) {
		        	Log.e( TAG, "error saving file", e );
		        }
		 
			} else {
				Log.e( TAG, "error getting programs", e );
				exceptionDialolg( e );
			}

			Log.v( TAG, "onPostExecute : exit" );
		}

	}

}
