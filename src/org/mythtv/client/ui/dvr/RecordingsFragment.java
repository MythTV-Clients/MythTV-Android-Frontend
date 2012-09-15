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
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.mythtv.R;
import org.mythtv.client.ui.util.MythtvListFragment;
import org.mythtv.service.dvr.BannerDownloadService;
import org.mythtv.service.dvr.RecordedDownloadService;
import org.mythtv.service.dvr.cache.BannerLruMemoryCache;
import org.mythtv.service.dvr.cache.RecordedLruMemoryCache;
import org.mythtv.service.guide.ProgramGuideDownloadService;
import org.mythtv.service.util.FileHelper;
import org.mythtv.services.api.ETagInfo;
import org.mythtv.services.api.dvr.Program;
import org.mythtv.services.api.dvr.Programs;
import org.mythtv.services.utils.ArticleCleaner;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Daniel Frey
 * 
 */
public class RecordingsFragment extends MythtvListFragment { // implements LoaderManager.LoaderCallbacks<Cursor> {

	private static final String TAG = RecordingsFragment.class.getSimpleName();
	private static final int REFRESH_ID = Menu.FIRST + 2;

	private OnProgramGroupListener listener = null;
	private ProgramGroupRowAdapter adapter;
	
	private RecordedDownloadReceiver recordedDownloadReceiver;
	private BannerDownloadReceiver bannerDownloadReceiver;

	private FileHelper mFileHelper;

	private RecordedLruMemoryCache cache;
	private BannerLruMemoryCache imageCache;

	private List<Program> programGroups = new ArrayList<Program>();
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated( Bundle savedInstanceState ) {
		Log.v( TAG, "onActivityCreated : enter" );

		super.onActivityCreated( savedInstanceState );

		mFileHelper = new FileHelper( getActivity() );

		cache = new RecordedLruMemoryCache( getActivity() );
		imageCache = new BannerLruMemoryCache( getActivity() );

		setHasOptionsMenu( true );
		setRetainInstance( true );

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
		recordedDownloadFilter.setPriority( IntentFilter.SYSTEM_LOW_PRIORITY );
        getActivity().registerReceiver( recordedDownloadReceiver, recordedDownloadFilter );

		IntentFilter bannerDownloadFilter = new IntentFilter( BannerDownloadService.ACTION_DOWNLOAD );
		bannerDownloadFilter.addAction( BannerDownloadService.ACTION_COMPLETE );
		bannerDownloadFilter.setPriority( IntentFilter.SYSTEM_LOW_PRIORITY );
        getActivity().registerReceiver( bannerDownloadReceiver, bannerDownloadFilter );

        Log.v( TAG, "onStart : exit" );
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		Log.v( TAG, "onResume : enter" );
		super.onResume();

		File programCache = mFileHelper.getProgramDataDirectory();
		if( programCache.exists() ) {

			File existing = new File( programCache, RecordedDownloadService.RECORDED_FILE );
			if( !existing.exists() ) {
				getActivity().startService( new Intent( RecordedDownloadService.ACTION_DOWNLOAD ) );
			} else {
				loadData();
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
		
		Programs programs = cache.get( RecordedDownloadService.RECORDED_FILE );
		
		Map<String, Program> filtered = new TreeMap<String, Program>();
		for( Program program : programs.getPrograms() ) {
			String cleanedTitle = ArticleCleaner.clean( program.getTitle() );
			
			if( !filtered.containsKey( cleanedTitle ) ) {
				filtered.put( cleanedTitle, program );
			}
		}
		
		List<Program> sorted = new ArrayList<Program>( filtered.values() );
		Collections.sort( sorted );
		
		programGroups = sorted;
		Log.d( TAG, "loadData : programGroups=" + programGroups );

	    adapter = new ProgramGroupRowAdapter( getActivity(), programGroups );
	    setListAdapter( adapter );
		
		Log.v( TAG, "loadData : exit" );
	}
	
	private class ProgramGroupRowAdapter extends ArrayAdapter<Program> {

		private LayoutInflater mInflater;

		private List<Program> programGroups;
		
		public ProgramGroupRowAdapter( Context context, List<Program> programGroups ) {
			super( context, R.id.program_group_row, programGroups );
			Log.v( TAG, "ProgramGroupRowAdapter : enter" );

			mInflater = LayoutInflater.from( context );

			this.programGroups = programGroups;
			
			Log.v( TAG, "ProgramGroupRowAdapter : exit" );
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			Log.v( TAG, "ProgramGroupRowAdapter.getView : enter" );
			
			convertView = mInflater.inflate( R.layout.program_group_row, parent, false );
			ViewHolder mHolder = new ViewHolder();
			
			mHolder.programGroupDetail = (LinearLayout) convertView.findViewById( R.id.program_group_detail );
			mHolder.programGroup = (TextView) convertView.findViewById( R.id.program_group_row );
			
			int textColor = getResources().getColor( R.color.body_text_1 );
			
			Program program = programGroups.get( position );

			mHolder.programGroup.setText( program.getTitle() );
			BitmapDrawable banner = imageCache.get( program.getInetref() + BannerDownloadService.BANNER_FILE_EXT );
			if( null != banner ) {
				Log.v( TAG, "getView : loading banner from adapter cache" );
					
				mHolder.programGroupDetail.setBackgroundDrawable( banner );
				mHolder.programGroup.setVisibility( View.INVISIBLE );
			} else {
				Log.v( TAG, "getView : banner not found in adapter cache" );

				mHolder.programGroupDetail.setBackgroundDrawable( null );
				mHolder.programGroup.setVisibility( View.VISIBLE );
				
				Intent downloadIntent = new Intent( BannerDownloadService.ACTION_DOWNLOAD );
				downloadIntent.putExtra( BannerDownloadService.BANNER_INETREF, program.getInetref() );
				getActivity().startService( downloadIntent );
			}

			Log.v( TAG, "ProgramGroupRowAdapter.getView : exit" );
			return convertView;
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
				ETagInfo eTag = ETagInfo.createEmptyETag();
				byte[] bytes = getApplicationContext().getMythServicesApi().contentOperations().getRecordingArtwork( BANNER_TYPE, inetref, -1, -1, -1, eTag );
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

//					adapter.notifyDataSetChanged();
	                
		        } catch( Exception e ) {
		        	Log.e( TAG, "error saving file", e );
		        }
		 
			} else {
				Log.e( TAG, "error getting program group banner", e );
			}

			Log.v( TAG, "onPostExecute : exit" );
		}

	}

	private class RecordedDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( RecordedDownloadService.ACTION_PROGRESS ) ) {
	        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : progress=" + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_PROGRESS ) );
	        }
	        
	        if ( intent.getAction().equals( RecordedDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "RecordedDownloadReceiver.onReceive : complete=" + intent.getStringExtra( ProgramGuideDownloadService.EXTRA_COMPLETE ) );
	        	
	        	Toast.makeText( getActivity(), "Recorded Programs updated!", Toast.LENGTH_SHORT ).show();
	        	
	        	cache.remove( RecordedDownloadService.RECORDED_FILE );
	        	loadData();
	        }

		}
		
	}

	private class BannerDownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive( Context context, Intent intent ) {
			
	        if ( intent.getAction().equals( BannerDownloadService.ACTION_COMPLETE ) ) {
	        	Log.i( TAG, "BannerDownloadReceiver.onReceive : complete=" + intent.getStringExtra( BannerDownloadService.EXTRA_COMPLETE ) );
	        	
	        	imageCache.remove( intent.getStringExtra( BannerDownloadService.EXTRA_COMPLETE_FILENAME ) );
	        	adapter.notifyDataSetChanged();
	        }

		}
		
	}

}
