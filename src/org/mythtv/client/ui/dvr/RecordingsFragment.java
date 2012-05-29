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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.mythtv.R;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.services.api.content.ArtworkInfo;
import org.mythtv.services.api.dvr.Program;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
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
public class RecordingsFragment extends MythtvListFragment {

	private static final String TAG = RecordingsFragment.class.getSimpleName();

	private OnProgramGroupListener listener = null;
	private ProgramGroupAdapter adapter = null;

	private List<Program> programs;
//	private List<String> programGroups;
	private Map<String, List<Integer>> recordingsInProgramGroups;
	private Map<String, Bitmap> programGroupBanners = new TreeMap<String, Bitmap>();
	
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		Log.v( TAG, "onCreate : enter" );

		super.onCreate( savedInstanceState );

		setRetainInstance( true );

		Log.v( TAG, "onCreate : exit" );
	}

	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );

		super.onResume();
	    
		if( null == programs || programs.isEmpty() ) {
			Log.v( TAG, "onResume : load recordings" );

			loadRecordings();
		}

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
	    
		if( null != listener ) {
			if( "all".equalsIgnoreCase( adapter.getItem( position ) ) ) {
				listener.onProgramGroupSelected( programs );
			} else {
				
				List<Program> programsInProgramGroup = new ArrayList<Program>();
				for( Integer index : recordingsInProgramGroups.get( adapter.getItem( position ) ) ) {
					programsInProgramGroup.add( programs.get( index ) );
				}
				listener.onProgramGroupSelected( programsInProgramGroup );
			}
		}

		Log.v( TAG, "onListItemClick : exit" );
	}
	  
	public void loadRecordings() {
		Log.v( TAG, "loadRecordings : enter" );

		new DownloadRecordedTask().execute();

		Log.v( TAG, "loadRecordings : exit" );
	}

	public void setOnProgramGroupListener( OnProgramGroupListener listener ) {
		Log.v( TAG, "setOnProgramGroupListener : enter" );

		this.listener = listener;

		Log.v( TAG, "setOnProgramGroupListener : exit" );
	}

	public interface OnProgramGroupListener {
		void onProgramGroupSelected( List<Program> programs );
	}

	private void setPrograms( List<Program> programs ) {
		Log.v( TAG, "setPrograms : enter" );

		this.programs = programs;
		adapter = new ProgramGroupAdapter( recordingsInProgramGroups.keySet() );
		setListAdapter( adapter );
		
		Log.v( TAG, "setPrograms : exit" );
	}

//	private void setProgramGroups( List<Program> programs ) {
//		Log.v( TAG, "setProgramGroups : enter" );
//		
//		programGroups = new ArrayList<String>();
//		
//		String title;
//		for( Program program : programs ) {
//			Log.v( TAG, "setProgramGroups : program iteration" );
//
//			title = program.getTitle();
//
//			if( !programGroups.contains( title ) ) {
//				Log.v( TAG, "setProgramGroups : adding program group" );
//
//				programGroups.add( title );
//			}
//		}
//
//		if( !programGroups.isEmpty() ) {
//			Log.v( TAG, "setProgramGroups : sorting program groups" );
//
//			Collections.sort( programGroups, String.CASE_INSENSITIVE_ORDER );
//		}
//
//		Log.v( TAG, "setProgramGroups : adding 'All' program group to start" );
//		programGroups.add( 0, "All" );
//				
//		Log.v( TAG, "setProgramGroups : exit" );
//	}
	
	private void setRecordingsInProgramGroups( List<Program> programs ) {
		Log.v( TAG, "setRecordingsInProgramGroups : enter" );
		
		recordingsInProgramGroups = new TreeMap<String, List<Integer>>();
		
		List<Integer> allRecordings = new ArrayList<Integer>();
		
		String title;
		for( int i = 0; i < programs.size(); i++ ) {
			Log.v( TAG, "setRecordingsInProgramGroups : program iteration" );
			allRecordings.add( i );
			
			Program program = programs.get( i );
			
			title = program.getTitle();
			Log.v( TAG, "setRecordingsInProgramGroups : program iteration" );
			
			if( !recordingsInProgramGroups.containsKey( title ) ) {
				List<Integer> recordingsInThisProgramGroup = new ArrayList<Integer>();
				recordingsInThisProgramGroup.add( i );
				
				Log.v( TAG, "setRecordingsInProgramGroups : adding new program group, title=" + title );
				recordingsInProgramGroups.put( title, recordingsInThisProgramGroup );
			} else {
				Log.v( TAG, "setRecordingsInProgramGroups : updating program group, title=" + title );

				recordingsInProgramGroups.get( title ).add( i );
			}
		}
		
		recordingsInProgramGroups.put( "All", allRecordings );
		
		Log.v( TAG, "setRecordingsInProgramGroups : exit" );
	}
	
	private void exceptionDialolg( Throwable t ) {
		AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

		builder
			.setTitle( R.string.exception )
			.setMessage( t.toString() )
			.setPositiveButton( R.string.close, null )
				.show();
	}

	private class DownloadRecordedTask extends AsyncTask<Void, Void, List<Program>> {

		private Exception e = null;

		@Override
		protected List<Program> doInBackground( Void... params ) {
			Log.v( TAG, "doInBackground : enter" );

			List<Program> lookup = null;

			try {
				Log.v( TAG, "doInBackground : lookup" );

				lookup = getApplicationContext().getMythServicesApi().dvrOperations().getRecordedList( 0, 0, true );
			} catch( Exception e ) {
				Log.v( TAG, "doInBackground : error" );

				this.e = e;
			}

			Log.v( TAG, "doInBackground : exit" );
			return lookup;
		}

		@Override
		protected void onPostExecute( List<Program> result ) {
			Log.v( TAG, "onPostExecute : enter" );

			if( null == e ) {
//				Map<String, String> banners = new HashMap<String, String>();

				Log.v( TAG, "onPostExecute : filter livetv" );
				List<Program> filteredResults = new ArrayList<Program>();
				for( Program program : result ) {
					if( !"livetv".equalsIgnoreCase( program.getRecording().getRecordingGroup() ) ) {
						filteredResults.add( program );

//						if( !banners.containsKey( program.getTitle() ) && ( null != program.getArtwork() && null != program.getArtwork().getArtworkInfos() && !program.getArtwork().getArtworkInfos().isEmpty() ) ) {
//
//							for( ArtworkInfo info : program.getArtwork().getArtworkInfos() ) {
//								if( info.getStorageGroup().equals( DownloadBannerImageTask.BANNERS_DIR ) ) {
//
//									if( info.getUrl().indexOf( "FileName" ) != -1 ) { 
//										String filename = info.getUrl().substring( info.getUrl().indexOf( "FileName" ) );
//
//										banners.put( program.getTitle(), ( filename.split( "=" ) )[1] );
//									}
//								}
//							}
//						}
					}
				}

//				for( String key : banners.keySet() ) {
//					Log.v( TAG, "onPostExecute : banner key=" + key + ", filename=" + banners.get( key ) );
//
//					new DownloadBannerImageTask().execute( banners.get( key ) );
//				}
			
//				setProgramGroups( filteredResults );
				setRecordingsInProgramGroups( filteredResults );
				setPrograms( filteredResults );
			} else {
				Log.e( TAG, "error getting programs", e );
				exceptionDialolg( e );
			}

			Log.v( TAG, "onPostExecute : exit" );
		}

	}

	private class ProgramGroupAdapter extends BaseAdapter {
		
		List<String> programGroups = null;

		ProgramGroupAdapter( Set<String> programGroups ) {
			super();

			this.programGroups = new ArrayList<String>( programGroups );
		}

		@Override
		public int getCount() {
			return programGroups.size();
		}

		@Override
		public String getItem( int position ) {
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

			String programGroup = getItem( position );

			if( !"All".equalsIgnoreCase( programGroup ) ) {
				List<Program> programsInProgramGroup = new ArrayList<Program>();
				for( Integer index : recordingsInProgramGroups.get( programGroup ) ) {
					Program program = programs.get( index );
					Log.v( TAG, "getView : program=" + program );

					programsInProgramGroup.add( program );
				}

				for( Program program : programsInProgramGroup ) {
					Log.v( TAG, "getView : programsInProgramGroup iteration" );

					if( !programGroupBanners.containsKey( programGroup ) && ( null != program.getArtwork() && null != program.getArtwork().getArtworkInfos() && !program.getArtwork().getArtworkInfos().isEmpty() ) ) {
						Log.v( TAG, "getView : programsInProgramGroup contains artwork" );

						for( ArtworkInfo info : program.getArtwork().getArtworkInfos() ) {
							Log.v( TAG, "getView : programsInProgramGroup artwork iteration" );

							if( info.getStorageGroup().equals( DownloadBannerImageTask.BANNERS_DIR ) ) {
								Log.v( TAG, "getView : programsInProgramGroup contains banner artwork" );

								if( info.getUrl().indexOf( "FileName" ) != -1 ) { 
									Log.v( TAG, "getView : downloading banner" );

									String filename = info.getUrl().substring( info.getUrl().indexOf( "FileName" ) );

									programGroupBanners.put( programGroup, null );
									new DownloadBannerImageTask().execute( programGroup, filename.split( "=" )[ 1 ], row );

									break;
								}
							}
						}
					}
				}
			}
			
			if( !programGroupBanners.containsKey( programGroup ) ) {
				Log.v( TAG, "getView : programsInProgramGroup contains no artwork" );

				TextView textView = (TextView) row.findViewById( R.id.program_group_row );
				textView.setText( programGroup );
			}
			
			Log.v( TAG, "getView : exit" );
			return row;
		}
	}

	private class DownloadBannerImageTask extends AsyncTask<Object, Void, Bitmap> {

		private static final String BANNERS_DIR = "Banners";
		
		private Exception e = null;

		private String programGroup;
		private View row;
		
		@Override
		protected Bitmap doInBackground( Object... params ) {
			Log.v( TAG, "doInBackground : enter" );

			programGroup = (String) params[ 0 ];
			row = (View) params[ 2 ];
			
			Bitmap bitmap = null;

			try {
				Log.v( TAG, "doInBackground : lookup" );

				byte[] bytes = getApplicationContext().getMythServicesApi().contentOperations().getImageFile( BANNERS_DIR, (String) params[ 1 ], -1, -1 );
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

				programGroupBanners.put( programGroup, result );
				
				TextView textView = (TextView) row.findViewById( R.id.program_group_row );
				textView.setText( "" );

				Drawable drawable = new BitmapDrawable( result );
				row.setBackgroundDrawable( drawable );
			} else {
				Log.e( TAG, "error getting programs", e );
				exceptionDialolg( e );
			}

			Log.v( TAG, "onPostExecute : exit" );
		}

	}

}
